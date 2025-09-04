# -*- coding: utf-8 -*-
"""
Auth Service

Цей сервіс відповідає за автентифікацію та авторизацію користувачів.
Він обробляє логін, створює нових користувачів (якщо їх немає) та генерує JWT-токени.
При створенні нового користувача, цей сервіс також викликає `user-service`
для створення профілю користувача.
"""

# --- 1. Імпорти (Imports) ---
# Стандартні бібліотеки
import os
from datetime import datetime, timedelta, timezone
from typing import Optional
from contextlib import asynccontextmanager

# Сторонні бібліотеки
import httpx  # Для асинхронних HTTP-запитів до інших сервісів
import jwt    # Для створення та перевірки JSON Web Tokens
from fastapi import Depends, FastAPI, HTTPException, status
from pydantic import BaseModel
from pydantic_settings import BaseSettings
from sqlalchemy import BigInteger, String, select
from sqlalchemy.ext.asyncio import AsyncSession, create_async_engine
from sqlalchemy.orm import DeclarativeBase, Mapped, mapped_column, sessionmaker

# --- 2. Налаштування (Configuration) ---
# Використовуємо Pydantic's BaseSettings для керування налаштуваннями.
# Це дозволяє завантажувати змінні середовища або використовувати значення за замовчуванням.
class Settings(BaseSettings):
    """
    Клас для зберігання всіх налаштувань додатку.
    """
    DATABASE_URL: str = "postgresql+asyncpg://default:password@localhost:5439/auth-db"
    JWT_SECRET_KEY: str = "your_super_secret_key"  # Секретний ключ для підпису JWT
    JWT_ALGORITHM: str = "HS256"                   # Алгоритм шифрування
    ACCESS_TOKEN_EXPIRE_MINUTES: int = 30          # Час життя токена доступу

    # URL для взаємодії з сервісом користувачів.
    # В Docker-середовищі ім'я хоста буде відповідати імені сервісу.
    USER_SERVICE_URL: str = "http://user-service:3002"

# Створюємо екземпляр налаштувань, який буде використовуватись у всьому додатку
settings = Settings()


# --- 3. Налаштування бази даних (Database Setup) ---
# Створюємо асинхронний "двигун" для SQLAlchemy, який буде взаємодіяти з БД.
engine = create_async_engine(settings.DATABASE_URL)

# Створюємо фабрику сесій для створення нових асинхронних сесій з БД.
AsyncSessionLocal = sessionmaker(engine, class_=AsyncSession, expire_on_commit=False)

# Базовий клас для всіх моделей SQLAlchemy.
class Base(DeclarativeBase):
    pass

# Модель `User` представляє таблицю `users` в базі даних.
# Цей сервіс зберігає мінімум інформації, потрібної для автентифікації.
class User(Base):
    __tablename__ = "users"

    id: Mapped[int] = mapped_column(BigInteger, primary_key=True, index=True)
    phone_number: Mapped[str] = mapped_column(String, unique=True, index=True)

# Функція для ініціалізації бази даних: видаляє та створює таблиці.
# Використовується при старті додатку для чистого середовища (добре для розробки).
async def create_db_and_tables():
    async with engine.begin() as conn:
        await conn.run_sync(Base.metadata.drop_all)
        await conn.run_sync(Base.metadata.create_all)

# Функція-залежність (dependency) для FastAPI.
# Вона створює сесію для кожного запиту і автоматично закриває її після завершення.
async def get_db():
    async with AsyncSessionLocal() as session:
        yield session


# --- 4. Схеми даних (Pydantic Schemas) ---
# Pydantic-моделі використовуються для валідації даних, що приходять в запитах
# та для форматування даних, що відправляються у відповідях.

class UserLoginRequest(BaseModel):
    """Схема для даних, що очікуються в тілі POST-запиту на /auth/login."""
    phoneNumber: str
    name: Optional[str] = None        # Ім'я є необов'язковим, але потрібне для реєстрації
    balance: Optional[float] = None   # Баланс також необов'язковий

class Token(BaseModel):
    """Схема для відповіді, що містить JWT-токен."""
    access_token: str
    token_type: str = "bearer"


# --- 5. Логіка JWT (JWT Logic) ---
def create_access_token(data: dict) -> str:
    """
    Створює новий JWT-токен доступу.

    Args:
        data: Словник з даними, які будуть закодовані в токен (payload).

    Returns:
        Закодований JWT-токен у вигляді рядка.
    """
    to_encode = data.copy()
    # Встановлюємо час закінчення терміну дії токена
    expire = datetime.now(timezone.utc) + timedelta(minutes=settings.ACCESS_TOKEN_EXPIRE_MINUTES)
    to_encode.update({"exp": expire})
    # Кодуємо токен з використанням секретного ключа та алгоритму
    encoded_jwt = jwt.encode(to_encode, settings.JWT_SECRET_KEY, algorithm=settings.JWT_ALGORITHM)
    return encoded_jwt


# --- 6. Головний додаток FastAPI та життєвий цикл ---
@asynccontextmanager
async def lifespan(app: FastAPI):
    """
    Менеджер контексту для життєвого циклу додатку FastAPI.
    Код перед `yield` виконується при старті, код після - при зупинці.
    """
    print("Запуск сервісу автентифікації...")
    await create_db_and_tables()
    print("Базу даних створено.")
    yield
    print("Зупинка сервісу автентифікації.")

# Створюємо екземпляр FastAPI додатку з визначеним життєвим циклом.
app = FastAPI(
    title="Authentication Service",
    description="Сервіс для автентифікації користувачів та видачі токенів.",
    version="1.0.0",
    lifespan=lifespan
)


# --- 7. Ендпоінти (API Endpoints) ---
@app.get("/", summary="Кореневий ендпоінт", description="Проста перевірка, що сервіс працює.")
def read_root():
    """Повертає привітальне повідомлення."""
    return {"Hello": "Auth Service"}

@app.post("/auth/login", response_model=Token, summary="Логін або реєстрація користувача")
async def login_for_access_token(form_data: UserLoginRequest, db: AsyncSession = Depends(get_db)):
    """
    Обробляє логін або реєстрацію користувача.

    - Якщо користувач з таким номером телефону існує, генерується JWT-токен.
    - Якщо користувача немає, він створюється в базі даних цього сервісу,
      після чого викликається `user-service` для створення повного профілю користувача.
      Після цього генерується JWT-токен.
    """
    # Шукаємо користувача в базі за номером телефону
    query = select(User).where(User.phone_number == form_data.phoneNumber)
    result = await db.execute(query)
    user = result.scalar_one_or_none()

    # --- Логіка реєстрації нового користувача ---
    if user is None:
        print(f"Користувач з номером {form_data.phoneNumber} не знайдений. Реєстрація...")

        # При реєстрації нового користувача поле 'name' є обов'язковим.
        if not form_data.name:
            raise HTTPException(
                status_code=status.HTTP_422_UNPROCESSABLE_ENTITY,
                detail="Поле 'name' є обов\'язковим при реєстрації нового користувача."
            )

        # 1. Створюємо запис в локальній базі `auth-db`
        new_user = User(phone_number=form_data.phoneNumber)
        db.add(new_user)
        await db.commit()
        await db.refresh(new_user)
        user = new_user
        print(f"Створено нового користувача в auth-db з ID: {user.id}")

        # 2. Викликаємо `user-service` для створення профілю користувача
        print(f"Виклик user-service для створення профілю...")
        user_profile_data = {
            "authId": str(user.id),
            "phone": user.phone_number,
            "name": form_data.name,
            "balance": form_data.balance if form_data.balance is not None else 0.0
        }

        try:
            async with httpx.AsyncClient() as client:
                response = await client.post(f"{settings.USER_SERVICE_URL}/users", json=user_profile_data)
                # Перевіряємо, чи був запит успішним. Якщо ні, генерується виключення.
                response.raise_for_status()
                print("Профіль в user-service успішно створено.")
        except httpx.RequestError as exc:
            # Обробка помилок мережі або невдалого запиту до user-service.
            # У реальному проекті тут може бути логіка повторних спроб (retry)
            # або відкладеного створення (через чергу повідомлень, наприклад RabbitMQ).
            print(f"ПОМИЛКА: Не вдалося викликати user-service: {exc}")
            # Повертаємо клієнту помилку, що сервіс тимчасово недоступний.
            raise HTTPException(
                status_code=status.HTTP_503_SERVICE_UNAVAILABLE,
                detail="Не вдалося створити профіль користувача. Спробуйте пізніше."
            )

    # --- Генерація токена ---
    print(f"Генерація токена для користувача ID: {user.id}")
    # Створюємо токен доступу. В 'sub' (subject) зазвичай кладуть ID користувача.
    access_token = create_access_token(
        data={"sub": str(user.id), "phone": user.phone_number}
    )

    # Повертаємо токен клієнту
    return {"access_token": access_token, "token_type": "bearer"} 

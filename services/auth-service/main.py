import os
from datetime import datetime, timedelta, timezone

from fastapi import Depends, FastAPI
from pydantic_settings import BaseSettings
from sqlalchemy import BigInteger, String, select
from sqlalchemy.ext.asyncio import AsyncSession, create_async_engine
from sqlalchemy.orm import DeclarativeBase, Mapped, mapped_column, sessionmaker
from pydantic import BaseModel
import jwt

# --- 1. Налаштування (Configuration) ---
# Використовуємо pydantic-settings для керування конфігурацією через змінні оточення.
class Settings(BaseSettings):
    # URL для підключення до бази даних.
    # Приклад: postgresql+asyncpg://user:password@host:port/dbname
    DATABASE_URL: str = "postgresql+asyncpg://default:password@localhost:5439/auth_db"
    # Секретний ключ для підпису JWT. Має бути надійним і зберігатися в секреті.
    JWT_SECRET_KEY: str = "your_super_secret_key"
    # Алгоритм для підпису JWT.
    JWT_ALGORITHM: str = "HS256"
    # Час життя токена доступу в хвилинах.
    ACCESS_TOKEN_EXPIRE_MINUTES: int = 30

settings = Settings()


# --- 2. Налаштування бази даних (Database Setup) ---
# Створюємо асинхронний "двигун" для SQLAlchemy.
engine = create_async_engine(settings.DATABASE_URL)

# Створюємо фабрику асинхронних сесій.
AsyncSessionLocal = sessionmaker(engine, class_=AsyncSession, expire_on_commit=False)

# Базовий клас для наших моделей SQLAlchemy.
class Base(DeclarativeBase):
    pass

# Модель користувача для бази даних.
class User(Base):
    __tablename__ = "users"

    id: Mapped[int] = mapped_column(BigInteger, primary_key=True, index=True)
    name: Mapped[str] = mapped_column(String, index=True)
    phone_number: Mapped[str] = mapped_column(String, unique=True, index=True)

# Функція для створення таблиць у базі даних при старті додатку.
async def create_db_and_tables():
    async with engine.begin() as conn:
        # Увага: drop_all видаляє всі таблиці. Використовувати тільки для розробки.
        await conn.run_sync(Base.metadata.drop_all)
        await conn.run_sync(Base.metadata.create_all)

# Залежність (Dependency) для отримання сесії бази даних у ендпоінтах.
async def get_db():
    async with AsyncSessionLocal() as session:
        yield session


# --- 3. Схеми даних (Pydantic Schemas) ---
# Схема для даних, що приходять в тілі запиту на логін/реєстрацію.
class UserLoginRequest(BaseModel):
    name: str
    phoneNumber: str

# Схема для відповіді з токеном.
class Token(BaseModel):
    access_token: str
    token_type: str = "bearer"


# --- 4. Логіка JWT (JWT Logic) ---

def create_access_token(data: dict):
    """Створює новий JWT токен доступу."""
    to_encode = data.copy()
    expire = datetime.now(timezone.utc) + timedelta(minutes=settings.ACCESS_TOKEN_EXPIRE_MINUTES)
    to_encode.update({"exp": expire})
    encoded_jwt = jwt.encode(to_encode, settings.JWT_SECRET_KEY, algorithm=settings.JWT_ALGORITHM)
    return encoded_jwt


from contextlib import asynccontextmanager

# --- 5. Головний додаток FastAPI та ендпоінти ---

@asynccontextmanager
async def lifespan(app: FastAPI):
    # Цей код виконується перед тим, як додаток почне приймати запити
    await create_db_and_tables()
    yield
    # Цей код виконується після того, як додаток завершить роботу (тут можна закривати з'єднання)

app = FastAPI(lifespan=lifespan)


@app.get("/")
def read_root():
    return {"Hello": "Auth Service"}

@app.post("/auth/login", response_model=Token)
async def login_for_access_token(form_data: UserLoginRequest, db: AsyncSession = Depends(get_db)):
    """
    Обробляє логін користувача, реалізуючи логіку "знайти або створити".
    1. Шукає користувача за номером телефону.
    2. Якщо користувач не знайдений - створює нового.
    3. Створює та повертає JWT токен для знайденого/створеного користувача.
    """

    # Шукаємо користувача в базі даних за номером телефону.
    query = select(User).where(User.phone_number == form_data.phoneNumber)
    result = await db.execute(query)
    user = result.scalar_one_or_none()

    # Якщо користувача не знайдено, створюємо його.
    if user is None:
        new_user = User(name=form_data.name, phone_number=form_data.phoneNumber)
        db.add(new_user)
        await db.commit()
        await db.refresh(new_user)
        user = new_user

    # Створюємо токен доступу для цього користувача.
    access_token = create_access_token(
        data={"sub": str(user.id), "name": user.name}
    )

    return {"access_token": access_token, "token_type": "bearer"}
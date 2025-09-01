# Мікросервісна архітектура ресторану

## Docker
``` bash
docker-compose --project-directory . -f infrastructure/docker/docker-compose.yml up --build
```

## Технічна специфікація проекту

### Огляд проекту

**Тип:** Open-source освітній проект

**Мета:** Демонстрація сучасної мікросервісної архітектури з візуалізацією, моніторингом та можливостями навантажувального тестування

---

## 1. Бізнес-вимоги

### Функціональні вимоги

**Основний функціонал:**

- Реєстрація та автентифікація користувачів
- Створення та управління замовленнями ресторану
- Управління складськими запасами (інгредієнти)
- Обробка платежів (mock-реалізація)
- Процес приготування страв з інтеграцією віртуального кухонного обладнання
- Система повідомлень (email, SMS, push)
- Адміністративна панель управління

**Демонстраційні можливості:**

- Візуалізація міжсервісної взаємодії в реальному часі
- Дашборд для моніторингу системи та метрик
- Навантажувальне тестування з автоматичним масштабуванням
- Управління конфігураціями сервісів через UI
- Трасування запитів (distributed tracing)

### Нефункціональні вимоги

**Продуктивність:**

- Підтримка до 1000 одночасних користувачів
- Час відгуку API менше 200ms для 95% запитів
- Пропускна здатність: 500 замовлень на хвилину

**Надійність:**

- Доступність системи 99.9%
- Автоматичне відновлення після збоїв
- Graceful degradation при відмові окремих сервісів

**Масштабованість:**

- Горизонтальне масштабування всіх сервісів
- Автоматичне масштабування на основі метрик CPU/Memory
- Підтримка від 1 до 10+ реплік кожного сервісу

---

## 2. Архітектурне рішення

### Архітектурні принципи

- **Microservices Architecture** - слабка зв'язність, висока автономність
- **Domain-Driven Design** - чіткий поділ бізнес-доменів
- **API-First Design** - всі взаємодії через добре документовані API
- **Event-Driven Architecture** - асинхронна обробка подій
- **Cloud-Native Patterns** - готовність до контейнеризації та оркестрації

### Стратегія даних

- **Database per Service** - кожен сервіс має власну БД
- **CQRS** для складних запитів (Order Service)
- **Event Sourcing** для аудиту критичних операцій

---

## 3. Технологічний стек

| Компонент | Технологія | Обґрунтування вибору |
| --- | --- | --- |
| **API Gateway** | Go + Gin Framework | Висока продуктивність, проста конфігурація |
| **Auth Service** | Python + FastAPI | Швидка розробка, відмінна документація API |
| **User Service** | Node.js + Express | JavaScript екосистема, швидке прототипування |
| **Order Service** | Java + Spring Boot | Enterprise-ready, багаті можливості |
| **Inventory Service** | Rust + Actix Web | Безпека пам'яті, висока продуктивність |
| **Payment Service** | Go + gRPC | Надійність, типізовані контракти |
| **Cooking Service** | C++ | Інтеграція з апаратним забезпеченням, продуктивність |
| **Notification Service** | Python + Celery | Асинхронна обробка, надійні черги |
| **Virtual Hardware** | C++ | Низькорівнева робота, емуляція заліза |

### Інфраструктурні компоненти

| Компонент | Технологія | Призначення |
| --- | --- | --- |
| **Container Platform** | Docker + Kubernetes | Оркестрація та управління контейнерами |
| **Service Mesh** | Istio | Управління трафіком, безпека, спостережливість |
| **Message Broker** | RabbitMQ | Асинхронна обробка подій |
| **Databases** | PostgreSQL, MongoDB, Redis | Різні патерни зберігання даних |
| **Monitoring** | Prometheus + Grafana | Збір метрик та візуалізація |
| **Tracing** | Jaeger | Distributed tracing |
| **Logging** | ELK Stack | Централізоване логування |
| **CI/CD** | GitHub Actions | Автоматизація збірки та деплою |

### Frontend Stack

| Компонент | Технологія | Призначення |
| --- | --- | --- |
| **Admin Dashboard** | React + TypeScript | Адміністративна панель |
| **Visualization** | D3.js + Three.js | 3D візуалізація мікросервісів |
| **Real-time Updates** | Socket.io | Оновлення в реальному часі |
| **UI Framework** | Material-UI | Сучасний дизайн компонентів |
| **State Management** | Redux Toolkit | Управління станом додатку |

---

## 4. Деталізація мікросервісів

### API Gateway Service (Go)

**Відповідальність:** Єдина точка входу, маршрутизація, автентифікація

**Особливості:**

- Rate limiting і throttling
- Request/Response трансформация
- Кореляційні ID для трасування
- Health checks и circuit breaker

### Authentication Service (Python + FastAPI)

**Відповідальність:** JWT токени, управління сесіями

**База даних:** PostgreSQL

**API:**

- `POST /auth/register` - реєстрація
- `POST /auth/login` - автентифікація
- `POST /auth/refresh` - оновлення токена
- `GET /auth/validate` - валідація токена

### User Service (Node.js + Express)

**Відповідальність:** Профілі користувачів, уподобання

**База даних:** MongoDB

**Особливості:**

- Кешування в Redis
- Webhook інтеграції для повідомлень

### Order Service (Java + Spring Boot)

**Відповідальність:** Життєвий цикл замовлень, координація процесу

**База даних:** PostgreSQL

**Патерни:**

- CQRS для поділу читання та запису
- Saga pattern для розподілених транзакцій
- Event sourcing для аудиту

### Inventory Service (Rust + Actix Web)

**Відповідальність:** Управління складом, резервування

**База даних:** PostgreSQL + Redis (кеш)

**Особливості:**

- Optimistic locking для конкурентного доступу
- Автоматичні повідомлення про низькі залишки

### Payment Service (Go + gRPC)

**Відповідальність:** Обробка платежів (mock)

**База даних:** PostgreSQL

**Особливості:**

- Ідемпотентність операцій
- Механізми повторних спроб з exponential backoff
- PCI DSS compliance patterns (симуляція)

### Cooking Service (C++)

**Відповідальність:** Координація приготування, інтеграція з залізом

**Особливості:**

- Взаємодія з віртуальним обладнанням через IPC
- Кінцеві автомати для процесів приготування
- Real-time моніторинг температур і таймерів

### Virtual Hardware Service (C++)

**Відповідальність:** Емуляція кухонного обладнання

**Компоненти:**

- Віртуальна духовка з датчиками температури
- Пательня з контролем нагріву
- Таймери приготування
- Датчики готовності страв

### Notification Service (Python + Celery)

**Відповідальність:** Відправка повідомлень

**Канали:**

- Email (SMTP)
- SMS (Twilio API mock)
- Push notifications (WebSocket)
- In-app notifications

---

## 5. Схема взаємодій

### Синхронні взаємодії (HTTP/gRPC)

```
Client → API Gateway → [Auth, User, Order, Inventory, Payment] Services

```

### Асинхронні взаємодії (RabbitMQ)

```
Events: order.created → payment.requested → order.paid → cooking.started → order.ready

```

### Event-Driven Flow

1. **Order Created** → Inventory check → Payment processing
2. **Payment Success** → Cooking initiation → Hardware commands
3. **Cooking Complete** → Notification dispatch → Order status update

---

## 6. База даних та персистентність

### Database Design per Service

| Service | Database | Schema Design |
| --- | --- | --- |
| **Auth** | PostgreSQL | users, sessions, refresh_tokens |
| **User** | MongoDB | user_profiles (JSON documents) |
| **Order** | PostgreSQL | orders, order_items, order_events |
| **Inventory** | PostgreSQL | ingredients, stock_levels, reservations |
| **Payment** | PostgreSQL | transactions, payment_methods |
| **Notification** | Redis | message queues, delivery status |

### Caching Strategy

- **Redis** для даних, що часто запитуються (користувачі, інвентар)
- **Application-level caching** с TTL
- **Cache invalidation** через події

---

## 7. Моніторинг та спостережливість

### Метрики (Prometheus)

**Business Metrics:**

- Кількість замовлень на хвилину
- Середня вартість замовлення
- Conversion rate (реєстрація → замовлення)
- Час приготування страв

**Technical Metrics:**

- Request rate, latency, error rate (RED metrics)
- CPU, Memory, Disk utilization (USE metrics)
- Database connection pools
- Message queue depth

### Трасування (Jaeger)

- End-to-end трасування кожного замовлення
- Кореляційні ID у всіх сервісах
- Розподілений контекст через HTTP headers

### Логування (ELK)

- Структуроване логування (JSON)
- Централізований збір через Filebeat
- Кореляція з трасами та метриками

---

## 8. Візуалізація та UI

### Admin Dashboard Features

**System Overview:**

- Топологія сервісів з real-time статусом
- Live traffic visualization між сервісами
- Resource utilization charts
- Error rate и latency heatmaps

**Management Panel:**

- Configuration management
- Service restart/scale controls
- Feature flag toggles
- Load testing launcher

**Analytics:**

- Business KPI dashboards
- Performance trends
- Capacity planning insights
- Cost analysis

### 3D Visualization

- **Interactive Service Map** з анімацією запитів
- **Virtual Kitchen View** з симуляцією обладнання
- **Real-time Data Flow** visualization
- **Network Topology** с health indicators

---

## 9. Deployment и DevOps

### Kubernetes Configuration

```yaml
# Приклад структури
├── k8s/
│   ├── namespaces/
│   ├── services/
│   ├── deployments/
│   ├── configmaps/
│   ├── secrets/
│   ├── ingress/
│   └── monitoring/

```

### CI/CD Pipeline

1. **Code Push** → GitHub webhook trigger
2. **Build Stage** → Docker image build per service
3. **Test Stage** → Unit, integration, contract tests
4. **Security Scan** → Container vulnerability check
5. **Deploy Stage** → Kubernetes rolling update
6. **Verification** → Health checks и smoke tests

### Environment Strategy

- **Local Development** - Docker Compose
- **Testing** - Minikube/Kind
- **Staging** - Managed Kubernetes (GKE/EKS/AKS)
- **Production** - Production cluster с HA setup

---

## 10. Тестування

### Testing Strategy

**Unit Tests:** 80%+ coverage для бізнес-логіки

**Integration Tests:** API contract testing

**End-to-End Tests:** Critical user journeys

**Performance Tests:** Load testing с k6

**Chaos Testing:** Chaos Monkey для resilience

### Load Testing Scenarios

1. **Normal Load:** 100 concurrent users
2. **Peak Load:** 500 concurrent users
3. **Spike Test:** Різкий ріст до 1000 users
4. **Endurance Test:** Тривале навантаження
5. **Capacity Test:** Пошук точки відмови

---

## 11. Безпека

### Security Measures

- **JWT Authentication** с rotation
- **HTTPS/TLS** для всіх зовнішніх з'єднань
- **mTLS** для inter-service communication
- **RBAC** в Kubernetes
- **Secret management** через Kubernetes secrets
- **Network policies** для ізоляції
- **Container security** scanning

---

## 12. План розробки

### Phase 1: Core Services (4-6 тижнів)

- API Gateway + основна маршрутизація
- Auth Service с JWT
- User Service с базовим CRUD
- Order Service с простішими статусами
- Базовий UI для тестування

### Phase 2: Business Logic (4-6 тижнів)

- Inventory Service с резервуванням
- Payment Service (mock implementation)
- Event-driven architecture с RabbitMQ
- Notification Service
- Enhanced Order workflows

### Phase 3: Hardware Integration (3-4 тижні)

- Cooking Service на C++
- Virtual Hardware simulation
- IPC між C++ сервісами
- Real-time cooking visualization

### Phase 4: Platform Features (4-6 тижнів)

- Kubernetes deployment
- Monitoring stack (Prometheus/Grafana/Jaeger)
- Admin Dashboard
- 3D Visualization engine
- Load testing framework

### Phase 5: Polish & Documentation (2-3 тижні)

- Performance optimization
- Comprehensive documentation
- Demo scenarios
- Video tutorials
- Security hardening

---

## 13. Структура репозиторію

```
restaurant-microservices/
├── services/
│   ├── api-gateway/          # Go
│   ├── auth-service/         # Python + FastAPI
│   ├── user-service/         # Node.js + Express
│   ├── order-service/        # Java + Spring Boot
│   ├── inventory-service/    # Rust + Actix Web
│   ├── payment-service/      # Go + gRPC
│   ├── cooking-service/      # C++
│   ├── virtual-hardware/     # C++
│   └── notification-service/ # Python + Celery
├── frontend/
│   ├── admin-dashboard/      # React + TypeScript
│   └── visualization/        # D3.js + Three.js
├── infrastructure/
│   ├── docker/              # Dockerfiles
│   ├── k8s/                 # Kubernetes manifests
│   ├── monitoring/          # Prometheus/Grafana
│   └── ci-cd/               # GitHub Actions
├── tests/
│   ├── unit/
│   ├── integration/
│   ├── e2e/
│   └── performance/
├── docs/
│   ├── api/                 # OpenAPI specs
│   ├── architecture/
│   └── deployment/
└── scripts/
    ├── setup/
    ├── load-test/
    └── monitoring/

```

---

## 14. Критерії успіху проекту

### Технічні критерії

- Всі сервіси успішно деплояться в Kubernetes
- Система витримує навантажувальні тести
- Моніторинг показує метрики в реальному часі
- Візуалізація коректно відображає взаємодії

### Демонстраційні критерії

- Зрозумілість архітектури для новачків
- Вражаюча візуалізація для технічних інтерв'ю
- Можливість live-демонстрації всіх компонентів
- Якісна документація для open-source

### Business Value

- Демонстрація знання сучасних технологій
- Показ навичок системного дизайну
- Досвід роботи з різними мовами програмування
- Розуміння мікросервісної архітектури на практиці

---

Цей проект стане відмінним showcase ваших навичок в області сучасної розробки і покаже глибоке розуміння розподілених систем, що особливо цінується при наймі на senior позиції.
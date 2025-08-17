# Deal Service

Сервис для управления финансовыми сделками (договорами, суммами, контрагентами и их ролями), с поддержкой фильтров, сортировки, пагинации и экспорта в Excel и интеграцией с сервисом контрагентов через RabbitMQ.

---

## 📆 Функциональность

* ✅ CRUD для сущности `Сделка`
* ✅ Управление контрагентами и их ролями
* ✅ Поиск сделок с фильтрацией и пагинацией
* ✅ Изменение статуса сделки
* ✅ Экспорт в Excel (с цветом и группировкой)
* ✅ Логическое удаление `is_active`
* ✅ Liquibase для миграций
* ✅ Интеграция с `Contractor Service` через RabbitMQ
* ✅ Поддержка паттернов **Inbox/Outbox** для надёжной доставки сообщений
* ✅ Dead-letter и retry механизмы для обработки ошибок при получении событий

---

## 🚀 Технологии

| Стек              | Использовано     |
| ----------------- | ---------------- |
| Java              | 17+              |
| Spring Boot       | 3.x              |
| Spring Data JPA   | PostgreSQL       |
| Hibernate         | 6.x              |
| Liquibase         | Миграции базы    |
| Apache POI        | Экспорт в Excel  |
| Swagger (OpenAPI) | Документация API |
| Lombok            | Упрощение кода   |
| Maven             | Сборка проекта   |
| RabbitMQ          |Асинхронные события|

---

## 📡 Интеграция с Contractor Service

### Поток сообщений
1. **Contractor Service** публикует событие об изменении контрагента в `contractors_contractor_exchange`.
2. Сообщение маршрутизируется в очередь `deals_contractor_queue`.
3. `Deal Service` получает сообщение, обрабатывает его и обновляет данные контрагента в сделках.
4. В случае ошибки:
    - сообщение уходит в `deals_dead_contractor_queue`.
    - через **5 минут** автоматически возвращается в `deals_contractor_queue` (retry-политика).
5. Используется **Inbox таблица**, чтобы не обрабатывать повторные сообщения.

---

## 📅 API Эндпоинты

### Сделки

| Method | URI                   | Description               |
| ------ | --------------------- | ------------------------- |
| PUT    | `/deal/save`          | Создать / обновить сделку |
| PATCH  | `/deal/change/status` | Изменить статус сделки    |
| GET    | `/deal/deal/{id}`     | Получить сделку по ID     |
| POST   | `/deal/search`        | Поиск с фильтрацией       |
| POST   | `/deal/search/export` | 📄 Экспорт в Excel        |

### Контрагенты

| Method | URI                            | Description            |
| ------ | ------------------------------ | ---------------------- |
| PUT    | `/deal-contractor/save`        | Сохранение контрагента |
| DELETE | `/deal-contractor/delete/{id}` | Логическое удаление    |

### Роли контрагентов

| Method | URI                          | Description               |
| ------ | ---------------------------- |---------------------------|
| POST   | `/contractor-to-role/add`    | Добавить роль контрагенту |
| DELETE | `/contractor-to-role/delete` | Логическое удаление       |

---

## 📄 Пример запроса на экспорт Excel

```json
{
  "page": 0,
  "size": 20,
  "sortBy": "agreementDate",
  "sortDirection": "DESC",
  "description": "кредит"
}
```

Результат: Excel-файл `.xlsx` с сделками.
![img.png](img.png)
---

## 📁 Liquibase

Миграции хранятся в:

```
src/main/resources/db/changelog/
```

---

## ⚠️ Обработка ошибок

```json
{
  "message": "Deal with id '...' not found",
  "timestamp": "2025-07-14T14:00:00"
}
```
---

---
## 📦 Inbox / Outbox паттерн

Для обеспечения надёжной доставки событий:

### Outbox: 
Contractor Service сохраняет события в таблицу outbox_event, а отдельный шедулер публикует их в RabbitMQ.

### Inbox:
Deal Service сохраняет идентификатор входящего сообщения в таблицу inbox_event, чтобы избежать повторной обработки одного и того же события.

---

## 💡 Старт локально с Docker

1. Создать `.env` в корне проекта:

```env
DB_NAME=deal-service-db
DB_USER=user
DB_PASSWORD=1
```
2. Старт:
```bash
docker compose up --build
```

---

## 👤 Автор

**Краковцев Артём**

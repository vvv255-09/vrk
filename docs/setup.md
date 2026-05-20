# ⚙️ Инструкция по установке и настройке

Данное руководство поможет вам развернуть систему Warehouse Management System с нуля.

## 1. Системные требования

- **Java Development Kit (JDK) 21**: Приложение использует возможности современной Java 21.
- **Apache Maven**: Для сборки и запуска проекта.
- **MySQL Server**: Рекомендуется версия 8.0 или выше.

## 2. Настройка базы данных

Приложение подключается к базе данных MySQL. Вам необходимо создать базу данных и соответствующие таблицы.

### Параметры подключения (по умолчанию):
- **Хост:** `localhost:3306`
- **Имя БД:** `warehouse_system`
- **Пользователь:** `root`
- **Пароль:** `19120355` (можно изменить в `Database.java`)

### Создание структуры БД
Поскольку в проекте отсутствуют `.sql` файлы, вам необходимо создать следующие таблицы:

1. **`users`**: (id, full_name, login, password, role)
2. **`products`**: (id, name, category, unit, quantity, min_quantity, price)
3. **`suppliers`**: (id, name, phone, address)
4. **`receipts`**: (id, product_id, supplier_id, quantity, receipt_date)
5. **`expenses`**: (id, product_id, quantity, expense_date, reason)
6. **`orders`**: (id, user_id, total_price, status, order_date)
7. **`order_items`**: (id, order_id, product_id, quantity, price)

## 3. Сборка и запуск

### С помощью Maven (IDE или терминал)
Для запуска приложения выполните команду в корне проекта:
```bash
mvn javafx:run
```

Для сборки исполняемого пакета:
```bash
mvn package
```

## 4. Устранение неполадок

- **Ошибка подключения к БД**: Убедитесь, что MySQL Server запущен и данные для входа в `Database.java` совпадают с вашими настройками.
- **Ошибка JavaFX**: Убедитесь, что установлена JDK 21. Если вы используете стороннюю IDE, проверьте настройки модулей JavaFX.

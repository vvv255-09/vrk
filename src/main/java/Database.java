import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class Database {
    private final String url;
    private final String user;
    private final String password;
    private Connection connection;

    private static String DB_HOST = "localhost";

    public Database(String url, String user, String password) {
        this.url = "jdbc:mysql://" + DB_HOST + ":3306/warehouse_system";
        this.user = "tsar";
        this.password = "tsar123";
    }

    public static void setHost(String host) {
        DB_HOST = host;
    }

    public void connect() {
        try {
            connection = DriverManager.getConnection(this.url, this.user, this.password);
            System.out.println("Подключение к БД успешно.");
        } catch (SQLException e) {
            System.out.println("Ошибка подключения к БД: " + e.getMessage());
        }
    }

    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Соединение закрыто.");
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при закрытии соединения: " + e.getMessage());
        }
    }

    public User loginUser(String login, String password) {
        String sql = "SELECT * FROM users WHERE login = ? AND password = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, login);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getString("login"),
                        rs.getString("password"),
                        rs.getString("role")
                );
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при авторизации: " + e.getMessage());
        }

        return null;
    }

    public void addProduct(Product product) {
        String sql = "INSERT INTO products (name, category, unit, quantity, min_quantity, price) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, product.getName());
            stmt.setString(2, product.getCategory());
            stmt.setString(3, product.getUnit());
            stmt.setInt(4, product.getQuantity());
            stmt.setInt(5, product.getMinQuantity());
            stmt.setDouble(6, product.getPrice());

            stmt.executeUpdate();
            System.out.println("Товар успешно добавлен.");
        } catch (SQLException e) {
            System.out.println("Ошибка при добавлении товара: " + e.getMessage());
        }
    }

    public void addUser(User userObj) {
        String sql = "INSERT INTO users (full_name, login, password, role) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, userObj.getFullName());
            stmt.setString(2, userObj.getLogin());
            stmt.setString(3, userObj.getPassword());
            stmt.setString(4, userObj.getRole());

            stmt.executeUpdate();
            System.out.println("Пользователь добавлен.");
        } catch (SQLException e) {
            System.out.println("Ошибка при добавлении пользователя: " + e.getMessage());
        }
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                users.add(new User(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getString("login"),
                        rs.getString("password"),
                        rs.getString("role")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Ошибка получения пользователей: " + e.getMessage());
        }
        return users;
    }

    public void deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Ошибка удаления пользователя: " + e.getMessage());
        }
    }

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Product product = new Product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getString("unit"),
                        rs.getInt("quantity"),
                        rs.getInt("min_quantity"),
                        rs.getDouble("price")
                );
                products.add(product);
            }

        } catch (SQLException e) {
            System.out.println("Ошибка при получении товаров: " + e.getMessage());
        }

        return products;
    }

    public void findProductByName(String keyword) {
        String sql = "SELECT * FROM products WHERE name LIKE ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + keyword + "%");
            ResultSet rs = stmt.executeQuery();

            boolean found = false;
            while (rs.next()) {
                found = true;
                Product product = new Product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getString("unit"),
                        rs.getInt("quantity"),
                        rs.getInt("min_quantity"),
                        rs.getDouble("price")
                );
                System.out.println(product);
            }

            if (!found) {
                System.out.println("Товар не найден.");
            }

        } catch (SQLException e) {
            System.out.println("Ошибка поиска товара: " + e.getMessage());
        }
    }

    public void updateProductQuantity(int productId, int newQuantity) {
        String sql = "UPDATE products SET quantity = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, newQuantity);
            stmt.setInt(2, productId);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Количество товара обновлено.");
            } else {
                System.out.println("Товар с таким ID не найден.");
            }
        } catch (SQLException e) {
            System.out.println("Ошибка обновления количества: " + e.getMessage());
        }
    }

    public void deleteProduct(int productId) {
        String sql = "DELETE FROM products WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, productId);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Товар удалён.");
            } else {
                System.out.println("Товар с таким ID не найден.");
            }
        } catch (SQLException e) {
            System.out.println("Ошибка удаления товара: " + e.getMessage());
        }
    }

    public void showLowStockProducts() {
        String sql = "SELECT * FROM products WHERE quantity <= min_quantity";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            boolean found = false;
            while (rs.next()) {
                found = true;
                Product product = new Product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getString("unit"),
                        rs.getInt("quantity"),
                        rs.getInt("min_quantity"),
                        rs.getDouble("price")
                );
                System.out.println(product);
            }

            if (!found) {
                System.out.println("Товаров с минимальным остатком нет.");
            }

        } catch (SQLException e) {
            System.out.println("Ошибка при выводе товаров с низким остатком: " + e.getMessage());
        }
    }
    public void updateProduct(Product product) {
        String sql = "UPDATE products SET name=?, category=?, unit=?, " +
                "min_quantity=?, price=? WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, product.getName());
            stmt.setString(2, product.getCategory());
            stmt.setString(3, product.getUnit());
            stmt.setInt(4, product.getMinQuantity());
            stmt.setDouble(5, product.getPrice());
            stmt.setInt(6, product.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Ошибка обновления товара: " + e.getMessage());
        }
    }

    public void addReceipt(int productId, int supplierId, int quantity, String date) {
        String insertReceipt = "INSERT INTO receipts (product_id, supplier_id, quantity, receipt_date) VALUES (?, ?, ?, ?)";
        String updateProduct = "UPDATE products SET quantity = quantity + ? WHERE id = ?";

        try {
            connection.setAutoCommit(false);

            try (PreparedStatement stmt1 = connection.prepareStatement(insertReceipt);
                 PreparedStatement stmt2 = connection.prepareStatement(updateProduct)) {

                stmt1.setInt(1, productId);
                stmt1.setInt(2, supplierId);
                stmt1.setInt(3, quantity);
                stmt1.setDate(4, Date.valueOf(date));
                stmt1.executeUpdate();

                stmt2.setInt(1, quantity);
                stmt2.setInt(2, productId);
                stmt2.executeUpdate();

                connection.commit();
                System.out.println("Поступление успешно добавлено.");
            }

        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                System.out.println("Ошибка rollback: " + ex.getMessage());
            }
            System.out.println("Ошибка при добавлении поступления: " + e.getMessage());
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                System.out.println("Ошибка восстановления autoCommit: " + e.getMessage());
            }
        }
    }

    public List<Receipt> getAllReceipts() {
        List<Receipt> list = new ArrayList<>();
        String sql = "SELECT r.id, r.product_id, p.name as product_name, " +
                "r.supplier_id, s.name as supplier_name, " +
                "r.quantity, r.receipt_date " +
                "FROM receipts r " +
                "JOIN products p ON r.product_id = p.id " +
                "JOIN suppliers s ON r.supplier_id = s.id";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Receipt(
                        rs.getInt("id"),
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getInt("supplier_id"),
                        rs.getString("supplier_name"),
                        rs.getInt("quantity"),
                        rs.getString("receipt_date")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Ошибка получения поступлений: " + e.getMessage());
        }
        return list;
    }

    public List<Supplier> getAllSuppliers() {
        List<Supplier> list = new ArrayList<>();
        String sql = "SELECT * FROM suppliers";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Supplier(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getString("address")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Ошибка получения поставщиков: " + e.getMessage());
        }
        return list;
    }

    public List<Expense> getAllExpenses() {
        List<Expense> list = new ArrayList<>();
        String sql = "SELECT e.id, e.product_id, p.name as product_name, " +
                "e.quantity, e.expense_date, e.reason " +
                "FROM expenses e " +
                "JOIN products p ON e.product_id = p.id";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Expense(
                        rs.getInt("id"),
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getInt("quantity"),
                        rs.getString("expense_date"),
                        rs.getString("reason")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Ошибка получения расходов: " + e.getMessage());
        }
        return list;
    }

    public List<String> getOperationsHistory() {
        List<String> history = new ArrayList<>();

        String receiptSql = "SELECT r.receipt_date as date, p.name, r.quantity, s.name as supplier " +
                "FROM receipts r " +
                "JOIN products p ON r.product_id = p.id " +
                "JOIN suppliers s ON r.supplier_id = s.id " +
                "ORDER BY r.receipt_date DESC LIMIT 50";

        String expenseSql = "SELECT e.expense_date as date, p.name, e.quantity, e.reason " +
                "FROM expenses e " +
                "JOIN products p ON e.product_id = p.id " +
                "ORDER BY e.expense_date DESC LIMIT 50";

        String orderSql = "SELECT o.order_date as date, u.full_name, o.total_price, " +
                "o.status, o.id as order_id " +
                "FROM orders o " +
                "JOIN users u ON o.user_id = u.id " +
                "ORDER BY o.order_date DESC LIMIT 50";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(receiptSql)) {
            while (rs.next()) {
                history.add("📥 " + rs.getString("date") +
                        " | Поступление: +" + rs.getInt("quantity") +
                        " шт — " + rs.getString("name") +
                        " от " + rs.getString("supplier"));
            }
        } catch (SQLException e) {
            System.out.println("Ошибка истории поступлений: " + e.getMessage());
        }

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(expenseSql)) {
            while (rs.next()) {
                history.add("📤 " + rs.getString("date") +
                        " | Расход: -" + rs.getInt("quantity") +
                        " шт — " + rs.getString("name") +
                        " (" + rs.getString("reason") + ")");
            }
        } catch (SQLException e) {
            System.out.println("Ошибка истории расходов: " + e.getMessage());
        }

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(orderSql)) {
            while (rs.next()) {
                history.add("🛒 " + rs.getString("date") +
                        " | Заказ #" + rs.getInt("order_id") +
                        " — " + rs.getString("full_name") +
                        " | " + String.format("%.2f сом", rs.getDouble("total_price")) +
                        " | " + rs.getString("status"));
            }
        } catch (SQLException e) {
            System.out.println("Ошибка истории заказов: " + e.getMessage());
        }

        Collections.sort(history, Collections.reverseOrder());
        return history;
    }

    public List<String> getProductHistory(int productId) {
        List<String> history = new ArrayList<>();

        String receiptSql = "SELECT r.receipt_date, r.quantity, s.name as supplier " +
                "FROM receipts r " +
                "JOIN suppliers s ON r.supplier_id = s.id " +
                "WHERE r.product_id = ? " +
                "ORDER BY r.receipt_date DESC";

        String expenseSql = "SELECT e.expense_date, e.quantity, e.reason " +
                "FROM expenses e " +
                "WHERE e.product_id = ? " +
                "ORDER BY e.expense_date DESC";

        try (PreparedStatement stmt = connection.prepareStatement(receiptSql)) {
            stmt.setInt(1, productId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                history.add("📥 " + rs.getString("receipt_date") +
                        " | Поступление: +" + rs.getInt("quantity") +
                        " шт от " + rs.getString("supplier"));
            }
        } catch (SQLException e) {
            System.out.println("Ошибка истории поступлений: " + e.getMessage());
        }

        try (PreparedStatement stmt = connection.prepareStatement(expenseSql)) {
            stmt.setInt(1, productId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                history.add("📤 " + rs.getString("expense_date") +
                        " | Расход: -" + rs.getInt("quantity") +
                        " шт (" + rs.getString("reason") + ")");
            }
        } catch (SQLException e) {
            System.out.println("Ошибка истории расходов: " + e.getMessage());
        }

        Collections.sort(history, Collections.reverseOrder());
        return history;
    }

    public void addExpense(int productId, int quantity, String date, String reason) {
        String checkSql = "SELECT quantity FROM products WHERE id = ?";
        String insertExpense = "INSERT INTO expenses (product_id, quantity, expense_date, reason) VALUES (?, ?, ?, ?)";
        String updateProduct = "UPDATE products SET quantity = quantity - ? WHERE id = ?";

        try {
            connection.setAutoCommit(false);

            int currentQuantity = 0;
            try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
                checkStmt.setInt(1, productId);
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    currentQuantity = rs.getInt("quantity");
                } else {
                    System.out.println("Товар не найден.");
                    connection.setAutoCommit(true);
                    return;
                }
            }

            if (currentQuantity < quantity) {
                System.out.println("Недостаточно товара на складе.");
                connection.setAutoCommit(true);
                return;
            }

            try (PreparedStatement stmt1 = connection.prepareStatement(insertExpense);
                 PreparedStatement stmt2 = connection.prepareStatement(updateProduct)) {

                stmt1.setInt(1, productId);
                stmt1.setInt(2, quantity);
                stmt1.setDate(3, Date.valueOf(date));
                stmt1.setString(4, reason);
                stmt1.executeUpdate();

                stmt2.setInt(1, quantity);
                stmt2.setInt(2, productId);
                stmt2.executeUpdate();

                connection.commit();
                System.out.println("Расход успешно добавлен.");
            }

        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                System.out.println("Ошибка rollback: " + ex.getMessage());
            }
            System.out.println("Ошибка при добавлении расхода: " + e.getMessage());
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                System.out.println("Ошибка восстановления autoCommit: " + e.getMessage());
            }
        }
    }
    public void addWriteOff(int productId, int quantity, String reason, String date) {
        String checkSql = "SELECT quantity FROM products WHERE id = ?";
        String insertSql = "INSERT INTO expenses (product_id, quantity, expense_date, reason) " +
                "VALUES (?, ?, ?, ?)";
        String updateSql = "UPDATE products SET quantity = quantity - ? WHERE id = ?";

        try {
            connection.setAutoCommit(false);

            int current = 0;
            try (PreparedStatement stmt = connection.prepareStatement(checkSql)) {
                stmt.setInt(1, productId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) current = rs.getInt("quantity");
            }

            if (current < quantity) {
                System.out.println("Недостаточно товара для списания.");
                connection.setAutoCommit(true);
                return;
            }

            try (PreparedStatement s1 = connection.prepareStatement(insertSql);
                 PreparedStatement s2 = connection.prepareStatement(updateSql)) {

                s1.setInt(1, productId);
                s1.setInt(2, quantity);
                s1.setDate(3, java.sql.Date.valueOf(date));
                s1.setString(4, "Списание: " + reason);
                s1.executeUpdate();

                s2.setInt(1, quantity);
                s2.setInt(2, productId);
                s2.executeUpdate();

                connection.commit();
            }
        } catch (SQLException e) {
            try { connection.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            System.out.println("Ошибка списания: " + e.getMessage());
        } finally {
            try { connection.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
    // Создать заказ
    public int createOrder(int userId, double totalPrice) {
        String sql = "INSERT INTO orders (user_id, total_price) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql,
                java.sql.Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, userId);
            stmt.setDouble(2, totalPrice);
            stmt.executeUpdate();
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        } catch (SQLException e) {
            System.out.println("Ошибка создания заказа: " + e.getMessage());
        }
        return -1;
    }

    // Добавить позицию в заказ
    public void addOrderItem(int orderId, int productId, int quantity, double price) {
        String sql = "INSERT INTO order_items (order_id, product_id, quantity, price) " +
                "VALUES (?, ?, ?, ?)";
        String updateStock = "UPDATE products SET quantity = quantity - ? WHERE id = ?";
        try {
            connection.setAutoCommit(false);
            try (PreparedStatement s1 = connection.prepareStatement(sql);
                 PreparedStatement s2 = connection.prepareStatement(updateStock)) {
                s1.setInt(1, orderId);
                s1.setInt(2, productId);
                s1.setInt(3, quantity);
                s1.setDouble(4, price);
                s1.executeUpdate();

                s2.setInt(1, quantity);
                s2.setInt(2, productId);
                s2.executeUpdate();

                connection.commit();
            }
        } catch (SQLException e) {
            try { connection.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            System.out.println("Ошибка добавления позиции: " + e.getMessage());
        } finally {
            try { connection.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    // Получить заказы пользователя
    public List<Order> getUserOrders(int userId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE user_id = ? ORDER BY order_date DESC";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                orders.add(new Order(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("status"),
                        rs.getDouble("total_price"),
                        rs.getString("order_date")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Ошибка получения заказов: " + e.getMessage());
        }
        return orders;
    }

    // Получить позиции заказа
    public List<OrderItem> getOrderItems(int orderId) {
        List<OrderItem> items = new ArrayList<>();
        String sql = "SELECT oi.*, p.name as product_name FROM order_items oi " +
                "JOIN products p ON oi.product_id = p.id " +
                "WHERE oi.order_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                items.add(new OrderItem(
                        rs.getInt("id"),
                        rs.getInt("order_id"),
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getInt("quantity"),
                        rs.getDouble("price")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Ошибка получения позиций: " + e.getMessage());
        }
        return items;
    }

    // Обновить статус заказа (для админа)
    public void updateOrderStatus(int orderId, String status) {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, orderId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Ошибка обновления статуса: " + e.getMessage());
        }
    }

    // Получить все заказы (для админа)
    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders ORDER BY order_date DESC";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                orders.add(new Order(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("status"),
                        rs.getDouble("total_price"),
                        rs.getString("order_date")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Ошибка получения всех заказов: " + e.getMessage());
        }
        return orders;
    }
    public String getUserById(int userId) {
        String sql = "SELECT full_name FROM users WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getString("full_name");
        } catch (SQLException e) {
            System.out.println("Ошибка получения пользователя: " + e.getMessage());
        }
        return "Неизвестно";
    }
}
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private final String url;
    private final String user;
    private final String password;
    private Connection connection;

    public Database(String url, String user, String password) {
        this.url = "jdbc:mysql://localhost:3306/warehouse_system";
        this.user = "root";
        this.password = "19120355";
    }

    public void connect() {
        try {
            connection = DriverManager.getConnection(url, user, password);
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
}
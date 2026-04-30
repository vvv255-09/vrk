import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        Database db = new Database(
                "jdbc:mysql://localhost:3306/warehouse_system",
                "root",
                "root"
        );

        db.connect();

        System.out.println("=== АВТОРИЗАЦИЯ ===");
        System.out.print("Введите логин: ");
        String login = scanner.nextLine();

        System.out.print("Введите пароль: ");
        String password = scanner.nextLine();

        User currentUser = db.loginUser(login, password);

        if (currentUser == null) {
            System.out.println("Неверный логин или пароль.");
            db.disconnect();
            return;
        }

        System.out.println("Добро пожаловать, " + currentUser.getFullName() + "!");
        System.out.println("Ваша роль: " + currentUser.getRole());

        switch (currentUser.getRole()) {
            case "admin":
                adminMenu(scanner, db);
                break;
            case "employee":
                employeeMenu(scanner, db);
                break;
            case "client":
                clientMenu(scanner, db);
                break;
            default:
                System.out.println("Неизвестная роль.");
        }

        db.disconnect();
    }

    public static void adminMenu(Scanner scanner, Database db) {
        while (true) {
            System.out.println("\n=== МЕНЮ АДМИНА ===");
            System.out.println("1. Показать все товары");
            System.out.println("2. Добавить товар");
            System.out.println("3. Найти товар по названию");
            System.out.println("4. Обновить количество товара");
            System.out.println("5. Удалить товар");
            System.out.println("6. Добавить поступление");
            System.out.println("7. Добавить расход");
            System.out.println("8. Показать товары с низким остатком");
            System.out.println("9. Добавить пользователя");
            System.out.println("0. Выход");
            System.out.print("Выберите пункт: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    showAllProducts(db);
                    break;
                case 2:
                    addProduct(scanner, db);
                    break;
                case 3:
                    findProduct(scanner, db);
                    break;
                case 4:
                    updateQuantity(scanner, db);
                    break;
                case 5:
                    deleteProduct(scanner, db);
                    break;
                case 6:
                    addReceipt(scanner, db);
                    break;
                case 7:
                    addExpense(scanner, db);
                    break;
                case 8:
                    db.showLowStockProducts();
                    break;
                case 9:
                    addUser(scanner, db);
                    break;
                case 0:
                    System.out.println("Выход.");
                    return;
                default:
                    System.out.println("Неверный пункт меню.");
            }
        }
    }

    public static void employeeMenu(Scanner scanner, Database db) {
        while (true) {
            System.out.println("\n=== МЕНЮ СОТРУДНИКА ===");
            System.out.println("1. Показать все товары");
            System.out.println("2. Найти товар по названию");
            System.out.println("3. Добавить поступление");
            System.out.println("4. Добавить расход");
            System.out.println("5. Показать товары с низким остатком");
            System.out.println("0. Выход");
            System.out.print("Выберите пункт: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    showAllProducts(db);
                    break;
                case 2:
                    findProduct(scanner, db);
                    break;
                case 3:
                    addReceipt(scanner, db);
                    break;
                case 4:
                    addExpense(scanner, db);
                    break;
                case 5:
                    db.showLowStockProducts();
                    break;
                case 0:
                    System.out.println("Выход.");
                    return;
                default:
                    System.out.println("Неверный пункт меню.");
            }
        }
    }

    public static void clientMenu(Scanner scanner, Database db) {
        while (true) {
            System.out.println("\n=== МЕНЮ КЛИЕНТА ===");
            System.out.println("1. Показать все товары");
            System.out.println("2. Найти товар по названию");
            System.out.println("0. Выход");
            System.out.print("Выберите пункт: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    showAllProducts(db);
                    break;
                case 2:
                    findProduct(scanner, db);
                    break;
                case 0:
                    System.out.println("Выход.");
                    return;
                default:
                    System.out.println("Неверный пункт меню.");
            }
        }
    }

    public static void showAllProducts(Database db) {
        List<Product> products = db.getAllProducts();
        for (Product product : products) {
            System.out.println(product);
        }
    }

    public static void addProduct(Scanner scanner, Database db) {
        System.out.print("Название: ");
        String name = scanner.nextLine();

        System.out.print("Категория: ");
        String category = scanner.nextLine();

        System.out.print("Единица измерения: ");
        String unit = scanner.nextLine();

        System.out.print("Количество: ");
        int quantity = scanner.nextInt();

        System.out.print("Минимальный остаток: ");
        int minQuantity = scanner.nextInt();

        System.out.print("Цена: ");
        double price = scanner.nextDouble();
        scanner.nextLine();

        Product product = new Product(name, category, unit, quantity, minQuantity, price);
        db.addProduct(product);
    }

    public static void findProduct(Scanner scanner, Database db) {
        System.out.print("Введите название товара: ");
        String keyword = scanner.nextLine();
        db.findProductByName(keyword);
    }

    public static void updateQuantity(Scanner scanner, Database db) {
        System.out.print("Введите ID товара: ");
        int productId = scanner.nextInt();

        System.out.print("Введите новое количество: ");
        int newQuantity = scanner.nextInt();
        scanner.nextLine();

        db.updateProductQuantity(productId, newQuantity);
    }

    public static void deleteProduct(Scanner scanner, Database db) {
        System.out.print("Введите ID товара для удаления: ");
        int deleteId = scanner.nextInt();
        scanner.nextLine();

        db.deleteProduct(deleteId);
    }

    public static void addReceipt(Scanner scanner, Database db) {
        System.out.print("Введите ID товара: ");
        int receiptProductId = scanner.nextInt();

        System.out.print("Введите ID поставщика: ");
        int supplierId = scanner.nextInt();

        System.out.print("Введите количество: ");
        int receiptQuantity = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Введите дату (YYYY-MM-DD): ");
        String receiptDate = scanner.nextLine();

        db.addReceipt(receiptProductId, supplierId, receiptQuantity, receiptDate);
    }

    public static void addExpense(Scanner scanner, Database db) {
        System.out.print("Введите ID товара: ");
        int expenseProductId = scanner.nextInt();

        System.out.print("Введите количество: ");
        int expenseQuantity = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Введите дату (YYYY-MM-DD): ");
        String expenseDate = scanner.nextLine();

        System.out.print("Введите причину расхода: ");
        String reason = scanner.nextLine();

        db.addExpense(expenseProductId, expenseQuantity, expenseDate, reason);
    }

    public static void addUser(Scanner scanner, Database db) {
        System.out.print("Введите ФИО: ");
        String fullName = scanner.nextLine();

        System.out.print("Введите логин: ");
        String login = scanner.nextLine();

        System.out.print("Введите пароль: ");
        String password = scanner.nextLine();

        System.out.print("Введите роль (admin / employee / client): ");
        String role = scanner.nextLine();

        User user = new User(fullName, login, password, role);
        db.addUser(user);
    }
}
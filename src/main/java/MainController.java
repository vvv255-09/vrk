import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.List;

public class MainController {

    @FXML private Button btnReceipts;
    @FXML private Button btnExpenses;
    @FXML private Button btnLowStock;
    @FXML private Label userNameLabel;
    @FXML private Label userRoleLabel;
    @FXML private Button btnUsers;
    @FXML private BorderPane rootPane;
    @FXML private Button btnReport;
    @FXML private Button btnHistory;
    @FXML private Button btnWriteOff;
    @FXML private Button btnCart;
    @FXML private Button btnOrders;
    @FXML private Button btnAdminOrders;

    private Database db;
    private User currentUser;

    public void setUser(User user) {
        this.currentUser = user;
        userNameLabel.setText(user.getFullName());
        userRoleLabel.setText(user.getRole());

        switch (user.getRole()) {
            case "client" -> {
                btnReceipts.setVisible(false);   btnReceipts.setManaged(false);
                btnExpenses.setVisible(false);   btnExpenses.setManaged(false);
                btnLowStock.setVisible(false);   btnLowStock.setManaged(false);
                btnUsers.setVisible(false);      btnUsers.setManaged(false);
                btnReport.setVisible(false);     btnReport.setManaged(false);
                btnHistory.setVisible(false);    btnHistory.setManaged(false);
                btnWriteOff.setVisible(false);   btnWriteOff.setManaged(false);
                btnAdminOrders.setVisible(false); btnAdminOrders.setManaged(false);
            }
            case "employee" -> {
                // Сотрудник не видит пользователей и отчёт
                btnUsers.setVisible(false);      btnUsers.setManaged(false);
                btnReport.setVisible(false);     btnReport.setManaged(false);
                // Корзина и заказы только для клиента
                btnCart.setVisible(false);       btnCart.setManaged(false);
                btnOrders.setVisible(false);     btnOrders.setManaged(false);
            }
            case "admin" -> {
                // Корзина и заказы только для клиента
                btnCart.setVisible(false);       btnCart.setManaged(false);
                btnOrders.setVisible(false);     btnOrders.setManaged(false);
            }
        }

        db = new Database("", "", "");
        db.connect();
        showProducts();
        if (user.getRole().equals("admin") || user.getRole().equals("employee")) {
            checkLowStock();
        }
    }

    private void checkLowStock() {
        List<Product> lowStock = db.getAllProducts().stream()
                .filter(p -> p.getQuantity() <= p.getMinQuantity())
                .toList();

        if (lowStock.isEmpty()) return;

        StringBuilder sb = new StringBuilder();
        sb.append("⚠️ Товары с низким остатком:\n\n");
        for (Product p : lowStock) {
            sb.append("• ").append(p.getName())
                    .append(" — ").append(p.getQuantity())
                    .append(" ").append(p.getUnit())
                    .append(" (мин: ").append(p.getMinQuantity()).append(")\n");
        }

        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Низкий остаток");
        alert.setHeaderText("Требуется пополнение склада!");
        alert.setContentText(sb.toString());
        alert.showAndWait();
    }


    @FXML
    private void showProducts() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/main_products.fxml"));
            VBox panel = loader.load();

            ProductsPanelController controller = loader.getController();
            controller.setDatabase(db);
            controller.setUser(currentUser);

            rootPane.setCenter(panel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showReceipts() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/receipts_panel.fxml"));
            VBox panel = loader.load();

            ReceiptsPanelController controller = loader.getController();
            controller.setDatabase(db);

            rootPane.setCenter(panel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showExpenses() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/expenses_panel.fxml"));
            VBox panel = loader.load();

            ExpensesPanelController controller = loader.getController();
            controller.setDatabase(db);

            rootPane.setCenter(panel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showLowStock() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/lowstock_panel.fxml"));
            VBox panel = loader.load();

            LowStockPanelController controller = loader.getController();
            controller.setDatabase(db);

            rootPane.setCenter(panel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showUsers() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/users_panel.fxml"));
            VBox panel = loader.load();

            UsersPanelController controller = loader.getController();
            controller.setDatabase(db);

            rootPane.setCenter(panel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showReport() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/report_panel.fxml"));
            VBox panel = loader.load();
            ReportPanelController controller = loader.getController();
            controller.setDatabase(db);
            rootPane.setCenter(panel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showHistory() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/history_panel.fxml"));
            VBox panel = loader.load();
            HistoryPanelController controller = loader.getController();
            controller.setDatabase(db);
            rootPane.setCenter(panel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void showWriteOff() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/writeoff_dialog.fxml"));
            Stage dialog = new Stage();
            dialog.setScene(new Scene(loader.load()));
            dialog.setTitle("Списание / Брак");

            WriteOffDialogController controller = loader.getController();
            controller.setDatabase(db);

            dialog.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void showCart() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/cart_panel.fxml"));
            VBox panel = loader.load();
            CartPanelController controller = loader.getController();
            controller.setData(db, currentUser);
            rootPane.setCenter(panel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showOrders() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/orders_panel.fxml"));
            VBox panel = loader.load();
            OrdersPanelController controller = loader.getController();
            controller.setData(db, currentUser);
            rootPane.setCenter(panel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void showAdminOrders() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/admin_orders_panel.fxml"));
            VBox panel = loader.load();
            AdminOrdersPanelController controller = loader.getController();
            controller.setDatabase(db);
            rootPane.setCenter(panel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void logout() {
        try {
            db.disconnect();
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/login.fxml"));
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Авторизация");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
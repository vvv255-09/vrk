import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainController {

    @FXML private Label userNameLabel;
    @FXML private Label userRoleLabel;
    @FXML private Button btnUsers;
    @FXML private BorderPane rootPane;

    private Database db;
    private User currentUser;

    public void setUser(User user) {
        this.currentUser = user;
        userNameLabel.setText(user.getFullName());
        userRoleLabel.setText(user.getRole());

        if (!user.getRole().equals("admin")) {
            btnUsers.setVisible(false);
            btnUsers.setManaged(false);
        }

        db = new Database("", "", "");
        db.connect();

        // Сразу открываем товары
        showProducts();
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
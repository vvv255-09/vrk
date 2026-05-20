import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField loginField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private TextField hostField;

    private Database db;

    public void initialize() {
        db = new Database(
                "jdbc:mysql://localhost:3306/warehouse_system",
                "root",
                "19120355"
        );
        db.connect();
    }

    @FXML
    private void handleLogin() {
        String host = hostField.getText().trim();
        if (!host.isEmpty()) {
            Database.setHost(host);
        }

        String login = loginField.getText().trim();
        String password = passwordField.getText().trim();

        if (login.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Заполните все поля.");
            return;
        }

        db = new Database("", "", "");
        db.connect();

        User user = db.loginUser(login, password);

        if (user == null) {
            errorLabel.setText("Неверный логин или пароль.");
            return;
        }

        // Открываем главное окно
        openMainWindow(user);
    }
    @FXML
    private void openRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/register.fxml"));
            Stage stage = (Stage) loginField.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Регистрация");
            stage.show();
        } catch (Exception e) {
            errorLabel.setText("Ошибка открытия регистрации.");
            e.printStackTrace();
        }
    }
    private void openMainWindow(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/main.fxml")
            );
            Stage stage = (Stage) loginField.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));

            // Передаём пользователя в главный контроллер
            MainController controller = loader.getController();
            controller.setUser(user);

            stage.setTitle("Склад — " + user.getFullName());
            stage.show();
        } catch (Exception e) {
            errorLabel.setText("Ошибка загрузки окна.");
            e.printStackTrace();
        }
    }

}
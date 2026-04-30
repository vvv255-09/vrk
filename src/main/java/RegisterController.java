import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class RegisterController {

    @FXML private TextField fullNameField;
    @FXML private TextField loginField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label errorLabel;
    @FXML private Label successLabel;

    private Database db;

    public void initialize() {
        db = new Database("jdbc:mysql://localhost:3306/warehouse_system", "root", "19120355");
        db.connect();
    }

    @FXML
    private void handleRegister() {
        errorLabel.setText("");
        successLabel.setText("");

        String fullName = fullNameField.getText().trim();
        String login    = loginField.getText().trim();
        String password = passwordField.getText().trim();
        String confirm  = confirmPasswordField.getText().trim();

        // Проверки
        if (fullName.isEmpty() || login.isEmpty() ||
                password.isEmpty() || confirm.isEmpty()) {
            errorLabel.setText("Заполните все поля.");
            return;
        }

        if (login.length() < 3) {
            errorLabel.setText("Логин должен быть не менее 3 символов.");
            return;
        }

        if (password.length() < 4) {
            errorLabel.setText("Пароль должен быть не менее 4 символов.");
            return;
        }

        if (!password.equals(confirm)) {
            errorLabel.setText("Пароли не совпадают.");
            return;
        }

        // Регистрация — роль client по умолчанию
        User newUser = new User(fullName, login, password, "client");
        db.addUser(newUser);

        successLabel.setText("Аккаунт создан! Можете войти.");

        // Очищаем поля
        fullNameField.clear();
        loginField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
    }

    @FXML
    private void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/login.fxml"));
            Stage stage = (Stage) loginField.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Авторизация");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
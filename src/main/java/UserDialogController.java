import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class UserDialogController {

    @FXML private TextField fullNameField;
    @FXML private TextField loginField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> roleBox;
    @FXML private Label errorLabel;

    private Database db;

    public void initialize() {
        roleBox.setItems(FXCollections.observableArrayList(
                "admin", "employee", "client"
        ));
        roleBox.setValue("employee");
    }

    public void setDatabase(Database db) {
        this.db = db;
    }

    @FXML
    private void handleSave() {
        String fullName = fullNameField.getText().trim();
        String login    = loginField.getText().trim();
        String password = passwordField.getText().trim();
        String role     = roleBox.getValue();

        if (fullName.isEmpty() || login.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Заполните все поля.");
            return;
        }
        if (login.length() < 3) {
            errorLabel.setText("Логин не менее 3 символов.");
            return;
        }
        if (password.length() < 4) {
            errorLabel.setText("Пароль не менее 4 символов.");
            return;
        }

        db.addUser(new User(fullName, login, password, role));
        closeDialog();
    }

    @FXML
    private void handleCancel() { closeDialog(); }

    private void closeDialog() {
        ((Stage) loginField.getScene().getWindow()).close();
    }
}
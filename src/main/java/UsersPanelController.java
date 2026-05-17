import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class UsersPanelController {

    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, String> colId;
    @FXML private TableColumn<User, String> colFullName;
    @FXML private TableColumn<User, String> colLogin;
    @FXML private TableColumn<User, String> colRole;
    @FXML private TableColumn<User, String> colActions;

    private Database db;

    public void setDatabase(Database db) {
        this.db = db;
        loadUsers();
    }

    public void initialize() {
        colId.setCellValueFactory(d ->
                new SimpleStringProperty(String.valueOf(d.getValue().getId())));
        colFullName.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getFullName()));
        colLogin.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getLogin()));
        colRole.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getRole()));

        // Колонка с кнопкой удаления
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button btnDelete = new Button("Удалить");
            {
                btnDelete.setStyle(
                        "-fx-font-size:11px; -fx-background-color:#FCEBEB;" +
                                "-fx-text-fill:#A32D2D; -fx-background-radius:4;");
                btnDelete.setOnAction(e -> {
                    User u = getTableView().getItems().get(getIndex());
                    handleDelete(u);
                });
            }
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnDelete);
            }
        });

        // Роль — цветная метка
        colRole.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String role, boolean empty) {
                super.updateItem(role, empty);
                if (empty || role == null) {
                    setGraphic(null);
                    return;
                }
                Label badge = new Label(role);
                String color = switch (role) {
                    case "admin"    -> "-fx-background-color:#E6F1FB; -fx-text-fill:#185FA5;";
                    case "employee" -> "-fx-background-color:#EAF3DE; -fx-text-fill:#3B6D11;";
                    default         -> "-fx-background-color:#FFF3E0; -fx-text-fill:#854F0B;";
                };
                badge.setStyle(color +
                        "-fx-padding: 2 8; -fx-background-radius: 20; -fx-font-size: 11px;");
                setGraphic(badge);
            }
        });
    }

    private void loadUsers() {
        usersTable.setItems(FXCollections.observableArrayList(db.getAllUsers()));
    }

    @FXML
    private void handleAdd() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/user_dialog.fxml"));
            Stage dialog = new Stage();
            dialog.setScene(new Scene(loader.load()));
            dialog.setTitle("Добавить пользователя");

            UserDialogController controller = loader.getController();
            controller.setDatabase(db);

            dialog.showAndWait();
            loadUsers();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleDelete(User u) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Удалить пользователя «" + u.getLogin() + "»?",
                ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                db.deleteUser(u.getId());
                loadUsers();
            }
        });
    }
}
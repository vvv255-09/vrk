import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.List;

public class MainController {

    // --- Левое меню ---
    @FXML private Label userNameLabel;
    @FXML private Label userRoleLabel;
    @FXML private Button btnUsers; // скрываем для не-админов

    // --- Поиск и таблица ---
    @FXML private TextField searchField;
    @FXML private Button btnAdd;
    @FXML private TableView<Product> productTable;

    @FXML private TableColumn<Product, String> colId;
    @FXML private TableColumn<Product, String> colName;
    @FXML private TableColumn<Product, String> colCategory;
    @FXML private TableColumn<Product, String> colUnit;
    @FXML private TableColumn<Product, String> colQuantity;
    @FXML private TableColumn<Product, String> colMinQuantity;
    @FXML private TableColumn<Product, String> colPrice;
    @FXML private TableColumn<Product, String> colActions;

    private Database db;
    private User currentUser;

    // Вызывается из LoginController после входа
    public void setUser(User user) {
        this.currentUser = user;
        userNameLabel.setText(user.getFullName());
        userRoleLabel.setText(user.getRole());

        // Скрываем раздел "Пользователи" для не-админов
        if (!user.getRole().equals("admin")) {
            btnUsers.setVisible(false);
            btnUsers.setManaged(false);
            btnAdd.setVisible(false);
            btnAdd.setManaged(false);
        }

        // Подключаемся к БД и загружаем товары
        db = new Database("", "", "");
        db.connect();
        loadProducts();
    }

    public void initialize() {
        // Привязываем колонки к полям Product
        colId.setCellValueFactory(d ->
                new SimpleStringProperty(String.valueOf(d.getValue().getId())));
        colName.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getName()));
        colCategory.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getCategory()));
        colUnit.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getUnit()));
        colQuantity.setCellValueFactory(d ->
                new SimpleStringProperty(String.valueOf(d.getValue().getQuantity())));
        colMinQuantity.setCellValueFactory(d ->
                new SimpleStringProperty(String.valueOf(d.getValue().getMinQuantity())));
        colPrice.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getPrice() + " ₽"));

        // Колонка с кнопками Изменить / Удалить
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button btnEdit   = new Button("Изменить");
            private final Button btnDelete = new Button("Удалить");
            private final HBox box = new HBox(6, btnEdit, btnDelete);

            {
                btnEdit.setStyle(
                        "-fx-font-size:11px; -fx-background-color:#E6F1FB; -fx-text-fill:#185FA5;");
                btnDelete.setStyle(
                        "-fx-font-size:11px; -fx-background-color:#FCEBEB; -fx-text-fill:#A32D2D;");

                btnEdit.setOnAction(e -> {
                    Product p = getTableView().getItems().get(getIndex());
                    handleEdit(p);
                });
                btnDelete.setOnAction(e -> {
                    Product p = getTableView().getItems().get(getIndex());
                    handleDelete(p);
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
    }

    private void loadProducts() {
        List<Product> list = db.getAllProducts();
        productTable.setItems(FXCollections.observableArrayList(list));
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadProducts();
            return;
        }
        // Фильтруем локально по названию
        List<Product> all = db.getAllProducts();
        List<Product> filtered = all.stream()
                .filter(p -> p.getName().toLowerCase()
                        .contains(keyword.toLowerCase()))
                .toList();
        productTable.setItems(FXCollections.observableArrayList(filtered));
    }

    @FXML
    private void handleAdd() {
        openProductDialog(null);
    }

    private void handleEdit(Product p) {
        openProductDialog(p);
    }

    private void openProductDialog(Product product) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/product_dialog.fxml"));
            Stage dialog = new Stage();
            dialog.setScene(new Scene(loader.load()));
            dialog.setTitle(product == null ? "Добавить товар" : "Редактировать товар");

            ProductDialogController controller = loader.getController();
            controller.setDatabase(db);
            if (product != null) {
                controller.setProduct(product);
            }

            // После закрытия диалога — обновляем таблицу
            dialog.showAndWait();
            loadProducts();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleDelete(Product p) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Удалить товар «" + p.getName() + "»?",
                ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                db.deleteProduct(p.getId());
                loadProducts();
            }
        });
    }

    // --- Меню навигации ---
    @FXML private void showProducts()  { loadProducts(); }
    @FXML private void showReceipts()  { showInfo("Раздел «Поступления» — скоро."); }
    @FXML private void showExpenses()  { showInfo("Раздел «Расходы» — скоро."); }
    @FXML private void showLowStock()  { showInfo("Раздел «Низкий остаток» — скоро."); }
    @FXML private void showUsers()     { showInfo("Раздел «Пользователи» — скоро."); }

    @FXML
    private void logout() {
        try {
            db.disconnect();
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/login.fxml"));
            Stage stage = (Stage) productTable.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Авторизация");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showInfo(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
    }
}
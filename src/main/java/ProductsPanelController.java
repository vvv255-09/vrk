import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import java.util.List;

public class ProductsPanelController {

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

    public void setDatabase(Database db) {
        this.db = db;
        loadProducts();
    }

    public void setUser(User user) {
        this.currentUser = user;
        if (!user.getRole().equals("admin")) {
            btnAdd.setVisible(false);
            btnAdd.setManaged(false);
        }
    }

    public void initialize() {
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

        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button btnEdit   = new Button("Изменить");
            private final Button btnDelete = new Button("Удалить");
            private final HBox box = new HBox(6, btnEdit, btnDelete);
            {
                btnEdit.setStyle(
                        "-fx-font-size:11px; -fx-background-color:#E6F1FB; -fx-text-fill:#185FA5;");
                btnDelete.setStyle(
                        "-fx-font-size:11px; -fx-background-color:#FCEBEB; -fx-text-fill:#A32D2D;");
                btnEdit.setOnAction(e -> handleEdit(
                        getTableView().getItems().get(getIndex())));
                btnDelete.setOnAction(e -> handleDelete(
                        getTableView().getItems().get(getIndex())));
            }
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
    }

    private void loadProducts() {
        productTable.setItems(
                FXCollections.observableArrayList(db.getAllProducts()));
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().trim().toLowerCase();
        if (keyword.isEmpty()) { loadProducts(); return; }
        List<Product> filtered = db.getAllProducts().stream()
                .filter(p -> p.getName().toLowerCase().contains(keyword))
                .toList();
        productTable.setItems(FXCollections.observableArrayList(filtered));
    }

    @FXML
    private void handleAdd() { openDialog(null); }

    private void handleEdit(Product p) { openDialog(p); }

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

    private void openDialog(Product product) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/product_dialog.fxml"));
            Stage dialog = new Stage();
            dialog.setScene(new Scene(loader.load()));
            dialog.setTitle(product == null ? "Добавить товар" : "Редактировать");

            ProductDialogController controller = loader.getController();
            controller.setDatabase(db);
            if (product != null) controller.setProduct(product);

            dialog.showAndWait();
            loadProducts();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
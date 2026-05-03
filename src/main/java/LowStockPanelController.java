import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class LowStockPanelController {

    @FXML private TableView<Product> lowStockTable;
    @FXML private TableColumn<Product, String> colId;
    @FXML private TableColumn<Product, String> colName;
    @FXML private TableColumn<Product, String> colCategory;
    @FXML private TableColumn<Product, String> colQuantity;
    @FXML private TableColumn<Product, String> colMinQuantity;
    @FXML private TableColumn<Product, String> colUnit;

    private Database db;

    public void setDatabase(Database db) {
        this.db = db;
        loadLowStock();
    }

    public void initialize() {
        colId.setCellValueFactory(d ->
                new SimpleStringProperty(String.valueOf(d.getValue().getId())));
        colName.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getName()));
        colCategory.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getCategory()));
        colQuantity.setCellValueFactory(d ->
                new SimpleStringProperty(String.valueOf(d.getValue().getQuantity())));
        colMinQuantity.setCellValueFactory(d ->
                new SimpleStringProperty(String.valueOf(d.getValue().getMinQuantity())));
        colUnit.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getUnit()));

        // Красим строки где остаток критически низкий
        lowStockTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Product p, boolean empty) {
                super.updateItem(p, empty);
                if (!empty && p != null && p.getQuantity() == 0) {
                    setStyle("-fx-background-color: #FCEBEB;");
                } else {
                    setStyle("");
                }
            }
        });
    }

    private void loadLowStock() {
        // Фильтруем товары где quantity <= min_quantity
        lowStockTable.setItems(
                FXCollections.observableArrayList(
                        db.getAllProducts().stream()
                                .filter(p -> p.getQuantity() <= p.getMinQuantity())
                                .toList()
                )
        );
    }
}
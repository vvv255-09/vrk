import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class ReceiptsPanelController {

    @FXML private TableView<Receipt> receiptsTable;
    @FXML private TableColumn<Receipt, String> colId;
    @FXML private TableColumn<Receipt, String> colProduct;
    @FXML private TableColumn<Receipt, String> colSupplier;
    @FXML private TableColumn<Receipt, String> colQuantity;
    @FXML private TableColumn<Receipt, String> colDate;

    private Database db;

    public void setDatabase(Database db) {
        this.db = db;
        loadReceipts();
    }

    public void initialize() {
        colId.setCellValueFactory(d ->
                new SimpleStringProperty(String.valueOf(d.getValue().getId())));
        colProduct.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getProductName()));
        colSupplier.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getSupplierName()));
        colQuantity.setCellValueFactory(d ->
                new SimpleStringProperty(String.valueOf(d.getValue().getQuantity())));
        colDate.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getReceiptDate()));
    }

    private void loadReceipts() {
        receiptsTable.setItems(
                FXCollections.observableArrayList(db.getAllReceipts())
        );
    }

    @FXML
    private void handleAdd() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/receipt_dialog.fxml"));
            Stage dialog = new Stage();
            dialog.setScene(new Scene(loader.load()));
            dialog.setTitle("Добавить поступление");

            ReceiptDialogController controller = loader.getController();
            controller.setDatabase(db);

            dialog.showAndWait();
            loadReceipts();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
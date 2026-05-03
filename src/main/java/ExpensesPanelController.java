import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class ExpensesPanelController {

    @FXML private TableView<Expense> expensesTable;
    @FXML private TableColumn<Expense, String> colId;
    @FXML private TableColumn<Expense, String> colProduct;
    @FXML private TableColumn<Expense, String> colQuantity;
    @FXML private TableColumn<Expense, String> colDate;
    @FXML private TableColumn<Expense, String> colReason;

    private Database db;

    public void setDatabase(Database db) {
        this.db = db;
        loadExpenses();
    }

    public void initialize() {
        colId.setCellValueFactory(d ->
                new SimpleStringProperty(String.valueOf(d.getValue().getId())));
        colProduct.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getProductName()));
        colQuantity.setCellValueFactory(d ->
                new SimpleStringProperty(String.valueOf(d.getValue().getQuantity())));
        colDate.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getExpenseDate()));
        colReason.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getReason()));
    }

    private void loadExpenses() {
        expensesTable.setItems(
                FXCollections.observableArrayList(db.getAllExpenses()));
    }

    @FXML
    private void handleAdd() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/expense_dialog.fxml"));
            Stage dialog = new Stage();
            dialog.setScene(new Scene(loader.load()));
            dialog.setTitle("Добавить расход");

            ExpenseDialogController controller = loader.getController();
            controller.setDatabase(db);

            dialog.showAndWait();
            loadExpenses();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
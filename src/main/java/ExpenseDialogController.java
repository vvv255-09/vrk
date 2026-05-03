import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

public class ExpenseDialogController {

    @FXML private ComboBox<Product> productBox;
    @FXML private TextField quantityField;
    @FXML private TextField dateField;
    @FXML private TextField reasonField;
    @FXML private Label errorLabel;

    private Database db;

    public void setDatabase(Database db) {
        this.db = db;

        List<Product> products = db.getAllProducts();
        productBox.setItems(FXCollections.observableArrayList(products));

        productBox.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Product p, boolean empty) {
                super.updateItem(p, empty);
                setText(empty || p == null ? null : p.getName());
            }
        });
        productBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Product p, boolean empty) {
                super.updateItem(p, empty);
                setText(empty || p == null ? null : p.getName());
            }
        });

        dateField.setText(LocalDate.now().toString());
    }

    @FXML
    private void handleSave() {
        errorLabel.setText("");

        Product product = productBox.getValue();
        String date     = dateField.getText().trim();
        String reason   = reasonField.getText().trim();

        if (product == null) {
            errorLabel.setText("Выберите товар.");
            return;
        }
        if (date.isEmpty()) {
            errorLabel.setText("Укажите дату.");
            return;
        }
        if (reason.isEmpty()) {
            errorLabel.setText("Укажите причину.");
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityField.getText().trim());
            if (quantity <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            errorLabel.setText("Количество должно быть больше 0.");
            return;
        }

        db.addExpense(product.getId(), quantity, date, reason);
        closeDialog();
    }

    @FXML
    private void handleCancel() { closeDialog(); }

    private void closeDialog() {
        ((Stage) quantityField.getScene().getWindow()).close();
    }
}
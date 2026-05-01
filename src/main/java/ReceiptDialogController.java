import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

public class ReceiptDialogController {

    @FXML private ComboBox<Product> productBox;
    @FXML private ComboBox<Supplier> supplierBox;
    @FXML private TextField quantityField;
    @FXML private TextField dateField;
    @FXML private Label errorLabel;

    private Database db;

    public void setDatabase(Database db) {
        this.db = db;

        // Загружаем товары и поставщиков в списки
        List<Product> products = db.getAllProducts();
        productBox.setItems(FXCollections.observableArrayList(products));

        List<Supplier> suppliers = db.getAllSuppliers();
        supplierBox.setItems(FXCollections.observableArrayList(suppliers));

        // Показываем имена в выпадающем списке
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

        supplierBox.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Supplier s, boolean empty) {
                super.updateItem(s, empty);
                setText(empty || s == null ? null : s.getName());
            }
        });
        supplierBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Supplier s, boolean empty) {
                super.updateItem(s, empty);
                setText(empty || s == null ? null : s.getName());
            }
        });

        // Дата по умолчанию — сегодня
        dateField.setText(LocalDate.now().toString());
    }

    @FXML
    private void handleSave() {
        errorLabel.setText("");

        Product product   = productBox.getValue();
        Supplier supplier = supplierBox.getValue();
        String date       = dateField.getText().trim();

        if (product == null || supplier == null) {
            errorLabel.setText("Выберите товар и поставщика.");
            return;
        }

        if (date.isEmpty()) {
            errorLabel.setText("Укажите дату.");
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

        db.addReceipt(product.getId(), supplier.getId(), quantity, date);
        closeDialog();
    }

    @FXML
    private void handleCancel() { closeDialog(); }

    private void closeDialog() {
        ((Stage) quantityField.getScene().getWindow()).close();
    }
}
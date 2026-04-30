import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class ProductDialogController {

    @FXML private Label titleLabel;
    @FXML private TextField nameField;
    @FXML private TextField categoryField;
    @FXML private ComboBox<String> unitBox;
    @FXML private TextField quantityField;
    @FXML private TextField minQuantityField;
    @FXML private TextField priceField;
    @FXML private Label errorLabel;

    private Database db;
    private Product existingProduct; // null = добавление, не null = редактирование

    public void initialize() {
        unitBox.setItems(FXCollections.observableArrayList(
                "шт", "кг", "г", "л", "м", "м²", "м³", "упак", "рулон"
        ));
        unitBox.setValue("шт");
    }

    public void setDatabase(Database db) {
        this.db = db;
    }

    // Режим редактирования — заполняем поля существующего товара
    public void setProduct(Product product) {
        this.existingProduct = product;
        titleLabel.setText("Редактировать товар");
        nameField.setText(product.getName());
        categoryField.setText(product.getCategory());
        unitBox.setValue(product.getUnit());
        quantityField.setText(String.valueOf(product.getQuantity()));
        minQuantityField.setText(String.valueOf(product.getMinQuantity()));
        priceField.setText(String.valueOf(product.getPrice()));
    }

    @FXML
    private void handleSave() {
        errorLabel.setText("");

        String name     = nameField.getText().trim();
        String category = categoryField.getText().trim();
        String unit     = unitBox.getValue();

        if (name.isEmpty() || category.isEmpty()) {
            errorLabel.setText("Заполните название и категорию.");
            return;
        }

        int quantity, minQuantity;
        double price;

        try {
            quantity    = Integer.parseInt(quantityField.getText().trim());
            minQuantity = Integer.parseInt(minQuantityField.getText().trim());
            price       = Double.parseDouble(priceField.getText().trim().replace(",", "."));
        } catch (NumberFormatException e) {
            errorLabel.setText("Количество и цена должны быть числами.");
            return;
        }

        if (quantity < 0 || minQuantity < 0 || price < 0) {
            errorLabel.setText("Значения не могут быть отрицательными.");
            return;
        }

        if (existingProduct == null) {
            // Добавление нового товара
            Product newProduct = new Product(0, name, category, unit,
                    quantity, minQuantity, price);
            db.addProduct(newProduct);
        } else {
            // Редактирование — обновляем количество
            existingProduct.setName(name);
            existingProduct.setCategory(category);
            existingProduct.setUnit(unit);
            existingProduct.setMinQuantity(minQuantity);
            existingProduct.setPrice(price);
            db.updateProductQuantity(existingProduct.getId(), quantity);
            db.updateProduct(existingProduct); // добавим этот метод в Database
        }

        closeDialog();
    }

    @FXML
    private void handleCancel() {
        closeDialog();
    }

    private void closeDialog() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }
}
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class ProductCardController {

    @FXML private Label nameLabel;
    @FXML private Label categoryLabel;
    @FXML private Label quantityLabel;
    @FXML private Label minQuantityLabel;
    @FXML private Label priceLabel;
    @FXML private ListView<String> historyList;

    private Database db;

    public void setData(Product product, Database db) {
        this.db = db;

        nameLabel.setText(product.getName());
        categoryLabel.setText(product.getCategory() + " · " + product.getUnit());
        quantityLabel.setText(String.valueOf(product.getQuantity()));
        minQuantityLabel.setText(String.valueOf(product.getMinQuantity()));
        priceLabel.setText(String.format("%.2f сом", product.getPrice()));

        historyList.setItems(FXCollections.observableArrayList(
                db.getProductHistory(product.getId())
        ));
    }

    @FXML
    private void handleClose() {
        ((Stage) nameLabel.getScene().getWindow()).close();
    }
}
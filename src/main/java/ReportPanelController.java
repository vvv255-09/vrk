import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.*;

public class ReportPanelController {

    @FXML private Label totalProductsLabel;
    @FXML private Label totalValueLabel;
    @FXML private Label lowStockLabel;
    @FXML private Label totalReceiptsLabel;

    @FXML private TableView<Map.Entry<String, double[]>> categoryTable;
    @FXML private TableColumn<Map.Entry<String, double[]>, String> colCategory;
    @FXML private TableColumn<Map.Entry<String, double[]>, String> colCount;
    @FXML private TableColumn<Map.Entry<String, double[]>, String> colTotal;
    @FXML private TableColumn<Map.Entry<String, double[]>, String> colValue;

    private Database db;

    public void setDatabase(Database db) {
        this.db = db;
        loadReport();
    }

    public void initialize() {
        colCategory.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getKey()));
        colCount.setCellValueFactory(d ->
                new SimpleStringProperty(String.valueOf((int) d.getValue().getValue()[0])));
        colTotal.setCellValueFactory(d ->
                new SimpleStringProperty(String.valueOf((int) d.getValue().getValue()[1])));
        colValue.setCellValueFactory(d ->
                new SimpleStringProperty(
                        String.format("%.2f сом", d.getValue().getValue()[2])));
    }

    private void loadReport() {
        List<Product> products = db.getAllProducts();

        // Карточки
        int total    = products.size();
        double value = products.stream()
                .mapToDouble(p -> p.getPrice() * p.getQuantity()).sum();
        long lowStock = products.stream()
                .filter(p -> p.getQuantity() <= p.getMinQuantity()).count();
        int receipts  = db.getAllReceipts().size();

        totalProductsLabel.setText(String.valueOf(total));
        totalValueLabel.setText(String.format("%.2f сом", value));
        lowStockLabel.setText(String.valueOf(lowStock));
        totalReceiptsLabel.setText(String.valueOf(receipts));

        // Таблица по категориям
        Map<String, double[]> categoryMap = new LinkedHashMap<>();
        for (Product p : products) {
            categoryMap.computeIfAbsent(p.getCategory(), k -> new double[3]);
            double[] arr = categoryMap.get(p.getCategory());
            arr[0]++; // кол-во позиций
            arr[1] += p.getQuantity(); // общее кол-во
            arr[2] += p.getPrice() * p.getQuantity(); // стоимость
        }

        categoryTable.setItems(
                FXCollections.observableArrayList(categoryMap.entrySet()));
    }
}
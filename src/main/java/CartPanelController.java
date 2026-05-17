import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.List;

public class CartPanelController {

    @FXML private ComboBox<Product> productBox;
    @FXML private TextField quantityField;
    @FXML private Label errorLabel;
    @FXML private TableView<OrderItem> cartTable;
    @FXML private TableColumn<OrderItem, String> colName;
    @FXML private TableColumn<OrderItem, String> colPrice;
    @FXML private TableColumn<OrderItem, String> colQty;
    @FXML private TableColumn<OrderItem, String> colTotal;
    @FXML private TableColumn<OrderItem, String> colRemove;
    @FXML private Label totalLabel;

    private Database db;
    private User currentUser;
    private ObservableList<OrderItem> cartItems = FXCollections.observableArrayList();

    public void setData(Database db, User user) {
        this.db = db;
        this.currentUser = user;

        // Загружаем товары в комбобокс
        List<Product> products = db.getAllProducts().stream()
                .filter(p -> p.getQuantity() > 0)
                .toList();
        productBox.setItems(FXCollections.observableArrayList(products));
        productBox.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Product p, boolean empty) {
                super.updateItem(p, empty);
                setText(empty || p == null ? null :
                        p.getName() + " (" + p.getQuantity() + " " + p.getUnit() + ")");
            }
        });
        productBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Product p, boolean empty) {
                super.updateItem(p, empty);
                setText(empty || p == null ? null : p.getName());
            }
        });
    }

    public void initialize() {
        colName.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getProductName()));
        colPrice.setCellValueFactory(d ->
                new SimpleStringProperty(
                        String.format("%.2f ₽", d.getValue().getPrice())));
        colQty.setCellValueFactory(d ->
                new SimpleStringProperty(String.valueOf(d.getValue().getQuantity())));
        colTotal.setCellValueFactory(d ->
                new SimpleStringProperty(
                        String.format("%.2f ₽",
                                d.getValue().getPrice() * d.getValue().getQuantity())));

        // Кнопка удалить из корзины
        colRemove.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("✕");
            {
                btn.setStyle(
                        "-fx-background-color:#FCEBEB; -fx-text-fill:#A32D2D;" +
                                "-fx-background-radius:4;");
                btn.setOnAction(e -> {
                    cartItems.remove(getTableView().getItems().get(getIndex()));
                    updateTotal();
                });
            }
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        cartTable.setItems(cartItems);
    }

    @FXML
    private void handleAddToCart() {
        errorLabel.setText("");
        Product product = productBox.getValue();

        if (product == null) {
            errorLabel.setText("Выберите товар.");
            return;
        }

        int qty;
        try {
            qty = Integer.parseInt(quantityField.getText().trim());
            if (qty <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            errorLabel.setText("Укажите количество больше 0.");
            return;
        }

        if (qty > product.getQuantity()) {
            errorLabel.setText("Недостаточно товара на складе. Доступно: " +
                    product.getQuantity() + " " + product.getUnit());
            return;
        }

        // Проверяем есть ли уже в корзине
        for (OrderItem item : cartItems) {
            if (item.getProductId() == product.getId()) {
                item.setQuantity(item.getQuantity() + qty);
                cartTable.refresh();
                updateTotal();
                quantityField.clear();
                return;
            }
        }

        cartItems.add(new OrderItem(
                0, 0, product.getId(),
                product.getName(), qty, product.getPrice()
        ));
        updateTotal();
        quantityField.clear();
    }

    @FXML
    private void handleOrder() {
        if (cartItems.isEmpty()) {
            errorLabel.setText("Корзина пуста.");
            return;
        }

        double total = cartItems.stream()
                .mapToDouble(i -> i.getPrice() * i.getQuantity())
                .sum();

        // Подтверждение
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Оформить заказ на сумму " + String.format("%.2f ₽", total) + "?",
                ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Подтверждение заказа");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                int orderId = db.createOrder(currentUser.getId(), total);
                if (orderId != -1) {
                    for (OrderItem item : cartItems) {
                        db.addOrderItem(orderId, item.getProductId(),
                                item.getQuantity(), item.getPrice());
                    }
                    cartItems.clear();
                    totalLabel.setText("0.00 ₽");

                    Alert success = new Alert(Alert.AlertType.INFORMATION,
                            "Заказ #" + orderId + " успешно оформлен!",
                            ButtonType.OK);
                    success.setTitle("Заказ оформлен");
                    success.showAndWait();
                }
            }
        });
    }

    private void updateTotal() {
        double total = cartItems.stream()
                .mapToDouble(i -> i.getPrice() * i.getQuantity())
                .sum();
        totalLabel.setText(String.format("%.2f ₽", total));
    }
}
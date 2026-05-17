import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class OrdersPanelController {

    @FXML private TableView<Order> ordersTable;
    @FXML private TableColumn<Order, String> colId;
    @FXML private TableColumn<Order, String> colDate;
    @FXML private TableColumn<Order, String> colStatus;
    @FXML private TableColumn<Order, String> colTotal;
    @FXML private TableColumn<Order, String> colItems;

    private Database db;
    private User currentUser;

    public void setData(Database db, User user) {
        this.db = db;
        this.currentUser = user;
        loadOrders();
    }

    public void initialize() {
        colId.setCellValueFactory(d ->
                new SimpleStringProperty(String.valueOf(d.getValue().getId())));
        colDate.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getOrderDate()));
        colTotal.setCellValueFactory(d ->
                new SimpleStringProperty(
                        String.format("%.2f ₽", d.getValue().getTotalPrice())));

        // Цветной статус
        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow().getItem() == null) {
                    setGraphic(null); return;
                }
                String status = ((Order) getTableRow().getItem()).getStatus();
                Label badge = new Label(status);
                String color = switch (status) {
                    case "В обработке" ->
                            "-fx-background-color:#FFF3E0; -fx-text-fill:#854F0B;";
                    case "Отправлен" ->
                            "-fx-background-color:#E6F1FB; -fx-text-fill:#185FA5;";
                    case "Завершён" ->
                            "-fx-background-color:#EAF3DE; -fx-text-fill:#3B6D11;";
                    default ->
                            "-fx-background-color:#f0f0f0; -fx-text-fill:#555;";
                };
                badge.setStyle(color +
                        "-fx-padding:2 8; -fx-background-radius:20; -fx-font-size:11px;");
                setGraphic(badge);
            }
        });

        // Кнопка посмотреть состав
        colItems.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Состав");
            {
                btn.setStyle(
                        "-fx-font-size:11px; -fx-background-color:#E6F1FB;" +
                                "-fx-text-fill:#185FA5; -fx-background-radius:4;");
                btn.setOnAction(e -> {
                    Order order = getTableView().getItems().get(getIndex());
                    showItems(order);
                });
            }
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
    }

    private void loadOrders() {
        ordersTable.setItems(FXCollections.observableArrayList(
                db.getUserOrders(currentUser.getId())));
    }

    private void showItems(Order order) {
        StringBuilder sb = new StringBuilder();
        sb.append("Заказ #").append(order.getId()).append("\n\n");
        for (var item : db.getOrderItems(order.getId())) {
            sb.append("• ").append(item.getProductName())
                    .append(" — ").append(item.getQuantity()).append(" шт")
                    .append(" × ").append(String.format("%.2f ₽", item.getPrice()))
                    .append(" = ").append(
                            String.format("%.2f ₽", item.getPrice() * item.getQuantity()))
                    .append("\n");
        }
        sb.append("\nИтого: ").append(String.format("%.2f ₽", order.getTotalPrice()));

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Состав заказа");
        alert.setHeaderText("Заказ #" + order.getId() + " — " + order.getStatus());
        alert.setContentText(sb.toString());
        alert.showAndWait();
    }
}
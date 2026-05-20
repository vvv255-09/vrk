import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

public class AdminOrdersPanelController {

    @FXML private TableView<Order> ordersTable;
    @FXML private TableColumn<Order, String> colId;
    @FXML private TableColumn<Order, String> colUser;
    @FXML private TableColumn<Order, String> colDate;
    @FXML private TableColumn<Order, String> colTotal;
    @FXML private TableColumn<Order, String> colStatus;
    @FXML private TableColumn<Order, String> colActions;

    private Database db;

    public void setDatabase(Database db) {
        this.db = db;
        loadOrders();
    }

    public void initialize() {
        colId.setCellValueFactory(d ->
                new SimpleStringProperty(String.valueOf(d.getValue().getId())));
        colDate.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getOrderDate()));
        colTotal.setCellValueFactory(d ->
                new SimpleStringProperty(
                        String.format("%.2f сом", d.getValue().getTotalPrice())));

        // Имя клиента
        colUser.setCellValueFactory(d ->
                new SimpleStringProperty(
                        db.getUserById(d.getValue().getUserId())));

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
                    case "Отменён" ->
                            "-fx-background-color:#FCEBEB; -fx-text-fill:#A32D2D;";
                    default ->
                            "-fx-background-color:#f0f0f0; -fx-text-fill:#555;";
                };
                badge.setStyle(color +
                        "-fx-padding:2 8; -fx-background-radius:20; -fx-font-size:11px;");
                setGraphic(badge);
            }
        });

        // Кнопки смены статуса
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button btnAccept   = new Button("✅ Принять");
            private final Button btnShip     = new Button("🚚 Отправить");
            private final Button btnComplete = new Button("✔ Завершить");
            private final Button btnCancel   = new Button("✕ Отменить");
            private final HBox box = new HBox(4, btnAccept, btnShip, btnComplete, btnCancel);

            {
                btnAccept.setStyle(
                        "-fx-font-size:10px; -fx-background-color:#EAF3DE;" +
                                "-fx-text-fill:#3B6D11; -fx-background-radius:4;");
                btnShip.setStyle(
                        "-fx-font-size:10px; -fx-background-color:#E6F1FB;" +
                                "-fx-text-fill:#185FA5; -fx-background-radius:4;");
                btnComplete.setStyle(
                        "-fx-font-size:10px; -fx-background-color:#EAF3DE;" +
                                "-fx-text-fill:#0F6E56; -fx-background-radius:4;");
                btnCancel.setStyle(
                        "-fx-font-size:10px; -fx-background-color:#FCEBEB;" +
                                "-fx-text-fill:#A32D2D; -fx-background-radius:4;");

                btnAccept.setOnAction(e -> updateStatus("В обработке"));
                btnShip.setOnAction(e -> updateStatus("Отправлен"));
                btnComplete.setOnAction(e -> updateStatus("Завершён"));
                btnCancel.setOnAction(e -> updateStatus("Отменён"));
            }

            private void updateStatus(String status) {
                Order order = getTableView().getItems().get(getIndex());
                db.updateOrderStatus(order.getId(), status);
                loadOrders();
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
    }

    private void loadOrders() {
        ordersTable.setItems(
                FXCollections.observableArrayList(db.getAllOrders()));
    }
}
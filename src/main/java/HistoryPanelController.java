import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class HistoryPanelController {

    @FXML private ListView<String> historyList;

    private Database db;

    public void setDatabase(Database db) {
        this.db = db;
        historyList.setItems(
                FXCollections.observableArrayList(db.getOperationsHistory()));
    }
}
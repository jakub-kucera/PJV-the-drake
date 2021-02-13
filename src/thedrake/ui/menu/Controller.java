package thedrake.ui.menu;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import thedrake.GameResult;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private Button buttonGameTwoPlayers;
    @FXML
    private Button buttonGameComputer;
    @FXML
    private Button buttonGameInternet;
    @FXML
    private Button buttonGameEndMenu;
    @FXML
    private Button buttonGamePlayAgain;
    @FXML
    private Button buttonGameEnd;
    @FXML
    private Label label;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void onExit() {
        System.exit(0);
    }

    public void onStart() {
        GameResult.gameState = GameResult.IN_PLAY;
        GameResult.gameStateChanged = true;
    }

    public void setWinMessage(String text) {
        label.setText(text);
    }
}

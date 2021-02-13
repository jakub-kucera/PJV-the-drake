package thedrake.ui;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import thedrake.*;
import thedrake.ui.menu.Controller;

public class TheDrakeApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        BorderPane menu = FXMLLoader.load(getClass().getResource("menu/startMenu.fxml"));
        Scene menuScene = new Scene(menu);

        FXMLLoader loader = new FXMLLoader();
        BorderPane endMenu = loader.load(getClass().getResource("menu/endMenu.fxml").openStream());
        Controller endController = loader.getController();
        Scene endMenuScene = new Scene(endMenu);

        primaryStage.setScene(menuScene);
        primaryStage.setTitle("The Drake");
        primaryStage.show();

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
//                System.out.println("State: " + GameResult.gameState);
                if (GameResult.gameStateChanged) {
                    GameResult.gameStateChanged = false;
                    switch (GameResult.gameState) {
                        case IN_PLAY:
                            primaryStage.setScene(new Scene(new BoardView(startGameState())));
                            primaryStage.show();
                            break;
                        case VICTORY:
                            primaryStage.setScene(endMenuScene);
                            primaryStage.show();
                            endController.setWinMessage(GameResult.gameEndMessage);
                            break;
                        case DRAW:
                            primaryStage.setScene(endMenuScene);
                            primaryStage.show();
                            endController.setWinMessage("Remíza");
                            break;
                    }
                }
            }
        };
        timer.start();
    }

    private static GameState startGameState() {
        Board board = new Board(4);
        PositionFactory positionFactory = board.positionFactory();
        board = board.withTiles(new Board.TileAt(positionFactory.pos(1, 1), BoardTile.MOUNTAIN));
        return new StandardDrakeSetup().startState(board);
    }

    private static GameState newSampleGameState() {
        Board board = new Board(4);
        PositionFactory positionFactory = board.positionFactory();
        board = board.withTiles(new Board.TileAt(positionFactory.pos(1, 1), BoardTile.MOUNTAIN));
        return new StandardDrakeSetup().startState(board)
                .placeFromStack(positionFactory.pos(0, 0))
                .placeFromStack(positionFactory.pos(3, 3))
                .placeFromStack(positionFactory.pos(0, 1))
                .placeFromStack(positionFactory.pos(3, 2))
                .placeFromStack(positionFactory.pos(1, 0))
                .placeFromStack(positionFactory.pos(2, 3));
    }

}

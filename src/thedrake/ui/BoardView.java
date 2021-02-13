package thedrake.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import thedrake.*;

import java.util.List;

public class BoardView extends GridPane implements TileViewContext {

    private GameState gameState;

    private TileView selectedTile;

    private StackView selectedStack;

    private ValidMoves validMoves;

    public BoardView(GameState gameState) {
        this.gameState = gameState;
        validMoves = new ValidMoves(gameState);

        //board tiles
        PositionFactory positionFactory = gameState.board().positionFactory();
        for (int y = 0; y < positionFactory.dimension(); y++) {
            for (int x = 0; x < positionFactory.dimension(); x++) {
                BoardPos boardPos = positionFactory.pos(x, positionFactory.dimension() - y - 1);
                add(new TileView(gameState.tileAt(boardPos), boardPos, this), x, y);
            }
        }

        //stack
        int i = 0;
        for (Troop troop : gameState.armyOnTurn().stack()) {
            add(new StackView(troop, gameState.armyOnTurn().side(), i, this), i++, positionFactory.dimension() + 1);
        }

        int j = 0;
        for (Troop troop : gameState.armyNotOnTurn().stack()) {
            add(new StackView(troop, gameState.armyNotOnTurn().side(), j, this), j++, positionFactory.dimension() + 2);
        }

        //captured
        for (int k = 0; k < 7; k++) {
            add(new CapturedView(k, gameState.armyOnTurn().side()), k, positionFactory.dimension() + 3);
        }

        for (int m = 0; m < 7; m++) {
            add(new CapturedView(m, gameState.armyNotOnTurn().side()), m, positionFactory.dimension() + 4);
        }


        setHgap(5);
        setVgap(5);
        setPadding(new Insets(15));
        setAlignment(Pos.CENTER);
    }

    private void updateTiles(int capturedDifference) {
        for (Node node : getChildren()) {
            BasicView basicView = (BasicView) node;
            if (basicView.getType() == ViewType.TILEVIEW) {
                TileView tileview = (TileView) node;
                tileview.setTile(gameState.tileAt(tileview.position()));
            }
            else if (basicView.getType() == ViewType.CAPTUREDVIEW && capturedDifference != 0) {
                CapturedView capturedView = (CapturedView) node;
                if (capturedView.getPlayingSide() == gameState.armyOnTurn().side()) {
                    if (capturedView.getOrder() == 0) {
                        capturedView.setTroop(gameState.armyNotOnTurn().captured().get(gameState.armyNotOnTurn().captured().size() - 1));
                    }
                    capturedView.decrementOrder();
                }
            }
        }
    }

    @Override
    public void tileViewSelected(TileView tileView) {
        if (selectedTile != null && selectedTile != tileView) {
            selectedTile.unselect();
        }

        if (selectedStack != null) {
            selectedStack.unselect();
        }

        selectedTile = tileView;

        clearMoves();
        showMoves(validMoves.boardMoves(tileView.position()));
    }

    @Override
    public void stackViewSelected(StackView stackView) {
        if (selectedStack != null && selectedStack != stackView) {
            selectedStack.unselect();
        }

        if (selectedTile != null) {
            selectedTile.unselect();
        }

        selectedStack = stackView;

        clearMoves();
        if (stackView.getPlayingSide() == gameState.armyOnTurn().side() && stackView.getOrder() == 0) {
            showMoves(validMoves.movesFromStack());
        }
    }

    @Override
    public void executeMove(Move move) {
        if (selectedTile != null) {
            selectedTile.unselect();
            selectedTile = null;
        }
        if (selectedStack != null) {
            selectedStack.unselect();
            selectedStack.setBlank();
            selectedStack = null;

            for (Node node : getChildren()) {
                if (((BasicView) node).getType() == ViewType.STACKVIEW) {
                    StackView stackView = (StackView) node;
                    if (stackView.getPlayingSide() == gameState.armyOnTurn().side()) {
                        stackView.decrementOrder();
                    }
                }
            }
        }

        clearMoves();
        int oldCapturedSize = gameState.armyOnTurn().captured().size();
        gameState = move.execute(gameState);
        int newCapturedSize = gameState.armyNotOnTurn().captured().size();
        validMoves = new ValidMoves(gameState);
        updateTiles(newCapturedSize - oldCapturedSize);

        if (!gameState.armyNotOnTurn().boardTroops().isLeaderPlaced() && gameState.armyNotOnTurn().stack().size() < 7) {
            GameResult.gameEndMessage = (gameState.armyOnTurn().side() == PlayingSide.ORANGE ? "Oranžový" : "Modrý") + " hráč vyhrál.";
            GameResult.gameState = GameResult.VICTORY;
            GameResult.gameStateChanged = true;
        }
        if (!gameState.armyOnTurn().boardTroops().isLeaderPlaced() && gameState.armyOnTurn().stack().size() < 7) {
            GameResult.gameEndMessage = (gameState.armyNotOnTurn().side() == PlayingSide.ORANGE ? "Oranžový" : "Modrý") + " hráč vyhrál.";
            GameResult.gameState = GameResult.VICTORY;
            GameResult.gameStateChanged = true;
        }
    }

    private void clearMoves() {
        for (Node node : getChildren()) {
            BasicView basicView = (BasicView) node;
            basicView.clearMove();
        }
    }

    private void showMoves(List<Move> moves) {
        for (Move move : moves) {
            tileViewAt(move.target()).setMove(move);
        }
    }

    private TileView tileViewAt(BoardPos target) {
        int index = (gameState.board().dimension() - 1 - target.j()) * 4 + target.i();
        return (TileView) getChildren().get(index);
    }
}

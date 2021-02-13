package thedrake;

import java.io.PrintWriter;

public class GameState implements JSONSerializable {
    private final Board board;
    private final PlayingSide sideOnTurn;
    private final Army blueArmy;
    private final Army orangeArmy;
    private final GameResult result;

    public GameState(
            Board board,
            Army blueArmy,
            Army orangeArmy) {
        this(board, blueArmy, orangeArmy, PlayingSide.BLUE, GameResult.IN_PLAY);
    }

    public GameState(
            Board board,
            Army blueArmy,
            Army orangeArmy,
            PlayingSide sideOnTurn,
            GameResult result) {
        this.board = board;
        this.sideOnTurn = sideOnTurn;
        this.blueArmy = blueArmy;
        this.orangeArmy = orangeArmy;
        this.result = result;
    }

    public Board board() {
        return board;
    }

    public PlayingSide sideOnTurn() {
        return sideOnTurn;
    }

    public GameResult result() {
        return result;
    }

    public Army army(PlayingSide side) {
        if (side == PlayingSide.BLUE) {
            return blueArmy;
        }

        return orangeArmy;
    }

    public Army armyOnTurn() {
        return army(sideOnTurn);
    }

    public Army armyNotOnTurn() {
        if (sideOnTurn == PlayingSide.BLUE)
            return orangeArmy;

        return blueArmy;
    }

    public Tile tileAt(TilePos pos) {
        if(armyOnTurn().boardTroops().at(pos).isPresent()) {
            return  armyOnTurn().boardTroops().at(pos).get();
        }
        if(armyNotOnTurn().boardTroops().at(pos).isPresent()) {
            return  armyNotOnTurn().boardTroops().at(pos).get();
        }
        if (!board.at(pos).hasTroop()) {
            return board.at(pos);
        }
        return BoardTile.EMPTY;//todo idk
    }

    private boolean canStepFrom(TilePos origin) { //todo check if troop on origin
        if (result != GameResult.IN_PLAY
                || origin == TilePos.OFF_BOARD
//                || !board.at(origin).hasTroop()
                || armyNotOnTurn().boardTroops().at(origin).isPresent()
                || armyOnTurn().boardTroops().at(origin).isEmpty()
                || armyOnTurn().boardTroops().isPlacingGuards() /* || board.at(origin).hasTroop()*/) {
            return false;
        }
        return true;
    }

    private boolean canStepTo(TilePos target) {
        if (result != GameResult.IN_PLAY
                || target == TilePos.OFF_BOARD
                || !board.at(target).canStepOn()
                || armyOnTurn().boardTroops().at(target).isPresent()
                || armyNotOnTurn().boardTroops().at(target).isPresent()) { //todo make better canStepOn/isPresent
            return false;
        }
//        if (!armyOnTurn().boardTroops().isLeaderPlaced() && ((target.row() == 1 && armyOnTurn().side() == PlayingSide.BLUE) || (target.row() == 4 && armyOnTurn().side() == PlayingSide.ORANGE))) {
//            return true;
//        }
        return true; //TODO check if any Troop can step on
    }

    private boolean canCaptureOn(TilePos target) {
        if (result != GameResult.IN_PLAY/* || !board.at(target).hasTroop()*/ || armyNotOnTurn().boardTroops().at(target).isEmpty()) { //TODO maybe armyOnTurn()
            return false;
        }
        return true;
    }

    public boolean canStep(TilePos origin, TilePos target) {
        return canStepFrom(origin) && canStepTo(target);
    }

    public boolean canCapture(TilePos origin, TilePos target) {
        return canStepFrom(origin) && canCaptureOn(target);
    }

    public boolean canPlaceFromStack(TilePos target) {
        if (!canStepTo(target) || armyOnTurn().stack().isEmpty()) {
            return false;
        }

        if (!armyOnTurn().boardTroops().isLeaderPlaced()) {
            if ((target.row() == 1 && armyOnTurn().side() == PlayingSide.BLUE) || (target.row() == board.dimension() && armyOnTurn().side() == PlayingSide.ORANGE)) {
                return true;
            }
            return false;
        }

        if (armyOnTurn().boardTroops().isPlacingGuards()) {
            if (target.isNextTo(armyOnTurn().boardTroops().leaderPosition())) {
                return true;
            }
            return false;
        }

//        armyOnTurn().boardTroops().at(target).get()

        for (BoardPos neighbour : ((BoardPos) target).neighbours()) {
            if (armyOnTurn().boardTroops().troopPositions().contains(neighbour)) {
                return true;
            }
        }

        //TODO check if any ally is neighbour

        return false;
    }

    public GameState stepOnly(BoardPos origin, BoardPos target) {
        if (canStep(origin, target))
            return createNewGameState(
                    armyNotOnTurn(),
                    armyOnTurn().troopStep(origin, target), GameResult.IN_PLAY);

        throw new IllegalArgumentException();
    }

    public GameState stepAndCapture(BoardPos origin, BoardPos target) {
        if (canCapture(origin, target)) {
            Troop captured = armyNotOnTurn().boardTroops().at(target).get().troop();
            GameResult newResult = GameResult.IN_PLAY;

            if (armyNotOnTurn().boardTroops().leaderPosition().equals(target))
                newResult = GameResult.VICTORY;

            return createNewGameState(
                    armyNotOnTurn().removeTroop(target),
//                    armyNotOnTurn().capture(armyNotOnTurn().boardTroops().at(target).get().troop()),
                    armyOnTurn().troopStep(origin, target).capture(captured), newResult);
        }

        throw new IllegalArgumentException();
    }

    public GameState captureOnly(BoardPos origin, BoardPos target) {
        if (canCapture(origin, target)) {
            Troop captured = armyNotOnTurn().boardTroops().at(target).get().troop();
            GameResult newResult = GameResult.IN_PLAY;

            if (armyNotOnTurn().boardTroops().leaderPosition().equals(target))
                newResult = GameResult.VICTORY;

            return createNewGameState(
                    armyNotOnTurn().removeTroop(target),
                    armyOnTurn().troopFlip(origin).capture(captured), newResult);
        }

        throw new IllegalArgumentException();
    }

    public GameState placeFromStack(BoardPos target) {
        if (canPlaceFromStack(target)) {
            return createNewGameState(
                    armyNotOnTurn(),
                    armyOnTurn().placeFromStack(target),
                    GameResult.IN_PLAY);
        }

        throw new IllegalArgumentException();
    }

    public GameState resign() {
        return createNewGameState(
                armyNotOnTurn(),
                armyOnTurn(),
                GameResult.VICTORY);
    }

    public GameState draw() {
        return createNewGameState(
                armyOnTurn(),
                armyNotOnTurn(),
                GameResult.DRAW);
    }

    private GameState createNewGameState(Army armyOnTurn, Army armyNotOnTurn, GameResult result) {
        if (armyOnTurn.side() == PlayingSide.BLUE) {
            return new GameState(board, armyOnTurn, armyNotOnTurn, PlayingSide.BLUE, result);
        }

        return new GameState(board, armyNotOnTurn, armyOnTurn, PlayingSide.ORANGE, result);
    }

    @Override
    public void toJSON(PrintWriter writer) {
        writer.printf("{\"result\":");
        result.toJSON(writer);
        writer.printf(",\"board\":");
        board.toJSON(writer);
        writer.printf(",\"blueArmy\":");
        blueArmy.toJSON(writer);
        writer.printf(",\"orangeArmy\":");
        orangeArmy.toJSON(writer);
        writer.printf("}");
    }
}

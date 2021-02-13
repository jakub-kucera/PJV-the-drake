package thedrake;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class TroopTile implements Tile, JSONSerializable {

    final Troop troop;
    final PlayingSide playingSide;
    final TroopFace troopFace;

    public TroopTile(Troop troop, PlayingSide playingSide, TroopFace troopFace) {
        this.troop = troop;
        this.playingSide = playingSide;
        this.troopFace = troopFace;
    }

    @Override
    public boolean canStepOn() {
        return false;
    }

    @Override
    public boolean hasTroop() {
        return true;
    }

    @Override
    public List<Move> movesFrom(BoardPos pos, GameState state) {
        List<TroopAction> actions = troop.actions(troopFace);
        List<Move> moves = new ArrayList<>();
        for (TroopAction action : actions) {
//            moves.add(action.movesFrom(pos, playingSide, state));
            moves.addAll(action.movesFrom(pos, state.sideOnTurn(), state));
//            System.out.println(moves.toString());
        }
        return moves;
    }

    public PlayingSide side() {
        return playingSide;
    }

    public TroopFace face() {
        return troopFace;
    }

    public Troop troop() {
        return troop;
    }

    public TroopTile flipped() {
        return new TroopTile(troop, playingSide, (troopFace == TroopFace.AVERS) ? TroopFace.REVERS : TroopFace.AVERS);
    }


    public String toJSONString() {
        return "{\"troop\":" + troop.toJSONString() +
                ",\"side\":" + playingSide.toJSONString() +
                ",\"face\":" + troopFace.toJSONString() +
                "}";
    }

    @Override
    public void toJSON(PrintWriter writer) {
        writer.printf(toJSONString());
//        writer.printf("{\"troop\":");
//        troop.toJSON(writer);
//        writer.printf(",\"side\":");
//        playingSide.toJSON(writer);
//        writer.printf(",\"face\":");
//        troopFace.toJSON(writer);
//        writer.printf("}");
    }
}

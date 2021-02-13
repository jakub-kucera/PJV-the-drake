package thedrake;

import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

public class BoardTroops implements JSONSerializable {
    private final PlayingSide playingSide;
    private final Map<BoardPos, TroopTile> troopMap;
    private final TilePos leaderPosition;
    private final int guards;

    public BoardTroops(PlayingSide playingSide) {
        this.playingSide = playingSide;
        troopMap = Collections.EMPTY_MAP;
        leaderPosition = TilePos.OFF_BOARD;
        guards = 0;
    }

    public BoardTroops(
            PlayingSide playingSide,
            Map<BoardPos, TroopTile> troopMap,
            TilePos leaderPosition,
            int guards) {
        this.playingSide = playingSide;
        this.troopMap = troopMap;
        this.leaderPosition = leaderPosition;
        this.guards = guards;
    }

    public Optional<TroopTile> at(TilePos pos) {
        TroopTile troopTile = troopMap.get(pos);
        if (troopTile == null) {
            return Optional.empty();
        }
        return Optional.of(troopTile);
    }

    public PlayingSide playingSide() {
        return playingSide;
    }

    public TilePos leaderPosition() {
        return leaderPosition;
    }

    public int guards() {
        return guards;
    }

    public boolean isLeaderPlaced() {
        return leaderPosition != TilePos.OFF_BOARD;
    }

    public boolean isPlacingGuards() {
        return isLeaderPlaced() && guards < 2; //todo maybe < 3
    }

    public Set<BoardPos> troopPositions() {
        Set<BoardPos> troopsPos = new HashSet<>();
        for (Map.Entry<BoardPos, TroopTile> entry : troopMap.entrySet()) {
            if (entry.getValue().hasTroop()) {
                troopsPos.add(entry.getKey());
            }
        }
        return troopsPos;
    }

    public BoardTroops placeTroop(Troop troop, BoardPos target) throws IllegalArgumentException {

        if (at(target).isPresent()) {
            throw new IllegalArgumentException();
        }

        TilePos newLeaderPosition = isLeaderPlaced() ? this.leaderPosition : target;
        int addGuard = isLeaderPlaced() && isPlacingGuards() ? 1 : 0;

        Map<BoardPos, TroopTile> newTroopMap = new HashMap<>(this.troopMap); //todo

        newTroopMap.put(target, new TroopTile(troop, this.playingSide, TroopFace.AVERS));
        return new BoardTroops(this.playingSide, newTroopMap, newLeaderPosition, guards() + addGuard);
    }

    public BoardTroops troopStep(BoardPos origin, BoardPos target) throws IllegalStateException, IllegalArgumentException {
        if (!isLeaderPlaced() || isPlacingGuards()) {
            throw new IllegalStateException();
        }

        if (at(origin).isEmpty() || at(target).isPresent()) {
            throw new IllegalArgumentException();
        }

        Map<BoardPos, TroopTile> newTroopMap = new HashMap<>(troopMap); //todo

        newTroopMap.put(target, newTroopMap.remove(origin).flipped());


        TilePos newLeaderPosition = origin.equals(leaderPosition) ? target : leaderPosition;

        return new BoardTroops(playingSide(), newTroopMap, newLeaderPosition, guards());

    }

    public BoardTroops troopFlip(BoardPos origin) {
        if (!isLeaderPlaced()) {
            throw new IllegalStateException(
                    "Cannot move troops before the leader is placed.");
        }

        if (isPlacingGuards()) {
            throw new IllegalStateException(
                    "Cannot move troops before guards are placed.");
        }

        if (at(origin).isEmpty())
            throw new IllegalArgumentException();

        Map<BoardPos, TroopTile> newTroops = new HashMap<>(troopMap);
        TroopTile tile = newTroops.remove(origin);
        newTroops.put(origin, tile.flipped());

        return new BoardTroops(playingSide(), newTroops, leaderPosition, guards);
    }

    public BoardTroops removeTroop(BoardPos target) {
        if (!isLeaderPlaced() || isPlacingGuards()) {
            throw new IllegalStateException();
        }

        if (at(target).isEmpty()) {
            throw new IllegalArgumentException();
        }

//        int remGuards = guards() == 0 ? 0 : 1;
//        int remGuards = 0;
        Map<BoardPos, TroopTile> newTroopMap = new HashMap<>(troopMap);
        newTroopMap.remove(target);
        TilePos newLeaderPosition = leaderPosition.equals(target) ? TilePos.OFF_BOARD : leaderPosition;


        return new BoardTroops(playingSide(), newTroopMap, newLeaderPosition, guards());
    }

    @Override
    public void toJSON(PrintWriter writer) {//todo commas between
        writer.printf("{\"side\":");

        playingSide.toJSON(writer);
        writer.printf(",\"leaderPosition\":");
        leaderPosition.toJSON(writer);
        writer.printf(",\"guards\":" + guards);
        //write troopMap
//        writer.printf(",\"troopMap\":{");
        writer.printf(",\"troopMap\":");

        String troopMapString = troopMap.keySet().stream().sorted(Comparator.comparing(BoardPos::toString)).map( key -> {
            return key.toJSONString() + ":" + troopMap.get(key).toJSONString();
        }).collect(Collectors.joining(",", "{", "}"));
        writer.printf(troopMapString);
//
//        troopMap.keySet().stream().sorted(Comparator.comparing(BoardPos::toString)).forEachOrdered(troop -> {
//            troop.toJSON(writer);
//            writer.printf(":");
//            troopMap.get(troop).toJSON(writer);
////            writer.printf("}");
//        });

//        writer.printf("}");


        writer.printf("}");
    }
}

package thedrake;

import java.io.PrintWriter;

public class Board implements JSONSerializable {

	private /*final*/ BoardTile[][] board;

	// Konstruktor. Vytvoří čtvercovou hrací desku zadaného rozměru, kde všechny dlaždice jsou prázdné, tedy BoardTile.EMPTY
	public Board(int dimension) {
		board = new BoardTile[dimension][dimension];
		for(int i = 0; i < dimension; i++) {
			for(int j = 0; j < dimension; j++) {
				board[i][j] = BoardTile.EMPTY;
			}
		}
	}

	// Rozměr hrací desky
	public int dimension() {
		return board.length;
	}

	// Vrací dlaždici na zvolené pozici.
	public BoardTile at(TilePos pos) {
		return board[pos.i()][pos.j()];
	}

	// Vytváří novou hrací desku s novými dlaždicemi. Všechny ostatní dlaždice zůstávají stejné
	public Board withTiles(TileAt ...ats) {
		Board newBoard =  new Board(board.length);
		for(int i = 0; i < board.length; i++) {
			newBoard.board[i] = board[i].clone();
		}

		for(TileAt tile : ats) {
			newBoard.board[tile.pos.i()][tile.pos.j()] = tile.tile;
		}

		return newBoard;
	}

	// Vytvoří instanci PositionFactory pro výrobu pozic na tomto hracím plánu
	public PositionFactory positionFactory() {
		return new PositionFactory(board.length);
		// Místo pro váš kód
	}

	@Override
	public void toJSON(PrintWriter writer) {//todo fix commas
//		"{\"dimension\":4,\"tiles\":[\"empty\",\"empty\",\"empty\",\"empty\",\"empty\",\"mountain\",\"empty\",\"empty\",\"empty\",\"empty\",\"empty\",\"mountain\",\"empty\",\"empty\",\"empty\",\"empty\"]}"+;

		writer.printf("{\"dimension\":" + dimension());
		writer.printf(",\"tiles\":[");
//		Arrays.stream(board).forEachOrdered(row -> Arrays.stream(row).forEachOrdered({tile -> tile.}));
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				board[j][i].toJSON(writer);
				if(i !=  board.length - 1 || j != board[i].length - 1) {
					writer.printf(",");;
				}
			}
		}
//		Arrays.stream(board).flatMap(Arrays::stream).sorted().forEachOrdered(tile -> {
//			tile.toJSON(writer);
//			writer.printf(",");});

		writer.printf("]");
		writer.printf("}");

	}

	public static class TileAt {
		public final BoardPos pos;
		public final BoardTile tile;
		
		public TileAt(BoardPos pos, BoardTile tile) {
			this.pos = pos;
			this.tile = tile;
		}
	}
}


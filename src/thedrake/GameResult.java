package thedrake;

import java.io.PrintWriter;

public enum GameResult  implements JSONSerializable{
	VICTORY, DRAW, IN_PLAY, NONE;

	public static GameResult gameState = NONE;
	public static boolean gameStateChanged = false;
	public static String gameEndMessage = "";

	@Override
	public void toJSON(PrintWriter writer) {
		writer.printf("\"" + this.toString() + "\"");
	}
}

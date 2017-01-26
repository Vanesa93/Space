package bg.fdiba.vgeorgieva;

public class Messages {
	// all messages for the game are here
	private static String message;	

	public static String getPause() {
		message = "Pause";
		return message;
	}

	public static String getResume() {
		message = "Press R to resume";
		return message;
	}

	public static String getPauseWithKey() {
		message = "Pause P";
		return message;
	}

	public static String getRestartWithK() {
		message = "Restart N";
		return message;
	}

	public static String getExitWithKey() {
		message = "Exit Esc";
		return message;
	}

	public static String getCurrentLevel() {
		message = "Current level %d";
		return message;
	}

	public static String getCollectTreasures() {
		message = "Collect %d Treasures";
		return message;
	}

	public static String getTreasuresCollected() {
		message = "Treasures collected %d/%d";
		return message;
	}

	public static String getScore() {
		message = "Score %d";
		return message;
	}

	public static String getGameOver() {
		message = "Game Over";
		return message;
	}

	public static String getRecord() {
		message = "Record %d";
		return message;
	}

	public static String getNewRecord() {
		message = "New Record %d";
		return message;
	}
	}

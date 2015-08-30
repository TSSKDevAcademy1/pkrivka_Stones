import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.util.Formatter;

import objects.Field;
import objects.NumberTile;
import objects.Tile;

enum Option {
	NEW_GAME, LOAD_GAME, HALL_OF_FAME, EXIT
};

public class ConsoleUI {
	private Field field;
	private BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
	public Tile[][] tiles;
	private long startPlayingTime;
	private long playingTime;
	private long loadedPlayingTime;
	private static final String SAVE_FILE = "field.bin";
	private String playerName;
	private int rows;
	private int columns;
	private ListOfBestTimes listOfBestTimes;
	private boolean checkLoad=false;

	/**
	 * Main menu.
	 */
	public void newGameStarted() {
		switch (showMenu()) {
		case NEW_GAME:
			System.out.println("Please, insert your name:");
			String name = readLine();
			playerName = name;
			System.out.println("Please, insert the number of rows:");
			rows = Integer.parseInt(readLine());
			System.out.println("Please, insert the number of columns:");
			columns = Integer.parseInt(readLine());
			newGame();
		case LOAD_GAME:
			this.field = loadGame();
			do {
				update();
				processInput();
				checkLoad=false;
			} while (true);
		case HALL_OF_FAME:
			printHallOfFame();
			System.out.println("r/return - return to menu");
			do {
				String input = readLine().toLowerCase().trim();
				if ("r".equals(input) || "return".equals(input)) {
					newGameStarted();
				} else {
					try {
						throw new WrongOptionException("Wrong option!");
					} catch (WrongOptionException e) {
						System.out.println(e.getMessage());
					}
				}

			} while (true);
		case EXIT:
			System.exit(0);
		}
	}

	/**
	 * Start new game with inserted rows and columns.
	 */
	private void newGame() {
		checkLoad=false;
		loadedPlayingTime=0;
		this.field = new Field(rows, columns);
		System.out.printf("%s",
				"w/up - move up\ns/down - move down\na/left - move left\nd/right - move right\nu/save - save game\nr/return - return to menu\nGood luck!\n");
		startPlayingTime = System.currentTimeMillis() / 1000;
		do {
			update();
			processInput();
		} while (true);
	}

	/**
	 * Update playing field.
	 */
	public void update() {
		int rowCount = field.getRowCount();
		int colCount = field.getColumnCount();
		StringBuilder sb = new StringBuilder();
		Formatter formatter = new Formatter(sb);
		for (int row = 0; row < rowCount; row++) {
			for (int column = 0; column < colCount; column++) {
				formatter.format("%3s", field.getTile(row, column));
			}
			formatter.format("%s", "\n");
		}
		System.out.print(sb);
		formatter.close();
		if (checkLoad==false){
			playingTime = System.currentTimeMillis() / 1000 - startPlayingTime+loadedPlayingTime;
			System.out.println("Your playing time is: "+playingTime+"s");
		}
		else {
			startPlayingTime=System.currentTimeMillis() / 1000;
			System.out.println("Your playing time is: "+loadedPlayingTime+"s");
		}
		
		int number = 1;
		for (int row = 0; row < rowCount; row++) {
			for (int column = 0; column < colCount; column++) {
				NumberTile numbertile = (NumberTile) field.getTile(row, column);
				if (field.getTile(row, column).getState() == Tile.State.NUMBER && numbertile.getValue() == number) {
					number++;
				}
				if (number == rowCount * colCount
						&& field.getTile(rowCount - 1, colCount - 1).getState() == Tile.State.OPEN) {
					playingTime = System.currentTimeMillis() / 1000 - startPlayingTime;
					System.out.println("Winner!");
					System.out.println("Your playing time was: " + playingTime + "s");
					DatabaseSettings.addTimeToHallOfFame(playerName, playingTime);
					System.out.println("n/new - play again\nr/return - return to menu\nx/exit - exit");
					do {
						String input = readLine();
						switch (input) {
						case "n":
						case "new":
							newGame();
						case "r":
						case "return":
							newGameStarted();
						case "x":
						case "exit":
							System.out.println("Thank you for your game. Goodbye!");
							System.exit(0);
						default:
							try {
								throw new WrongOptionException("Wrong option!");
							} catch (WrongOptionException e) {
								System.out.println(e.getMessage());
							}
						}
					} while (true);
				}
			}
		}
	}

	/**
	 * Load and print Hall of Fame from database.
	 */
	public void printHallOfFame() {
		listOfBestTimes = DatabaseSettings.loadHallOfFame();
		if (listOfBestTimes.getSize() > 0) {
			StringBuilder sb = new StringBuilder();
			Formatter f = new Formatter(sb);
			int lengthOfName = 0;
			for (int i = 0; i < listOfBestTimes.getSize(); i++) {
				int count = listOfBestTimes.getPlayerTime(i).getName().length();
				if (count > lengthOfName) {
					lengthOfName = count;
				}
			}
			drawLine(f, lengthOfName);
			f.format("%s %" + (lengthOfName) + "s %s%n", "| Name:", " ", "Time: |");
			drawLine(f, lengthOfName);
			for (int i = 0; i < listOfBestTimes.getSize(); i++) {
				f.format("%s", "| " + listOfBestTimes.getPlayerTime(i).getName());
				for (int j = 0; j < lengthOfName - listOfBestTimes.getPlayerTime(i).getName().length() + 1; j++) {
					f.format("%s", " ");
				}
				f.format("%13s%n", "" + listOfBestTimes.getPlayerTime(i).getTime() + "s |");
			}
			drawLine(f, lengthOfName);
			System.out.println(sb);
			f.close();
		}
	}

	/**
	 * Draw single line
	 * 
	 * @param f
	 *            formatter
	 * @param lengthOfName
	 *            length of the longest name
	 */
	private static void drawLine(Formatter f, int lengthOfName) {
		f.format("%s", " ");
		for (int i = 0; i < lengthOfName + 14; i++) {
			f.format("%s", "-");
		}
		f.format("%s%n", "");
	}

	/**
	 * Load previous saved game from file.
	 * 
	 * @return return previous game(its field)
	 */
	public Field loadGame() {
		checkLoad=true;
		try (ObjectInputStream os = new ObjectInputStream(new FileInputStream(SAVE_FILE))) {
			Field field = (Field) os.readObject();
			loadedPlayingTime=(Long) os.readObject();
			playerName=(String) os.readObject();
			System.out.println("Game succesfully loaded!");
			return field;
		} catch (Exception e) {
			System.err.println("Error occured while loading game: ");
			e.printStackTrace();
			newGameStarted();
		}
		return null;
	}

	/**
	 * Show main menu.
	 * 
	 * @return return option
	 */
	private Option showMenu() {
		System.out.println("Menu.");
		for (Option option : Option.values()) {
			System.out.printf("%d. %s%n", option.ordinal() + 1, option);
		}
		System.out.println("------------------");

		int selection = -1;
		do {
			System.out.println("Option: ");
			selection = Integer.parseInt(readLine());
		} while (selection <= 0 || selection > Option.values().length);
		return Option.values()[selection - 1];
	}

	/**
	 * Read user input.
	 * 
	 * @return return user input
	 */
	private String readLine() {
		try {
			return input.readLine();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Swap neighboring tiles based on user input.
	 * 
	 * @param input
	 *            user input
	 * @throws WrongOptionException
	 *             throws exception in case of wrong option
	 */
	public void swapTile(String input) throws WrongOptionException {
		switch (input.toLowerCase().trim()) {
		case "w":
		case "up":
			field.move(1, 0);
			break;
		case "s":
		case "down":
			field.move(-1, 0);
			break;
		case "d":
		case "right":
			field.move(0, -1);
			break;
		case "a":
		case "left":
			field.move(0, 1);
			break;
		case "r":
		case "return":
			newGameStarted();
			break;
		case "u":
		case "save":
			playingTime = System.currentTimeMillis() / 1000 - startPlayingTime;
			field.saveField(playingTime,playerName);
			break;
		default:
			throw new WrongOptionException("Wrong option!");
		}
	}
	
	/**
	 * Read user input and call swapTile method.
	 */
	private void processInput() {
		String input = readLine();
		try {
			swapTile(input);
		} catch (Exception ex) {
			if (ex instanceof ArrayIndexOutOfBoundsException) {
				System.out.println("Error, out of array!");
			} else
				System.out.println(ex.getMessage());
		}
	}
}

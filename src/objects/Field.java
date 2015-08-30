package objects;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Random;

public class Field implements Serializable {
	private static final long serialVersionUID = 1L;
	public int rowCount;
	public int columnCount;
	public int number;
	public Tile[][] tiles;
	private static final String SAVE_FILE = "field.bin";
	private Position emptyposition;

	/**
	 * Constructor
	 * 
	 * @param rowCount
	 *            number of rows
	 * @param columnCount
	 *            number of columns
	 */
	public Field(int rowCount, int columnCount) {
		this.rowCount = rowCount;
		this.columnCount = columnCount;
		tiles = new Tile[rowCount][columnCount];
		generate();
	}

	/**
	 * Generate playing field.
	 */
	public void generate() {
		Random ran = new Random();
		int number = 1;
		for (int row = 0; row < rowCount; row++) {
			for (int col = 0; col < columnCount; col++) {
				if (tiles[row][col] == null) {
					tiles[row][col] = new NumberTile(number);
					tiles[row][col].setState(Tile.State.NUMBER);
					number++;
				}
			}
		}
		tiles[rowCount - 1][columnCount - 1] = new OpenTile();
		tiles[rowCount - 1][columnCount - 1].setState(Tile.State.OPEN);
		emptyposition = new Position(rowCount - 1, columnCount - 1);
		int previousMovement = 5;
		for (int i = 0; i < 100; i++) {
			int movement = ran.nextInt(4);
			switch (movement) {
			case 0:
				if (emptyposition.getRow() < rowCount - 1 && previousMovement != 1) {
					move(1, 0);
				} else
					i--;
				break;
			case 1:
				if (emptyposition.getRow() > 0 && previousMovement != 0) {
					move(-1, 0);
				} else
					i--;
				break;
			case 2:
				if (emptyposition.getColumn() > 0 && previousMovement != 3) {
					move(0, -1);
				} else
					i--;
				break;
			case 3:
				if (emptyposition.getColumn() < columnCount - 1 && previousMovement != 2) {
					move(0, 1);
				} else
					i--;
				break;
			}
			previousMovement = movement;
		}
	}

	/**
	 * Find position of the open tile.
	 * 
	 * @return return position
	 */
	public Position check() {
		for (int row = 0; row < rowCount; row++) {
			for (int col = 0; col < columnCount; col++) {
				if (tiles[row][col].getState() == Tile.State.OPEN) {
					emptyposition = new Position(row, col);
					return emptyposition;
				}
			}
		}
		return null;
	}

	/**
	 * Move tiles in four directions.
	 * 
	 * @param rowOffset
	 *            offset of the row
	 * @param columnOffset
	 *            offset of the column
	 */
	public void move(int rowOffset, int columnOffset) {
		Position position = check();
		NumberTile value_tile = (NumberTile) tiles[position.getRow() + rowOffset][position.getColumn() + columnOffset];
		int value = value_tile.getValue();
		tiles[position.getRow()][position.getColumn()] = new NumberTile(value);
		tiles[position.getRow()][position.getColumn()].setState(Tile.State.NUMBER);
		tiles[position.getRow() + rowOffset][position.getColumn() + columnOffset].setState(Tile.State.OPEN);
		Position newEmptyPosition = new Position(position.getRow() + rowOffset, position.getColumn() + columnOffset);
		emptyposition = newEmptyPosition;
	}

	/**
	 * Save game(object Field) to the file.
	 * 
	 * @param playingTime
	 *            players playing time
	 * @param playerName
	 *            players name
	 */
	public void saveField(long playingTime, String playerName) {
		try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(SAVE_FILE))) {
			os.writeObject(this);
			os.writeObject(playingTime);
			os.writeObject(playerName);
			System.out.println("Game succesfully saved!");
		} catch (Exception e) {
			System.err.println("Error occured while saving game: ");
			e.printStackTrace();
		}
	}

	/**
	 * Get tile on the defined position.
	 * 
	 * @param row
	 * @param column
	 * @return return Tile
	 */
	public Tile getTile(int row, int column) {
		return tiles[row][column];
	}

	/**
	 * Get number of rows
	 * 
	 * @return return number of rows.
	 */
	public int getRowCount() {
		return rowCount;
	}

	/**
	 * Set number of rows.
	 * 
	 * @param rowCount
	 *            number of rows to set
	 */
	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
	}

	/**
	 * Get number of columns.
	 * 
	 * @return return number of columns
	 */
	public int getColumnCount() {
		return columnCount;
	}

	/**
	 * Set number of columns.
	 * 
	 * @param columnCount
	 *            number of columns to set
	 */
	public void setColumnCount(int columnCount) {
		this.columnCount = columnCount;
	}

}

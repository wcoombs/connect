
//------------------------------------------------------
//
// CLASS: BoardArray
//
// Author: William Coombs, 6852347
//
// REMARKS: The purpose of this class is to create and
//          manage a 2D array that stores the game
//          board. One board is created by each of the
//          classes that implement the ConnectController
//          and ConnectPlayer interfaces.
//
//------------------------------------------------------

public class BoardArray
{
	private Status[][] board;
	private final int WIDTH = 7;
	private final int HEIGHT = 6;
	private final int objective = 4;

	// generic constructor
	public BoardArray()
	{
		this.board = new Status[HEIGHT][WIDTH];
		clearBoard();
	}

	// ------------------------------------------------------
	//
	// clearBoard
	//
	// PURPOSE: The purpose of this method is to set the game
	// board as empty (i.e., where each cell in the
	// board is set to Status.NEITHER (a '-' mark)).
	//
	// PARAMETERS: None.
	//
	// RETURNS: None.
	//
	// ------------------------------------------------------
	private void clearBoard()
	{
		for (int row = 0; row < this.HEIGHT; row++)
		{
			for (int col = 0; col < this.WIDTH; col++)
			{
				this.board[row][col] = Status.NEITHER;
			}
		}
	}

	// various getters for the class
	public Status[][] getBoard()
	{
		return this.board;
	}

	public int getWidth()
	{
		return this.WIDTH;
	}

	public int getHeight()
	{
		return this.HEIGHT;
	}

	public int getObjective()
	{
		return this.objective;
	}

	public Status getCell(int row, int col)
	{
		return this.board[row][col];
	}

	// a setter for the class for a single cell
	public void setCell(int row, int col, Status token)
	{
		this.board[row][col] = token;
	}
}

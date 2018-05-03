
//------------------------------------------------------
//
// CLASS: DifficultyOne
//
// Author: William Coombs, 6852347
//
// REMARKS: The purpose of this class is to create and
//          manage a simple AI.
//
//------------------------------------------------------

public class DifficultyOne extends GameAI
{
	private int chosenColumn; // the latest column chosen by the human player

	// generic constructor
	public DifficultyOne()
	{
		this.chosenColumn = 0; // the latest column is initialized to 0
	}

	// ------------------------------------------------------
	//
	// makeMove
	//
	// PURPOSE: The purpose of this method is to let the AI
	// select which column it would like to play.
	//
	// PARAMETERS:
	// - lastCol: the column that was last selected by the
	// player.
	//
	// RETURNS: chosenColumn, an int that tells the calling
	// location which column was selected by the AI.
	//
	// ------------------------------------------------------
	public int makeMove(int lastCol)
	{
		// due to the number of rows, this move will always be valid
		// there is no need to check if this.board[0][lastCol] == Status.NEITHER
		this.chosenColumn = lastCol;

		return this.chosenColumn;
	}
}

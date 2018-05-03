
//------------------------------------------------------
//
// CLASS: GameLogic
//
// Author: William Coombs, 6852347
//
// REMARKS: The purpose of this class is to manage the
//          logic of the game itself, such as the
//          gravity, when the game is over, and adding
//          player pieces to the game board.
//
//------------------------------------------------------

public class GameLogic implements ConnectController
{
	private GameDisplay gameDisplay; // a pointer to the program's GameDisplay class,
	private GameAI gameAI;
	private BoardArray board; // this class's version of the game board, kept identical to the GameAI class's
	private final int difficulties = 2; // the total number of AI difficulties that are available to select
	private int chosenDifficulty;

	// generic constructor
	public GameLogic(GameDisplay gd)
	{
		this.gameDisplay = gd;
	}

	public void gameOver(Status PlayerNumber)
	{
		this.gameDisplay.gameOver(PlayerNumber);
	}

	private int promptForOpponentDifficulty(int maxDifficulty)
	{
		return this.gameDisplay.promptForOpponentDifficulty(maxDifficulty);
	}

	// ------------------------------------------------------
	//
	// addPiece
	//
	// PURPOSE: The purpose of this method is to attempt to
	// add the player's piece to the game board, barring if
	// the selected column is valid or not. If not, false is
	// returned to the calling location. If true, then the
	// method will update this class's version of the game
	// board, then check if as a result of the added piece
	// the game is over (according to the logic of other
	// methods within this class). If the game is over, this
	// method will display the updated board to the
	// GameDisplay then prompt the GUI's gameOver method.
	// Otherwise, this method will then prompt the selected
	// game AI to make its move. This method will then update
	// its game board based on the AI's chosen column, then
	// update the GameDisplay, then check if the game is over
	// as a result of the AI's move. Then, finally, true
	// is returned to the calling location.
	//
	// PARAMETERS:
	// - col: the column that was selected by the player.
	//
	// RETURNS: a boolean, telling the calling location if
	// the requested piece was added to the board or not.
	//
	// ------------------------------------------------------
	public boolean addPiece(int col)
	{
		boolean result = false;

		if (this.board.getCell(0, col) == Status.NEITHER) // check if the desired column has an empty space at the top
		{
			// set the cell at the top of the board at the desired column
			// as now occupied by the player
			this.board.setCell(0, col, Status.ONE);
			result = true;
		}

		if (result)
		{
			updateForNewToken(col); // shift the added piece down the board (as a result of gravity)

			Status gameOverToken = checkGameOver();

			if (gameOverToken != null)
			{
				this.gameDisplay.updateBoard(this.board.getBoard());
				gameOver(gameOverToken);
			}
			else
			{
				int aiMove = this.gameAI.makeMove(col);

				// update this class's board to reflect the AI's chosen column
				this.board.setCell(0, aiMove, Status.TWO);
				updateForNewToken(aiMove); // shift the added token down the board (as a result of gravity)
				this.gameDisplay.updateBoard(this.board.getBoard());

				gameOverToken = checkGameOver();

				if (gameOverToken != null)
				{
					gameOver(gameOverToken);
				}
			}
		}

		return result;
	}

	// ------------------------------------------------------
	//
	// checkGameOver
	//
	// PURPOSE: The purpose of this method is to determine
	// whether or not the game board has four of the same
	// token (status) in a row, either vertically,
	// horizontally, or diagonally, or if the board is full
	// of tokens but there is not four in a row, which
	// indicates a draw.
	//
	// PARAMETERS: None.
	//
	// RETURNS: Null if the game is not over, otherwise the
	// status indicating which player has won, or
	// Status.NEITHER, indicating a draw.
	//
	// ------------------------------------------------------
	private Status checkGameOver()
	{
		Status token = checkVerticals();

		if (token == null)
		{
			token = checkHorizontals();

			if (token == null)
			{
				token = checkDiagonals();

				if (token == null)
				{
					token = checkFull();
				}
			}
		}

		return token;
	}

	// ------------------------------------------------------
	//
	// reset
	//
	// PURPOSE: The purpose of this method is to create a new
	// game by creating a brand new board then prompting the
	// player to select an AI difficulty. A new AI of the
	// selected difficulty is then created.
	//
	// PARAMETERS: None.
	//
	// RETURNS: None.
	//
	// ------------------------------------------------------
	public void reset()
	{
		this.board = new BoardArray();
		this.chosenDifficulty = promptForOpponentDifficulty(this.difficulties);

		if (this.chosenDifficulty == 1)
		{
			this.gameAI = new DifficultyOne();
		}
		else if (this.chosenDifficulty == 2)
		{
			this.gameAI = new DifficultyTwo();
		}
	}

	// ------------------------------------------------------
	//
	// updateForNewToken
	//
	// PURPOSE: The purpose of this method is to shift all
	// tokens down the board as far as possible (due to
	// gravity). The only token that will be shifted down is
	// the most recently added token in the given column. As
	// this method is called whenever a new token is added,
	// the board will always be in a valid state by the end
	// of the method, despite adjusting a single column.
	//
	// PARAMETERS:
	// - col: the given column where the most recent token
	// was added to.
	//
	// RETURNS: None.
	//
	// ------------------------------------------------------
	private void updateForNewToken(int col)
	{
		for (int row = 0; row < this.board.getHeight(); row++)
		{
			if (this.board.getCell(row, col) != Status.NEITHER)
			{
				Status token = this.board.getCell(row, col);

				// check that we are at least in the second-last row
				if (row < this.board.getHeight() - 1)
				{
					// check the row directly underneath the current one for an empty space.
					// this will never check the very last row of the board, since we are
					// stopping at the second-last row in the previous if-check
					if (this.board.getCell(row + 1, col) == Status.NEITHER)
					{
						this.board.setCell(row, col, Status.NEITHER); // set the current row and column's cell as empty

						// set the next row and current column's cell as the previously found token
						this.board.setCell(row + 1, col, token);
					}
				}
			}
		}
	}

	// ------------------------------------------------------
	//
	// checkVerticals
	//
	// PURPOSE: The purpose of this method is to check if
	// there are four of the same token in a row on a
	// vertical line. Since we are moving down from the
	// top of the board and moving left to right, we need
	// only check if there are four in a line directly below
	// the currently found token, rather than needing to
	// check both above and below on the vertical.
	//
	// PARAMETERS: None.
	//
	// RETURNS: a status, indicating which token was found,
	// or null, indicating that four in a row was not found.
	//
	// ------------------------------------------------------
	private Status checkVerticals()
	{
		Status token = null;

		for (int row = 0; row < this.board.getHeight() && token == null; row++)
		{
			for (int col = 0; col < this.board.getWidth() && token == null; col++)
			{
				if (this.board.getCell(row, col) != Status.NEITHER)
				{
					// a token has been found, therefore, we need only check
					// the next three rows down to see if there are four in a line
					Status currToken = this.board.getCell(row, col);

					// check if the current row allows for enough space
					// for there to be the potential of four in a line
					if ((row + this.board.getObjective() - 1) < this.board.getHeight())
					{
						// check the tokens at the cells three spaces below the current row
						if ((this.board.getCell(row + 1, col) == currToken)
								&& (this.board.getCell(row + 2, col) == currToken)
								&& (this.board.getCell(row + 3, col) == currToken))
						{
							token = currToken;
						}
					}
				}
			}
		}

		return token;
	}

	// ------------------------------------------------------
	//
	// checkHorizontals
	//
	// PURPOSE: The purpose of this method is to check if
	// there are four of the same token in a row on a
	// horizontal line. Since we are moving down from the
	// top of the board and moving left to right, we need
	// only check if there are four in a line to the right
	// of the currently found token, rather than needing to
	// check both left and right on the horizontal.
	//
	// PARAMETERS: None.
	//
	// RETURNS: a status, indicating which token was found,
	// or null, indicating that four in a row was not found.
	//
	// ------------------------------------------------------
	private Status checkHorizontals()
	{
		Status token = null;

		for (int row = 0; row < this.board.getHeight() && token == null; row++)
		{
			for (int col = 0; col < this.board.getWidth() && token == null; col++)
			{
				if (this.board.getCell(row, col) != Status.NEITHER)
				{
					// a token has been found, therefore, we need only check
					// the next three columns to the right to see if there are four in a line
					Status currToken = this.board.getCell(row, col);

					// check if the current column allows for enough space
					// for there to be the potential of four in a line
					if ((col + this.board.getObjective() - 1) < this.board.getWidth())
					{
						// check the tokens at the cells three spaces directly to the right of the current column
						if ((this.board.getCell(row, col + 1) == currToken)
								&& (this.board.getCell(row, col + 2) == currToken)
								&& (this.board.getCell(row, col + 3) == currToken))
						{
							token = currToken;
						}
					}
				}
			}
		}

		return token;
	}

	// ------------------------------------------------------
	//
	// checkDiagonals
	//
	// PURPOSE: The purpose of this method is to check if
	// there are four of the same token in a row on a
	// diagonal line, both going left and right.
	//
	// PARAMETERS: None.
	//
	// RETURNS: a status, indicating which token was found,
	// or null, indicating that four in a row was not found.
	//
	// ------------------------------------------------------
	private Status checkDiagonals()
	{
		Status token = checkRightDiagonal();

		if (token == null)
		{
			token = checkLeftDiagonal();
		}

		return token;
	}

	// ------------------------------------------------------
	//
	// checkRightDiagonal
	//
	// PURPOSE: The purpose of this method is to check if
	// there are four of the same token in a row on a right
	// diagonal line. Since we are moving down from the top
	// of the board and moving left to right, we need only
	// check if there are four in a line to the right and
	// down of the currently found token, rather than needing
	// to check both up and left or right and down on the
	// diagonal.
	//
	// PARAMETERS: None.
	//
	// RETURNS: a status, indicating which token was found,
	// or null, indicating that four in a row was not found.
	//
	// ------------------------------------------------------
	private Status checkRightDiagonal()
	{
		Status token = null;

		for (int row = 0; row < this.board.getHeight() && token == null; row++)
		{
			for (int col = 0; col < this.board.getWidth() && token == null; col++)
			{
				if (this.board.getCell(row, col) != Status.NEITHER)
				{
					// a token has been found, therefore, we need only check
					// the next three rows and columns down and right, respectively,
					// to see if there are four in a line
					Status currToken = this.board.getCell(row, col);

					// check if the current row and column allows for enough space
					// for there to be the potential of four in a line
					if ((row + this.board.getObjective() - 1) < this.board.getHeight()
							&& (col + this.board.getObjective() - 1) < this.board.getWidth())
					{
						// check the tokens at the cells three spaces down and right,
						// diagonally, of the current row and column
						if ((this.board.getCell(row + 1, col + 1) == currToken)
								&& (this.board.getCell(row + 2, col + 2) == currToken)
								&& (this.board.getCell(row + 3, col + 3) == currToken))
						{
							token = currToken;
						}
					}
				}
			}
		}

		return token;
	}

	// ------------------------------------------------------
	//
	// checkLeftDiagonal
	//
	// PURPOSE: The purpose of this method is to check if
	// there are four of the same token in a row on a left
	// diagonal line. Since we are moving down from the top
	// of the board and moving left to right, we need only
	// check if there are four in a line to the left and
	// down of the currently found token, rather than needing
	// to check both up and right or left and down on the
	// diagonal.
	//
	// PARAMETERS: None.
	//
	// RETURNS: a status, indicating which token was found,
	// or null, indicating that four in a row was not found.
	//
	// ------------------------------------------------------
	private Status checkLeftDiagonal()
	{
		Status token = null;

		for (int row = 0; row < this.board.getHeight() && token == null; row++)
		{
			for (int col = 0; col < this.board.getWidth() && token == null; col++)
			{
				if (this.board.getCell(row, col) != Status.NEITHER)
				{
					// a token has been found, therefore, we need only check
					// the next three rows and columns down and left, respectively,
					// to see if there are four in a line
					Status currToken = this.board.getCell(row, col);

					// check if the current row and column allows for enough space
					// for there to be the potential of four in a line
					if ((row + this.board.getObjective() - 1) < this.board.getHeight()
							&& (col - (this.board.getObjective() - 1)) >= 0)
					{
						// check the tokens at the cells three spaces down and left,
						// diagonally, of the current row and column
						if ((this.board.getCell(row + 1, col - 1) == currToken)
								&& (this.board.getCell(row + 2, col - 2) == currToken)
								&& (this.board.getCell(row + 3, col - 3) == currToken))
						{
							token = currToken;
						}
					}
				}
			}
		}

		return token;
	}

	// ------------------------------------------------------
	//
	// checkFull
	//
	// PURPOSE: The purpose of this method is to check if the
	// board is full or not.
	//
	// PARAMETERS: None.
	//
	// RETURNS: a status, Neither, if the board is full, or
	// null, if the board has at least one empty space.
	//
	// ------------------------------------------------------
	private Status checkFull()
	{
		Status token = Status.NEITHER; // assume an empty board

		for (int col = 0; col < this.board.getWidth() && token != null; col++)
		{
			if (this.board.getCell(0, col) == Status.NEITHER)
			{
				token = null;
			}
		}

		return token;
	}
}


//------------------------------------------------------
//
// CLASS: DifficultyTwo
//
// Author: William Coombs, 6852347
//
// REMARKS: The purpose of this class is to create and
//          manage a more sophisticated AI than the
//          DifficultyOne AI.
//
//------------------------------------------------------

import java.util.Random;

public class DifficultyTwo extends GameAI
{
	private BoardArray board; // this class's version of the game board, kept identical to the GameLogic class's
	private final int defaultChosenColumn = -1; // an invalid location to place a token (i.e., outside the game board)

	// generic constructor
	public DifficultyTwo()
	{
		this.board = new BoardArray();
	}

	// ------------------------------------------------------
	//
	// makeMove
	//
	// PURPOSE: The purpose of this method is to let the AI
	// decide (semi-intelligently) which column to play its
	// token. This method will first update the class's
	// game board to reflect the player's most recent play.
	// Then, the method will check the vertical, horizontals,
	// and diagonals to see if there are three of its own
	// tokens in a line, and if so, play it's token (if
	// applicable) in such a way as to make four in a line
	// and win the game. If this is not possible, the method
	// will then check to see if it can block the player from
	// having four tokens in a line in the same manner as
	// before. If this is not possible, then the method will
	// simply choose a random column to play its token. The
	// dimensions of the game board are in such a way that
	// the AI will always be able to make a valid move (i.e.,
	// the top row in some column is empty).
	//
	// PARAMETERS:
	// - lastCol: the column that was last selected by the
	// player.
	//
	// RETURNS: chosenColumn, an int that tells the calling
	// location which column was selected by the method.
	//
	// ------------------------------------------------------
	public int makeMove(int lastCol)
	{
		// add the player's most recent token to this class's version of
		// the game board, in order to keep it synchronized with the
		// GameLogic class's board, then update the board
		this.board.setCell(0, lastCol, Status.ONE);
		updateForNewToken(lastCol);

		int chosenColumn = findWinningColumn();

		if (chosenColumn == defaultChosenColumn)
		{
			chosenColumn = blockPlayer();

			if (chosenColumn == defaultChosenColumn)
			{
				chosenColumn = chooseRandomColumn();
			}
		}

		// the chosenColumn will never be -1 at this point, since the chooseRandomColumn
		// method will always return a valid column. as such, update this
		// class's version of the game board to reflect the chosen column
		this.board.setCell(0, chosenColumn, Status.TWO);
		updateForNewToken(chosenColumn);

		return chosenColumn;
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
		// this AI difficulty level needs to update it's own board when a player makes a move
		// and the column the player chose gets passed to this class, as well as when
		// the AI makes its own move. As such, this block of code is identical to the
		// GameLogic class's updateForNewToken() method. This seems like redundant
		// duplication of code, but it prevents this class from needing access to the
		// GameLogic class itself, and as such promotes containment
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
	// findWinningColumn
	//
	// PURPOSE: The purpose of this method is to attempt to
	// find a column to play the AI's token such that when
	// the token is placed and gravity shifts the token down
	// as far as it can, the token will end up in a spot such
	// that there will be four of the AI's tokens in a line.
	//
	// PARAMETERS: None.
	//
	// RETURNS: chosenColumn, an int, telling the calling
	// location the chosen column to play the AI's token.
	//
	// ------------------------------------------------------
	private int findWinningColumn()
	{
		int chosenColumn = checkVertical(Status.TWO);

		if (chosenColumn == defaultChosenColumn)
		{
			chosenColumn = checkHorizontals(Status.TWO);

			if (chosenColumn == defaultChosenColumn)
			{
				chosenColumn = checkDiagonals(Status.TWO);
			}
		}

		return chosenColumn;
	}

	// ------------------------------------------------------
	//
	// blockPlayer
	//
	// PURPOSE: The purpose of this method is to attempt to
	// find a column to play the AI's token such that when
	// the token is placed and gravity shifts the token down
	// as far as it can, the token will end up in a spot such
	// that it will prevent the player from winning the game.
	//
	// PARAMETERS: None.
	//
	// RETURNS: chosenColumn, an int, telling the calling
	// location the chosen column to play the AI's token.
	//
	// ------------------------------------------------------
	private int blockPlayer()
	{
		int chosenColumn = checkVertical(Status.ONE);

		if (chosenColumn == defaultChosenColumn)
		{
			chosenColumn = checkHorizontals(Status.ONE);

			if (chosenColumn == defaultChosenColumn)
			{
				chosenColumn = checkDiagonals(Status.ONE);
			}
		}

		return chosenColumn;
	}

	// ------------------------------------------------------
	//
	// chooseRandomColumn
	//
	// PURPOSE: The purpose of this method is to select a
	// random column to play the AI's token.
	//
	// PARAMETERS: None.
	//
	// RETURNS: chosenColumn, an int, telling the calling
	// location the chosen column to play the AI's token.
	//
	// ------------------------------------------------------
	private int chooseRandomColumn()
	{
		int chosenColumn = defaultChosenColumn;
		Random generator = new Random();
		boolean validColumn = false;

		while (!validColumn)
		{
			chosenColumn = generator.nextInt(this.board.getHeight() + 1);

			// check if this class's version of the board has an empty spot
			// at the top row and selected column. if so, the column
			// is valid and the method no longer needs to keep looping trying
			// to find a valid column
			if (this.board.getCell(0, chosenColumn) == Status.NEITHER)
			{
				validColumn = true;
			}
		}

		return chosenColumn;
	}

	// ------------------------------------------------------
	//
	// checkVertical
	//
	// PURPOSE: The purpose of this method is to try to find
	// three tokens in a line vertically that are of the
	// givenToken's status.
	//
	// PARAMETERS:
	// givenToken - the token (status) that the method is
	// trying to find three in a line of.
	//
	// RETURNS: chosenColumn, an int, telling the calling
	// location the chosen column to play the AI's token.
	//
	// ------------------------------------------------------
	private int checkVertical(Status givenToken)
	{
		int chosenColumn = defaultChosenColumn;

		for (int row = 0; row < this.board.getHeight() && chosenColumn == defaultChosenColumn; row++)
		{
			for (int col = 0; col < this.board.getWidth() && chosenColumn == defaultChosenColumn; col++)
			{
				if (this.board.getCell(row, col) == givenToken)
				{
					if ((row + 2) < this.board.getHeight())
					{
						if ((this.board.getCell(row + 1, col) == givenToken)
								&& (this.board.getCell(row + 2, col) == givenToken))
						{
							// three of the same token have now been found in a line
							// attempt to block/win at this row, if applicable
							if (row - 1 >= 0)
							{
								// there is enough space to place the given token, check if the space is empty
								if (this.board.getCell(row - 1, col) == Status.NEITHER)
								{
									chosenColumn = col;
								}
							}
						}
					}
				}
			}
		}

		return chosenColumn;
	}

	// ------------------------------------------------------
	//
	// checkHorizontals
	//
	// PURPOSE: The purpose of this method is to try to find
	// three tokens in a line horizontally that are of the
	// givenToken's status.
	//
	// PARAMETERS:
	// givenToken - the token (status) that the method is
	// trying to find three in a line of.
	//
	// RETURNS: chosenColumn, an int, telling the calling
	// location the chosen column to play the AI's token.
	//
	// ------------------------------------------------------
	private int checkHorizontals(Status givenToken)
	{
		int chosenColumn = defaultChosenColumn;

		for (int row = 0; row < this.board.getHeight() && chosenColumn == defaultChosenColumn; row++)
		{
			for (int col = 0; col < this.board.getWidth() && chosenColumn == defaultChosenColumn; col++)
			{
				if (this.board.getCell(row, col) == givenToken)
				{
					if (col + 2 < this.board.getWidth())
					{
						if (this.board.getCell(row, col + 1) == givenToken
								&& this.board.getCell(row, col + 2) == givenToken)
						{
							// three of the same token have now been found in a line
							// attempt to block/win at this row, if applicable

							// attempt to block/win on the right side
							if (col + 3 < this.board.getWidth())
							{
								// check if there is an empty space on the right side of the board
								if (this.board.getCell(row, col + 3) == Status.NEITHER)
								{
									// the space on the right is not currently occupied
									if ((row + 1 < this.board.getHeight()
											&& this.board.getCell(row + 1, col + 3) != Status.NEITHER)
											|| (row == (this.board.getHeight() - 1)))
									{
										// there is a token directly underneath the current row OR
										// the current row is the bottom of the board.
										// in either case, placing a token here will result in a block or a win
										chosenColumn = col + 3;
									}
								}
							}

							// attempt to block/win on the left side
							if (chosenColumn == defaultChosenColumn && col - 1 >= 0)
							{
								// check if there is an empty space on the left side of the board
								if (this.board.getCell(row, col - 1) == Status.NEITHER)
								{
									// the space on the left is not currently occupied
									if ((row + 1 < this.board.getHeight()
											&& this.board.getCell(row + 1, col - 1) != Status.NEITHER)
											|| (row == (this.board.getHeight() - 1)))
									{
										// there is a token directly underneath the current row OR
										// the current row is the bottom of the board.
										// in either case, placing a token here will result in a block or a win
										chosenColumn = col - 1;
									}
								}
							}
						}
					}
				}
			}
		}

		return chosenColumn;
	}

	// ------------------------------------------------------
	//
	// checkDiagonals
	//
	// PURPOSE: The purpose of this method is to try to find
	// three tokens in a line diagonally that are of the
	// givenToken's status.
	//
	// PARAMETERS:
	// givenToken - the token (status) that the method is
	// trying to find three in a line of.
	//
	// RETURNS: chosenColumn, an int, telling the calling
	// location the chosen column to play the AI's token.
	//
	// ------------------------------------------------------
	private int checkDiagonals(Status givenToken)
	{
		int chosenColumn = checkRightDiagonal(givenToken);

		if (chosenColumn == defaultChosenColumn)
		{
			chosenColumn = checkLeftDiagonal(givenToken);
		}

		return chosenColumn;
	}

	// ------------------------------------------------------
	//
	// checkRightDiagonal
	//
	// PURPOSE: The purpose of this method is to try to find
	// three tokens in a line on a right diagonal that are
	// of the givenToken's status.
	//
	// PARAMETERS:
	// givenToken - the token (status) that the method is
	// trying to find three in a line of.
	//
	// RETURNS: chosenColumn, an int, telling the calling
	// location the chosen column to play the AI's token.
	//
	// ------------------------------------------------------
	private int checkRightDiagonal(Status givenToken)
	{
		int chosenColumn = defaultChosenColumn;

		for (int row = 0; row < this.board.getHeight() && chosenColumn == defaultChosenColumn; row++)
		{
			for (int col = 0; col < this.board.getWidth() && chosenColumn == defaultChosenColumn; col++)
			{
				if (this.board.getCell(row, col) == givenToken)
				{
					if ((row + 2) < this.board.getHeight() && (col + 2) < this.board.getWidth())
					{
						if ((this.board.getCell(row + 1, col + 1) == givenToken)
								&& (this.board.getCell(row + 2, col + 2) == givenToken))
						{
							// three of the same token have been found in a line on a right diagonal
							// attempt to block/win if appropriate

							// check if we can block/win on the right side of the diagonal (bottom right)
							if (row + 3 < this.board.getHeight() && col + 3 < this.board.getWidth())
							{
								if (this.board.getCell(row + 3, col + 3) == Status.NEITHER)
								{
									// there is a blank space at the appropriate location
									// check if there is a token underneath occupying the space
									// OR if it's at the bottom of the board
									if ((row + 4 < this.board.getHeight()
											&& this.board.getCell(row + 4, col + 3) != Status.NEITHER)
											|| (row + 3 == (this.board.getHeight() - 1)))
									{
										chosenColumn = col + 3;
									}
								}
							}

							// check if we can block/win on the left side of the diagonal (top left)
							if (chosenColumn == defaultChosenColumn && (row - 1 >= 0) && (col - 1 >= 0))
							{
								if (this.board.getCell(row - 1, col - 1) == Status.NEITHER)
								{
									// there is a blank space at the appropriate location
									// check if there is a token underneath occupying the space
									if (this.board.getCell(row, col - 1) != Status.NEITHER)
									{
										chosenColumn = col - 1;
									}
								}
							}
						}
					}
				}
			}
		}

		return chosenColumn;
	}

	// ------------------------------------------------------
	//
	// checkLeftDiagonal
	//
	// PURPOSE: The purpose of this method is to try to find
	// three tokens in a line on a left diagonal that are
	// of the givenToken's status.
	//
	// PARAMETERS:
	// givenToken - the token (status) that the method is
	// trying to find three in a line of.
	//
	// RETURNS: chosenColumn, an int, telling the calling
	// location the chosen column to play the AI's token.
	//
	// ------------------------------------------------------
	private int checkLeftDiagonal(Status givenToken)
	{
		int chosenColumn = defaultChosenColumn;

		for (int row = 0; row < this.board.getHeight() && chosenColumn == defaultChosenColumn; row++)
		{
			for (int col = 0; col < this.board.getWidth() && chosenColumn == defaultChosenColumn; col++)
			{
				if (this.board.getCell(row, col) == givenToken)
				{
					if ((row + 2) < this.board.getHeight() && (col - 2) >= 0)
					{
						if ((this.board.getCell(row + 1, col - 1) == givenToken)
								&& (this.board.getCell(row + 2, col - 2) == givenToken))
						{
							// three of the same token have been found in a line on a left diagonal
							// attempt to block/win if appropriate

							// check if we can block/win on the left side of the diagonal (bottom left)
							if (row + 3 < this.board.getHeight() && col - 3 >= 0)
							{
								if (this.board.getCell(row + 3, col - 3) == Status.NEITHER)
								{
									// there is a blank space at the appropriate location
									// check if there is a token underneath occupying the space
									// OR if it's at the bottom of the board
									if ((row + 4 < this.board.getHeight()
											&& this.board.getCell(row + 4, col - 3) != Status.NEITHER)
											|| (row + 3 == (this.board.getHeight() - 1)))
									{
										chosenColumn = col - 3;
									}
								}
							}

							// check if we can block/win on the right side of the diagonal (top right)
							if (chosenColumn == defaultChosenColumn && (row - 1 >= 0)
									&& (col + 1 < this.board.getWidth()))
							{
								if (this.board.getCell(row - 1, col + 1) == Status.NEITHER)
								{
									// there is a blank space at the appropriate location
									// check if there is a token underneath occupying the space
									if (this.board.getCell(row, col + 1) != Status.NEITHER)
									{
										chosenColumn = col + 1;
									}
								}
							}
						}
					}
				}
			}
		}

		return chosenColumn;
	}
}


//------------------------------------------------------
//
// CLASS: GameAI
//
// Author: William Coombs, 6852347
//
// REMARKS: The purpose of this class is to manage the
//          game's selected AI difficulty, implementing
//          the ConnectPlayer interface. This class is
//          created as an abstract class so that the
//          various difficulty levels may
//          polymorphically call makeMove().
//
//------------------------------------------------------

public abstract class GameAI extends BoardArray implements ConnectPlayer
{
	public abstract int makeMove(int lastCol); // the game AI's equivalent of the player's addPiece() method
}

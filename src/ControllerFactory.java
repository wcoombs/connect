
//------------------------------------------------------
//
// CLASS: ControllerFactory
//
// Author: William Coombs, 6852347
//
// REMARKS: The purpose of this class is to create the
//          class that implements the ConnectController
//          interface (the GameLogic class), so that the
//          GameDisplay class need not know of the name
//          of the GameLogic class.
//
//------------------------------------------------------

public class ControllerFactory
{
	public static ConnectController makeController(GameDisplay gd)
	{
		return new GameLogic(gd);
	}
}

package model.sokobanSolver.searchable;

import java.util.HashMap;
import items.Position;
import search.Action;
import search.State;

public class SearchablePlayerToBox extends CommonSearchable
{
	public SearchablePlayerToBox(Position fromPosition, Position toPosition, char[][] board)
	{
		super(fromPosition, toPosition, board);
	}

	@Override
	public HashMap<Action, State<Position>> getAllPossibleStates(State<Position> state)
	{

		HashMap<Action, State<Position>> states = new HashMap<>();

		// get the position from the state
		Position currPos = state.getState();

		// move up
		if (isValidPos(currPos.getX() - 1, currPos.getY()) && (this.board[currPos.getX() - 1][currPos.getY()] == ' '
				|| this.board[currPos.getX() - 1][currPos.getY()] == 'o'))
		{

			Position newPos = new Position(currPos.getX() - 1, currPos.getY());
			State<Position> newState = new State<Position>(newPos, state.getCost() + 1);
			states.put(new Action("up"), newState);

		}
		// move down
		if (isValidPos(currPos.getX() + 1, currPos.getY()) && (this.board[currPos.getX() + 1][currPos.getY()] == ' '
				|| this.board[currPos.getX() + 1][currPos.getY()] == 'o'))
		{
			Position newPos = new Position(currPos.getX() + 1, currPos.getY());
			State<Position> newState = new State<Position>(newPos, state.getCost() + 1);
			states.put(new Action("down"), newState);
		}
		// move left
		if (isValidPos(currPos.getX(), currPos.getY() - 1) && (this.board[currPos.getX()][currPos.getY() - 1] == ' '
				|| this.board[currPos.getX()][currPos.getY() - 1] == 'o'))
		{
			Position newPos = new Position(currPos.getX(), currPos.getY() - 1);
			State<Position> newState = new State<Position>(newPos, state.getCost() + 1);
			states.put(new Action("left"), newState);
		}
		// move right
		if (isValidPos(currPos.getX(), currPos.getY() + 1) && (this.board[currPos.getX()][currPos.getY() + 1] == ' '
				|| this.board[currPos.getX()][currPos.getY() + 1] == 'o'))
		{
			Position newPos = new Position(currPos.getX(), currPos.getY() + 1);
			State<Position> newState = new State<Position>(newPos, state.getCost() + 1);
			states.put(new Action("right"), newState);
		}

		return states;
	}
}

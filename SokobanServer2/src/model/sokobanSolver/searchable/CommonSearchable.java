package model.sokobanSolver.searchable;

import db.Level;
import items.Position;
import search.iSearchable;
import search.State;

public abstract class CommonSearchable implements iSearchable<Position>
{
	private Position fromPosition;
	private Position toPosition;
	protected char[][] board;
	protected Level level;

	public CommonSearchable(Position fromPosition, Position toPosition, char[][] board)
	{
		initParams(fromPosition, toPosition, board);
	}

	public void initParams(Position fromPosition, Position toPosition, char[][] board)
	{
		this.fromPosition = fromPosition;
		this.toPosition = toPosition;
		this.board = copyBoard(board);
		this.board[fromPosition.getX()][fromPosition.getY()] = ' ';
	}

	public CommonSearchable(Position fromPosition, Position toPosition, Level level)
	{
		this.fromPosition = fromPosition;
		this.toPosition = toPosition;
		this.board = copyBoard(level.getLevelBoard());
		this.level = level;
		this.board[fromPosition.getX()][fromPosition.getY()] = ' ';
	}

	@Override
	public State<Position> getInitialState()
	{
		State<Position> start = new State<Position>(fromPosition, 0);
		start.setCameFrom(null);
		start.setAction(null);
		return start;
	}

	@Override
	public State<Position> getGoalState()
	{
		return new State<Position>(toPosition, 0);
	}

	protected char[][] copyBoard(char[][] board)
	{
		char[][] newBoard = new char[board.length][board[0].length];
		
		for (int i = 0; i < board.length; i++)
			for (int j = 0; j < board[0].length; j++)
				newBoard[i][j] = board[i][j];
		
		return newBoard;
	}

	protected boolean isValidPos(int x, int y)
	{
		if (x >= 0 && x < this.board.length && y >= 0 && y < this.board[0].length)
			return true;
		
		else
			return false;
	}
}

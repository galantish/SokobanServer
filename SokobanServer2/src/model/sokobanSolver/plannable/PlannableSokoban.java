package model.sokobanSolver.plannable;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;
import db.Level;
import items.Box;
import items.Position;
import items.Target;
import search.Action;
import search.BFS;
import search.Solution;
import model.sokobanSolver.searchable.SearchableBoxToTarget;
import strips.Clause;
import strips.PlanAction;
import strips.Plannable;
import strips.Predicate;

public class PlannableSokoban implements Plannable
{
	private Level level;
	private HashMap<String, Box> boxesHash;
	private Random rand;
	private List<Target> targetsList;
	private List<Box> boxesList;
	private char[][] unmoveableBoard;
	private BFS<Position> search;

	public PlannableSokoban(Level level)
	{
		this.level = level;
		this.boxesHash = new HashMap<>();
		this.rand = new Random();
		this.targetsList = level.getTargets();
		this.boxesList = level.getBoxes();
		this.unmoveableBoard = generateUnMoveableBoard();
		this.search = new BFS<Position>();
	}

	@Override
	public Clause getGoal()
	{
		PriorityQueue<Target> queue = heuristicGoal();
		Clause goal = new Clause(null);
		Predicate predicate;
		Target t;
		int i = targetsList.size() - 1;
		Predicate sortedPredicates[] = new Predicate[targetsList.size()];

		while (!queue.isEmpty())
		{
			t = queue.poll();
			predicate = new SokPredicate("boxAt", "?", t.getPosition().toString());
			goal.add(predicate);
			sortedPredicates[i--] = predicate;
		}
		
		goal.setSortedPredicates(sortedPredicates);
		
		return goal;
	}

	@Override
	public Clause getKnowledgeBase()
	{
		Clause knowledgeBase = new Clause(null);
		Predicate predicate;

		//Adding the player to the knowledgeBase clause
		predicate = new SokPredicate("playerAt", "p1", level.getPlayers().get(0).getPosition().toString());
		knowledgeBase.add(predicate);

		for (Target t : targetsList)
		{
			predicate = new SokPredicate("clear", "?", t.getPosition().toString());
			knowledgeBase.add(predicate);
		}

		int i = 0;
		for (Box b : boxesList)
		{
			predicate = new SokPredicate("boxAt", "b" + i, b.getPosition().toString());
			knowledgeBase.add(predicate);
			this.boxesHash.put("b" + i, b);
			i++;
		}

		return knowledgeBase;
	}

	@Override
	public Set<PlanAction> getSatisfyingActions(Predicate top)
	{
		return null;
	}

	@Override
	public PlanAction getSatisfyingAction(Predicate top, Clause knowledgeBase)
	{
		this.search.initBFS();

		// "toPosition"
		String pos = top.getValue();
		Position toPosition = generatePosition(pos);

		int xid;
		String bid;
		Solution sol = null;
		int maxRounds = 10;
		int count = 0;
		
		do
		{
			count++;
			
			// "fromPosition"
			xid = rand.nextInt(boxesList.size());
			bid = "b" + xid;

			Box choosenBox = boxesHash.get(bid);
			Position fromPosition = null;

			if (choosenBox != null)
			{
				fromPosition = choosenBox.getPosition();
				SearchableBoxToTarget searchableboxToTarget = new SearchableBoxToTarget(fromPosition, toPosition, generateBoredByKnowledgeBase(knowledgeBase));
				sol = search.search(searchableboxToTarget);
			}
		} 
		while ((sol == null) && (count <= maxRounds));

		if (sol != null)
		{
			boxesHash.remove(bid);
			Move moveAction = new Move("Move", bid, toPosition.toString(), sol.getTheSolution());
			moveAction.setPreconditions(new Clause(new Predicate("clear", "?", toPosition.toString())));

			Position playerPos = getPlayerPos(sol.getTheSolution().get(sol.getTheSolution().size() - 1), toPosition);
			Clause effects = new Clause(new Predicate("boxAt", "b" + xid, toPosition.toString()), new Predicate("playerAt", "p1", playerPos.toString()));
			moveAction.setEffects(effects);
			moveAction.setDeleteEffects(new Clause(new Predicate("clear", "?", toPosition.toString())));
			return moveAction;
		}

		return null;
	}

	public char[][] generateBoredByKnowledgeBase(Clause knowledgeBase)
	{
		char[][] stateBoard = copyBoard(unmoveableBoard);
		Position pos;

		for (Predicate p : knowledgeBase.getPredicates())
		{
			if (p.getType() == "boxAt" && p.getId() != "?")
			{
				pos = generatePosition(p.getValue());
				stateBoard[pos.getX()][pos.getY()] = '@';
			}
			
			if (p.getType() == "playerAt")
			{
				pos = generatePosition(p.getValue());
				stateBoard[pos.getX()][pos.getY()] = 'A';
			}
		}
	
		return stateBoard;
	}

	public Position generatePosition(String pos)
	{
		String str = pos.replace("(", "");
		str = str.replace(")", "");
		String[] arr = str.split(",");
		int x = Integer.parseInt(arr[0]);
		int y = Integer.parseInt(arr[1]);

		return new Position(x, y);

	}

	public Position randomBox()
	{
		return null;
	}

	public char[][] generateUnMoveableBoard()
	{
		char[][] board = level.getLevelBoard();
		char[][] newBoard = new char[board.length][board[0].length];

		for (int i = 0; i < board.length; i++)
		{
			for (int j = 0; j < board[0].length; j++)
			{
				if (board[i][j] == ' ' || board[i][j] == 'o' || board[i][j] == '#')
					newBoard[i][j] = board[i][j];
				else
					newBoard[i][j] = ' ';
			}
		}

		return newBoard;
	}

	protected char[][] copyBoard(char[][] board)
	{
		char[][] newBoard = new char[board.length][board[0].length];
	
		for (int i = 0; i < board.length; i++)
			for (int j = 0; j < board[0].length; j++)
				newBoard[i][j] = board[i][j];
		
		return newBoard;
	}

	private Position getPlayerPos(Action action, Position currPos)
	{
		if (action != null)
		{
			String act = action.getAction();
			if (act.equals("up"))
				return new Position(currPos.getX() + 1, currPos.getY());
			if (act.equals("down"))
				return new Position(currPos.getX() - 1, currPos.getY());
			if (act.equals("left"))
				return new Position(currPos.getX(), currPos.getY() + 1);
			if (act.equals("right"))
				return new Position(currPos.getX(), currPos.getY() - 1);
		}
		
		return null;
	}

	private PriorityQueue<Target> heuristicGoal()
	{
		PriorityQueue<Target> priorityQueue = new PriorityQueue<>(new Comparator<Target>()
		{
			@Override
			public int compare(Target o1, Target o2)
			{
				return getManhattanDistance(o1.getPosition()) - getManhattanDistance(o2.getPosition());
			}
		});

		for (Target t : targetsList)
			priorityQueue.add(t);

		return priorityQueue;
	}

	private int getManhattanDistance(Position pos)
	{
		Position playerPos = level.getPlayers().get(0).getPosition();

		int x = Math.abs(playerPos.getX() - pos.getX());
		int y = Math.abs(playerPos.getY() - pos.getY());

		return x + y;
	}
}
package model.sokobanSolver;

import java.util.ArrayList;
import java.util.List;
import db.Level;
import model.sokobanSolver.plannable.Move;
import model.sokobanSolver.plannable.PlannableSokoban;
import search.Action;
import strips.PlanAction;
import strips.Planner;
import strips.Strips;

public class SokobanSolver
{
	public List<Action> solveLevel(Level level)
	{
		PlannableSokoban p = new PlannableSokoban(level);

		// Defining the maximum times we would be abble to run the Strips algorithm
		Strips.count = 0;
		Strips.max = 10;

		Planner planner = new Strips();

		List<PlanAction> listActions = planner.plan(p);
		
		List<Action> resultActions = new ArrayList<>();
		
		if (listActions != null)
		{
			for (PlanAction planAction : listActions)
			{
				for (Action action : ((Move) planAction).getSearchResult())
					resultActions.add(action);
			}
		} 
		
		else
			return null;

		return resultActions;
	}
}

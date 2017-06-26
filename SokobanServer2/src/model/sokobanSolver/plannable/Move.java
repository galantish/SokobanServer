package model.sokobanSolver.plannable;

import java.util.List;
import search.Action;
import strips.PlanAction;

public class Move extends PlanAction
{
	private List<Action> searchResult;

	public Move(String type, String id, String value, List<Action> searchResult)
	{
		super(type, id, value);
		this.searchResult = searchResult;
	}

	public List<Action> getSearchResult()
	{
		return searchResult;
	}

	public void setSearchResult(List<Action> searchResult)
	{
		this.searchResult = searchResult;
	}
}

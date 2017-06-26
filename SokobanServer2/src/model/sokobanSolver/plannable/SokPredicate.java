package model.sokobanSolver.plannable;

import strips.Predicate;

public class SokPredicate extends Predicate
{
	public SokPredicate(String type, String id, String value)
	{
		super(type, id, value);
	}

	@Override
	public boolean contradicts(Predicate p)
	{
		return super.contradicts(p) || (!getId().equals(p.getId()) && getValue().equals(p.getValue()));
	}
}

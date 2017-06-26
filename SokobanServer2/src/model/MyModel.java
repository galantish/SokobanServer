package model;

import java.util.List;
import db.Level;
import db.Record;
import db.User;
import model.highScores.QueryParameters;
import model.highScores.SokobanDBManager;
import model.sokobanSolver.SokobanSolver;

public class MyModel implements iModel
{
	private SokobanDBManager dbManager;
	private SokobanSolver solver;
	
	public MyModel()
	{
		this.dbManager = SokobanDBManager.getInstance();
	}
	
	@Override
	public List<Record> dbQuery(QueryParameters params)
	{
		List<Record> records = this.dbManager.recordsQuery(params);
		return records;
	}

	@Override
	public void addUser(User user)
	{
		if(!this.dbManager.isExistUser(user.getName()))
			this.dbManager.add(user);
	}

	@Override
	public void addRecord(Record record)
	{
		this.dbManager.add(record);
	}

	@Override
	public void addLevel(Level level)
	{
		if(!this.dbManager.isExistLevel(level.getLevelID()))
			this.dbManager.add(level);
	}

	@Override
	public String getSolution(Level level)
	{
		//Compress the solution!
		String sol = this.solver.solveLevel(level).toString();
		return sol;
	}

}

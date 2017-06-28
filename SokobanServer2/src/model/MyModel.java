package model;

import java.util.List;
import java.util.concurrent.locks.Lock;
import db.Level;
import db.Record;
import db.User;
import db.QueryParameters;
import model.highScores.SokobanDBManager;
import model.sokobanSolver.SokobanSolver;

public class MyModel implements iModel
{
	private SokobanDBManager dbManager;
	private SokobanSolver solver;
	private Lock lock;
	
	public MyModel(Lock lock)
	{
		this.lock = lock;
		this.solver = new SokobanSolver();
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
		
		lock.unlock();
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
		System.out.println("Solution From The Server = " + sol);
		return sol;
	}

}

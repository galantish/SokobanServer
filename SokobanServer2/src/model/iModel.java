package model;

import java.util.List;
import db.Level;
import db.Record;
import db.User;
import db.QueryParameters;

public interface iModel
{
	public List<Record> dbQuery(QueryParameters params);
	public void addUser(User user);
	public void addRecord(Record record);
	public void addLevel(Level level);
	public String getSolution(Level level);
}

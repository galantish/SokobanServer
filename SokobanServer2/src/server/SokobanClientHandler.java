package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.locks.Lock;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import db.CompressedLevel;
import db.Level;
import db.Record;
import db.User;
import model.MyModel;
import model.iModel;
import model.highScores.Commands;
import db.QueryParameters;

public class SokobanClientHandler implements iClientHandler
{
	private BufferedReader readFromClient;
	private PrintWriter writeToClient;
	private GsonBuilder builder;
	private Gson json;
	private iModel model;
	private Lock lock;
	
	public SokobanClientHandler(Lock lock) 
	{
		this.lock = lock;
		this.readFromClient = null;
		this.writeToClient = null;
		this.builder = new GsonBuilder();
		this.json = this.builder.create();
		this.model = new MyModel(this.lock);
	}
	
	@Override
	public void handleClient(InputStream inFromClient, OutputStream outToClient) 
	{
		this.readFromClient = new BufferedReader(new InputStreamReader(inFromClient));
		this.writeToClient = new PrintWriter(outToClient);
		handlerClientCommands();
	}
	
	public void handlerClientCommands()
	{
		this.lock.lock();
		
		String commandString;
		Commands command; 
		String params;
		
		try
		{
			commandString = readFromClient.readLine();

			command = this.json.fromJson(commandString, Commands.class);
			System.out.println("COMMAND = " + command);
			
			params = this.readFromClient.readLine();

			switch (command)
			{
				case ADD_USER:
					User user = this.json.fromJson(params, User.class);	
					this.model.addUser(user);
				break;	
					
				case ADD_LEVEL:
					this.lock.unlock();
					CompressedLevel compLevel = this.json.fromJson(params, CompressedLevel.class);	
					Level level = compLevel.deCompressedLevel();
					this.model.addLevel(level);
				break;
				
				case ADD_RECORD:
					this.lock.unlock();
					Record record = this.json.fromJson(params, Record.class);	
					this.model.addRecord(record);
				break;
				
				case DB_QUERY:
					this.lock.unlock();
					QueryParameters queryParams = this.json.fromJson(params, QueryParameters.class);
					List<Record> records = this.model.dbQuery(queryParams);
					String stringJson = this.json.toJson(records);
					this.writeToClient.print(stringJson);
					this.writeToClient.flush();
					
				break;
					
				case GET_SOLUTION:
					this.lock.unlock();
					CompressedLevel compLevelToSol = this.json.fromJson(params, CompressedLevel.class);
					Level levelToSolve = compLevelToSol.deCompressedLevel();
					if(levelToSolve == null)
					{
						String jsonLevel = this.json.toJson("ERROR");
						this.writeToClient.println(jsonLevel);
						this.writeToClient.flush();
						break;
					}
					
					String sol = this.model.getSolution(levelToSolve);
					String jsonLevel = this.json.toJson(sol.toString());
					this.writeToClient.println(jsonLevel);
					this.writeToClient.flush();
					
				break;
				
				default:
					break;
			}
			
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

}
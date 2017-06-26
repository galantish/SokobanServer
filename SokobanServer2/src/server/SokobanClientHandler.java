package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import db.Level;
import db.Record;
import db.User;
import model.iModel;
import model.highScores.Commands;
import model.highScores.QueryParameters;

public class SokobanClientHandler implements iClientHandler
{
	private BufferedReader readFromClient;
	private PrintWriter writeToClient;
	private GsonBuilder builder;
	private Gson json;
	private iModel model;
	
	public SokobanClientHandler() 
	{
		this.readFromClient = null;
		this.writeToClient = null;
		this.builder = new GsonBuilder();
		this.json = this.builder.create();
	}
	
	public SokobanClientHandler(iModel model) 
	{
		this.readFromClient = null;
		this.writeToClient = null;
		this.builder = new GsonBuilder();
		this.json = this.builder.create();
		this.model = model;
	}
	
	@Override
	public void handleClient(InputStream inFromClient, OutputStream outToClient) 
	{
		this.readFromClient = new BufferedReader(new InputStreamReader(inFromClient));
		this.writeToClient = new PrintWriter(outToClient);
		
	
	}
	
	public void handlerClientCommands()
	{
		String commandString;
		Commands command; 
		String params;
		
		try
		{
			commandString = readFromClient.readLine();
			System.out.println("JSON = " + commandString);
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
					Level level = this.json.fromJson(params, Level.class);	
					this.model.addLevel(level);
				break;
				
				case ADD_RECORD:
					Record record = this.json.fromJson(params, Record.class);	
					this.model.addRecord(record);
				break;
				
				case DB_QUERY:
					QueryParameters queryParams = this.json.fromJson(params, QueryParameters.class);
					List<Record> records = this.model.dbQuery(queryParams);

				break;
					
				case GET_SOLUTION:
					
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
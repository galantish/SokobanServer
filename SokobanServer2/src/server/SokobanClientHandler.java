package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.locks.Lock;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import commands.Commands;
import commons.MyClient;
import db.CompressedLevel;
import db.Level;
import db.LevelSolutionData;
import db.Record;
import db.User;
import model.AdminModel;
import model.MyModel;
import model.iModel;
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
	public void handleClient(int clientId, Socket socket) 
	{
		
		InputStream inFromClient = null;
		OutputStream outToClient = null;
		
		try
		{ 
			inFromClient = socket.getInputStream();
			outToClient = socket.getOutputStream();
			
			this.readFromClient = new BufferedReader(new InputStreamReader(inFromClient));
			this.writeToClient = new PrintWriter(outToClient);
			
			String strJson = this.readFromClient.readLine();
			String str = this.json.fromJson(strJson, String.class);
			
			str = "client " + clientId + str;
			
			MyClient client = new MyClient(socket, socket.getPort(), ++clientId, socket.getRemoteSocketAddress().toString());
			
			String msg = "Task Number " + clientId + " From Client On IP Address: " + socket.getRemoteSocketAddress().toString();
			
			AdminModel.getInstance().addClient(msg, client);
			handlerClientCommands();
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}	
		finally
		{
			try
			{
				inFromClient.close();
				outToClient.close();
				this.readFromClient.close();
				this.writeToClient.close();
			} 
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
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
				
				case GET_CLUE:
					this.lock.unlock();
					CompressedLevel compLevelToClue = this.json.fromJson(params, CompressedLevel.class);
					Level levelToClue = compLevelToClue.deCompressedLevel();
					if(levelToClue == null)
					{
						String jsonLevel = this.json.toJson("ERROR");
						this.writeToClient.println(jsonLevel);
						this.writeToClient.flush();
						break;
					}
					
					String sol = this.model.getSolution(levelToClue);
					String jsonClue = this.json.toJson(sol.toString());
					this.writeToClient.println(jsonClue);
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
					
					//Checking if there is a solution to the current level
					Client client = ClientBuilder.newClient();
					WebTarget webTarget2 = client.target("http://localhost:8080/SokobanWebServices/SokobanServices/get/" + levelToSolve.getLevelID());
					Invocation.Builder invocationBuilder2 = webTarget2.request();
					Response response = invocationBuilder2.get();
					String msgJson = response.readEntity(String.class);
					System.out.println("Message: " + msgJson);
					
					if(msgJson.equals("null"))
					{
						String mySol = this.model.getSolution(levelToSolve);
						String jsonLevel = this.json.toJson(mySol.toString());
						this.writeToClient.println(jsonLevel);
						this.writeToClient.flush();
						
						//Sending the level's solution to the server
						LevelSolutionData levelSolution = new LevelSolutionData(levelToSolve.getLevelID(), mySol, null);
						String jsonSolution = this.json.toJson(levelSolution);
						
						WebTarget webTarget = client.target("http://localhost:8080/SokobanWebServices/SokobanServices/add");
						Invocation.Builder invocationBuilder = webTarget.request();
						invocationBuilder.post(Entity.entity(jsonSolution, MediaType.TEXT_PLAIN));
						
						break;
					}
					
					else
					{
						LevelSolutionData levelSolution = this.json.fromJson(msgJson, LevelSolutionData.class);
						String jsonLevel = this.json.toJson(levelSolution.getSolution());
						this.writeToClient.println(jsonLevel);
						this.writeToClient.flush();
						break;
					}
					
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
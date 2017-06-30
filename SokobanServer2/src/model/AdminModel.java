package model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import commons.MyClient;

public class AdminModel
{
	private static class AdminHolder
	{
		public static final AdminModel instance = new AdminModel();
	}
	
	public static AdminModel getInstance()
	{
		return AdminHolder.instance;
	}
	
	private HashMap<String, MyClient> clients;
	
	public AdminModel()
	{
		this.clients = new HashMap<>();
	}
	
	public void addClient(String id, MyClient client)
	{
		this.clients.put(id, client);
	}
	
	public List<String> getClients()
	{
		List<String> list = new ArrayList<String>();
		for(String client: this.clients.keySet())
			list.add(client);
		
		return list;
	}
	
	public void disconnectClient(String clientId)
	{
		MyClient client = this.clients.get(clientId);
		
		try
		{
			client.getSocket().close();
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

}

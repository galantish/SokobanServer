package commons;

import java.net.Socket;

public class MyClient
{
	private Socket socket;
	private int port;
	private int id;
	private String ip;
	
	public MyClient(Socket socket, int port, int id, String ip)
	{
		this.socket = socket;
		this.port = port;
		this.id = id;
		this.ip = ip;
	}
	
	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getIp()
	{
		return ip;
	}

	public void setIp(String ip)
	{
		this.ip = ip;
	}

	public int getPort()
	{
		return port;
	}

	public void setPort(int port)
	{
		this.port = port;
	}

	public Socket getSocket()
	{
		return socket;
	}

	public void setSocket(Socket socket)
	{
		this.socket = socket;
	}
	
	
}

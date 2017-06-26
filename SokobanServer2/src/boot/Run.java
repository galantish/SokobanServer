package boot;

import server.MyServer;

public class Run 
{
	public static void main(String[] args)
	{
		MyServer server = new MyServer(7777);
		server.start();
		
		
	}
}
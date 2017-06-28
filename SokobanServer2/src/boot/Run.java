package boot;

//import db.CompressedLevel;
//import db.Level;
import server.MyServer;

public class Run 
{
	public static void main(String[] args)
	{
		MyServer server = new MyServer(7580);
		server.start();
	}
}
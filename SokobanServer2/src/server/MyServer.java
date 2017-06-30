package server;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;

public class MyServer
{
	private static class LockerHolder
	{
		public static final Lock lock = new ReentrantLock();
	}
	
	private int port;
	private volatile boolean stop;
	private ExecutorService excutor;
	private Lock lock;
	
	private static int clientId = 0;

	public MyServer(int port)
	{
		this.port = port;
		this.stop = false;
		this.excutor = Executors.newFixedThreadPool(15);
		this.lock = LockerHolder.lock;
	}

	private void runServer() throws Exception
	{
		ServerSocket server = new ServerSocket(this.port);
		System.out.println("Server is alive..");
		server.setSoTimeout(1000);

		while (!stop)
		{
			// we want to wait to the next client- we handle the clients in a
			try
			{
				Socket aClient = server.accept(); // blocking call

				excutor.execute(() ->
				{
					System.out.println("The client is connected");
						
					new SokobanClientHandler(this.lock).handleClient(++clientId, aClient);

				});

			} 
			catch (SocketTimeoutException e){}
		}

		server.close();
	}

	public void start()
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					System.out.println("Running");
					runServer();
				} 
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}).start();
	}

	public void stop()
	{
		this.excutor.shutdown();
		
		try
		{
			this.excutor.awaitTermination(5, TimeUnit.SECONDS);
		} 
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		finally 
		{
			stop = true;
		}
	}
}
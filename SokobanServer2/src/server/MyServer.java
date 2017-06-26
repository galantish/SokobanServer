package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MyServer
{
	private int port;
	private volatile boolean stop;
	private ExecutorService excutor;

	public MyServer(int port)
	{
		this.port = port;
		this.stop = false;
		this.excutor = Executors.newFixedThreadPool(3);
	}

	private void runServer() throws Exception
	{
		ServerSocket server = new ServerSocket(this.port);
		System.out.println("Server alive");
		server.setSoTimeout(1000);

		while (!stop)
		{
			// we want to wait to the next client- we handle the clients in a
			try
			{
				Socket aClient = server.accept(); // blocking call

				excutor.execute(() ->
				{
					try
					{
						System.out.println("The client is connected");

						InputStream inFromClient = aClient.getInputStream();
						OutputStream outToClient = aClient.getOutputStream();

						new SokobanClientHandler().handleClient(inFromClient, outToClient);
						inFromClient.close();
						outToClient.close();
						aClient.close();
					}

					catch (IOException e){}
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
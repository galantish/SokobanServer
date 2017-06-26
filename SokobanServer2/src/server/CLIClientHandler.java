package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Timer;
import java.util.TimerTask;

public class CLIClientHandler
{
	private InputStream inFromClient;
	private OutputStream outToClient;
	private Timer timer;
	
	public CLIClientHandler(InputStream inFromClient, OutputStream outToClient) 
	{
		this.inFromClient=inFromClient;
		this.outToClient=outToClient;
		this.timer=new Timer();
	}
	
	public void handleClient() 
	{
		try 
		{
			BufferedReader readFromClient=new BufferedReader(new InputStreamReader(inFromClient));
			BufferedReader readFromServer=new BufferedReader(new InputStreamReader(System.in));

			PrintWriter writeToClient=new PrintWriter(outToClient);
			PrintWriter writeToServer=new PrintWriter(System.out);
			
			writeToClient.println(readFromServer.readLine());
			writeToClient.flush();
			
			String line="";
			line=readFromClient.readLine();
			writeToServer.println("from client: "+line);
			writeToServer.flush();
			writeToClient.println("ECHO: "+line);
			writeToClient.flush();
			
			
			TimerTask task=new TimerTask() 
			{
				@Override
				public void run() 
				{
					writeToClient.println("yarden");
					writeToClient.flush();
				}
			};			
			
			this.timer.scheduleAtFixedRate(task, 0, 1000);
			
			Thread.sleep(15000);
			
			task.cancel();
			
			line=readFromClient.readLine();
			writeToServer.println("from client: "+line);
			writeToServer.flush();
			
			writeToClient.println("bye");
			writeToClient.flush();
			
			readFromClient.close();
			readFromClient.close();
			writeToClient.close();
			writeToServer.close();
			}
		
		catch (IOException e) 
		{
			e.printStackTrace();
		} 
		
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		} 
	}
//	
//	private void readInputsAndSend(BufferedReader in, PrintWriter out,String exitStr){ 
//		try {
//			String line; 
//			while(!(line=in.readLine()).equals(exitStr)){ 
//				out.println(line); 
//				out.flush();
//			} 
//		} catch (IOException e) { e.printStackTrace();} 
//	} 
//	
//	private Thread aSyncReadInputsAndSend(BufferedReader in, PrintWriter out,String exitStr){
//		Thread t=new Thread(new Runnable() {
//			public void run() {readInputsAndSend(in, out, exitStr);} 
//		});
//	    t.start();
//		return t;
//	}

}

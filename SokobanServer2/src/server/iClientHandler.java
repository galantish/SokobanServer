package server;

import java.io.InputStream;
import java.io.OutputStream;

public interface iClientHandler 
{
	void handleClient(InputStream inFromClient, OutputStream outToClient);
}

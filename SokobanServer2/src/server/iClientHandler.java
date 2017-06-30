package server;

import java.net.Socket;

public interface iClientHandler 
{
	void handleClient(int clientId, Socket socket);
}

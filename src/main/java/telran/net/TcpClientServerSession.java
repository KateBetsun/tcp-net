package telran.net;

import java.net.*;
import java.io.*;
import static telran.net.TcpConfigurationProperties.*;

public class TcpClientServerSession extends Thread{
	Socket socket;
	Protocol protocol;
	TcpServer tcpServer;

	public TcpClientServerSession(Socket socket, Protocol protocol, TcpServer tcpServer) {
		this.socket = socket;
		this.tcpServer = tcpServer;
		//using the method socket.setSoTimeout() and some solution for getting 
		// session to know about shutdown
		//you should stop the thread after shutdown command
		this.protocol = protocol;
	}
	public void run() {
		try (BufferedReader receiver =
				new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintStream sender = new PrintStream(socket.getOutputStream())){
			String line = null;
			boolean sessionRunning = true;
			socket.setSoTimeout(SOCKET_TIMEOUT);
			long idleTime = 0;
			//figure out solution for exiting from the thread after shutdown
			while(tcpServer.running && sessionRunning) {
				try{
					line = receiver.readLine();
					if(line == null) {
						break;
					}
					String responseStr = protocol.getResponseWithJSON(line);
					sender.println(responseStr);
					idleTime = 0;
				}catch(SocketTimeoutException e) {
					idleTime += SOCKET_TIMEOUT;
					if(idleTime > SESSION_IDLE_TIMEOUT) {
						sessionRunning = false;
					}
				}
			}
			socket.close();
			
			//handling SocketTimeoutException for exiting from the thread on two conditions
			//1.Shutdown has been performed
			//2.Thread exists in IDLE state more than 1 minute
			//exiting from the cycle should be followed by closing connection
			
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
}

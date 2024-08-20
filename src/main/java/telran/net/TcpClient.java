package telran.net;

import java.io.*;
import java.net.*;
import java.time.Instant;
import static telran.net.TcpConfigurationProperties.*;
import org.json.JSONObject;

public class TcpClient implements Closeable{       //delegate
		private static final long DEFAULT_INTERVAL = 3000;
		private static final int DEFAULT_NUMBER_ATTEMPTS = 10;
		String hostName;
		int port;
		Socket socket;
		BufferedReader receiver;
		PrintStream sender;
		long interval;
		int nAttempts;
		public TcpClient(String hostName, int port, long interval, int nAttempts) {
			this.hostName = hostName;
			this.port = port;
			this.interval = interval;
			this.nAttempts = nAttempts;
			if(this.interval < 0) {
				this.interval = 0;
			}
			connect();
		}
		private void connect() {
			int counter = nAttempts;
			do {
				try {
					socket = new Socket(hostName, port);
					sender = new PrintStream(socket.getOutputStream());
					receiver = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					counter = 0;
					
				}catch(IOException e) {
					waitForInterval();
					counter--;
				}
				
			}while(counter != 0);
			
		}
		private void waitForInterval() {
			Instant finished = Instant.now().plusMillis(interval);
			while(Instant.now().isBefore(finished)) {
				
			}
			
		}
		public TcpClient(String hostName, int port) {
			this(hostName, port, DEFAULT_INTERVAL, DEFAULT_NUMBER_ATTEMPTS);
		}
		@Override
		public void close()  {
			try {
				socket.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			
		}
		public String sendAndReceive(Request request) {
			try {
				sender.println(request);
				String responseJSON = receiver.readLine();
				if (responseJSON == null) {
					throw new RuntimeException("Server closed connection");
				}
				JSONObject jsonObj = new JSONObject(responseJSON);
				ResponseCode responseCode = jsonObj.getEnum(ResponseCode.class,
						RESPONSE_CODE_FIELD);
				String responseData = jsonObj.getString(RESPONSE_DATA_FIELD);
				if(responseCode != ResponseCode.OK) {
					throw new RuntimeException(responseData);
				}
				return responseData;
				
			} catch (IOException e) {
				connect();
				throw new RuntimeException("Server is unavailable, repeat later on");
			}
		}
		
		
	}								


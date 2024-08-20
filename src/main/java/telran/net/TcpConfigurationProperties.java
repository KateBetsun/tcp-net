package telran.net;

public interface TcpConfigurationProperties {
	String REQUEST_TYPE_FIELD = "requestType";
	String REQUEST_DATA_FIELD = "requestData";
	String RESPONSE_CODE_FIELD = "responseCode";
	String RESPONSE_DATA_FIELD = "responseData";
	int SESSION_IDLE_TIMEOUT = 60000;
	int SOCKET_TIMEOUT = 1000;
	String SHUTDOWN = "shutdown";
	int MAX_WAITING_TIME_IN_SECONDS = 10;
}

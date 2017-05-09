package elastest.tormpocplugin;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.CountDownLatch;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import hudson.model.TaskListener;

@ClientEndpoint
public class WSocketClientToLogs {
	
	 public static CountDownLatch latch;
	 
	 private String message;
	 
	 private TaskListener listener;	 	  
	 
	 public WSocketClientToLogs(){
		 		 
	 }
	 
	public WSocketClientToLogs(URI uri, TaskListener listener) {
		this.listener = listener;
		try {
//			WebSocketContainer container = ContainerProvider.getWebSocketContainer();
//			WebSocketClient transport = new StandardWebSocketClient(container);
//			WebSocketStompClient stompClient = new WebSocketStompClient(transport);
//			stompClient.setMessageConverter(new StringMessageConverter());
//			container.connectToServer(this, uri);
//			stompClient.connect(url, handler, uriVars)
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	    @OnOpen
	    public void onOpen(Session session) {
	    	listener.getLogger().println("Inside onOpen method of the class WSocketClientToLogs:" );       
	            try {
	                session.getBasicRemote().sendText("start");
	            } catch (IOException e) {
	            	listener.getLogger().println("Error inside class WSocketClientToLogs:"+e.getMessage());
	            	listener.getLogger().println("Error inside class WSocketClientToLogs:"+e.getCause().getMessage());
	                throw new RuntimeException(e);
	            }
	    }
	 
	    @OnMessage
	    public String onMessage(String message, Session session) {
	    	this.message = message;
	    	listener.getLogger().println("Remote Log:"+ this.message);
	    	if (message.equals("END")){
	    		latch.countDown();
	    	}
	    		
	    	return "OK";
//	    	 BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
//	    	 try {
//	             
//	             String userInput = bufferRead.readLine();
//	             return userInput;
//	         } catch (IOException e) {
//	             throw new RuntimeException(e);
//	         }
	    }
	 
	    @OnClose
	    public void onClose(Session session, CloseReason closeReason) {
	        
	        latch.countDown();
	    }

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}
	 
	
}

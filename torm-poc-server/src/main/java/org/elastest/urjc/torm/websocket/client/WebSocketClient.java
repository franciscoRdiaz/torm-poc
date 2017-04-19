package org.elastest.urjc.torm.websocket.client;

import java.lang.reflect.Type;

import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

@Service
public class WebSocketClient {
	
	public void stomp (){
		StandardWebSocketClient webSocketClient = new StandardWebSocketClient();
		WebSocketStompClient stompClient = new WebSocketStompClient(webSocketClient);
		stompClient.setMessageConverter(new StringMessageConverter());
		
		String url = "ws://localhost:8080/logssss";
		StompSessionHandler sessionHandler = new MyStompSessionHandler();
		stompClient.connect(url, sessionHandler);
	}
	
	public class MyStompSessionHandler extends StompSessionHandlerAdapter {

	    @Override
	    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
	    	session.subscribe("/topic/logs", new StompFrameHandler() {
				
				@Override
				public void handleFrame(StompHeaders headers, Object payload) {
					System.out.println(payload.toString());
					
				}
				
				@Override
				public Type getPayloadType(StompHeaders headers) {
			        return String.class;
				}
			}); 
	    	
	    	session.send("/topic/logs", "payload");

	    }
	}
}

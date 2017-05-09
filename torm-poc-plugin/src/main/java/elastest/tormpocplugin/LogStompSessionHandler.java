package elastest.tormpocplugin;

import java.lang.reflect.Type;
import java.security.Security;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;


import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;

public class LogStompSessionHandler implements StompSessionHandler {

    

    private final CountDownLatch waitForEndOfMessage;

    public LogStompSessionHandler(CountDownLatch waitForEndOfMessage) {
	this.waitForEndOfMessage = waitForEndOfMessage;
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
	return String.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
	// TODO
    }

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
	
	session.send("/torm/logs", "END");

	session.subscribe("/topic/logs", new StompFrameHandler() {

	    @Override
	    public void handleFrame(StompHeaders headers, Object payload) {
	    	
	    	System.out.println("Received message:"+payload.toString());
		
		if (payload.equals("[END]: The logs would go here")) {
		    waitForEndOfMessage.countDown();
		}
	    }

	    @Override
	    public Type getPayloadType(StompHeaders headers) {
		return String.class;
	    }
	});
    }

    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
	
	waitForEndOfMessage.countDown();
    }

    @Override
    public void handleTransportError(StompSession session, Throwable exception) {
	
	waitForEndOfMessage.countDown();
    }

}
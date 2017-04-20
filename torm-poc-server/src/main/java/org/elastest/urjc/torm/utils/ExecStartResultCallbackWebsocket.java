package org.elastest.urjc.torm.utils;

import java.io.IOException;
import java.io.OutputStream;

import javax.ws.rs.POST;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.core.async.ResultCallbackTemplate;
import com.github.dockerjava.core.command.ExecStartResultCallback;

@Service
public class ExecStartResultCallbackWebsocket extends ResultCallbackTemplate<ExecStartResultCallback, Frame> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExecStartResultCallback.class);
	
	@Autowired
	private  SimpMessagingTemplate messagingTemplate;
//	@Autowired
//	private LogsByStompOverWSController logsByStompOverWSController;
	
	
	private OutputStream stdout, stderr;

//	@Autowired
//	public ExecStartResultCallbackWebsocket(OutputStream stdout, OutputStream stderr, SimpMessagingTemplate messagingTemplate) {
//		this.stdout = stdout;
//		this.stderr = stderr;
//	}
	
//	public ExecStartResultCallbackWebsocket(OutputStream stdout, OutputStream stderr) {
//		this.stdout = stdout;
//		this.stderr = stderr;
//	}

//	@Autowired
//	public ExecStartResultCallbackWebsocket(SimpMessagingTemplate messagingTemplate) {
//		//this(null, null, messagingTemplate);
//		this.messagingTemplate = messagingTemplate;		
//		
//	}

	@Override
	public void onNext(Frame frame) {
		if (frame != null) {
			try {
				switch (frame.getStreamType()) {
				case STDOUT:
				case RAW:
					if (stdout != null) {
						try {
							//logsByStompOverWSController.sendLogs(frame.toString());
						} catch (Exception e) {
							//ToDo
							
							e.printStackTrace();
						}
						stdout.write(frame.getPayload());
						stdout.flush();
						}					
					afterTradeExecuted();
					break;
				case STDERR:
					if (stderr != null) {
						try {
							//logsByStompOverWSController.sendLogs("Stderr: " + frame.toString());
						} catch (Exception e) {
							//ToDo
						}
						stderr.write("Stderr: ".getBytes());
						stderr.write(frame.getPayload());
						stderr.flush();
					}
					break;
				default:
					LOGGER.error("unknown stream type:" + frame.getStreamType());
				}
			} catch (IOException e) {
				onError(e);
			}

			LOGGER.debug(frame.toString());
		}
	}
	
    
	public void afterTradeExecuted() {
		//System.out.println("[Log]:"+ );
		try{
			this.messagingTemplate.convertAndSend("/topic/logs", "OK");
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public OutputStream getStdout() {
		return stdout;
	}

	public void setStdout(OutputStream stdout) {
		this.stdout = stdout;
	}

	public OutputStream getStderr() {
		return stderr;
	}

	public void setStderr(OutputStream stderr) {
		this.stderr = stderr;
	}
	
}

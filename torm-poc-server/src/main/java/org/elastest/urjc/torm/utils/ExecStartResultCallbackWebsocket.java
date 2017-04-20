package org.elastest.urjc.torm.utils;

import java.io.IOException;
import java.io.PrintWriter;

import org.elastest.urjc.torm.api.data.LogTrace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.core.async.ResultCallbackTemplate;
import com.github.dockerjava.core.command.ExecStartResultCallback;

@Service
public class ExecStartResultCallbackWebsocket extends ResultCallbackTemplate<ExecStartResultCallback, Frame> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExecStartResultCallback.class);
	
	@Autowired
	private  SimpMessagingTemplate messagingTemplate;
	
	private PrintWriter stdout, stderr;

	@Override
	public void onNext(Frame frame) {
		if (frame != null) {
			try {
				switch (frame.getStreamType()) {
				case STDOUT:
				case RAW:
					if (stdout != null) {
						writeTrace(frame, stdout, "");
					}					
					break;
				case STDERR:
					if (stderr != null) {
						writeTrace(frame, stderr, "Stderr: ");
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

	public void writeTrace(Frame frame, PrintWriter pw, String label) throws IOException{
		String frameString = frame.toString();
		
		LogTrace trace = new LogTrace(frameString);
		afterTradeExecuted(trace);
		
		pw.println(frameString);
	}
    
	public void afterTradeExecuted(LogTrace trace) {
		try{
			this.messagingTemplate.convertAndSend("/topic/logs", trace.toJSON());
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public PrintWriter getStdout() {
		return stdout;
	}

	public void setStdout(PrintWriter stdout) {
		this.stdout = stdout;
	}

	public PrintWriter getStderr() {
		return stderr;
	}

	public void setStderr(PrintWriter stderr) {
		this.stderr = stderr;
	}
	
}

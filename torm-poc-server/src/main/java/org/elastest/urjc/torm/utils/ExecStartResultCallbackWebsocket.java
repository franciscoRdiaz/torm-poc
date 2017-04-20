package org.elastest.urjc.torm.utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.io.OutputStreamWriter;


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
	
	private OutputStream stdout, stderr;

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

	public void writeTrace(Frame frame, OutputStream out, String label) throws IOException{
		LogTrace trace = new LogTrace(frame.toString());
		afterTradeExecuted(trace);
		
		Writer writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
		writer.write(label);
		writer.write(frame.toString());
		writer.flush();
	}
    
	public void afterTradeExecuted(LogTrace trace) {
		try{
			this.messagingTemplate.convertAndSend("/topic/logs", trace.toJSON());
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

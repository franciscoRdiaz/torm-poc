package org.elastest.urjc.torm.utils;

import java.io.IOException;
import java.io.OutputStream;

import org.elastest.urjc.torm.api.ws.LogsByStompOverWSController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.core.async.ResultCallbackTemplate;
import com.github.dockerjava.core.command.ExecStartResultCallback;

public class ExecStartResultCallbackWebsocket extends ResultCallbackTemplate<ExecStartResultCallback, Frame> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExecStartResultCallback.class);

	@Autowired
	private LogsByStompOverWSController logsByStompOverWSController;

	private OutputStream stdout, stderr;

	public ExecStartResultCallbackWebsocket(OutputStream stdout, OutputStream stderr) {
		this.stdout = stdout;
		this.stderr = stderr;
	}

	public ExecStartResultCallbackWebsocket() {
		this(null, null);
	}

	@Override
	public void onNext(Frame frame) {
		if (frame != null) {
			try {
				switch (frame.getStreamType()) {
				case STDOUT:
				case RAW:
					if (stdout != null) {
						try {
							logsByStompOverWSController.sendLogs(frame.toString());
						} catch (Exception e) {
							//ToDo
						}
						stdout.write(frame.getPayload());
						stdout.flush();
					}
					break;
				case STDERR:
					if (stderr != null) {
						try {
							logsByStompOverWSController.sendLogs("Stderr: " + frame.toString());
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
}

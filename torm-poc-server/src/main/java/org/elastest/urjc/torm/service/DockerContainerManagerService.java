package org.elastest.urjc.torm.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.elastest.urjc.torm.api.data.DockerContainerInfo;
import org.elastest.urjc.torm.utils.ExecStartResultCallbackWebsocket;
import org.elastest.urjc.torm.websocket.client.WebSocketClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.Info;
import com.github.dockerjava.api.model.Ports;
import com.github.dockerjava.api.model.Ports.Binding;
import com.github.dockerjava.api.model.Volume;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.command.PullImageResultCallback;

@Service
public class DockerContainerManagerService {
	
	private static final String image = "edujgurjc/torm-test-01";
	private static final String volumeDirectory = "/reports";
	private static final String appDirectory = "/torm/torm-test-01";
	
	@Autowired
	private WebSocketClient webSocketClient;
	
	private DockerClient dockerClient;
	private CreateContainerResponse container;
		
	public DockerContainerInfo createDockerContainer() {

		this.dockerClient = DockerClientBuilder.getInstance().build();
	
//		DockerClientConfig config =
//		DefaultDockerClientConfig.createDefaultConfigBuilder()
//		.withDockerHost("tcp://192.168.99.100:2376")
//		.build();
//
//		this.dockerClient = DockerClientBuilder.getInstance(config).build();
		
		Info info = this.dockerClient.infoCmd().exec();
		System.out.println("Info: " + info);

		ExposedPort tcp8080 = ExposedPort.tcp(8080);

		Ports portBindings = new Ports();
		portBindings.bind(tcp8080, Binding.bindPort(8088));


		Volume volume1 = new Volume(volumeDirectory);

		this.dockerClient.pullImageCmd(image).exec(new PullImageResultCallback()).awaitSuccess();

		this.container = this.dockerClient.createContainerCmd(image).withExposedPorts(tcp8080)
				.withPortBindings(portBindings).withVolumes(volume1).withBinds(new Bind("/var" + appDirectory, volume1))
				.exec();

		this.dockerClient.startContainerCmd(this.container.getId()).exec();
		
		this.manageLogs();

		DockerContainerInfo dockerContainerInfo = new DockerContainerInfo();
		dockerContainerInfo.setId(this.container.getId());

		try {
			this.dockerClient.removeImageCmd(image).withForce(true).exec();
		} catch (NotFoundException e) {
			// just ignore if not exist
		}

		return dockerContainerInfo;
	}
	
	
	public void manageLogs(){
		webSocketClient.stomp();
		
		FileOutputStream fop = null;
		File file;

		try {
			file = new File("/var" + appDirectory + "/log.txt");
			fop = new FileOutputStream(file);

			if (!file.exists()) {
				file.createNewFile();
			}

			ExecStartResultCallbackWebsocket loggingCallback = new ExecStartResultCallbackWebsocket(fop, fop);

			try {
				this.dockerClient.logContainerCmd(this.container.getId()).withStdErr(true).withStdOut(true).withFollowStream(true)
						.exec(loggingCallback).awaitCompletion();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fop != null) {
					fop.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

package org.elastest.urjc.torm.service;

import org.elastest.urjc.torm.api.data.DockerContainerInfo;
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
import com.github.dockerjava.core.command.ExecStartResultCallback;
import com.github.dockerjava.core.command.PullImageResultCallback;

@Service
public class DockerContainerManagerService {
	
	public DockerContainerInfo createDockerContainer(){
		String image = "edujgurjc/torm-test-01";
		String volumeDirectory = "/reports";
		String appDirectory = "/torm/torm-test-01";
		
		DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
				.withDockerHost("tcp://192.168.99.100:2376").build();
		
		DockerClient dockerClient = DockerClientBuilder.getInstance(config).build();

		Info info = dockerClient.infoCmd().exec();
		System.out.println("Hola: "+info);

		ExposedPort tcp8080 = ExposedPort.tcp(8080);
		//ExposedPort tcp3306 = ExposedPort.tcp(3306);

		Ports portBindings = new Ports();
		portBindings.bind(tcp8080, Binding.bindPort(8088));
		//portBindings.bind(tcp3306, Binding.bindPort(3366));

		//String envVar = "MYSQL_PASS=\"admin\"";
		
		Volume volume1 = new Volume(volumeDirectory);
		
		dockerClient.pullImageCmd(image).exec(new PullImageResultCallback()).awaitSuccess();
		
		CreateContainerResponse container = dockerClient.createContainerCmd(image)
			.withExposedPorts(tcp8080)
			.withPortBindings(portBindings)
			.withVolumes(volume1)
			.withBinds(new Bind("/var" + appDirectory, volume1))
			//.withEnv(envVar)
			.exec();
				
		dockerClient.startContainerCmd(container.getId()).exec();

        ExecStartResultCallback loggingCallback = new ExecStartResultCallback(System.out, System.err);
        
		try {
			dockerClient.logContainerCmd(container.getId()).withStdErr(true).withStdOut(true).withFollowStream(true).exec(loggingCallback).awaitCompletion();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		DockerContainerInfo dockerContainerInfo = new DockerContainerInfo();
		dockerContainerInfo.setId(container.getId());
		
		  try {
	            dockerClient.removeImageCmd(image).withForce(true).exec();
	        } catch (NotFoundException e) {
	            // just ignore if not exist
	        }
		
		return dockerContainerInfo;
	}

}

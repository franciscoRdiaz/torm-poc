package org.elastest.urjc.torm.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.maven.plugins.surefire.report.ReportTestSuite;
import org.apache.maven.plugins.surefire.report.SurefireReportParser;
import org.apache.maven.reporting.MavenReportException;
import org.elastest.urjc.torm.api.data.TestExecutionInfo;
import org.elastest.urjc.torm.api.data.TestExecutionInfoExt;
import org.elastest.urjc.torm.api.data.EndExecutionMessage;
import org.elastest.urjc.torm.utils.ExecStartResultCallbackWebsocket;
import org.elastest.urjc.torm.utils.StompMessageSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class TestManagerService {

	private static String image = "edujgurjc/torm-test-01";
	private static final String volumeDirectory = "/resources";
	private static final String volumeDirectory2 = "/testcontainers-java-examples/selenium-container";
	private static final String appDirectory = "/torm/torm-test-01";
	private static final String topicEndExecutionMessage = "/topic/endExecutionTest";
	private static final String topicLogTrace = "/topic/logs";

//	@Autowired
//	private ExecStartResultCallbackWebsocket execStartResultCallbackWebsocket;
	
	@Autowired
    private ApplicationContext context;
	
	@Autowired
	private StompMessageSenderService stompMessageSenderService;

	private DockerClient dockerClient;
	private CreateContainerResponse container;

	public TestExecutionInfo executeTest(TestExecutionInfo testExecutionInfo) {
		
		this.image = testExecutionInfo.getImageName();
		
		//this.dockerClient = DockerClientBuilder.getInstance().build();
//		docker run -v /testcontainers-java-examples/selenium-container/resources:/resources -v /testcontainers-java-examples/selenium-container:/testcontainers-java-examples/selenium-container -e "DOCKER_HOST=tcp://172.17.0.1:2376" -p 6080:6080 edujgurjc/torm-test-02


		 DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
		 .withDockerHost("tcp://192.168.99.101:2376")
		 .build();
		 this.dockerClient = DockerClientBuilder.getInstance(config).build();

		Info info = this.dockerClient.infoCmd().exec();
		System.out.println("Info: " + info);

		ExposedPort tcp6080 = ExposedPort.tcp(6080);

		Ports portBindings = new Ports();
		portBindings.bind(tcp6080, Binding.bindPort(6080));
		
		String envVar = "DOCKER_HOST=tcp://172.17.0.1:2376";

		Volume volume2 = new Volume(volumeDirectory2);
		
		this.dockerClient.pullImageCmd(image).exec(new PullImageResultCallback()).awaitSuccess();

		this.container = this.dockerClient.createContainerCmd(image).withExposedPorts(tcp6080)
				.withPortBindings(portBindings).withVolumes(volume2).withBinds(new Bind("/testcontainers-java-examples/selenium-container", volume2)).withEnv(envVar)
				.exec();

		this.dockerClient.startContainerCmd(this.container.getId()).exec();

		this.manageLogs();
		
		
		testExecutionInfo.setId(this.container.getId());

		try {
			this.dockerClient.removeImageCmd(image).withForce(true).exec();
		} catch (NotFoundException e) {
			// just ignore if not exist
		}

		return testExecutionInfo;
	}

	public void manageLogs() {
		FileWriter file = null;
		PrintWriter pw = null;

		try {

			file = new FileWriter("D:/logs/torm/log.txt");
			pw = new PrintWriter(file);

			ExecStartResultCallbackWebsocket execStartResultCallbackWebsocket = context.getBean(ExecStartResultCallbackWebsocket.class);			
			execStartResultCallbackWebsocket.setStdout(pw);
			execStartResultCallbackWebsocket.setStderr(pw);

			try {
				this.dockerClient.logContainerCmd(this.container.getId()).withStdErr(true).withStdOut(true)
						.withFollowStream(true).exec(execStartResultCallbackWebsocket).awaitCompletion();
								
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (file != null) {
					file.close();
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		this.saveTestSuite();
		stompMessageSenderService.sendStompMessage(topicEndExecutionMessage, new EndExecutionMessage("END"));		
		
	}

	public void saveTestSuite() {
		File surefireXML = new File("D:/logs/torm/");
		List<File> reportsDir = new ArrayList<>();
		reportsDir.add(surefireXML);

		SurefireReportParser surefireReport = new SurefireReportParser(reportsDir, new Locale("en", "US"), null);
		try {
			List<ReportTestSuite> testSuites = surefireReport.parseXMLReportFiles();
			
			ObjectMapper mapper = new ObjectMapper();
			//Object to JSON in file
			try {
				mapper.writeValue(new File("D:/logs/torm/testsuites.json"), testSuites);
			} catch (JsonGenerationException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch (MavenReportException e) {
			e.printStackTrace();
		}
	}
}

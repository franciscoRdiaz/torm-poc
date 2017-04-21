package org.elastest.urjc.torm.api;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.elastest.urjc.torm.api.data.DockerContainerInfo;
import org.elastest.urjc.torm.service.DockerContainerManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/containers")
public class DockerContainerManagerController {

	@Autowired
	private DockerContainerManagerService dockerContainerManagerService;

	@CrossOrigin(origins = "http://localhost:4200")
	@RequestMapping(value = "/", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	public DockerContainerInfo createDockerContainer(@RequestBody DockerContainerInfo dockerContainerInfo) {

		return dockerContainerManagerService.createDockerContainer();
	}

	@CrossOrigin(origins = "http://localhost:4200")
	@RequestMapping(value = "/id", method = RequestMethod.PUT)
	@ResponseStatus(HttpStatus.OK)
	public DockerContainerInfo startDockerContainer(@PathVariable String id,
			@RequestBody DockerContainerInfo dockerContainerInfo) {
		// DockerContainerInfo dockerContainerInfo = new DockerContainerInfo();

		return dockerContainerInfo;
	}

	@CrossOrigin(origins = "http://localhost:4200")
	@RequestMapping(value = "/testInfo", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public String getTestResults() {
		String testResult, line;
		testResult = "";
		InputStream fis;
		try {
			fis = new FileInputStream("D:/logs/torm/testsuites.json");
			InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
			BufferedReader br = new BufferedReader(isr);
			try {
				while ((line = br.readLine()) != null) {
					testResult += line;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return testResult;
	}

}

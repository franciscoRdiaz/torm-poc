package org.elastest.urjc.torm.api;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.elastest.urjc.torm.api.data.TestExecutionInfo;
import org.elastest.urjc.torm.api.data.TestExecutionInfoExt;
import org.elastest.urjc.torm.service.DockerContainerManagerService;
import org.elastest.urjc.torm.service.TestManagerService;
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
public class TestManagerController {

	@Autowired
	private DockerContainerManagerService dockerContainerManagerService;
	
	@Autowired
	private TestManagerService testManagerService;

	@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8080"})
	@RequestMapping(value = "/", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	public TestExecutionInfo createDockerContainer(@RequestBody TestExecutionInfo dockerContainerInfo) {

		return dockerContainerManagerService.executeTest();
	}
	
	@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8080"})
	@RequestMapping(value = "/external/api/", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	public TestExecutionInfoExt createAndExecuteTest(@RequestBody TestExecutionInfoExt testExecutionInfoExt) {
		System.out.println("Image name received" + testExecutionInfoExt.getImageName());
		
		//return testExecutionInfoExt;

		return (TestExecutionInfoExt) testManagerService.executeTest(testExecutionInfoExt);
	}

	@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8080"})
	@RequestMapping(value = "/id", method = RequestMethod.PUT)
	@ResponseStatus(HttpStatus.OK)
	public TestExecutionInfo startDockerContainer(@PathVariable String id,
			@RequestBody TestExecutionInfo dockerContainerInfo) {
		// DockerContainerInfo dockerContainerInfo = new DockerContainerInfo();

		return dockerContainerInfo;
	}

	@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8080"})
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

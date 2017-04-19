package org.elastest.urjc.torm.api;

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
	@RequestMapping (value = "/", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	public DockerContainerInfo createDockerContainer(@RequestBody DockerContainerInfo dockerContainerInfo){
		
		return dockerContainerManagerService.createDockerContainer();		
	}
	
	@RequestMapping (value = "/id", method = RequestMethod.PUT)
	@ResponseStatus(HttpStatus.OK)
	public DockerContainerInfo startDockerContainer(@PathVariable String id, @RequestBody DockerContainerInfo dockerContainerInfo){
		//DockerContainerInfo dockerContainerInfo = new DockerContainerInfo();
		
		return dockerContainerInfo;
	}

}

package org.elastest.urjc.torm.api.ws;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LogsByStompOverWSController {

	private SimpMessagingTemplate template;
	
	@Autowired
	public LogsByStompOverWSController(SimpMessagingTemplate template){
		this.template = template;
	}
	
	@MessageMapping("/logs")
	@SendTo("topic/logs")
	public String logsHandler(String mess) throws Exception	{
		
		Thread.sleep(10000);		
		return "["+mess+"]: The logs would go here";
	}
	
	@RequestMapping(path="/logs")
	public void sendLogs(String logs){
		this.template.convertAndSend("/topic/logs","Logs:"+logs);
	}
	
}

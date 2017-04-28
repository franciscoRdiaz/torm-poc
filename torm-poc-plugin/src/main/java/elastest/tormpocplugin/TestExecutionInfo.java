package elastest.tormpocplugin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestExecutionInfo {

	private String id;
	
	private String imageName;

	private String testUrl;
		
	public TestExecutionInfo(){		
	}
	
	public TestExecutionInfo(String id, String imageName, String testUrl){
		this.id = id;
		this.imageName = imageName;
		this.testUrl = testUrl;
	}
	
	public String getId() {
		return id;
	}	

	public void setId(String id) {
		this.id = id;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String name) {
		this.imageName = name;
	}
	
	public String getTestUrl() {
		return testUrl;
	}

	public void setTestUrl(String testUrl) {
		this.testUrl = testUrl;
	}

	
	public String toJSON(){
		ObjectMapper mapper = new ObjectMapper();

		//Object to JSON in String
		String jsonInString;
		try {
			jsonInString = mapper.writeValueAsString(this);
			return jsonInString;

		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
}

package org.elastest.urjc.torm.api.data;

public class TestExecutionInfo {

	private String id;
	
	private String imageName;
	
	public TestExecutionInfo(){		
	}
	
	public TestExecutionInfo(String id, String imageName){
		this.id = id;
		this.imageName = imageName;
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
}

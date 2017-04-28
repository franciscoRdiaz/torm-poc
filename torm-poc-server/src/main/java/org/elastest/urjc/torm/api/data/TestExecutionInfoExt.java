package org.elastest.urjc.torm.api.data;

public class TestExecutionInfoExt extends TestExecutionInfo {

	private String testUrl;
	
	public TestExecutionInfoExt(){
		
	}
	
	public TestExecutionInfoExt(String id, String name, String testUrl ){
		super(id, name);
		this.testUrl = testUrl;
	}
	

	public String getTestUrl() {
		return testUrl;
	}

	public void setTestUrl(String testUrl) {
		this.testUrl = testUrl;
	}
	
	
}

package elastest.tormpocplugin;

import java.util.List;

public class LogFragmentContainer {
	
	private List<LogTrace> logsFragments;
	
	public LogFragmentContainer(){
		
	}

	public List<LogTrace> getLogsFragments() {
		return logsFragments;
	}

	public void setLogsFragments(List<LogTrace> logsFragments) {
		this.logsFragments = logsFragments;
	}
}

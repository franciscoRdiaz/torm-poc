package elastest.tormpocplugin;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.servlet.ServletException;

import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.RestTemplateXhrTransport;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;
import jenkins.tasks.SimpleBuildStep;
import net.sf.json.JSONObject;

/**
 * Sample {@link Builder}.
 *
 * <p>
 * When the user configures the project and enables this builder,
 * {@link DescriptorImpl#newInstance(StaplerRequest)} is invoked
 * and a new {@link NewPipelineCommandStep} is created. The created
 * instance is persisted to the project configuration XML by using
 * XStream, so this allows you to use instance fields (like {@link #imageName})
 * to remember the configuration.
 *
 * <p>
 * When a build is performed, the {@link #perform} method will be invoked. 
 *
 * @author Kohsuke Kawaguchi
 */
public class NewPipelineCommandStep extends Builder implements SimpleBuildStep {

    private final String imageName;
    
    private final String command;
    
   // private static CountDownLatch latch;
    
    private String endMessage;
    
    private WSocketClientToLogs wsClientToLogs;
    private final String webSocketAddress = "ws://localhost:8090/logs";
    
    

    // Fields in config.jelly must match the parameter names in the "DataBoundConstructor"
    @DataBoundConstructor
    public NewPipelineCommandStep(String command,String imageName) {
        this.imageName = imageName;
        this.command = command;
    }

    public String getCommand() {
		return command;
	}

	/**
     * We'll use this from the {@code config.jelly}.
     */
    public String getImageName() {
        return imageName;
    }

    @Override
    public void perform(Run<?,?> build, FilePath workspace, Launcher launcher, TaskListener listener) {
        // This is where you 'build' the project.
        // Since this is a dummy, we just say 'hello world' and call that a build.
    	ObjectMapper objetMapper = new ObjectMapper();
    	
    	listener.getLogger().println("Test writting in log:"+imageName);
    	listener.getLogger().println("http://localhost:4200/#/test-manager");
    	
    	endMessage = "";   	
    	
		try {			

			Client client = Client.create();

			String elastestHostURL = getDescriptor().getelastestHost();
			WebResource webResource = client.resource(elastestHostURL);

			TestExecutionInfo testExecutionInfo = new TestExecutionInfo();
			testExecutionInfo.setImageName(imageName);			

			ClientResponse response = webResource.type("application/json").post(ClientResponse.class, testExecutionInfo.toJSON());
			TestExecutionInfo output = objetMapper.readValue(response.getEntity(String.class), TestExecutionInfo.class);
			listener.getLogger().println("Server Response:"+output.getTestUrl());
			
			//WSocketClientToLogs.latch = new CountDownLatch(1);
			//ClientManager clientWs = ClientManager.createClient();
			CountDownLatch waitForEndOfMessage = new CountDownLatch(1);
						
			try{
				listener.getLogger().println("Start Websocket config.");
				//WebSocketContainer container = ContainerProvider.getWebSocketContainer();
				List<Transport> transports = new ArrayList<>(2);
				transports.add(new WebSocketTransport(new StandardWebSocketClient()));
				transports.add(new RestTemplateXhrTransport());

				WebSocketClient webSocketClient = new SockJsClient(transports);
				//WebSocketClient webSocketClient = new StandardWebSocketClient();
				//WebSocketClient transport = new StandardWebSocketClient(container);
				WebSocketStompClient stompClient = new WebSocketStompClient(webSocketClient);
				stompClient.setMessageConverter(new StringMessageConverter());
				LogStompSessionHandler handler = new LogStompSessionHandler(waitForEndOfMessage);	
				listener.getLogger().println("Trying to connect 4.");
				stompClient.connect("ws://localhost:8090/logs", handler);
				listener.getLogger().println("Stablished connection");
				waitForEndOfMessage.await();
				
				
				 //clientWs.connectToServer(new WSocketClientToLogs(listener), new URI("ws://localhost:8090/topic/logs"));
				 //WSocketClientToLogs.latch.await();
			} catch (Exception e) {
				listener.getLogger().println("Error message:"+e.getMessage() +"-Cause:"+ e.getCause());				
	            throw new RuntimeException(e);
	        }
			

			if (response.getStatus() != 201) {
				throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
			}			
			
			
			listener.getLogger().println("End test:"+imageName);
			

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			listener.getLogger().println("Error:"+e.getMessage());
		}

        
    }
    
	private void initializeWebSocket(TaskListener listener) throws URISyntaxException {
		// ws://localhost:7101/CinemaMonitor/cinemaSocket/
		listener.getLogger().println("REST service: open websocket client at " + webSocketAddress);
		wsClientToLogs = new WSocketClientToLogs(new URI(webSocketAddress), listener);
	}

    // Overridden for better type safety.
    // If your plugin doesn't really define any property on Descriptor,
    // you don't have to do this.
    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl)super.getDescriptor();
    }

    /**
     * Descriptor for {@link NewPipelineCommandStep}. Used as a singleton.
     * The class is marked as public so that it can be accessed from views.
     *
     * <p>
     * See {@code src/main/resources/hudson/plugins/hello_world/HelloWorldBuilder/*.jelly}
     * for the actual HTML fragment for the configuration screen.
     */
    @Symbol("elastest")
    @Extension // This indicates to Jenkins that this is an implementation of an extension point.
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {
        /**
         * To persist global configuration information,
         * simply store it in a field and call save().
         *
         * <p>
         * If you don't want fields to be persisted, use {@code transient}.
         */
        private String elastestHost;

        /**
         * In order to load the persisted global configuration, you have to 
         * call load() in the constructor.
         */
        public DescriptorImpl() {
            load();
        }

        /**
         * Performs on-the-fly validation of the form field 'name'.
         *
         * @param value
         *      This parameter receives the value that the user has typed.
         * @return
         *      Indicates the outcome of the validation. This is sent to the browser.
         *      <p>
         *      Note that returning {@link FormValidation#error(String)} does not
         *      prevent the form from being saved. It just means that a message
         *      will be displayed to the user. 
         */
        public FormValidation doCheckName(@QueryParameter String value)
                throws IOException, ServletException {
            if (value.length() == 0)
                return FormValidation.error("Please set a name");
            if (value.length() < 4)
                return FormValidation.warning("Isn't the name too short?");
            return FormValidation.ok();
        }

        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            // Indicates that this builder can be used with all kinds of project types 
            return true;
        }

        /**
         * This human readable name is used in the configuration screen.
         */
        public String getDisplayName() {
            return "Elastest Step Command";
        }
        
        

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            // To persist global configuration information,
            // set that to properties and call save().
            elastestHost = formData.getString("elastestHost");
            // ^Can also use req.bindJSON(this, formData);
            //  (easier when there are many fields; need set* methods for this, like setelastestHost)
            save();
            return super.configure(req,formData);
        }

        /**
         * This method returns true if the global configuration says we should speak French.
         *
         * The method name is bit awkward because global.jelly calls this method to determine
         * the initial state of the checkbox by the naming convention.
         */
        public String getelastestHost() {
            return elastestHost;
        }
    }
}


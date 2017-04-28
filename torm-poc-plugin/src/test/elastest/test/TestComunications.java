package elastest.test;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class TestComunications {

	@Test
	public void comunicationWithTormTest(){
		try {			
			
			Client client = Client.create();

			WebResource webResource = client
			   .resource("http://localhost:8090/containers/externa/api/");

			String input = "{\"id\":\"a\",\"name\":\"b\"}";

			ClientResponse response = webResource.type("application/json")
			   .post(ClientResponse.class, input);

			if (response.getStatus() != 201) {
				throw new RuntimeException("Failed : HTTP error code : "
				     + response.getStatus());
			}

			System.out.println("Output from Server .... \n");
			String output = response.getEntity(String.class);
			System.out.println(output);
					
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
}

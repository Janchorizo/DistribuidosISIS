package server;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("dispatcher")
@Singleton
public class Dispatcher {
	private Process proceso1, proceso2;
	
	public Dispatcher(){
		this.proceso1 = new Process();
		this.proceso2 = new Process();
		
		proceso1.start();
		proceso2.start();
	}
	
	@POST
	@Path(" ")
	public void __(){
		
	}
}

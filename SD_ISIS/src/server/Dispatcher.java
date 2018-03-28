package server;

import java.util.ArrayList;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/dispatcher")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Singleton
public class Dispatcher {
	private Process proceso1, proceso2;
	private ArrayList<String[]> processList;
	
	public Dispatcher(){
		processList = new ArrayList<String[]>();
		
		this.proceso1 = new Process( "1", this.processList);
		this.proceso2 = new Process( "2", this.processList);
		
		proceso1.start();
		proceso2.start();
	}
	
	@POST
	@Path("mandarMensaje")
	public void mandarMensaje(@PathParam("id") int id, mensajes.Mensaje msg){
		//coger el proceso correspondiente
		this.proceso1.recibirMensaje( msg);
	}
	
	@POST
	@Path("mandarPropuesta")
	public void mandarPropuesta(@PathParam("id") int id, mensajes.Propuesta msg){
		//coger el proceso correspondiente
		this.proceso1.recibirPropuesta( msg);
	}
	
	@POST
	@Path("mandarAcuerdo")
	public void mandarAcuerdo(@PathParam("id") int id, mensajes.Acuerdo msg){
		//coger el proceso correspondiente
		this.proceso1.recibirAcuerdo( msg);
	}
}

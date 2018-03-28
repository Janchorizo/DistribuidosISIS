package server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/dispatcher")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Singleton
public class Dispatcher {
	private HashMap<String, Process> procesosLocales;
	private ArrayList<String[]> procesos;
	
	public Dispatcher(){
		if( this.procesosLocales == null){
			this.procesosLocales =  new HashMap<String, Process>();
			
			this.procesosLocales.put("1", new Process( "1"));
			this.procesosLocales.put("2", new Process( "2"));
			
			this.procesos = new ArrayList<String[]>();
			
			for( Process proceso : procesosLocales.values()){
				proceso.start();
			}	
		}
	}
	
	@PUT
	@Path("mandarMensaje")
	public void mandarMensaje( mensajes.Mensaje msg){
		//coger el proceso correspondiente
		this.procesosLocales.get(msg.receptor).recibirMensaje( msg);
	}
	
	@PUT
	@Path("mandarPropuesta")
	public void mandarPropuesta( mensajes.Propuesta msg){
		//coger el proceso correspondiente
		this.procesosLocales.get(msg.receptor).recibirPropuesta( msg);
	}
	
	@PUT
	@Path("mandarAcuerdo")
	public void mandarAcuerdo( mensajes.Acuerdo msg){
		//coger el proceso correspondiente
		this.procesosLocales.get(msg.receptor).recibirAcuerdo( msg);
	}
}

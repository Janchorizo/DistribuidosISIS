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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/dispatcher")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Singleton
public class Dispatcher {
	private HashMap<String, Process> procesosLocales;
	private ArrayList<ProcessDir> procesos;
	
	public Dispatcher(){
		if( this.procesosLocales == null){
			this.procesosLocales =  new HashMap<String, Process>();
			
			this.procesosLocales.put("1", new Process( "1"));
			this.procesosLocales.put("2", new Process( "2"));
			
			this.procesos = new ArrayList<ProcessDir>();
			this.procesos.add(new ProcessDir( "1", "localhost"));
			this.procesos.add(new ProcessDir( "2", "localhost"));
			
			for( Process proceso : procesosLocales.values()){
				proceso.putProcesos( procesos);
				proceso.start();
			}	
		}
	}
	
	@PUT
	@Path("mandarMensaje")
	public void mandarMensaje(@QueryParam("proceso") String proceso, mensajes.Mensaje msg){
		//coger el proceso correspondiente
		this.procesosLocales.get(proceso).recibirMensaje( msg);
	}
	
	@PUT
	@Path("mandarPropuesta")
	public void mandarPropuesta(@QueryParam("proceso") String proceso, mensajes.Propuesta msg){
		//coger el proceso correspondiente
		this.procesosLocales.get(proceso).recibirPropuesta( msg);
	}
	
	@PUT
	@Path("mandarAcuerdo")
	public void mandarAcuerdo(@QueryParam("proceso") String proceso, mensajes.Acuerdo msg){
		//coger el proceso correspondiente
		this.procesosLocales.get(proceso).recibirAcuerdo( msg);
	}
	
	@PUT
	@Path("forzarMulticast")
	public void multicast(){
		for( Process proceso : procesosLocales.values()){
			mensajes.Mensaje msg = new mensajes.Mensaje();
			msg.id = "12"; 
			msg.emisor = "dispatcher";
			msg.orden = 2;
			msg.cuerpo = "Esto es una prueba";
			
			System.out.println("despachando mensaje");
			proceso.multicast( msg, "mandarMensaje");
		}
	}
}

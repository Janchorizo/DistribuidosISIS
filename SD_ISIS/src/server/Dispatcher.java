package server;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;


/**
 * Despachador para el sistema distribuido
 * <br><br>
 * Cada instancia tiene asociados unos procesos a los que enruta los mensajes que 
 * tengan como destino el mismo.
 * Los mensajes llegan a través de una api REST expuesta, que utiliza argumentos en
 * forma de documento JSON para transimitir el contenido.
 * <br>
 * Cada instancia se encarga de mantener actualizado un directorio con el resto de los
 * despachadores en la red local, y los procesos que tienen cada uno.
 * <br>
 * Ruta : /dispatcher
 * 
 * @author alex
 * @see Process
 */
@Path("/dispatcher")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Singleton
public class Dispatcher {
	private HashMap<String, Process> procesosLocales;
	private ArrayList<ProcessDir> procesos;
	private String logDir;
	
	public Dispatcher(){
		this.logDir = "/home/i0917867/Documentos";
		this.procesosLocales =  new HashMap<String, Process>();
		
		this.procesosLocales.put("1", new Process( "1", logDir));
		this.procesosLocales.put("2", new Process( "2", logDir));
		
		this.procesos = new ArrayList<ProcessDir>();
		this.procesos.add(new ProcessDir( "1", "localhost"));
		this.procesos.add(new ProcessDir( "2", "localhost"));
		//this.procesos.add(new ProcessDir( "3", "192.168.1.110"));
		//this.procesos.add(new ProcessDir( "4", "192.168.1.110"));

		for( Process proceso : procesosLocales.values()){
			proceso.putProcesos( procesos);
			proceso.start();
		}	
	}
	
	@PUT
	@Path("start")
	public void start(){
		System.out.println(" server started");
	}
	
	/**
	 * Enruta el mensaje 'msg' al proceso 'process', siendo el proceso local
	 * al objeto Dispatcher
	 * {@literal} Path : mandarMensaje 
	 * @see Dispatcher
	 * @see mensajes.Mensaje
	 * @param proceso Identificador del proceso local
	 * @param msg Mensaje a enrutar
	 */
	@PUT
	@Path("mandarMensaje")
	public void mandarMensaje(@QueryParam("proceso") String proceso, mensajes.Mensaje msg){
		if( msg != null)
			this.procesosLocales.get(proceso).recibirMensaje( msg);
	}
	
	/**
	 * Enruta la propuesta de orden 'msg' al proceso 'process', siendo el proceso
	 * local al objeto Dispatcher
	 * @see Dispatcher
	 * @see mensajes.Propuesta
	 * @param proceso Identificador del proceso local
	 * @param msg Mensaje a enrutar
	 */
	@PUT
	@Path("mandarPropuesta")
	public void mandarPropuesta(@QueryParam("proceso") String proceso, mensajes.Propuesta msg){
		if( msg != null)
			this.procesosLocales.get(proceso).recibirPropuesta( msg);
	}
	
	/**
	 * Enruta el acuerdo de orden 'msg' al proceso 'process', siendo el proceso local
	 * al objeto Dispatcher
	 * @see Dispatcher
	 * @see mensajes.Acuerdo
	 * @param proceso Identificador del proceso local
	 * @param msg Mensaje a enrutar
	 */
	@PUT
	@Path("mandarAcuerdo")
	public void mandarAcuerdo(@QueryParam("proceso") String proceso, mensajes.Acuerdo msg){
		if( msg != null)
			this.procesosLocales.get(proceso).recibirAcuerdo( msg);
	}
	
	/**
	 * Envía un mensaje de prueba a los procesos locales para que realicen una 
	 * multidifucsión del mismo.
	 * Usar únicamente con fines de debuggeo
	 */
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
	
	@GET 
	@Path("recuperarResultados")
	@Produces( MediaType.TEXT_PLAIN)
	public String recuperar( @QueryParam("proceso") String proceso){
		java.nio.file.Path rutaLog;
		rutaLog = Paths.get(logDir+"isis_"+proceso+".log");
		String r = "";
		if( this.procesosLocales.containsKey(proceso))
			try {
				r = new String(Files.readAllBytes(rutaLog), StandardCharsets.UTF_8);
			} catch (IOException e) {
				e.printStackTrace();
			} 
		return r;
	}
	
	@GET 
	@Path("comprobarResultados")
	@Produces( MediaType.TEXT_PLAIN)
	public String comprobar(){
		Client client; 
		URI uri;
		WebTarget target;
		FileWriter fw;
		BufferedWriter bw;
		PrintWriter pw;
		String msg = "Comprobación de los resultados";
		
		for( ProcessDir proceso : this.procesos){
			client = ClientBuilder.newClient();
			uri = UriBuilder.fromUri( "http://"+ proceso.dispatcherIp +":8080/SD_ISIS/dispatcher/recuperarResultados/").build();
			target = client.target( uri);
			
			String respuesta = target
				.queryParam("proceso", proceso.processId)
				.request(MediaType.TEXT_PLAIN)
				.get( String.class);
			
			try {
				fw = new FileWriter(logDir+"/respuesta"+proceso.processId+".txt");
			
				bw = new BufferedWriter(fw);
				pw = new PrintWriter(bw); 
			
				pw.println(respuesta);
				
				pw.flush();
				pw.close();
				bw.close();
				fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return msg;
	}
}

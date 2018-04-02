package server;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
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
	private String ipDescubrimientoServicios;
	private int puertoDescubrimientoServicios;
	
	public Dispatcher(){
		this.puertoDescubrimientoServicios = 8888;
		this.ipDescubrimientoServicios = "225.0.0.1";
		
		if( this.procesosLocales == null){
			this.procesosLocales =  new HashMap<String, Process>();
			
			this.procesosLocales.put("1", new Process( "1"));
			this.procesosLocales.put("2", new Process( "2"));
			
			this.procesos = new ArrayList<ProcessDir>();
			this.procesos.add(new ProcessDir( "1", "localhost"));
			this.procesos.add(new ProcessDir( "2", "localhost"));
			
			this.descubrirProcesos( 12);
			
			for( Process proceso : procesosLocales.values()){
				proceso.putProcesos( procesos);
				proceso.start();
			}	
		}
	}
	
	/**
	 * Se hace uso de un canal de multicast para la notificación y recibo de 
	 * notificaciones de otros Dispatchers, para descubrir sus procesos.
	 * @param timeout Tiempo dedicado al descubrimiento de servicicios
	 */
	private void descubrirProcesos( long timeout){
		try {
			MulticastSocket msk = new MulticastSocket( this.puertoDescubrimientoServicios);
			msk.joinGroup( InetAddress.getByName( this.ipDescubrimientoServicios));
			
			byte [] data = new byte [1024];
			DatagramPacket dpg = new DatagramPacket( data, data.length);
			
			for( int i = 0; i<4; i++){
				this.difundirProcesos();
				msk.receive(dpg);
				System.out.println( "Recibido " + new String( dpg.getData(), Charset.forName("UTF-8")));
			}
			
			msk.leaveGroup( InetAddress.getByName( this.ipDescubrimientoServicios));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Se manda la información sobre los procesos a una ip multicast
	 */
	private void difundirProcesos( ){
		try {
			MulticastSocket skt = new MulticastSocket( this.puertoDescubrimientoServicios);
			
			Object[] nombreProcesosLocales = this.procesosLocales.keySet().toArray();
			byte [] data = ( "dispatcher" +"-"+
					nombreProcesosLocales[0]+"-"+
					nombreProcesosLocales[1]).getBytes( Charset.forName("UTF-8"));
			DatagramPacket dpg = new DatagramPacket( data, 
					data.length, 
					InetAddress.getByName( this.ipDescubrimientoServicios), 
					this.puertoDescubrimientoServicios);
			//System.out.println( "Mensaje : " + new String( data, Charset.forName("UTF-8")));
			
			skt.send(dpg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		//coger el proceso correspondiente
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
		//coger el proceso correspondiente
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
		//coger el proceso correspondiente
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
}

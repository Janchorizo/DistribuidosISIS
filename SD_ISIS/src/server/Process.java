package server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.UriBuilder;

import mensajes.Msg;

public class Process extends Thread{
	private ArrayList<ProcessDir> dirProcesos;
	private ArrayList<mensajes.Mensaje> mensajesRecibidos;
	private ConcurrentHashMap<String, mensajes.Propuesta> propuestasRecibidas;
	private Semaphore sem_mensajes, sem_orden, sem_fichero, sem_terminado;
	private String id;
	private String rutaFichero;
	private int orden, acuerdos;
	
	public Process( String id){
		this.id = id;
		this.orden = 0;
		this.acuerdos = 0;
		this.sem_orden = new Semaphore( 1);
		this.sem_mensajes = new Semaphore( 1);
		this.sem_fichero = new Semaphore( 1);
		this.sem_terminado = new Semaphore( 0);
		
		this.rutaFichero = "isis_"+ this.id +".log";
		try{
			File fd = new File( this.rutaFichero);
			
			this.sem_fichero.acquire();
			if( fd.exists())
				fd.delete();
			else
				fd.createNewFile();
			this.sem_fichero.release(1);
		}catch( Exception e){
			e.printStackTrace();
		}
		
		this.mensajesRecibidos = new ArrayList<mensajes.Mensaje> ();
		this.propuestasRecibidas = new ConcurrentHashMap<String, mensajes.Propuesta> ();
	}
	
	public void putProcesos( ArrayList<ProcessDir> dirProcesos){
		this.dirProcesos = dirProcesos;
	}
	
	private int lc1(){
		int err = 0;
		try {
			this.sem_orden.acquire(1);
			this.orden = this.orden + 1;
			this.sem_orden.release(1);
		} catch (InterruptedException e) {
			System.err.println( "Process : Error : lc1");
			e.printStackTrace( );
			err = -1;
		}
		return err;
	}
	
	private int lc2( int orden2){
		int err = 0;
		try {
			this.sem_orden.acquire(1);
			this.orden = Math.max( this.orden, orden2) + 1;
			this.sem_orden.release(1);
		} catch (InterruptedException e) {
			System.err.println( "Process : Error : lc2");
			e.printStackTrace( );
			err = -1;
		}
		return err;
	}
	
	public void run(){
		mensajes.Mensaje msg;
		long tiempo;
		int contador;
		
		for( contador = 0; contador < 100; contador++){
			if( -1 == this.lc1())
				break;
			
			msg = new mensajes.Mensaje( this.id + String.format("%03d", contador), this.id, this.orden,
					"P" + this.id + " " + String.format("%03d", contador));
			this.multicast(msg, "mandarMensaje");
				
			try {	
				tiempo = (long)(1000*( 1 + 0.5*Math.random()));		
				Thread.sleep(tiempo);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {	
			tiempo = (long)(1000*( 1 + 0.5*Math.random()));		
			Thread.sleep(tiempo);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		try {
			this.sem_terminado.acquire( 1);
			System.out.println("Proceso "+this.id);
			for(mensajes.Mensaje m : this.mensajesRecibidos){
				System.out.println(m);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public int recibirMensaje( mensajes.Mensaje msg){
		int err = 0;
		if( -1 == this.lc2(msg.orden))
			return -1;
		
		try{
			this.sem_mensajes.acquire();
			this.mensajesRecibidos.add(msg);
			this.mensajesRecibidos.sort(mensajes.Msg.MsgOrderComparator);
			this.sem_mensajes.release(1);
		}catch( Exception e){
			e.printStackTrace();
		}
		
		mensajes.Propuesta prop = new mensajes.Propuesta( msg.id, this.id, this.orden, 0);
		
		ProcessDir receptor = null;
		for (ProcessDir pd : this.dirProcesos){
			if( pd.processId.equals( msg.emisor)){
				receptor = new ProcessDir( pd.processId, pd.dispatcherIp);
				break;
			}
		}
		if( receptor == null)
			throw new java.lang.RuntimeException("Not able to retrieve dir");
		
		this.unicast(prop, "mandarPropuesta", receptor);
		return( err);
	}
	
	public int recibirPropuesta( mensajes.Propuesta msg){
		int err = 0;
		mensajes.Propuesta prop;
		if( -1 == this.lc2(msg.orden))
			return -1;
		
		if( !this.propuestasRecibidas.containsKey(msg.id))
			this.propuestasRecibidas.put(msg.id, msg);
			
		prop = this.propuestasRecibidas.get(msg.id);
		prop.numPropuestas += 1;
		prop.orden = Math.max( prop.orden, msg.orden);
		
		if( prop.numPropuestas == this.dirProcesos.size()){
			mensajes.Acuerdo ac = new mensajes.Acuerdo( msg.id,
					this.id,
					prop.orden);
			this.multicast(ac, "mandarAcuerdo");
		}
		return( err);
	}
	
	public int recibirAcuerdo( mensajes.Acuerdo msg){
		int err = 0;
		mensajes.Mensaje msg_final;
		if( -1 == this.lc2(msg.orden))
			return -1;
		
		try{
			this.sem_mensajes.acquire();
			//Actualizar el orden del mensaje correspondiente y reordenar
			for( mensajes.Mensaje m : this.mensajesRecibidos){
				if( m.id.equals( msg.id)){
					m.definitivo = true;
					m.orden = msg.orden;
					this.mensajesRecibidos.sort( mensajes.Msg.MsgOrderComparator);
							
					break;
				}
			}
		}catch( Exception e){
			e.printStackTrace();
		}finally{
			this.sem_mensajes.release(1);
		}
		
		try{
			this.sem_mensajes.acquire();
			while(!this.mensajesRecibidos.isEmpty() && this.mensajesRecibidos.get(0).definitivo == true){
				msg_final = this.mensajesRecibidos.remove(0);
				File fd = new File( this.rutaFichero);
				
				this.sem_fichero.acquire();
				FileWriter fw = new FileWriter(fd, true);
				BufferedWriter bw = new BufferedWriter(fw);
				PrintWriter pw = new PrintWriter(bw); 
				
				pw.println(msg_final);
				
				pw.flush();
				pw.close();
				bw.close();
				fw.close();
				this.acuerdos += 1;
				this.sem_fichero.release(1);
			}
			
			this.sem_mensajes.release(1);
		
		}catch( Exception e){
			e.printStackTrace();
		}
		
		if( this.acuerdos == 400)
			this.sem_terminado.release(1);
		
		return( err);
	}
	
	public int multicast( Msg msg, String serviceUri){
		int err = 0;
		for( ProcessDir proceso : this.dirProcesos){
			err = unicast( msg, serviceUri, proceso);
			if( -1 == err)
				break;
			/*
			try {	
				long tiempo = (long)(1000*( 0.2 + 0.3*Math.random()));		
				Thread.sleep(tiempo);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}*/
		}
		return err;
	}
	
	public int unicast( Msg msg, String serviceUri, ProcessDir dir){		
		Client client; 
		URI uri;
		WebTarget target;
		
		int err = 0;
		msg.emisor = this.id;
		
		client = ClientBuilder.newClient();
		uri = UriBuilder.fromUri( "http://"+ dir.dispatcherIp +":8080/SD_ISIS/dispatcher").build();
		target = client.target( uri);
		
		target
			.path( serviceUri)
			.queryParam("proceso", dir.processId)
			.request()
			.header("Content-type", "application/json")
			.put( Entity.json(msg.toJSON()));
			
		return( err);
	}
}

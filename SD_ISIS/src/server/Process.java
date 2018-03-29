package server;

import java.net.URI;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.UriBuilder;

import mensajes.Msg;

public class Process extends Thread{
	private ArrayList<ProcessDir> dirProcesos;
	private Semaphore allDone;
	private String id;
	private int contador;
	private int orden;
	
	public Process( String id){
		this.id = id;
		this.orden = 0;
	}
	
	public void putProcesos( ArrayList<ProcessDir> dirProcesos){
		this.dirProcesos = dirProcesos;
	}
	
	public void run(){
		String mensaje;
		long tiempo;
		
		for( this.contador = 0; this.contador < 100; this.contador++){
			try {
				mensaje = this.id + " " + String.format("%03d", this.contador);	
				tiempo = (long)(1000*( 1 + 0.5*Math.random()));
						
				Thread.sleep(tiempo);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public int recibirMensaje( mensajes.Mensaje msg){
		int err = 0;
		this.orden = Math.max(this.orden, msg.orden) + 1;
		
		System.out.println("Proceso " + this.id + "\n" +
				"Recibido " + msg.id + " de " + msg.emisor + "\n" +
				"Orden : " + msg.orden + "\n" + 
				"Cuerpo : " + msg.cuerpo);
		
		return( err);
	}
	
	public int recibirPropuesta( mensajes.Propuesta msg){
		int err = 0;
		this.orden = Math.max(this.orden, msg.orden) + 1;
		
		System.out.println("Proceso " + this.id + "\n" +
			"Recibido " + msg.id + " de " + msg.emisor + "\n" +
			"Orden : " + msg.orden +
			"Definitivo : " + msg.definitivo);
		
		return( err);
	}
	
	public int recibirAcuerdo( mensajes.Acuerdo msg){
		int err = 0;
		this.orden = Math.max(this.orden, msg.orden) + 1;
		
		System.out.println("Proceso " + this.id + "\n" +
			"Recibido " + msg.id + " de " + msg.emisor + "\n" +
			"Orden : " + msg.orden +
			"Definitivo : " + msg.definitivo);
		
		return( err);
	}
	
	public int multicast( Msg msg, String serviceUri){
		
		int err = 0;
		
		Client client = ClientBuilder.newClient();
		URI uri;
		WebTarget target; 
		
		for( ProcessDir proceso : this.dirProcesos){
			uri = UriBuilder.fromUri( "http://"+ proceso.dispatcherIp +":8080/SD_ISIS/dispatcher").build();
			target = client.target( uri);
			
			target
				.path( serviceUri)
				.queryParam("proceso", proceso.processId)
				.request()
				.header("Content-type", "application/json")
				.put( Entity.json(msg));
		}
		
		return( err);
	}
}

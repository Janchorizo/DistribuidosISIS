package server;

import java.net.URI;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.UriBuilder;

public class Process extends Thread{
	private ArrayList<String[]> processList;
	private Semaphore allDone;
	private String id;
	private int contador;
	private int orden;
	
	public Process( String id, ArrayList<String[]>processList){
		this.id = id;
		this.processList = new ArrayList<String[]>( processList);
		this.contador = 0;
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
		
		System.out.println("Proceso " + this.id + "\n" +
				"Recibido " + msg.id + " de " + msg.emisor + "\n" +
				"Orden : " + msg.orden +
				"Cuerpo : " + msg.cuerpo);
		
		return( err);
	}
	
	public int recibirPropuesta( mensajes.Propuesta msg){
		int err = 0;
		
		System.out.println("Proceso " + this.id + "\n" +
			"Recibido " + msg.id + " de " + msg.emisor + "\n" +
			"Orden : " + msg.orden +
			"Definitivo : " + msg.definitivo);
		
		return( err);
	}
	
	public int recibirAcuerdo( mensajes.Acuerdo msg){
		int err = 0;
		
		System.out.println("Proceso " + this.id + "\n" +
			"Recibido " + msg.id + " de " + msg.emisor + "\n" +
			"Orden : " + msg.orden +
			"Definitivo : " + msg.definitivo);
		
		return( err);
	}
	
	private int broadcastMsg( String msg, String serviceUri){
		
		int err = 0;
		/*
		Client client = ClientBuilder.newClient();
		URI uri;
		WebTarget target; 
		
		for( String[] process : this.processList){
			uri = UriBuilder.fromUri( );
			target = client.target( uri);
			
			target
				.path( serviceUri)
				.request()
				.header("Content-type", "application/json")
				.post( );
		}
		
	*/	return( err);
	}
}

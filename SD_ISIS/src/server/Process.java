package server;

import java.util.concurrent.Semaphore;

public class Process extends Thread{
	private Semaphore allDone;
	private String identificador;
	private int contador;
	
	public Process(){
		this.identificador = "";
		this.contador = 0;
	}
	
	public void run(){
		String mensaje;
		long tiempo;
		
		for( this.contador = 0; this.contador < 100; this.contador++){
			try {
				mensaje = this.identificador + " " + String.format("%03d", this.contador);	
				tiempo = (long)(1000*( 1 + 0.5*Math.random()));
						
				Thread.sleep(tiempo);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}

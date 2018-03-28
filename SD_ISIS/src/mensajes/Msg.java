package mensajes;

public class Msg {
	private String contenido, id, orden, estado;
	private int numProc;
	
	public Msg(){
		this.contenido = "";
		this.id = "";
		this.orden = "";
		this.numProc = 0;
		this.estado = "";
	}
	
	public Msg( String contenido, String id, String orden, int numProc, String estado){
		
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public int getNumProc() {
		return numProc;
	}

	public String getContenido() {
		return contenido;
	}

	public String getId() {
		return id;
	}

	public String getOrden() {
		return orden;
	}
}
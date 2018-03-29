package mensajes;

public class Msg {
	public String id;
	public String receptor;
	public String emisor;
	public int orden;
	
	public String toJSON(){
		return String.format("{ \"id\":\"%s\", \"emisor\":\"%s\", \"orden\":\"%d\" }", 
				this.id, this.emisor, this.orden);
	}
}
package mensajes;

public class Mensaje extends Msg {
	public String cuerpo;
	
	public String toJSON(){
		return String.format("{ \"id\":\"%s\", \"emisor\":\"%s\", \"orden\":\"%d\", \"cuerpo\":\"%s\" }", 
				this.id, this.emisor, this.orden, this.cuerpo);
	}
}

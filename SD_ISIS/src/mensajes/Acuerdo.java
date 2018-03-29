package mensajes;

public class Acuerdo extends Msg {
	public boolean definitivo;
	
	public String toJSON(){
		return String.format("{ \"id\":\"%s\", \"emisor\":\"%s\", \"orden\":\"%d\", \"definitivo\":\"%s\" }", 
				this.id, this.emisor, this.orden, this.definitivo);
	}
}

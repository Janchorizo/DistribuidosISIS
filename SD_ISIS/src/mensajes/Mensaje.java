package mensajes;

/**
 * Mensaje asociado con la primera etapa en el algoritmo de multidifusión con ordenación
 * total ISIS.
 * <br>
 * Éste se difunde a todos los procesos del sistema distribuido, y contiene el cuerpo del mensaje.
 * @author alex
 * @see Acuerdo
 * @see Propuesta
 */
public class Mensaje extends Msg {
	public String cuerpo;
	
	public Mensaje(){
		super();
		this.cuerpo = "";
	}
	
	public Mensaje( String id, String emisor, int orden, String cuerpo){
		super( id, emisor, orden);
		this.cuerpo = cuerpo;
	}
	
	@Override
	public String toString(){
		return this.toJSON();
	}
	
	/**
	 * Genera una cadena con el formato JSON donde las claves son el nombre de los
	 * atributos de la clase, y el valor, el actual para la instancia correspondiente.
	 * @return <code>String</code>: Cadena con el contenido del mensaje en formato JSON
	 */
	@Override
	public String toJSON(){
		return String.format("{ \"id\":\"%s\", \"emisor\":\"%s\", \"orden\":\"%d\", \"definitivo\":\"%s\", \"cuerpo\":\"%s\" }", 
				this.id, this.emisor, this.orden, this.definitivo, this.cuerpo);
	}
}

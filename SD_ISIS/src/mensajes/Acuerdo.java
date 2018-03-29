package mensajes;

/**
 * Mensaje asociado con la tercera etapa en el algoritmo de multidifusión con ordenación
 * total ISIS.
 * <br>
 * Éste se difunde a todos los procesos en el sistema distribuido, y contiene el orden elegido
 * por el proceso que envía el mensaje.
 * @author alex
 * @see Mensaje
 * @see Propuesta
 */
public class Acuerdo extends Msg {
	/**
	 * Indica si el orden del mensaje asociado es final o no.<br>
	 * En el caso de un acuerdo, es el orden definitivo.  
	 */
	public final boolean definitivo = true;
	
	public Acuerdo(){
		super();
	}
	
	public Acuerdo( String id, String emisor, int orden){
		super( id, emisor, orden);
	}
	
	/**
	 * Genera una cadena con el formato JSON donde las claves son el nombre de los
	 * atributos de la clase, y el valor, el actual para la instancia correspondiente.
	 * @return <code>String</code>: Cadena con el contenido del mensaje en formato JSON
	 */
	@Override
	public String toJSON(){
		return String.format("{ \"id\":\"%s\", \"emisor\":\"%s\", \"orden\":\"%d\", \"definitivo\":\"%s\" }", 
				this.id, this.emisor, this.orden, this.definitivo);
	}
}

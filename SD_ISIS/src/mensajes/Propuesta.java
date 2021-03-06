package mensajes;

/**
 * Mensaje asociado con la segunda etapa en el algoritmo de multidifusión con ordenación
 * total ISIS.
 * <br>
 * Éste se envía al proceso emisor del mensaje inicial, con el orden propuesto por el actual emisor.
 * @author alex
 * @see Acuerdo
 * @see Mensaje
 */
public class Propuesta extends Msg {
	/**
	 * Indica si el orden del mensaje asociado es final o no.<br>
	 * En el caso de una propuesta, es el orden no es definitivo.  
	 */
	public int numPropuestas;
	
	public Propuesta(){
		super();
		this.numPropuestas = 0;
	}
	
	public Propuesta( String id, String emisor, int orden, int numPropuestas){
		super( id, emisor, orden);
		this.numPropuestas = numPropuestas;
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
		return String.format("{ \"id\":\"%s\", \"emisor\":\"%s\", \"orden\":\"%d\", \"definitivo\":\"%s\", \"numPropuestas\":\"%d\" }", 
				this.id, this.emisor, this.orden, this.definitivo, this.numPropuestas);
	}
}

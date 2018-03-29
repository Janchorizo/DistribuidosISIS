package mensajes;

/**
 * Envoltorio para el envío y recepción de información a través de servicios
 * web REST que usen el formato JSON para los argumentos.<br>
 * <br>
 * Para el correcto funcionamiento, es necesario
 * <ul>
 * 	<li> Que la clase que exponga la API tenga los siguientes decoradores <br>
 * {@code   @Consumes(MediaType.APPLICATION_JSON) }<br>{@code  @Produces(MediaType.APPLICATION_JSON)  }
 * <br>
 * 	</li>
 * 	<li> Que el procedimiento que realice la llamda a la API REST incluya una cabecera describiendo el
 * uso de JSON <br>Un ejemplo de uso : <br><br>
 <PRE>
  uri = UriBuilder.fromUri( __).build();
  target = client.target( uri);			
	target
		.path( __)
		.queryParam( __, __)
		.request()
		.header("Content-type", "application/json")
		.put( Entity.json( __JSON));
 </PRE>
 * También proporciona el método para obtener su representacion JSON en una cadena.
 * </li>
 * </ul>
 * @author alex
 *
 */
public class Msg {
	/**
	 * Identificador del mensaje
	 */
	public String id;
	/**
	 * Identificador del emisor, para mantener la referencia y poder responder
	 */
	public String emisor;
	/**
	 * Se corresponde con el tiempo lógico de Lamport
	 */
	public int orden;
	
	public Msg(){
		this.id = "none";
		this.emisor = "none";
		this.orden = -1;
	}
	
	public Msg( String id, String emisor, int orden){
		this.id = id;
		this.emisor = emisor;
		this.orden = orden;
	}
	
	/**
	 * Genera una cadena con el formato JSON donde las claves son el nombre de los
	 * atributos de la clase, y el valor, el actual para la instancia correspondiente.
	 * @return <code>String</code> Cadena con el contenido del mensaje en formato JSON
	 */
	public String toJSON(){
		return String.format("{ \"id\":\"%s\", \"emisor\":\"%s\", \"orden\":\"%d\" }", 
				this.id, this.emisor, this.orden);
	}
}
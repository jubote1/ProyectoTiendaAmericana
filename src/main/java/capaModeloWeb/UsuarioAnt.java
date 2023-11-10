package capaModeloWeb;

/**
 * Clase que implementa la entidad Usuario.
 * @author JuanDavid
 *
 */
public class UsuarioAnt {
	
	private String nombreUsuario;
	private String contrasena;
	private String nombreLargo;
	public String getNombreUsuario() {
		return nombreUsuario;
	}
	public void setNombreUsuario(String nombreUsuario) {
		this.nombreUsuario = nombreUsuario;
	}
	public String getContrasena() {
		return contrasena;
	}
	public void setContrasena(String contrasena) {
		this.contrasena = contrasena;
	}
	public String getNombreLargo() {
		return nombreLargo;
	}
	public void setNombreLargo(String nombreLargo) {
		this.nombreLargo = nombreLargo;
	}
	public UsuarioAnt(String nombreUsuario, String contrasena, String nombreLargo) {
		super();
		this.nombreUsuario = nombreUsuario;
		this.contrasena = contrasena;
		this.nombreLargo = nombreLargo;
	}
	public UsuarioAnt(String nombreUsuario) {
		super();
		this.nombreUsuario = nombreUsuario;
	}
	
	
	
	

}

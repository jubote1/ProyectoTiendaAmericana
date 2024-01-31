package capaControladorServicios;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import capaControladorServicios.AutenticacionCtrl;
import capaDAOPixelpos.UsuarioDAO;
import capaModeloWeb.UsuarioAnt;
import capaModeloPOS.Usuario;

/**
 * Clase AutenticacionCtrl tiene como objetivo hacer las veces de Controlador para la autenticación de usuarios
 * en el aplicatiov
 * @author Juan David Botero Duque
 * @
 */
public class AutenticacionCtrl {
	
	
	private static AutenticacionCtrl instance;
	
	//singleton controlador
	public static AutenticacionCtrl getInstance(){
		if(instance == null){
			instance = new AutenticacionCtrl();
		}
		return instance;
	}
	
	/**
	 * 
	 * @param usuario El usuario de logueo de la aplicación
	 * @param contrasena Contraseña asociada al usuario que se está logueando
	 * @return Se retona un valor booleano indicando si el usuario y contraseña corresponde con alguien logueado
	 * en al aplicación
	 */
	public boolean autenticarUsuario(String usuario, String contrasena){
		UsuarioAnt usu = new UsuarioAnt(usuario, contrasena, "");
		boolean resultado = UsuarioDAO.validarUsuario(usu);
		return(resultado);
	}
	
	
	public String validarClaveRapida(String claveRapida)
	{
		JSONObject respuesta = new JSONObject();
		Usuario usuario = UsuarioDAO.validarClaveRapida(claveRapida);
		respuesta.put("idusuario", usuario.getIdUsuario());
		respuesta.put("nombreusuario", usuario.getNombreUsuario());
		respuesta.put("nombrelargo", usuario.getNombreLargo());
		respuesta.put("idtipoempleado", usuario.getIdTipoEmpleado());
		respuesta.put("tipoinicio", usuario.getTipoInicio());
		respuesta.put("administrador", usuario.isAdministrador());
		respuesta.put("estadodomiciliario", usuario.getEstadoDomiciliario());
		return(respuesta.toJSONString());
	}
	
	/**
	 * 
	 * @param usuario Se recibe el usuario de aplicación con el fin de validar si el usuario pasado como parámetro está
	 * o no logueado en la aplicación
	 * @return Se retorna un valor booleano indicando si el usuario se encuentra o no logueado en el aplicativo.
	 */
	public String validarAutenticacion(String usuario)
	{
		JSONArray listJSON = new JSONArray();
		JSONObject Respuesta = new JSONObject();
		UsuarioAnt usu = new UsuarioAnt(usuario);
		String resultado = UsuarioDAO.validarAutenticacion(usu);
		if (resultado.equals(new  String ("N")) ){
			Respuesta.put("respuesta", "OK");
    		
		} 
		else if(resultado.equals(new  String ("S"))){
			Respuesta.put("respuesta", "OKA");
    	
		}else
		{
			Respuesta.put("respuesta", "NOK");
		}
		Respuesta.put("nombreusuario", usu.getNombreLargo());
		listJSON.add(Respuesta);
		return(listJSON.toJSONString());
	}

}

package capaDAOPixelpos;
import capaConexion.ConexionBaseDatos;
import capaModelo.Usuario;
import capaModeloWeb.UsuarioAnt;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import org.apache.log4j.Logger;
/**
 * Clase que se encarga de implementar toda la interacción con la base de datos para la entidad Usuario.
 * @author JuanDavid
 *
 */
public class UsuarioDAO {

	/**
	 * Método que se encarga de validar la existencia y de un usuario y su contraseña en la base de datos.
	 * @param usuario Se recibe como parámetro un objeto MOdelo Usuario, el cual trae la información base para la validación,
	 * autenticación del usuario.
	 * @return Se retorna un valor booleano que indica si el proceso de autenticación es satifactorio o no.
	 */
	public static boolean validarUsuario(UsuarioAnt usuario)
	{
		Logger logger = Logger.getLogger("log_file");
		ConexionBaseDatos con = new ConexionBaseDatos();
		Connection con1 = con.obtenerConexionBDLocal();
		try
		{
			Statement stm = con1.createStatement();
			String consulta = "select count(*) from usuario where nombre = '" + usuario.getNombreUsuario() + "' and password = '" + usuario.getContrasena()+"'";
			logger.info(consulta);
			ResultSet rs = stm.executeQuery(consulta);
			while(rs.next()){
				int cantidad=0;
				try{
					cantidad = Integer.parseInt(rs.getString(1));
					if (cantidad > 0){
						return(true);
					}
				}catch(Exception e){
					logger.error(e.toString());
					return(false);
				}
				rs.close();
				stm.close();
				con1.close();
			}
		}catch (Exception e){
			try
			{
				con1.close();
			}catch(Exception e1)
			{
			}
		}
		return(false);
		
	}
	
	/**
	 * Método que se encarga de validar si un usuario existe o no en la base de datos
	 * @param usuario Recibe como parámetro un objeto Modelo Usuario con base en el cual se realiza la consulta.
	 * @return Se retorna un valor booleano con base en el cual se realiza la validación del usuario en base de datos
	 * 
	 */
	public static String validarAutenticacion(UsuarioAnt usuario)
	{
		ConexionBaseDatos con = new ConexionBaseDatos();
		Connection con1 = con.obtenerConexionBDLocal();
		String resultado = "";
		try
		{
			Statement stm = con1.createStatement();
			String consulta = "select administrador from usuario where nombre = '" + usuario.getNombreUsuario() + "'";
			ResultSet rs = stm.executeQuery(consulta);
			while(rs.next()){
				
				try{
					resultado = rs.getString(1);
					
				}catch(Exception e){
					
					
				}
				rs.close();
				stm.close();
				con1.close();
			}
		}catch (Exception e){
			try
			{
				con1.close();
			}catch(Exception e1)
			{
			}
		}
		return(resultado);
	}
	
	public static Usuario validarClaveRapida(String claveRapida)
	{
		Logger logger = Logger.getLogger("log_file");
		ConexionBaseDatos con = new ConexionBaseDatos();
		Connection con1 = con.obtenerConexionBDLocal();
		Usuario usuario = new Usuario(0,"","","",0,"",false);
		try
		{
			Statement stm = con1.createStatement();
			String consulta = "select * from usuario where claverapida = '" + claveRapida + "'";
			logger.info(consulta);
			ResultSet rs = stm.executeQuery(consulta);
			int idUsuario;
			String nombreUsuario;
			String contrasena = "";
			String nombreLargo;
			int idTipoEmpleado;
			String tipoInicio;
			boolean administrador;
			int estadoDomiciliario;
			while(rs.next()){
				
				try{
					idUsuario = rs.getInt("id");
					nombreUsuario = rs.getString("nombre_largo");
					nombreLargo = rs.getString("nombre_largo");
					idTipoEmpleado = rs.getInt("idtipoempleado");
					estadoDomiciliario = rs.getInt("estadoDomiciliario");
					tipoInicio = rs.getString("tipoinicio");
					if(rs.getString("administrador").equals(new String("S")))
					{
						administrador = true;
					}else
					{
						administrador = false;
					}
					usuario = new Usuario(idUsuario,nombreLargo,contrasena, nombreLargo, idTipoEmpleado, tipoInicio,administrador);
					usuario.setEstadoDomiciliario(estadoDomiciliario);
					break;
				}catch(Exception e){
					
					
				}
				rs.close();
				stm.close();
				con1.close();
			}
		}catch (Exception e){
			try
			{
				con1.close();
			}catch(Exception e1)
			{
			}
		}
		return(usuario);
	}
	
}

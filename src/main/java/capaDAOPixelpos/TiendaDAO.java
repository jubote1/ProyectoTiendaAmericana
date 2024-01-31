package capaDAOPixelpos;

import java.sql.Connection;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import capaConexion.ConexionBaseDatos;
import capaModeloWeb.Tienda;
import capaModeloWeb.UsuarioAnt;
import capaModeloPOS.FechaSistema;

import org.apache.log4j.Logger;
/**
 * Clase que se encarga de implementar todo lo relacionado con la base de datos de la entidad tienda.
 * @author JuanDavid
 *
 */
public class TiendaDAO {
	
/**
 * Método que se encarga de retornar todas las entidades Tiendas definidas en la base de datos
 * @return Se retorna un ArrayList con todas las entidades Tiendas definidas en la base de datos.
 */
	public static Tienda obtenerTienda()
	{
		Logger logger = Logger.getLogger("log_file");
		ConexionBaseDatos con = new ConexionBaseDatos();
		Connection con1 = con.obtenerConexionBDLocal();
		Tienda tien = new Tienda();
		try
		{
			Statement stm = con1.createStatement();
			String consulta = "select * from tienda";
			logger.info(consulta);
			ResultSet rs = stm.executeQuery(consulta);
			while(rs.next()){
				int idTienda = rs.getInt("idtienda");
				String nombre = rs.getString("nombretienda");
				String urlcontact = rs.getString("urlcontact");
				tien = new Tienda(idTienda, nombre, urlcontact);
			}
			rs.close();
			stm.close();
			con1.close();
		}catch (Exception e){
			logger.info(e.toString());
			try
			{
				con1.close();
			}catch(Exception e1)
			{
			}
		}
		return(tien);
		
	}
	
	public static FechaSistema obtenerFechasSistema()
	{
		Logger logger = Logger.getLogger("log_file");
		ConexionBaseDatos con = new ConexionBaseDatos();
		Connection con1 = con.obtenerConexionBDLocal();
		FechaSistema fechaSistema = new FechaSistema();
		try
		{
			Statement stm = con1.createStatement();
			String consulta = "select fecha_apertura, fecha_ultimo_cierre from tienda";
			ResultSet rs = stm.executeQuery(consulta);
			String fechaApertura = "";
			String fechaUltimoCierre = "";
			while(rs.next()){
				
				fechaApertura = rs.getString("fecha_apertura");
				fechaUltimoCierre = rs.getString("fecha_ultimo_cierre");
				
			}
			fechaSistema = new FechaSistema(fechaApertura, fechaUltimoCierre);
			rs.close();
			stm.close();
			con1.close();
		}catch (Exception e){
			logger.info(e.toString());
			try
			{
				con1.close();
			}catch(Exception e1)
			{
			}
		}
		return(fechaSistema);
	}
	
	
}

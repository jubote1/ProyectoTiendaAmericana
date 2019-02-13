package capaDAOPixelpos;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import capaConexion.ConexionBaseDatos;

import org.apache.log4j.Logger;

public class TiempoPedidoDAO {
	
	public static int retornarTiempoPedido()
	{
		int tiempo = 0;
		Logger logger = Logger.getLogger("log_file");
		ConexionBaseDatos con = new ConexionBaseDatos();
		Connection con1 = con.obtenerConexionBDLocal();
		try
		{
			Statement stm = con1.createStatement();
			String consulta = "select valornumerico from parametros where valorparametro = 'TIEMPOPEDIDO'";
			logger.info(consulta);
			ResultSet rs = stm.executeQuery(consulta);
			while(rs.next()){
				
				try
				{
					tiempo = Integer.parseInt(rs.getString("valornumerico"));
				}catch(Exception e)
				{
					System.out.println(e.toString()+ e.getMessage());
					logger.error(e.toString());
					tiempo = 0;
				}
				
			}
			rs.close();
			stm.close();
			con1.close();
		}catch (Exception e)
		{
			System.out.println(e.toString() + e.getMessage());
			try
			{
				con1.close();
			}catch(Exception e1)
			{
				System.out.println(e.toString()+ e.getMessage());
			}
		}
		return(tiempo);
	}
	
	public static boolean actualizarTiempoPedido(int nuevotiempo, int idtienda, String user)
	{
		boolean respuesta;
		Logger logger = Logger.getLogger("log_file");
		ConexionBaseDatos con = new ConexionBaseDatos();
		Connection con1 = con.obtenerConexionBDLocal();
		try
		{
			Statement stm = con1.createStatement();
			String actualizacion = "update parametros set valornumerico = " + nuevotiempo +  " where valorparametro = 'TIEMPOPEDIDO'";
			logger.info(actualizacion);
			stm.executeUpdate(actualizacion);
			String insercionLog = "insert into log_tiempo_tienda (idtienda, usuario, nuevotiempo) values (" + idtienda +" , '" + user + "' , " + nuevotiempo + ")" ;
			logger.info(insercionLog);
			stm.executeUpdate(insercionLog);
			return(true);
			
		}catch (Exception e){
			
			try
			{
				System.out.println(e.toString());
				con1.close();
			}catch(Exception e1)
			{
			}
			return(false);
		}
		
	}

}

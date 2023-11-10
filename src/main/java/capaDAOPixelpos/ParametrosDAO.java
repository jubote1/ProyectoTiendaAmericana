package capaDAOPixelpos;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import capaConexion.ConexionBaseDatos;
import capaModeloPOS.Impuesto;
import capaModeloPOS.Parametro;

public class ParametrosDAO {
	
	public static int retornarValorNumerico(String variable)
	{
		int valor = 0;
		Logger logger = Logger.getLogger("log_file");
		ConexionBaseDatos con = new ConexionBaseDatos();
		Connection con1 = con.obtenerConexionBDLocal();
		try
		{
			Statement stm = con1.createStatement();
			String consulta = "select valornumerico from parametros where valorparametro = '"+ variable +"'";
			ResultSet rs = stm.executeQuery(consulta);
			while(rs.next()){
				
				try
				{
					valor = Integer.parseInt(rs.getString("valornumerico"));
				}catch(Exception e)
				{
				
					logger.error(e.toString());
					valor = 0;
				}
				
			}
			rs.close();
			stm.close();
			con1.close();
		}catch (Exception e)
		{
			
			try
			{
				con1.close();
			}catch(Exception e1)
			{
				
			}
		}
		return(valor);
	}
	
	public static String retornarValorAlfanumerico(String variable)
	{
		String valor = "";
		Logger logger = Logger.getLogger("log_file");
		ConexionBaseDatos con = new ConexionBaseDatos();
		Connection con1 = con.obtenerConexionBDLocal();
		try
		{
			Statement stm = con1.createStatement();
			String consulta = "select valortexto from parametros where valorparametro = '"+ variable +"'";
			ResultSet rs = stm.executeQuery(consulta);
			while(rs.next()){
				
				try
				{
					valor = rs.getString("valortexto");
				}catch(Exception e)
				{
				
					logger.error(e.toString());
					valor = "";
				}
				
			}
			rs.close();
			stm.close();
			con1.close();
		}catch (Exception e)
		{
			
			try
			{
				con1.close();
			}catch(Exception e1)
			{
				
			}
		}
		return(valor);
	}
	
	public static Parametro obtenerParametro(String valorParametro)
	{
		Logger logger = Logger.getLogger("log_file");
		ConexionBaseDatos con = new ConexionBaseDatos();
		Connection con1 = con.obtenerConexionBDLocal();
		Parametro parametro = new Parametro("", 0, "");
		
		try
		{
			Statement stm = con1.createStatement();
			String consulta = "select * from parametros where valorparametro = '" + valorParametro +"'";
			ResultSet rs = stm.executeQuery(consulta);
			int valorNumerico = 0;
			String valorTexto = "";
			while(rs.next()){
				
				valorNumerico = rs.getInt("valornumerico");
				valorTexto = rs.getString("valortexto");
				parametro = new Parametro(valorParametro, valorNumerico, valorTexto);
				break;
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
		return(parametro);
		
	}
		
}

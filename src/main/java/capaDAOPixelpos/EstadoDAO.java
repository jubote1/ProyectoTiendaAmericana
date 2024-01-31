package capaDAOPixelpos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import capaConexion.ConexionBaseDatos;
import capaModeloWeb.Tienda;
import capaModeloWeb.UsuarioAnt;
import capaModeloPOS.Estado;
import capaModeloPOS.EstadoPedido;
import capaModeloPOS.Impuesto;
import capaModeloPOS.Producto;

import org.apache.log4j.Logger;

import com.mysql.cj.jdbc.result.ResultSetMetaData;

/**
 * Clase que se encarga de implementar todo lo relacionado con la base de datos de la entidad producto
 * @author JuanDavid
 *
 */
public class EstadoDAO {
	
/**
 * Método que se encarga de retonar todos los productos parametrizado en el sistema.
 * @return Retorna un arrayList con tipos de datos genéricos.
 */
	public static ArrayList obtenerEstado(boolean auditoria)
	{
		Logger logger = Logger.getLogger("log_file");
		ConexionBaseDatos con = new ConexionBaseDatos();
		Connection con1 = con.obtenerConexionBDLocal();
		ArrayList estados = new ArrayList();
		
		try
		{
			Statement stm = con1.createStatement();
			String consulta = "select a.idestado, a.descripcion, b.descripcion, b.idtipopedido, a.descripcion_corta from estado a, tipo_pedido b where a.idtipopedido = b.idtipopedido";
			if(auditoria)
			{
				logger.info(consulta);
			}
			ResultSet rs = stm.executeQuery(consulta);
			ResultSetMetaData rsMd = (ResultSetMetaData) rs.getMetaData();
			int numeroColumnas = rsMd.getColumnCount();
			
			while(rs.next()){
				String [] fila = new String[numeroColumnas];
				for(int y = 0; y < numeroColumnas; y++)
				{
					fila[y] = rs.getString(y+1);
				}
				estados.add(fila);
				
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
		return(estados);
		
	}
	
	//Realizamos modificación para detectar si el pedido está en un estado final o no, para de esta manera
	//tomar unas u otras decisiones al momento del despliegue de los tiempos de un pedido.
	public static ArrayList obtenerHistoriaEstadoPedido(int idPedido, boolean auditoria)
	{
		Logger logger = Logger.getLogger("log_file");
		ConexionBaseDatos con = new ConexionBaseDatos();
		Connection con1 = con.obtenerConexionBDLocal();
		ArrayList historiaEstados = new ArrayList();
		int idEstado = 0;
		try
		{
			Estado estadoPedido = PedidoPOSPMDAO.obtenerEstadoPedido(idPedido);
			//En este punto ya obtenenido el idEstado, nos disponemos a verificar si es un estado final o no.
			boolean esEstFinal = EstadoDAO.esEstadoFinal(estadoPedido.getIdestado(), auditoria);
			Statement stm = con1.createStatement();
			String consulta = "select a.fechacambio, IFNULL(b.descripcion, 'Tomando Pedido'), IFNULL(c.descripcion, 'Tomando Pedido')  from cambios_estado_pedido a left outer join  estado b on a.idestadoanterior = b.idestado left outer join estado c on a.idestadoposterior = c.idestado where a.idpedidotienda = " + idPedido + " order by fechacambio asc";
			if(auditoria)
			{
				logger.info(consulta);
			}
			ResultSet rs = stm.executeQuery(consulta);
			ResultSetMetaData rsMd = (ResultSetMetaData) rs.getMetaData();
			int numeroColumnas = rsMd.getColumnCount() ;
			
			while(rs.next()){
				String [] fila = new String[numeroColumnas + 1];
				for(int y = 0; y < numeroColumnas; y++)
				{
					fila[y] = rs.getString(y+1);
				}
				fila[numeroColumnas] = Boolean.toString(esEstFinal);
				historiaEstados.add(fila);
				
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
		return(historiaEstados);
		
	}
	

		public static ArrayList obtenerHistoriaEstadoPedidosFecha(String fecha)
		{
			Logger logger = Logger.getLogger("log_file");
			ConexionBaseDatos con = new ConexionBaseDatos();
			Connection con1 = con.obtenerConexionBDLocal();
			ArrayList historiaEstados = new ArrayList();
			int idEstado = 0;
			try
			{
				//Rediseñamos el método base para traer todos los pedidos del dia con sus estados
				ArrayList<EstadoPedido>	estadosPedido = PedidoPOSPMDAO.obtenerEstadoPedidosFecha(fecha);
				Statement stm = con1.createStatement();
				String consulta = "select a.fechacambio, IFNULL(b.descripcion, 'Tomando Pedido'), IFNULL(c.descripcion, 'Tomando Pedido'), d.idpedidotienda from pedido d, cambios_estado_pedido a left outer join  estado b on a.idestadoanterior = b.idestado left outer join estado c on a.idestadoposterior = c.idestado where  a.idpedidotienda = d.idpedidotienda and d.fechapedido = '" + fecha + "' order by a.idpedidotienda, a.fechacambio asc";
				ResultSet rs = stm.executeQuery(consulta);
				ResultSetMetaData rsMd = (ResultSetMetaData) rs.getMetaData();
				int numeroColumnas = rsMd.getColumnCount() ;
				int idPedidoTienda = 0;
				while(rs.next()){
					String [] fila = new String[numeroColumnas + 1];
					for(int y = 0; y < numeroColumnas; y++)
					{
						fila[y] = rs.getString(y+1);
						if(y == (numeroColumnas - 1))
						{
							idPedidoTienda = Integer.parseInt(fila[y]);
						}
					}
					//Debemos realizar la busqueda del estado del idPedidoTienda
					for(int i = 0; i < estadosPedido.size(); i++)
					{
						if(estadosPedido.get(i).getIdPedidoTienda() == idPedidoTienda)
						{
							fila[numeroColumnas] = Boolean.toString(estadosPedido.get(i).isEsFinal());
							break;
						}
					}
					historiaEstados.add(fila);
					
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
			return(historiaEstados);
			
		}
	
	public static boolean esEstadoFinal(int idEstado, boolean auditoria)
	{
		Logger logger = Logger.getLogger("log_file");
		boolean respuesta = false;
		ConexionBaseDatos con = new ConexionBaseDatos();
		Connection con1 = con.obtenerConexionBDLocal();
		try
		{
			Statement stm = con1.createStatement();
			String consulta = "select * from estado where estado_final = 1 and  idestado = " + idEstado; 
			if(auditoria)
			{
				logger.info(consulta);
			}
			ResultSet rs = stm.executeQuery(consulta);
			while(rs.next())
			{
				respuesta = true;
			}
			stm.close();
			con1.close();
		}
		catch (Exception e){
			logger.error(e.toString());
			try
			{
				con1.close();
				respuesta = false;
			}catch(Exception e1)
			{
			}
			
		}
		return(respuesta);
	}
	
	public static ArrayList<Estado> obtenerTodosEstado(boolean auditoria)
	{
		Logger logger = Logger.getLogger("log_file");
		ConexionBaseDatos con = new ConexionBaseDatos();
		Connection con1 = con.obtenerConexionBDLocal();
		ArrayList<Estado> estados = new ArrayList();
		
		try
		{
			Statement stm = con1.createStatement();
			String consulta = "select * from estado";
			if(auditoria)
			{
				logger.info(consulta);
			}
			ResultSet rs = stm.executeQuery(consulta);
			int idestado, idTipoPedido;
			String descripcion, descripcionCorta;
			boolean estInicial = false;
			boolean estFinal = false;
			boolean impresion = false;
			boolean rutaDomicilio = false;
			boolean entregaDomicilio = false;
			int intEstInicial, intEstFinal, intImpresion, intRutaDomicilio, intEntregaDomicilio;
			int colorr=0, colorg=0, colorb=0;
			while(rs.next()){
				idestado = rs.getInt("idestado");
				descripcion = rs.getString("descripcion");
				descripcionCorta = rs.getString("descripcion_corta");
				idTipoPedido = rs.getInt("idtipopedido");
				intEstInicial = rs.getInt("estado_inicial");
				intEstFinal = rs.getInt("estado_final");
				colorr = rs.getInt("colorr");
				colorg = rs.getInt("colorg");
				colorb = rs.getInt("colorb");
				intImpresion = rs.getInt("impresion");
				intEntregaDomicilio = rs.getInt("ruta_domicilio");
				intRutaDomicilio = rs.getInt("entrega_domicilio");
				if(intImpresion == 1)
				{
					impresion = true;
				}
				if(intEstInicial == 1)
				{
					estInicial = true;
				}
				if(intEstFinal == 1)
				{
					estFinal = true;
				}
				if(intEntregaDomicilio == 1)
				{
					entregaDomicilio = true;
				}
				if(intRutaDomicilio == 1)
				{
					rutaDomicilio = true;
				}
				Estado est = new Estado(idestado, descripcion, descripcionCorta, idTipoPedido, "", estInicial, estFinal, colorr, colorg, colorb, impresion,rutaDomicilio, entregaDomicilio);
				estados.add(est);
				
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
		return(estados);
		
	}
	
	/**
	 * Método que se encarga de retornar los estados que tiene asignados un tipo de empleado para ver en la ventana de comanda de pedidos
	 * @param idTipoPedido Se recibe como parámetro un valor entero con base
	 * @return un Arraylist con objetos de tipo estado, de acuerdo al idtipoempleado.
	 */
	
	
	
	public static Estado obtenerEstado(int idEstado, boolean auditoria)
	{
		Logger logger = Logger.getLogger("log_file");
		ConexionBaseDatos con = new ConexionBaseDatos();
		Connection con1 = con.obtenerConexionBDLocal();
		Estado estado = new Estado();
		
		try
		{
			Statement stm = con1.createStatement();
			String consulta = "select * from estado where idestado = " + idEstado;
			if(auditoria)
			{
				logger.info(consulta);
			}
			ResultSet rs = stm.executeQuery(consulta);
			int idTipoPedido = 0;
			String descripcion = "";
			String descripcionCorta = "";
			boolean estInicial = false;
			boolean estFinal = false;
			boolean impresion = false;
			boolean rutaDomicilio = false;
			boolean entregaDomicilio = false;
			int intEstInicial, intEstFinal, intImpresion, intRutaDomicilio, intEntregaDomicilio;	
			int colorr=0, colorg=0, colorb=0;
			byte[] imagen = null;
			while(rs.next()){
				idTipoPedido = rs.getInt("idtipopedido");
				descripcion = rs.getString("descripcion");
				descripcionCorta = rs.getString("descripcion_corta");
				intEstInicial = rs.getInt("estado_inicial");
				intEstFinal = rs.getInt("estado_final");
				intImpresion = rs.getInt("impresion");
				colorr = rs.getInt("colorr");
				colorg = rs.getInt("colorg");
				colorb = rs.getInt("colorb");
				imagen = rs.getBytes("imagen");
				intEntregaDomicilio = rs.getInt("entrega_domicilio");
				intRutaDomicilio = rs.getInt("ruta_domicilio");
				if(intImpresion == 1)
				{
					impresion = true;
				}
				if(intEstInicial == 1)
				{
					estInicial = true;
				}
				if(intEstFinal == 1)
				{
					estFinal = true;
				}
				if(intEntregaDomicilio == 1)
				{
					entregaDomicilio = true;
				}
				if(intRutaDomicilio == 1)
				{
					rutaDomicilio = true;
				}
				estado = new Estado(idEstado, descripcion, descripcionCorta,idTipoPedido, "", estInicial, estFinal,colorr,colorg,colorb, impresion, rutaDomicilio, entregaDomicilio);
				estado.setImagen(imagen);
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
		return(estado);
		
	}
	
	
	public static int insertarEstado(Estado estado, boolean auditoria)
	{
		Logger logger = Logger.getLogger("log_file");
		int idEstadoIns = 0;
		ConexionBaseDatos con = new ConexionBaseDatos();
		Connection con1 = con.obtenerConexionBDLocal();
		try
		{
			Statement stm = con1.createStatement();
			int estadoInicial = 0;
			int estadoFinal = 0;
			int impresion = 0;
			int rutaDomicilio = 0;
			int entregaDomicilio = 0;
			if(estado.isImpresion())
			{
				impresion = 1;
			}
			if(estado.isEstadoFinal())
			{
				estadoFinal = 1;
			}
			if(estado.isEstadoInicial())
			{
				estadoInicial= 1;
			}
			if(estado.isRutaDomicilio())
			{
				rutaDomicilio = 1;
			}
			if(estado.isEntregaDomicilio())
			{
				entregaDomicilio = 1;
			}
			String insert = "insert into estado (descripcion, descripcion_corta, estado_inicial, estado_final, colorr, colorg, colorb, idtipopedido, impresion, imagen, ruta_domicilio, entrega_domicilio) values('" + estado.getDescripcion() + "' , '" + estado.getDescripcionCorta() + "', "+ estadoInicial + " ," + estadoFinal + " , " + estado.getColorr() + " , " + estado.getColorg() + " , " + estado.getColorb() + ", " + estado.getIdTipoPedido() +", " + impresion + " , " + estado.getImagen() + " , " + rutaDomicilio + " , " + entregaDomicilio +")";
			if(auditoria)
			{
				logger.info(insert);
			}
			stm.executeUpdate(insert, Statement.RETURN_GENERATED_KEYS);
			ResultSet rs = stm.getGeneratedKeys();
			if (rs.next()){
				idEstadoIns=rs.getInt(1);
				if(auditoria)
				{
					logger.info("id estado insertada en bd " + idEstadoIns);
				}
				
	        }
			stm.close();
			con1.close();
		}
		catch (Exception e){
			logger.error(e.toString());
			try
			{
				con1.close();
			}catch(Exception e1)
			{
			}
			return(0);
		}
		return(idEstadoIns);
	}
	
	
	public static boolean eliminarEstado(int idestado, boolean auditoria)
	{
		Logger logger = Logger.getLogger("log_file");
		boolean respuesta = true;
		ConexionBaseDatos con = new ConexionBaseDatos();
		Connection con1 = con.obtenerConexionBDLocal();
		try
		{
			Statement stm = con1.createStatement();
			String delete = "delete from estado where idestado = " + idestado;
			if(auditoria)
			{
				logger.info(delete);
			}
			stm.executeUpdate(delete);
			respuesta = true;
			stm.close();
			con1.close();
		}
		catch (Exception e){
			logger.error(e.toString());
			try
			{
				con1.close();
				respuesta = false;
			}catch(Exception e1)
			{
			}
			
		}
		return(respuesta);
	}
	
	
	public static boolean editarEstado(Estado estado, boolean auditoria)
	{
		Logger logger = Logger.getLogger("log_file");
		boolean respuesta;
		ConexionBaseDatos con = new ConexionBaseDatos();
		Connection con1 = con.obtenerConexionBDLocal();
		try
		{
			int estadoInicial = 0;
			int estadoFinal = 0;
			int impresion = 0;
			int rutaDomicilio = 0;
			int entregaDomicilio = 0;
			if(estado.isImpresion())
			{
				impresion = 1;
			}
			if(estado.isEstadoFinal())
			{
				estadoFinal = 1;
			}
			if(estado.isEstadoInicial())
			{
				estadoInicial= 1;
			}
			if(estado.isRutaDomicilio())
			{
				rutaDomicilio = 1;
			}
			if(estado.isEntregaDomicilio())
			{
				entregaDomicilio = 1;
			}
			Statement stm = con1.createStatement();
			String update = "update estado set descripcion = '" + estado.getDescripcion() + "' , descripcion_corta = '" + estado.getDescripcionCorta() + "' , estado_inicial =" + estadoInicial + " , estado_final = " + estadoFinal + ", colorr =  " + estado.getColorr() + " , colorg =" + estado.getColorg() + " , ruta_domicilio =" + rutaDomicilio + " , entrega_domicilio = " + entregaDomicilio + " , colorb = " + estado.getColorb() + " , impresion =" + impresion + " , imagen = ? where idEstado = " + estado.getIdestado();  
			PreparedStatement actualiz = null;
			actualiz = con1.prepareStatement(update);
			actualiz.setBytes(1, estado.getImagen());
			if(auditoria)
			{
				logger.info(update);
			}
			actualiz.executeUpdate();
			
			stm.close();
			con1.close();
		}
		catch (Exception e){
			logger.error(e.toString());
			try
			{
				con1.close();
			}catch(Exception e1)
			{
			}
			return(false);
		}
		return(true);
	}
	
	public static boolean tieneEstadoInicial( int idTipoPedido, int idEstado, boolean auditoria)
	{
		Logger logger = Logger.getLogger("log_file");
		ConexionBaseDatos con = new ConexionBaseDatos();
		Connection con1 = con.obtenerConexionBDLocal();
		boolean respuesta = false;
		
		try
		{
			Statement stm = con1.createStatement();
			String consulta = "select * from estado a where estado_inicial = 1 and a.idtipopedido = " + idTipoPedido + " and a.idestado != " + idEstado;
			if(auditoria)
			{
				logger.info(consulta);
			}
			ResultSet rs = stm.executeQuery(consulta);
						
			while(rs.next()){
				respuesta = true;
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
		return(respuesta);
		
	}
	
	
	public static boolean tieneEstadoFinal( int idTipoPedido, int idEstado, boolean auditoria)
	{
		Logger logger = Logger.getLogger("log_file");
		ConexionBaseDatos con = new ConexionBaseDatos();
		Connection con1 = con.obtenerConexionBDLocal();
		boolean respuesta = false;
		
		try
		{
			Statement stm = con1.createStatement();
			String consulta = "select * from estado a where estado_final = 1 and a.idtipopedido = " + idTipoPedido + " and a.idestado != " + idEstado;
			if(auditoria)
			{
				logger.info(consulta);
			}
			ResultSet rs = stm.executeQuery(consulta);
						
			while(rs.next()){
				respuesta = true;
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
		return(respuesta);
		
	}
	
	public static int obtenerEstadoFinal( int idTipoPedido, boolean auditoria)
	{
		Logger logger = Logger.getLogger("log_file");
		ConexionBaseDatos con = new ConexionBaseDatos();
		Connection con1 = con.obtenerConexionBDLocal();
		int estadoFinal = 0;
		
		try
		{
			Statement stm = con1.createStatement();
			String consulta = "select idestado from estado  where estado_final = 1 and idtipopedido = " + idTipoPedido;
			if(auditoria)
			{
				logger.info(consulta);
			}
			ResultSet rs = stm.executeQuery(consulta);
						
			while(rs.next()){
				estadoFinal = rs.getInt("idestado");
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
		return(estadoFinal);
		
	}
	
	/**
	 * Método que retorna toda la información completa de un estado inicial para un tipo de pedido
	 * @param idTipoPedido Se recibe el tipo de pedido para el cual desea obtenerse el estado inicila
	 * @param auditoria parámetro para habilitar o no la auditoria
	 * @return Un objeto de tipo estado con la información.
	 */
	public static Estado obtenerEstadoInicial( int idTipoPedido, boolean auditoria)
	{
		Logger logger = Logger.getLogger("log_file");
		ConexionBaseDatos con = new ConexionBaseDatos();
		Connection con1 = con.obtenerConexionBDLocal();
		int estadoInicial = 0;
		Estado estado = new Estado();
		try
		{
			Statement stm = con1.createStatement();
			String consulta = "select * from estado  where estado_inicial = 1 and idtipopedido = " + idTipoPedido;
			if(auditoria)
			{
				logger.info(consulta);
			}
			ResultSet rs = stm.executeQuery(consulta);
			String descripcion = "";
			String descripcionCorta = "";
			boolean estInicial = false;
			boolean estFinal = false;
			boolean impresion = false;
			boolean rutaDomicilio = false;
			boolean entregaDomicilio = false;
			int intEstInicial, intEstFinal, intImpresion, intRutaDomicilio, intEntregaDomicilio;	
			int colorr=0, colorg=0, colorb=0;
			byte[] imagen = null;
			
			while(rs.next()){
				estadoInicial = rs.getInt("idestado");
				descripcion = rs.getString("descripcion");
				descripcionCorta = rs.getString("descripcion_corta");
				intEstInicial = rs.getInt("estado_inicial");
				intEstFinal = rs.getInt("estado_final");
				intImpresion = rs.getInt("impresion");
				colorr = rs.getInt("colorr");
				colorg = rs.getInt("colorg");
				colorb = rs.getInt("colorb");
				imagen = rs.getBytes("imagen");
				intEntregaDomicilio = rs.getInt("ruta_domicilio");
				intRutaDomicilio = rs.getInt("entrega_domicilio");
				if(intImpresion == 1)
				{
					impresion = true;
				}
				if(intEstInicial == 1)
				{
					estInicial = true;
				}
				if(intEstFinal == 1)
				{
					estFinal = true;
				}
				if(intEntregaDomicilio == 1)
				{
					entregaDomicilio = true;
				}
				if(intRutaDomicilio == 1)
				{
					rutaDomicilio = true;
				}
				estado = new Estado(estadoInicial, descripcion, descripcionCorta,idTipoPedido, "", estInicial, estFinal,colorr,colorg,colorb, impresion, rutaDomicilio, entregaDomicilio);
				estado.setImagen(imagen);
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
		return(estado);
		
	}
	
	/**
	 * Método que se encarga dado un tipo de pedido y un estado determinar si dicho pedido es o no inicial
	 * @param idTipoPedido el tipo de pedido del cual se desea confirmar la información
	 * @param idEstado idEstado del cual se desea confirmar la información de si es un estado inicial
	 * @return Se retorna un valor booleano que indica si la combinación de estado y tipo de pedido corresponde a un estado inicial
	 */
	public static boolean esEstadoInicial(int idTipoPedido, int idEstado, boolean auditoria)
	{
		Logger logger = Logger.getLogger("log_file");
		ConexionBaseDatos con = new ConexionBaseDatos();
		Connection con1 = con.obtenerConexionBDLocal();
		boolean respuesta = false;
		
		try
		{
			Statement stm = con1.createStatement();
			String consulta = "select * from estado where idtipopedido =" + idTipoPedido + " and idestado = " + idEstado + " and estado_inicial = 1";
			if(auditoria)
			{
				logger.info(consulta);
			}
			ResultSet rs = stm.executeQuery(consulta);
			while(rs.next()){
				respuesta = true;
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
		return(respuesta);
		
	}
	
	/**
	 * Método que se encarga dado un tipo de pedido y un estado determinar si dicho pedido es o no inicial
	 * @param idTipoPedido el tipo de pedido del cual se desea confirmar la información
	 * @param idEstado idEstado del cual se desea confirmar la información de si es un estado inicial
	 * @return Se retorna un valor booleano que indica si la combinación de estado y tipo de pedido corresponde a un estado inicial
	 */
	public static boolean esEstadoFinal(int idTipoPedido, int idEstado, boolean auditoria)
	{
		Logger logger = Logger.getLogger("log_file");
		ConexionBaseDatos con = new ConexionBaseDatos();
		Connection con1 = con.obtenerConexionBDLocal();
		boolean respuesta = false;
		
		try
		{
			Statement stm = con1.createStatement();
			String consulta = "select * from estado where idtipopedido =" + idTipoPedido + " and idestado = " + idEstado + " and estado_final = 1";
			if(auditoria)
			{
				logger.info(consulta);
			}
			ResultSet rs = stm.executeQuery(consulta);
			while(rs.next()){
				respuesta = true;
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
		return(respuesta);
		
	}
	public static boolean esEstadoRutaDomicilio( int idEstado, boolean auditoria)
	{
		Logger logger = Logger.getLogger("log_file");
		ConexionBaseDatos con = new ConexionBaseDatos();
		Connection con1 = con.obtenerConexionBDLocal();
		boolean respuesta = false;
		
		try
		{
			Statement stm = con1.createStatement();
			String consulta = "select * from estado where  idestado = " + idEstado + " and ruta_domicilio = 1";
			if(auditoria)
			{
				logger.info(consulta);
			}
			ResultSet rs = stm.executeQuery(consulta);
			while(rs.next()){
				respuesta = true;
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
		return(respuesta);
		
	}
}

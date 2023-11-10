package capaDAOPixelpos;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import com.mysql.cj.jdbc.result.ResultSetMetaData;
import capaModeloPOS.Estado;
import capaModeloPOS.EstadoPedido;
import capaModeloWeb.Cliente;
import capaModeloWeb.DetallePedidoPixel;
import capaModeloWeb.EstadoPedidoTienda;
import capaModeloWeb.RespuestaPedidoPixel;
import capaModeloWeb.Tienda;
import capaConexion.ConexionBaseDatos;
public class PedidoPOSPMDAO {
	

	
	
	
	/**
	 * Método en la capa de acceso a datos que se encarga de lanzar la consulta para conocer los estados de los pedidos de una
	 * tienda en el día en curso, retorma la información de estos pedidos en un ArrayList con objetos de tipo EstadoPedidoTienda
	 * @param dsnODBC Se recibe como parámetro el string con dsn para la conexión a la tienda.
	 * @return Se retorna un arrayList con objetos tipo EstadoPedidoTienda.
	 */
	public static ArrayList<EstadoPedidoTienda> ConsultarEstadoPedidoTienda()
	{
		 ConexionBaseDatos conexion = new ConexionBaseDatos();
		 Connection con = conexion.obtenerConexionBDLocal();
		 ArrayList<EstadoPedidoTienda> estadosPedido = new ArrayList();
		 try
			{
				Statement state = con.createStatement();
				String consulta = "SELECT * FROM ((SELECT b.nombre_largo as domiciliario,"
						+"a.idpedidotienda AS Transaccion,a.fechainsercion	 as horapedido, "
						+" IF(e.estado_final = 1, 'Finalizado' ,CASE when e.ruta_domicilio = 1 then 'En Ruta' when e.entrega_domicilio = 1 then 'Finalizado' when e.estado_cocina = 1 then 'En Cocina' when e.programado = 1 then 'Programado' ELSE 'Esperando' END) as estatus, " 
						+"   IF(e.ruta_domicilio = 1, TIMESTAMPDIFF(MINUTE,(select g.fechacambio from cambios_estado_pedido g where g.idpedidotienda = a.idpedidotienda and g.idestadoposterior = e.idestado order by g.fechacambio desc limit 1),NOW()), 0) as tiempoenruta, "
						+"  TIMESTAMPDIFF(MINUTE,(select f.fechacambio from cambios_estado_pedido f where f.idpedidotienda = a.idpedidotienda and f.idestadoanterior = 0 and f.idestadoposterior = 0  ), NOW()) as tiempototal , "
						+"g.nombre as tomadordepedido, "
						+"c.direccion as direccion,c.telefono AS telefono, "
						+"concat(c.nombre,' ',c.apellido) as nombrecompleto, "
						+"i.nombre as formapago "
						+"FROM pedido a join cliente c ON a.idcliente = c.idcliente "
						+"JOIN tipo_pedido j ON a.idtipopedido = j.idtipopedido and j.esdomicilio = 1 "
						+"JOIN usuario g ON g.nombre = a.usuariopedido "
						+"JOIN pedido_forma_pago h ON h.idpedidotienda = a.idpedidotienda "
						+"JOIN forma_pago i ON i.idforma_pago = h.idforma_pago "
						+"JOIN tienda d ON a.fechapedido = d.fecha_apertura " 
						+"JOIN estado e ON a.idestado = e.idestado "
						+"LEFT JOIN  usuario b ON a.iddomiciliario = b.id "
						+"ORDER BY a.idpedidotienda DESC)  "
						+"UNION"
						+"(SELECT b.nombre_largo as domiciliario,"
						+"a.idpedidotienda AS Transaccion,a.fechainsercion	 as horapedido, "
						+" IF(e.estado_final = 1, 'Finalizado' ,CASE when e.ruta_domicilio = 1 then 'En Ruta' when e.entrega_domicilio = 1 then 'Finalizado' when e.estado_cocina = 1 then 'En Cocina' when e.programado = 1 then 'Programado' ELSE 'Esperando' END) as estatus, " 
						+"   IF(e.ruta_domicilio = 1, TIMESTAMPDIFF(MINUTE,(select g.fechacambio from cambios_estado_pedido g where g.idpedidotienda = a.idpedidotienda and g.idestadoposterior = e.idestado order by g.fechacambio desc limit 1),NOW()), 0) as tiempoenruta, "
						+"  TIMESTAMPDIFF(MINUTE,(select f.fechacambio from cambios_estado_pedido f where f.idpedidotienda = a.idpedidotienda and f.idestadoanterior = 0 and f.idestadoposterior = 0  ), NOW()) as tiempototal , "
						+"g.nombre as tomadordepedido, "
						+"c.direccion as direccion,c.telefono AS telefono, "
						+"concat(c.nombre,' ',c.apellido) as nombrecompleto, "
						+"i.nombre as formapago "
						+"FROM pedido a join cliente c ON a.idcliente = c.idcliente "
						+"JOIN tipo_pedido j ON a.idtipopedido = j.idtipopedido and j.esdomicilio = 0 "
						+"JOIN usuario g ON g.nombre = a.usuariopedido "
						+"JOIN pedido_forma_pago h ON h.idpedidotienda = a.idpedidotienda "
						+"JOIN forma_pago i ON i.idforma_pago = h.idforma_pago "
						+"JOIN tienda d ON a.fechapedido = d.fecha_apertura " 
						+"JOIN estado e ON a.idestado = e.idestado AND e.descripcion_corta LIKE '%En Elabora%' "
						+"LEFT JOIN  usuario b ON a.iddomiciliario = b.id "
						+"ORDER BY a.idpedidotienda DESC)) AS z ORDER BY z.Transaccion desc ";
				System.out.println(consulta);
				ResultSet rs = state.executeQuery(consulta);
				String domiciliario;
				String estatus;
				int transaccion;
				String horaPedido;
				String tiempoTotal;
				String tiempoEnRuta;
				String direccion;
				String telefono;
				String nombreCompleto;
				String tomadorDePedido;
				String formaPago;
				while(rs.next())
				{
					domiciliario = rs.getString("domiciliario");
					estatus = rs.getString("estatus");
					transaccion = rs.getInt("transaccion");
					horaPedido = rs.getString("horaPedido");
					tiempoTotal = rs.getString("tiempototal");
					direccion= rs.getString("direccion");
					telefono = rs.getString("telefono");
					nombreCompleto = rs.getString("nombrecompleto");
					tiempoEnRuta = rs.getString("tiempoenruta");
					tomadorDePedido = rs.getString("tomadordepedido");
					formaPago = rs.getString("formapago");
					EstadoPedidoTienda estPed = new EstadoPedidoTienda(domiciliario,estatus, transaccion, horaPedido,tiempoTotal,direccion, telefono,nombreCompleto, tiempoEnRuta, tomadorDePedido, formaPago);
					estadosPedido.add(estPed);
					
				}
				state.close();
				con.close();
				
			}catch(Exception e)
			{
				System.out.println(e.getMessage());
				
			}
		 	
		 	return(estadosPedido);
	}
	
	public static ArrayList<EstadoPedidoTienda> ConsultarMonitoreoPlataformas()
	{
		 ConexionBaseDatos conexion = new ConexionBaseDatos();
		 Connection con = conexion.obtenerConexionBDLocal();
		 ArrayList<EstadoPedidoTienda> estadosPedido = new ArrayList();
		 try
			{
				Statement state = con.createStatement();
				String consulta = "SELECT b.nombre_largo as domiciliario,"
						+"a.idpedidotienda AS Transaccion,a.fechainsercion	 as horapedido, "
						+" IF(e.estado_final = 1, 'Finalizado' ,CASE when e.ruta_domicilio = 1 then 'En Ruta' when e.entrega_domicilio = 1 then 'Finalizado' when e.estado_cocina = 1 then 'En Cocina' when e.programado = 1 then 'Programado' ELSE 'Esperando' END) as estatus, " 
						+"   IF(e.ruta_domicilio = 1, TIMESTAMPDIFF(MINUTE,(select g.fechacambio from cambios_estado_pedido g where g.idpedidotienda = a.idpedidotienda and g.idestadoposterior = e.idestado order by g.fechacambio desc limit 1),NOW()), 0) as tiempoenruta, "
						+"  TIMESTAMPDIFF(MINUTE,(select f.fechacambio from cambios_estado_pedido f where f.idpedidotienda = a.idpedidotienda and f.idestadoanterior = 0 and f.idestadoposterior = 0  ), NOW()) as tiempototal , "
						+"g.nombre as tomadordepedido, "
						+"c.direccion as direccion,c.telefono AS telefono, "
						+"concat(c.nombre,' ',c.apellido) as nombrecompleto, "
						+"i.nombre as formapago, a.estacion, a.idpedidoalt "
						+"FROM pedido a join cliente c ON a.idcliente = c.idcliente AND (a.estacion LIKE '%RAPPI%' OR a.estacion LIKE '%DIDI%')"
						+"JOIN tipo_pedido j ON a.idtipopedido = j.idtipopedido and j.esdomicilio = 1 "
						+"JOIN usuario g ON g.nombre = a.usuariopedido "
						+"JOIN pedido_forma_pago h ON h.idpedidotienda = a.idpedidotienda "
						+"JOIN forma_pago i ON i.idforma_pago = h.idforma_pago "
						+"JOIN tienda d ON a.fechapedido = d.fecha_apertura " 
						+"JOIN estado e ON a.idestado = e.idestado "
						+"LEFT JOIN  usuario b ON a.iddomiciliario = b.id "
						+"ORDER BY a.idpedidotienda DESC  ";
				System.out.println(consulta);
				ResultSet rs = state.executeQuery(consulta);
				String domiciliario;
				String estatus;
				int transaccion;
				String horaPedido;
				String tiempoTotal;
				String tiempoEnRuta;
				String direccion;
				String telefono;
				String nombreCompleto;
				String tomadorDePedido;
				String formaPago;
				String estacion;
				String idPedidoAlt;
				while(rs.next())
				{
					domiciliario = rs.getString("domiciliario");
					estatus = rs.getString("estatus");
					transaccion = rs.getInt("transaccion");
					horaPedido = rs.getString("horaPedido");
					tiempoTotal = rs.getString("tiempototal");
					direccion= rs.getString("direccion");
					telefono = rs.getString("telefono");
					nombreCompleto = rs.getString("nombrecompleto");
					tiempoEnRuta = rs.getString("tiempoenruta");
					tomadorDePedido = rs.getString("tomadordepedido");
					formaPago = rs.getString("formapago");
					estacion = rs.getString("estacion");
					idPedidoAlt = rs.getString("idpedidoalt");
					EstadoPedidoTienda estPed = new EstadoPedidoTienda(domiciliario,estatus, transaccion, horaPedido,tiempoTotal,direccion, telefono,nombreCompleto, tiempoEnRuta, tomadorDePedido, formaPago);
					estPed.setEstacion(estacion);
					estPed.setIdPedidoAlt(idPedidoAlt);
					estadosPedido.add(estPed);
					
				}
				state.close();
				con.close();
				
			}catch(Exception e)
			{
				System.out.println(e.getMessage());
				
			}
		 	
		 	return(estadosPedido);
	}
	
	
	/**
	 * Este método fue creado con el fin de retornar los pedidos empacados para ser desplegados desde la ventana de comandas
	 * se podrá tener opción de mejora a futuro para  que los estados sean parametrizados.
	 * @param fechaPedido
	 * @return
	 */
	public static ArrayList obtenerPedidosEmpacadosDomicilio(String fechaPedido, long estEnRutaDom)
	{
		Logger logger = Logger.getLogger("log_file");
		ConexionBaseDatos con = new ConexionBaseDatos();
		Connection con1 = con.obtenerConexionBDLocal();
		ArrayList pedidos = new ArrayList();
		//En este punto y de manera temporal vamos a quemar el idEstado del producto empacado para domicilios
		try
		{
			Statement stm = con1.createStatement();
			String consulta = "select a.idpedidotienda, a.fechapedido, concat_ws(' ', b.nombre,  b.apellido) as nombres, c.descripcion as tipopedido, d.descripcion_corta as estado, b.direccion, a.idtipopedido, a.idestado, b.latitud, b.longitud, '' from pedido a, cliente b, tipo_pedido c, estado d  where a.idestado = d.idestado and a.idcliente = b.idcliente and d.estado_final <> 1 and a.idmotivoanulacion IS NULL and a.idtipopedido = c.idtipopedido and a.idestado = " + estEnRutaDom + " and fechapedido = '" + fechaPedido + "' order by a.fechainsercion desc";
			ResultSet rs = stm.executeQuery(consulta);
			ResultSetMetaData rsMd = (ResultSetMetaData) rs.getMetaData();
			int numeroColumnas = rsMd.getColumnCount();
			
			while(rs.next()){
				String [] fila = new String[numeroColumnas];
				for(int y = 0; y < numeroColumnas; y++)
				{
					fila[y] = rs.getString(y+1);
				}
				pedidos.add(fila);
				
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
		return(pedidos);
		
	}
	
	public static ArrayList<EstadoPedido> obtenerEstadoPedidosFecha(String fecha)
	{
		Logger logger = Logger.getLogger("log_file");
		ConexionBaseDatos con = new ConexionBaseDatos();
		Connection con1 = con.obtenerConexionBDLocal();
		ArrayList<EstadoPedido> estadosPedidos = new ArrayList();
		try
		{
			Statement stm = con1.createStatement();
			String consulta = "select a.idpedidotienda, a.idestado, b.estado_final from pedido a, estado b  where a.idestado = b.idestado and a.fechapedido = '" + fecha + "'";
			ResultSet rs = stm.executeQuery(consulta);
			int idPedidoTienda = 0;
			int idEstado = 0;
			int intEstadoFinal = 0;
			boolean estadoFinal = false;
			EstadoPedido estPedido = new EstadoPedido(0,0,false);
			while(rs.next()){
				idPedidoTienda = rs.getInt("idpedidotienda");
				idEstado = rs.getInt("idestado");
				intEstadoFinal = rs.getInt("estado_final");
				if(intEstadoFinal == 1)
				{
					estadoFinal = true;
				}
				estPedido = new EstadoPedido(idPedidoTienda, idEstado, estadoFinal);
				estadosPedidos.add(estPedido);
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
		return(estadosPedidos);
		
	}
	
	public static Estado obtenerEstadoPedido(int idPedidoTienda)
	{
		Logger logger = Logger.getLogger("log_file");
		ConexionBaseDatos con = new ConexionBaseDatos();
		Connection con1 = con.obtenerConexionBDLocal();
		Estado estadoPedido = new Estado(0, "", "", 0, "", false, false, 0, 0, 0, false,false, false);
		try
		{
			Statement stm = con1.createStatement();
			String consulta = "select a.idestado, b.descripcion, c.descripcion as desc_tipo, a.idtipopedido, b.imagen  from pedido a, estado b, tipo_pedido c   where  a.idtipopedido = c.idtipopedido and a.idestado = b.idestado and a.idtipopedido = b.idtipopedido and idpedidotienda = " + idPedidoTienda + "";
						ResultSet rs = stm.executeQuery(consulta);
			int idEstado = 0;
			int idTipoPedido = 0;
			String descEstado = "";
			String descTipo = "";
			byte[] imagen = null;
			while(rs.next()){
				idEstado = rs.getInt("idestado");
				idTipoPedido = rs.getInt("idtipopedido");
				descEstado = rs.getString("descripcion");
				descTipo = rs.getString("desc_tipo");
				imagen = rs.getBytes("imagen");
				
			}
			estadoPedido = new Estado(idEstado,descEstado, descEstado, idTipoPedido, "", false, false, 0, 0, 0, false, false,false);
			estadoPedido.setImagen(imagen);
			estadoPedido.setTipoPedido(descTipo);
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
		return(estadoPedido);
		
	}
	
	public static boolean validarPedidoContactExiste(int idPedidoContact)
	{
		Logger logger = Logger.getLogger("log_file");
		ConexionBaseDatos con = new ConexionBaseDatos();
		Connection con1 = con.obtenerConexionBDLocal();
		boolean respuesta = false;
		//En este punto y de manera temporal vamos a quemar el idEstado del producto empacado para domicilios
		try
		{
			Statement stm = con1.createStatement();
			String consulta = "select * from pedido_contact_center where idpedidocontact = "+ idPedidoContact;
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
	
	public static boolean insertarPedidoContactExiste(int idPedidoContact, int idPedidoTienda)
	{
		Logger logger = Logger.getLogger("log_file");
		ConexionBaseDatos con = new ConexionBaseDatos();
		Connection con1 = con.obtenerConexionBDLocal();
		boolean respuesta = false;
		//En este punto y de manera temporal vamos a quemar el idEstado del producto empacado para domicilios
		try
		{
			Statement stm = con1.createStatement();
			String insertar = "insert into pedido_contact_center (idpedidocontact, idpedidotienda) values ("+ idPedidoContact + "," + idPedidoTienda + ")";
			stm.executeUpdate(insertar);
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

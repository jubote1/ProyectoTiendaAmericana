package capaDAOPixelpos;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import capaConexion.ConexionBaseDatos;
import capaModeloWeb.Cliente;
import capaModeloWeb.DetallePedidoPixel;
import capaModeloWeb.EstadoPedidoTienda;
import capaModeloWeb.RespuestaPedidoPixel;
import capaModeloWeb.Tienda;
public class PedidoPixelDAO {
	

	public static RespuestaPedidoPixel confirmarPedidoPixelTienda(int idpedido,double valorformapago, double valortotal, Cliente cliente, boolean indicadorAct, String dsnTienda, ArrayList<DetallePedidoPixel> envioPixel, int idformapagotienda )
	{
		// Se define la variable a retornar
		int numFactura = 0;
		// Se instancia el objeto de la clase que nos servirá para la inserción del pedido
		Main principal = new Main();
				
		//Si memcode = 0 es porque hay que crear el cliente
		//Si memcode <> 0 y indicador igual a true hay que actualizar
		//Si memcode <> 0 y indicador igual a false hay que actualizar
		int memcodeAnt = cliente.getMemcode();
		// Se invoca el método para la inserción del pedido y se retornará el número de la factura
		numFactura = principal.main(envioPixel, dsnTienda,cliente.getMemcode(),cliente, indicadorAct, valorformapago, idpedido, idformapagotienda);
		boolean creaCliente = false;
		RespuestaPedidoPixel resPedPixel;
		if (memcodeAnt == 0)
		{
			creaCliente = true;
		}
		if(numFactura != 0)
		{
			resPedPixel = new RespuestaPedidoPixel(creaCliente, numFactura, cliente.getMemcode(),idpedido,cliente.getIdcliente());
		}else
		{
			resPedPixel = new RespuestaPedidoPixel(false, 0, 0, 0, 0);
		}
		
		
		return(resPedPixel);
	}
	
	
	/**
	 * Método en la capa de acceso a datos que se encarga de lanzar la consulta para conocer los estados de los pedidos de una
	 * tienda en el día en curso, retorma la información de estos pedidos en un ArrayList con objetos de tipo EstadoPedidoTienda
	 * @param dsnODBC Se recibe como parámetro el string con dsn para la conexión a la tienda.
	 * @return Se retorna un arrayList con objetos tipo EstadoPedidoTienda.
	 */
	public static ArrayList<EstadoPedidoTienda> ConsultarEstadoPedidoTienda(String dsnODBC)
	{
		 ConexionBaseDatos conexion = new ConexionBaseDatos();
		 Connection con = conexion.obtenerConexionBDTienda(dsnODBC);
		 ArrayList<EstadoPedidoTienda> estadosPedido = new ArrayList();
		 try
			{
				Statement state = con.createStatement();
				String consulta = "SELECT b.POSNAME as domiciliario,"
						+"a.Transact AS Transaccion,d.timeend as horapedido, "
						+"CASE when (a.DeliveryStatus = 0) then string('Esperando') " 
						+"when (a.DeliveryStatus = 1) then string('En Ruta') " 
						+"when (a.DeliveryStatus = 2) then string('Finalizado')  END as estatus, "
						+"CASE when (a.DeliveryStatus = 0) then string('Pediente de entrega') "
						+"when (a.DeliveryStatus = 1) then string(DATEDIFF(mi,a.TimeOut,now()),' min') " 
						+"when (a.DeliveryStatus = 2) then string(DATEDIFF(mi,a.TimeOut,a.TimeIn),' min')  END as tiempoenruta, "
						+"CASE WHEN (a.DeliveryStatus > 2) THEN string('') "
						+"WHEN (a.DeliveryStatus = 2) THEN string(DATEDIFF(mi,d.timeend,a.TimeIn),' min') "
						+"WHEN (a.DeliveryStatus < 2) THEN string(DATEDIFF(mi,d.timeend,now()),' min') END as tiempototal , "
						+"g.POSNAME as tomadordepedido, "
						+"string(c.ADRESS1,' ',c.ADRESS2,' ',c.Directions) as direccion,c.HOMETELE AS telefono, "
						+"string(c.FIRSTNAME,' ',c.LASTNAME) as nombrecompleto, "
						+"i.DESCRIPT as formapago "
						+"FROM dba.PosHDelivery a join dba.Member c ON a.MemCode = c.MEMCODE "
						+"JOIN dba.POSHEADER d ON a.Transact = d.transact "
						+"JOIN dba.employee g ON g.EmpNum = d.whostart "
						+"JOIN dba.Howpaid h ON h.transact = d.transact and h.voidedlink = 0 "
						+"JOIN dba.MethodPay i ON i.METHODNUM = h.METHODNUM "
						+"LEFT JOIN  dba.employee b ON a.EmpNum = b.EMPNUM "
						+"WHERE  a.OpenDate = dba.PixOpenDate() "
						+"ORDER BY a.Transact DESC  ";
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
	
	
	

}

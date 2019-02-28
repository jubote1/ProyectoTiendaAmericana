package capaControladorServicios;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import capaControlador.ClienteCtrl;
import capaControlador.ParametrosProductoCtrl;
import capaControlador.ParametrosCtrl;
import capaDAOPixelpos.EstadoDAO;
import capaDAOPixelpos.PedidoPOSPMDAO;
import capaDAOPixelpos.Main;
import capaDAOPixelpos.PedidoPOSPMDAO;
import capaDAOPixelpos.PedidoPixelDAO;
import capaDAOPixelpos.TiendaDAO;
import capaModelo.DetallePedido;
import capaModelo.FechaSistema;
import capaModelo.Municipio;
import capaModelo.NomenclaturaDireccion;
import capaModelo.Parametro;
import capaModelo.TiempoPedido;
import capaModeloWeb.Cliente;
import capaModeloWeb.DetallePedidoPixel;
import capaModeloWeb.EstadoPedidoTienda;
import capaModeloWeb.RespuestaPedidoPixel;
import capaModeloWeb.Tienda;
import interfazGrafica.PrincipalLogueo;
import interfazGrafica.Sesion;
import interfazGrafica.VentPedTomarPedidos;

public class PedidoCtrl {
	
	public PedidoCtrl()
	{
		
	}
	
	
	/**
	 * Método que tiene toda la lógica para descomponer el json entrante, insertar el pedido en la tienda y entregar
	 * un objeto tipo Respuesta Pedido y entregar finalmente un json de respuesta.
	 * @param datos Se recibe un string en formato JSON con todos los datos para la inserción del pedido.
	 * @return Se retorna un string en formato JSON con la respuesta de la inserción del pedido.
	 */
	public String InsertarPedidoPixel(String datos)
	{
		// Debemos de procesar todo datos para extraer todo los datos requeridos
		 JSONParser parser = new JSONParser();
		 JSONArray respuestaJSON = new JSONArray();
		 try {
			  Object objParser = parser.parse(datos);
			 JSONObject jsonPedido = (JSONObject) objParser;
			 int idpedido;
			 boolean insertado;
			 String dsnTienda;
			 int memcode;
			 String clienteJSON;
			 String usuario;
			 boolean indicadorAct;
			 double valorformapago;
			 double valortotal;
			 int cantidaditempedido;
			 int idformapagotienda;
			 int POS = 0;
			 int tiempoPedido;
			  // Realizamos el parseo inicial de la información que se le envía al servicio
			 Long tempidpedido = new Long((long)jsonPedido.get("idpedido"));
			 idpedido =  (new Integer(tempidpedido.intValue()));
			 //insertado = (boolean)jsonPedido.get("insertado");
			 dsnTienda = (String)jsonPedido.get("dsntienda");
			 Long tempPOS = new Long((long)jsonPedido.get("pos"));
			 POS = tempPOS.intValue();
			 Long tempmemcode = new Long((long)jsonPedido.get("memcode"));
			 memcode = (new Integer(tempmemcode.intValue()));
			 clienteJSON = (String)jsonPedido.get("cliente");
			 indicadorAct = (boolean)jsonPedido.get("indicadoract");
			 valorformapago = new Double((long)jsonPedido.get("valorformapago"));
			 valortotal = new Double((long)jsonPedido.get("valortotal"));
			 Long idformapagotientmp = new Long((long)jsonPedido.get("idformapagotienda"));
			 idformapagotienda = (new Integer(idformapagotientmp.intValue()));
			  // Realizamos el parseo para el nivel de clientes
			 Object objParserCliente = parser.parse(clienteJSON);
			 JSONObject jsonCliente =(JSONObject) ((JSONArray)objParserCliente).get(0);
			 Long tempidcliente = new Long((long)jsonCliente.get("idcliente"));
			 Long tempclimemcode = new Long((long)jsonCliente.get("memcode"));
			 usuario = (String)jsonPedido.get("usuariopedido");
			 Long temptiempoPedido = new Long( (long)jsonPedido.get("tiempopedido"));
			 tiempoPedido = temptiempoPedido.intValue();
			 Tienda tienda = TiendaDAO.obtenerTienda();
			 //Realizamos el casteo controlado de las variables para la creación del cliente
			 int idMunicipio;
			 float latitud;
			 float longitud;
			 String municipio;
			 int idNomenclatura;
			 try{
				 latitud = (new Double( (double) jsonCliente.get("latitud"))).floatValue();
			 }catch(Exception e)
			 {
				 System.out.println("PROBLEMA LATITUD " + e.toString());
				 latitud = 0;
			 }
			 try{
				 longitud = (new Double( (double) jsonCliente.get("longitud"))).floatValue();
			 }catch(Exception e)
			 {
				 System.out.println("PROBLEMA LONGITUD " + e.toString());
				 longitud = 0;
			 }
			 try{
				 idMunicipio = (new Long( (long) jsonCliente.get("idmunicipio"))).intValue();
			 }catch(Exception e)
			 {
				 System.out.println("PROBLEMA ID MUNICIPIO " + e.toString());
				 idMunicipio = 0;
			 }
			 try{
				 municipio = (String) jsonCliente.get("municipio");
			 }catch(Exception e)
			 {
				 System.out.println("PROBLEMA MUNICIPIO " + e.toString());
				 municipio = "";
			 }
			 try{
				 idNomenclatura = (new Long( (long) jsonCliente.get("idnomenclatura"))).intValue();
			 }catch(Exception e)
			 {
				 System.out.println("PROBLEMA IDNOMENCLATURA " + e.toString());
				 idNomenclatura = 0;
			 }
			 Cliente cliente = new Cliente((new Integer(tempidcliente.intValue())), (String) jsonCliente.get("telefono") , (String) jsonCliente.get("nombres"), (String) jsonCliente.get("apellidos"), (String) jsonCliente.get("nombrecompania"), (String) jsonCliente.get("direccion"), municipio, idMunicipio,
					latitud, longitud, (String) jsonCliente.get("zonadireccion"), (String) jsonCliente.get("observacion"), tienda.getNombretienda(), tienda.getIdTienda(), (new Integer(tempclimemcode.intValue())),idNomenclatura, (String) jsonCliente.get("numnomenclatura"), (String) jsonCliente.get("numnomenclatura2"), (String) jsonCliente.get("num3"), (String) jsonCliente.get("nomenclatura"));
			 //Sacamos la cantidad de detalles pedidos
			 Long tempcantidaditem = new Long((long)jsonPedido.get("cantidaditempedido"));
			 cantidaditempedido = (new Integer(tempcantidaditem.intValue()));
			 // Realizamos el parseo para el pedido
			 ArrayList<DetallePedidoPixel> detPedidoPixel= new ArrayList();
			 Long tempidprod;
			 double tempcantidad;
			 boolean esMaster;
			 int idMaster;
			 int idModificador;
			 int idDetalle;
			 if(POS == 1)
			 {
				 for(int i = 1; i<= cantidaditempedido; i++)
				 {
					 
					 tempidprod = new Long((long)jsonPedido.get("idproductoext"+ i));
					 tempcantidad = ((Number)jsonPedido.get("cantidad" + i)).doubleValue();
					 esMaster = (boolean)jsonPedido.get("esmaster" + i);
					 idMaster = (new Long((long)jsonPedido.get("idmaster" + i))).intValue();
					 idModificador = (new Long((long)jsonPedido.get("idmodificador" + i))).intValue();
					 idDetalle = (new Long((long)jsonPedido.get("iddetalle" + i))).intValue();
					 //System.out.println("DETALLE DEL PEDIDO OJO " + tempidprod + " "  + tempcantidad + " " + esMaster + " " + idMaster + " " + idModificador  + " " + idDetalle );
					 detPedidoPixel.add(new DetallePedidoPixel( tempidprod.intValue(), tempcantidad, esMaster,idMaster,idModificador,idDetalle ));
				 }
			 }else if(POS == 2)
			 {
				 for(int i = 1; i<= cantidaditempedido; i++)
				 {
					 
					 tempidprod = new Long((long)jsonPedido.get("idproductoext"+ i));
					 tempcantidad = ((Number)jsonPedido.get("cantidad" + i)).doubleValue();
					 detPedidoPixel.add(new DetallePedidoPixel( tempidprod.intValue(), tempcantidad ));
				 }
			 }
			 RespuestaPedidoPixel respuesta  = new RespuestaPedidoPixel();
			 //Realizamos la validación de que POS para terminar el pedido.
			 if(POS == 2)
			 {
				 //Bajo esta condición estamos en el sistema Pixel POS
				 respuesta = PedidoPixelDAO.confirmarPedidoPixelTienda(idpedido,valorformapago, valortotal, cliente,indicadorAct, dsnTienda, detPedidoPixel,idformapagotienda);
			 }else
			 {
				 //Para esta situación estaremos en el sistema POS Pizza Americana
				 respuesta = confirmarPedidoPOSPMTienda(idpedido,valorformapago, valortotal, cliente,indicadorAct, dsnTienda, detPedidoPixel,idformapagotienda, tienda.getIdTienda(), usuario, tiempoPedido);
			 }
			  JSONObject cadaResJSON = new JSONObject();
			 cadaResJSON.put("creacliente", respuesta.getClienteCreado());
			 cadaResJSON.put("numerofactura", respuesta.getNumeroFactura());
			 cadaResJSON.put("membercode", respuesta.getMembercode());
			 cadaResJSON.put("idpedido", respuesta.getIdpedido());
			 cadaResJSON.put("idcliente", respuesta.getIdcliente());
			 respuestaJSON.add(cadaResJSON);
			 
			 
		 }catch (Exception e) {
	            e.printStackTrace();
	     } 
		 return(respuestaJSON.toString());
	}
	
	/**
	 * Método de la capa controladora que se encarga de retornar en formato Json los estados de los pedidos tipo 
	 * domicilio que tiene la tienda, recopilando los que están pendientes de preparación, los que están en ruta
	 * y los que están en estado finalizado.
	 * @param dsnODBC Se recibe el datasourcename para la conexión a la base de datos.
	 * @return
	 */
	public String ConsultarEstadoPedidoTienda(String dsnODBC, int pos)
	{
		JSONArray listJSON = new JSONArray();
		if(pos == 2)
		{
			ArrayList<EstadoPedidoTienda> estPedidos = PedidoPixelDAO.ConsultarEstadoPedidoTienda(dsnODBC);
			for (EstadoPedidoTienda estPed : estPedidos) {
				JSONObject cadaEstadoPedidoJSON = new JSONObject();
				cadaEstadoPedidoJSON.put("domiciliario", estPed.getDomiciliario());
				cadaEstadoPedidoJSON.put("estatus", estPed.getEstatus());
				cadaEstadoPedidoJSON.put("transaccion", estPed.getTransaccion());
				cadaEstadoPedidoJSON.put("horapedido", estPed.getHoraPedido());
				cadaEstadoPedidoJSON.put("tiempototal", estPed.getTiempoTotal());
				cadaEstadoPedidoJSON.put("direccion", estPed.getDireccion());
				cadaEstadoPedidoJSON.put("telefono", estPed.getTelefono());
				cadaEstadoPedidoJSON.put("nombrecompleto", estPed.getNombreCompleto());
				cadaEstadoPedidoJSON.put("tiempoenruta", estPed.getTiempoEnRuta());
				cadaEstadoPedidoJSON.put("tomadordepedido", estPed.getTomadorDePedido());
				cadaEstadoPedidoJSON.put("formapago", estPed.getFormaPago());
				listJSON.add(cadaEstadoPedidoJSON);
			}
		}else if(pos == 1)
		{
			ArrayList<EstadoPedidoTienda> estPedidos = PedidoPOSPMDAO.ConsultarEstadoPedidoTienda();
			for (EstadoPedidoTienda estPed : estPedidos) {
				JSONObject cadaEstadoPedidoJSON = new JSONObject();
				cadaEstadoPedidoJSON.put("domiciliario", estPed.getDomiciliario());
				cadaEstadoPedidoJSON.put("estatus", estPed.getEstatus());
				cadaEstadoPedidoJSON.put("transaccion", estPed.getTransaccion());
				cadaEstadoPedidoJSON.put("horapedido", estPed.getHoraPedido());
				cadaEstadoPedidoJSON.put("tiempototal", estPed.getTiempoTotal());
				cadaEstadoPedidoJSON.put("direccion", estPed.getDireccion());
				cadaEstadoPedidoJSON.put("telefono", estPed.getTelefono());
				cadaEstadoPedidoJSON.put("nombrecompleto", estPed.getNombreCompleto());
				cadaEstadoPedidoJSON.put("tiempoenruta", estPed.getTiempoEnRuta());
				cadaEstadoPedidoJSON.put("tomadordepedido", estPed.getTomadorDePedido());
				cadaEstadoPedidoJSON.put("formapago", estPed.getFormaPago());
				listJSON.add(cadaEstadoPedidoJSON);
			}
		}
		
		
		return listJSON.toJSONString();
		
	}
	
	
	/**
	 * Método en la capa controladora que se encarga de la inserción del pedido tomado desde el contac center
	 * en el sistema POS de Pizza Americana
	 * @param idpedido se refiere al idpedido que se le dio al pedido en el sistema contact center
	 * @param valorformapago el valor que se va a pagar con la forma de pago determinada
	 * @param valortotal el valor total del pedido
	 * @param cliente Objeto tipo cliente que encapsula toda la información del cliente
	 * @param indicadorAct --
	 * @param dsnTienda --
	 * @param envioPixel Arreglo con toda la información del detalle del pedido
	 * @param idformapagotienda el identificador de la forma de pago parametrizada por el cliente
	 * @param idTienda identificador de la tienda en el sistema POS
	 * @param usuario usuario que está tomando el pedido
	 * @param tiempoPedido El tiempo dado al pedido
	 * @return
	 */
	public static RespuestaPedidoPixel confirmarPedidoPOSPMTienda(int idpedido,double valorformapago, double valortotal, Cliente cliente, boolean indicadorAct, String dsnTienda, ArrayList<DetallePedidoPixel> envioPixel, int idformapagotienda, int idTienda , String usuario, int tiempoPedido)
	{
		// Se define la variable a retornar
		int idPedidoTienda = 0;
		//Iremos a separar la lógica de lo nuevo y de lo pasado
		capaControlador.PedidoCtrl pedCtrl = new capaControlador.PedidoCtrl(false);
		capaControlador.ParametrosProductoCtrl parProductoCtrl = new capaControlador.ParametrosProductoCtrl(false);
		capaControlador.ParametrosCtrl parCtrl = new capaControlador.ParametrosCtrl(false);
		//Obtenemos la fecha del sistema
		FechaSistema fecha = pedCtrl.obtenerFechasSistema();
		//Obtenemos la fecha actual como un String
		String fechaPedido = fecha.getFechaApertura();
		//Revisamos si el cliente existe o no en cuyo caso deberemos de crearlo
		int idClienteTienda = 0;
		//Definimos un valor booleano para indicar si el cliente fue creado o no
		boolean creaCliente = false;
		//Si el cliente no existe
		String telefono = cliente.getTelefono();
		String nombre = cliente.getNombres();
		String apellido = cliente.getApellidos();
		String compania = cliente.getNombreCompania();
		String numNomen1 = cliente.getNumNomenclatura();
		String numNomen2 = cliente.getNumNomenclatura2();
		String num3 = cliente.getNum3();
		String zona = cliente.getZonaDireccion();
		String observacion = cliente.getObservacion();
		float latitud = cliente.getLatitud();
		float longitud = cliente.getLontitud();
		if(observacion.length() > 200)
		{
			observacion = observacion.substring(0, 200);
		}
		String tienda = cliente.getTienda();
		int idMunicipio = cliente.getIdMunicipio();
		int idNomemclatura = cliente.getIdnomenclatura();
		//conformamos el campo dirección con base en la nomenclatura definida
		String direccion = cliente.getDireccion();
		//Creamos el objeto cliente de la capa controladara
		ClienteCtrl clienteCtrl = new ClienteCtrl(PrincipalLogueo.habilitaAuditoria);
		//Creamos el objeto cliente con todos los parámetros
		if(cliente.getMemcode() == 0)
		{
			//Realizamos la creación del cliente capturando todos los valores enviados como parámetros
			capaModelo.Cliente crearCliente = new capaModelo.Cliente(0, telefono, nombre, apellido, compania, direccion, "", idMunicipio,latitud, longitud, zona , observacion, tienda, idTienda, 0, idNomemclatura, numNomen1, numNomen2, num3, "");
			//Realizamos la inserción del cliente
			int idCliIns = clienteCtrl.insertarCliente(crearCliente);
			//llevamos a la variable idClienteTienda el id del cliente insertado
			idClienteTienda = idCliIns;
			//Prendemos el indicador de que el cliente fue creado.
			creaCliente = true;
			
		}else
		{
			//Creamos objeto para la actualización del cliente
			capaModelo.Cliente actualizaCliente = new capaModelo.Cliente(cliente.getMemcode(), telefono, nombre, apellido, compania, direccion, "", idMunicipio,latitud, longitud, zona , observacion, tienda, idTienda, 0, idNomemclatura, numNomen1, numNomen2, num3, "");
			//Obtenemos el idClienteTienda del valor que ya viene en los parámetros dado que ya este existe.
			idClienteTienda = cliente.getMemcode();
			//Realizamos la actualización del cliente si viene al caso
			clienteCtrl.actualizarCliente(actualizaCliente);
		}
		
		//Realizamos la inserción del encabezado pedido
		idPedidoTienda = pedCtrl.InsertarEncabezadoPedido(idTienda, idClienteTienda, fechaPedido, usuario);
		
		//Iniciamos la inserción de los detalles pedidos
		double cantidad;
		int idProducto;
		double precio;
		boolean esMaster = false;
		// Esta variable hace alusión a como viene el idDetalleMaster del sistema de contact center que obviamente será
		//diferente al número que queda en el sistema tienda
		int idDetalleMaster = 0;
		// Esta variabla hace alusión al idDetallePedido que ya le queda al idDetalleMaster luego de la inserción
		// en el sistema de tienda.
		int idDetalleMasterDef = 0;
		// La siguiente variable hace alusión a la idDetalleMaestro en el detalle hijo
		int idDetalleHijoMaster = 0;
		capaControlador.ParametrosProductoCtrl parPro = new ParametrosProductoCtrl(true);
		int contadorDetallePedido = 0;
		//Variable donde guardaremos el idProductoModificador, para ver si este tiene modificador asociado
		DetallePedido detPedido = new DetallePedido(0,0,0,0,0,0,"",0,"","",0);
		//Recuperamos las combinaciones de producto, producto eleccion y precio configuradas en el sistema tienda
		ArrayList<capaModelo.ProductoEleccionPrecio> proElePrecio = parProductoCtrl.obtenerProductoEleccionPrecio();
		//Definimos variables que nos serviran para buscar el precio
		int idProductoPrincipal = 0;
		int idProductoEleccion = 0;
		String strPrecio = "";
		//Recorremos el detallePedidoPixel con los items del pedido.
		for(int i = 0; i  < envioPixel.size(); i++)
		{
			strPrecio = "";
			capaModeloWeb.DetallePedidoPixel cadaDetallePedido = envioPixel.get(i);
			//Capturamos los valores para la insercion de los detalles
			idProducto = cadaDetallePedido.getIdproductoext();
			cantidad = cadaDetallePedido.getCantidad();
			// Aqui no podemos obtener el precio de una manera loca, pues no sabemos cual es la pregunta forzada
			precio = parPro.obtenerPrecioPilaProducto(idProducto);
			//Validamos si el DetallePedido que estamos procesando es master esto para realizar la insercion con este objetivo
			esMaster = cadaDetallePedido.isEsMaster();
			if(esMaster)
			{
				idProductoPrincipal = idProducto;
				idDetalleMaster = cadaDetallePedido.getIdDetallePedido();
				//Creamos el objeto Detalle Pedido
				detPedido = new DetallePedido(0,idPedidoTienda,idProducto,cantidad, precio, cantidad*precio, "",0, "N","", contadorDetallePedido);
				//Tomamos el idDetalleMaster para insertarlo en los futuros hijos
				idDetalleMasterDef = pedCtrl.insertarDetallePedido(detPedido);
				//Guardamos el y el idDetalle generado en la inserción
				cadaDetallePedido.setIdDetallePedIns(idDetalleMasterDef);
			}else if(idProducto != 2002)
			{
				
				idDetalleHijoMaster = cadaDetallePedido.getIdMaster();
				if(idDetalleHijoMaster == idDetalleMaster)
				{
					//Hacemos la validacion de que no sea un item de modificador
					if(cadaDetallePedido.getIdModificador()== 0)
					{
						idProductoEleccion = idProducto;
						//Buscamos el precio a aplicar para ver si cambia
						for(int z = 0; z < proElePrecio.size(); z++ )
						{
							capaModelo.ProductoEleccionPrecio proEleTemp = proElePrecio.get(z);
							//Validamos si el producto principal y el producto de eleccion corresponden
							if((proEleTemp.getIdproducto() == idProductoPrincipal) &&(proEleTemp.getIdproductoEleccion() == idProductoEleccion))
							{
								
								strPrecio = proEleTemp.getPrecio();
								break;
							}
						}
						//Si el encontro un precio en el anterior ciclo buscaremos este precio del producto
						if(!strPrecio.equals(new String("")))
						{
							precio = parPro.obtenerPrecioProducto(idProductoEleccion, strPrecio);
						}
						//Creamos el objeto Detalle Pedido
						detPedido = new DetallePedido(0,idPedidoTienda,idProducto,cantidad, precio, cantidad*precio, "",idDetalleMasterDef, "N","", contadorDetallePedido);
						int idDetalle = pedCtrl.insertarDetallePedido(detPedido);
						cadaDetallePedido.setIdDetallePedIns(idDetalle);
					}else //Sino se cumple esta condición es porque es un modificador por lo tanto hay que devolverse a ver el modificador
					{
						//Tenemos qeu devolvernos en el arreglo para saber cual es el padre del modificador es decir
						//el modificador a quien está relacionado
						int idDetalleModificadorPadre = 0;
						for(int j = i-1; j >= 0; j--)
						{
							
							capaModeloWeb.DetallePedidoPixel cadaDetallePedidotmp = envioPixel.get(j);
							if(cadaDetallePedidotmp.getIdproductoext() == cadaDetallePedido.getIdModificador() )
							{
								idDetalleModificadorPadre = cadaDetallePedidotmp.getIdDetallePedIns();
								break;
							}
						}
						detPedido = new DetallePedido(0,idPedidoTienda,idProducto,cantidad, precio, cantidad*precio, "",idDetalleMasterDef, "N","", contadorDetallePedido);
						detPedido.setIdDetalleModificador(idDetalleModificadorPadre);
						int idDetalle = pedCtrl.insertarDetallePedido(detPedido);
						cadaDetallePedido.setIdDetallePedIns(idDetalle);
					}
					
				}
				
			}
			contadorDetallePedido++;
		}
		
		int idTipoPedido = capaDAO.TipoPedidoDAO.obtenerTipoPedidoDomicilio(true);
		
		//Insertamos la forma pago
		//NOS TOCA AFINAR LOS PARÁMETROS PARA INSERTAR CORRECTAMENTE LA FORMA DE PAGO
		//Recuperamos la forma de pago del pedido
		capaModelo.FormaPago forPago = parCtrl.retornarFormaPago(idformapagotienda);
		if(forPago.getTipoforma().equals("EFECTIVO"))
		{
			boolean resFormaPago = pedCtrl.insertarPedidoFormaPago(	valorformapago, 0,0, valortotal, (valorformapago - valortotal), idPedidoTienda);
		}else if(forPago.getTipoforma().equals("TARJETA"))
		{
			boolean resFormaPago = pedCtrl.insertarPedidoFormaPago(	0, valorformapago,0, valortotal, (valorformapago - valortotal), idPedidoTienda);
		}else if(forPago.getTipoforma().equals("PAGO-ONLINE"))
		{
			boolean resFormaPago = pedCtrl.insertarPedidoFormaPago(	0, 0,valorformapago, valortotal, (valorformapago - valortotal), idPedidoTienda);
		}
		
		//Finalizamos el pedido
		//System.out.println("PARÁMETROS PARA FINALIZAR " + idPedidoTienda + " " + tiempoPedido + " " + idTipoPedido);
		boolean resFinPedido = pedCtrl.finalizarPedido(idPedidoTienda, tiempoPedido, idTipoPedido);
		

		
				
		RespuestaPedidoPixel resPedPixel = new RespuestaPedidoPixel();
				
		if(idPedidoTienda != 0)
		{
			resPedPixel = new RespuestaPedidoPixel(creaCliente, idPedidoTienda, idClienteTienda ,idpedido,cliente.getIdcliente());
		}else
		{
			resPedPixel = new RespuestaPedidoPixel(false, 0, 0, 0, 0);
		}
		
		
		return(resPedPixel);
	}
	
	
	public boolean sePuedeFacturar(FechaSistema fechaSistema)
	{
		
		if(fechaSistema.getFechaApertura() == "" || fechaSistema.getFechaApertura() == null)
		{
			return(false);
		}
		return(true);
	}
	
	public String obtenerPedidosEmpacadosDomicilio()
	{
		long estEmpDom;
		//Necesitamos obtener el id del estado pedido en ruta domicilio
		long valNum;
		ParametrosCtrl parCtrl = new ParametrosCtrl(false);
		capaModelo.Parametro parametro = parCtrl.obtenerParametro("EMPACADODOMICILIO");
		try
		{
			valNum = (long) parametro.getValorNumerico();
		}catch(Exception e)
		{
			System.out.println("SE TUVO ERROR TOMANDO LA CONSTANTE DE PEDIDOS EN RUTA");
			valNum = 0;
		}
		estEmpDom = valNum;
		FechaSistema fechaSistema = TiendaDAO.obtenerFechasSistema();
		ArrayList pedidos = new ArrayList();
		if(sePuedeFacturar(fechaSistema))
		{
			pedidos = PedidoPOSPMDAO.obtenerPedidosEmpacadosDomicilio(fechaSistema.getFechaApertura(), estEmpDom);
			int idPedido = 0;
			//Obtenemos los tiempos de Pedido
			ArrayList<TiempoPedido> tiemposPedido = calcularTiemposPedidos(fechaSistema.getFechaApertura());
			TiempoPedido tiempoPedidoTemp = new TiempoPedido(0,"");
			String tiempoPedido = "";
			//Adicionamos los tiempos pedidos
			for(int i = 0 ; i < pedidos.size(); i++)
			{
				String[]fila = (String[]) pedidos.get(i);
				idPedido =Integer.parseInt(fila[0]);
				for(int j = 0; j < tiemposPedido.size(); j++)
				{
					tiempoPedidoTemp = tiemposPedido.get(j);
					// SI el pedido es igual al que estamos procesando extraemos el tiempo del pedido
					if(tiempoPedidoTemp.getIdPedidoTienda() == idPedido)
					{
						tiempoPedido = tiempoPedidoTemp.getTiempoPedido();
						break;
					}
				}
				fila[fila.length-1] = tiempoPedido;
				pedidos.set(i, fila);
			}
		}
		//Luego de esto recorreremos el ArrayList de pedidos para formatearlo en JSON
		JSONArray listJSON = new JSONArray();
		JSONObject cadaPedidoJSON = new JSONObject();
		for(int i = 0; i < pedidos.size(); i++)
		{
			
			cadaPedidoJSON = new JSONObject();
			String[] fila =(String[]) pedidos.get(i);
			cadaPedidoJSON.put("idpedidotienda", fila[0]);
			cadaPedidoJSON.put("fechapedido", fila[1]);
			cadaPedidoJSON.put("nombres", fila[2]);
			cadaPedidoJSON.put("tipopedido", fila[3]);
			cadaPedidoJSON.put("estadopedido", fila[4]);
			cadaPedidoJSON.put("direccion", fila[5]);
			cadaPedidoJSON.put("idtipopedido", fila[6]);
			cadaPedidoJSON.put("idestado", fila[7]);
			cadaPedidoJSON.put("latitud", fila[8]);
			cadaPedidoJSON.put("longitud", fila[9]);
			cadaPedidoJSON.put("tiempopedido", fila[10]);
			listJSON.add(cadaPedidoJSON);
		}
		return(listJSON.toJSONString());
	}
	
	public ArrayList<TiempoPedido> calcularTiemposPedidos(String fecha)
	{
		//Instanciamos el arreglo donde tendremos la respuesta
		ArrayList<TiempoPedido> tiemposPedidos = new ArrayList();
		//Obtenemos el arreglo con todos los estados
		ArrayList hisEstadosPedidos = EstadoDAO.obtenerHistoriaEstadoPedidosFecha(fecha);
		//el ciclo tendrá vida mientras existe estados de historia de pedido a procesar
		ArrayList hisEstPedido;
		int idPedidoActual = 0;
		boolean indicador = true;
		//Es la variable donde se calcular cada tiempo Pedido
		String respuesta = "";
		while(hisEstadosPedidos.size() > 0)
		{
			//Debemos de extraer la historia pedido de cada uno particular
			//Instanciamos el temporal para extraer la historia de cada pedido particular
			hisEstPedido = new ArrayList();
			//Extraemos el idPedido que vamos a tratar para esta iteración
			String[] filaHistoria = (String[])hisEstadosPedidos.get(0);
			idPedidoActual = Integer.parseInt(filaHistoria[filaHistoria.length - 2]);
			hisEstPedido.add(filaHistoria);
			//Removemos el primer elemento que ya procesamos
			hisEstadosPedidos.remove(0);
			while(indicador)
			{
				filaHistoria = (String[])hisEstadosPedidos.get(0);
				int idPedidoTemp = Integer.parseInt(filaHistoria[filaHistoria.length - 2]);
				if(idPedidoTemp == idPedidoActual)
				{
					hisEstPedido.add(filaHistoria);
					hisEstadosPedidos.remove(0);
				}
				else
				{
					indicador = false;
				}
			}
			//En este punto ya podemos realizar validación si el estado es final o no y de acuerdo a esto tomar ciertas
			//determinaciones.
			int cantHist  = hisEstPedido.size();
			boolean esEstadoFinal = false;
			if(cantHist > 0 )
			{
				String[] fila = (String[]) hisEstPedido.get(0);
				//Retornamos si es estado final
				esEstadoFinal = Boolean.parseBoolean(fila[fila.length -1]);
				
			}
			Date fechaActual = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd H:m:s");
			int difTiempo = 0;
			int minutos = 0;
			int tamEstados = hisEstPedido.size();
			if(tamEstados == 1)
			{
				//Tomar la fecha
				String[] fila = (String[]) hisEstPedido.get(0);
				String strFecPedido = fila[0];
				Date datFecPedido = new Date();
				try
				{
					datFecPedido = dateFormat.parse(strFecPedido);
					
				}catch(Exception e)
				{
					 datFecPedido = new Date();
				}
				difTiempo =(int) (fechaActual.getTime() - datFecPedido.getTime());
				Math.abs(minutos = (int)TimeUnit.MILLISECONDS.toMinutes(difTiempo ));
				respuesta = "Actual: " + minutos + " Total: " + minutos;
			}
			else if(tamEstados > 1)
			{
				String[] fila = (String[]) hisEstPedido.get(0);
				String strFecPedido = fila[0];
				Date datFecPedido = new Date();
				try
				{
					datFecPedido = dateFormat.parse(strFecPedido);
					
				}catch(Exception e)
				{
					 datFecPedido = new Date();
				}
				
				difTiempo =Math.abs((int) (datFecPedido.getTime() - fechaActual.getTime() ));
				Math.abs(minutos = (int)TimeUnit.MILLISECONDS.toMinutes(difTiempo ));
				String[] filaAnterior;
				String[] filaFinal;
				if(esEstadoFinal)
				{
					filaAnterior =(String[]) hisEstPedido.get(0);
					filaFinal =(String[]) hisEstPedido.get(tamEstados-1);
					String strFecEstAnterior = filaAnterior[0];
					String strFecEstFinal = filaFinal[0];
					Date datEstAnt, datEstFin;
					try
					{
						datEstAnt = dateFormat.parse(strFecEstAnterior);
						datEstFin = dateFormat.parse(strFecEstFinal);
						
					}catch(Exception e)
					{
						datEstAnt = new Date();
						datEstFin = new Date();
					}
					difTiempo =(int) (datEstFin.getTime() - datEstAnt.getTime());
					Math.abs(minutos = (int)TimeUnit.MILLISECONDS.toMinutes(difTiempo ));
					respuesta = respuesta + " TOTAL: " + minutos;
				}
				else
				{
					respuesta = "Total: " + minutos;
					//Para el estado actual
					filaAnterior =(String[]) hisEstPedido.get(tamEstados-2);
					filaFinal =(String[]) hisEstPedido.get(tamEstados-1);
					//String strFecEstAnterior = filaAnterior[0];
					String strFecEstFinal = filaFinal[0];
					Date datEstAnt, datEstFin;
					try
					{
						//datEstAnt = dateFormat.parse(strFecEstAnterior);
						datEstFin = dateFormat.parse(strFecEstFinal);
						
					}catch(Exception e)
					{
						//datEstAnt = new Date();
						datEstFin = new Date();
					}
					difTiempo =Math.abs((int) (fechaActual.getTime() - datEstFin.getTime()));
					minutos = (int)TimeUnit.MILLISECONDS.toMinutes(difTiempo );
					respuesta = respuesta + " Actual: " + minutos;
				}
				
			}
			TiempoPedido tiemPedido =
				new TiempoPedido(idPedidoActual, respuesta);
			tiemposPedidos.add(tiemPedido);
		}
		return(tiemposPedidos);
	}
	
	//Realizamos desarrollo en capa controlador de lo que atenderá la necesidad de consulta de pedidos 
	public String consultarPedidosDomiciliario(String idUsuario, int tipoConsulta)
	{
		ArrayList pedidos = new ArrayList();
		capaControlador.PedidoCtrl pedidoCtrlTienda = new capaControlador.PedidoCtrl(false);
		if(tipoConsulta == 1)
		{
			pedidos = pedidoCtrlTienda.obtenerPedidosVentanaComandaDomEnRutaTablet(Integer.parseInt(idUsuario));
		}else if(tipoConsulta == 2)
		{
			pedidos = pedidoCtrlTienda.obtenerPedidosVentanaComandaDomTablet(Integer.parseInt(idUsuario));
		}
		//Realizamos recorrido del ArrayList para generar el JSON correspondiente
		JSONArray listJSON = new JSONArray();
		JSONObject cadaPedidoJSON = new JSONObject();
		for(int i = 0; i < pedidos.size(); i++)
		{
			
			cadaPedidoJSON = new JSONObject();
			String[] fila =(String[]) pedidos.get(i);
			cadaPedidoJSON.put("idpedido", fila[1]);
			cadaPedidoJSON.put("nombres", fila[3]);
			cadaPedidoJSON.put("estadopedido", fila[5]);
			cadaPedidoJSON.put("direccion", fila[6]);
			cadaPedidoJSON.put("domiciliario", fila[9]);
			cadaPedidoJSON.put("tp", fila[10]);
			cadaPedidoJSON.put("tiempo", fila[11]);
			listJSON.add(cadaPedidoJSON);
		}
		return(listJSON.toJSONString());
	}
	
	
	//Método que se encargará de dar llegada a los domiciliarios desde el sistema TABLET
	public String darLlegadaDomicilios(String idUsuario, String usuario)
	{
		//Se traen el listado de pedidos 
		ArrayList pedidos = new ArrayList();
		capaControlador.PedidoCtrl pedidoCtrlTienda = new capaControlador.PedidoCtrl(false);
		pedidos = pedidoCtrlTienda.obtenerPedidosVentanaComandaDomEnRuta(Integer.parseInt(idUsuario));
		//Realizamos un recorrido de cada uno de los pedidos para darle la llegada al domiciliario
		long estEnRutaDom;
		//variable que indica el estado cuando un domicilio es entregado
		long estEntregaDom;
		
		
		//Inicialiazamos los valores de los estados pedidos
		ParametrosCtrl parCtrl = new ParametrosCtrl(false);
		long valNum = 0;
		Parametro parametro = parCtrl.obtenerParametro("ENRUTADOMICILIO");
		try
		{
			valNum = (long) parametro.getValorNumerico();
		}catch(Exception e)
		{
			System.out.println("SE TUVO ERROR TOMANDO LA CONSTANTE DE PEDIDOS EN RUTA");
			valNum = 0;
		}
		estEnRutaDom = valNum;
		parametro = parCtrl.obtenerParametro("ENTREGADODOMICILIO");
		try
		{
			valNum = (long) parametro.getValorNumerico();
		}catch(Exception e)
		{
			System.out.println("SE TUVO ERROR TOMANDO LA CONSTANTE DE DOMICILIOS ENTREGADOS");
			valNum = 0;
		}
		estEntregaDom = valNum;
		
		
		//Realizamos recorrido del ArrayList con el fin dar llegada a los domicilios
		for(int i = 0; i < pedidos.size(); i++)
		{
			String[] fila =(String[]) pedidos.get(i);
			int idPedido = Integer.parseInt(fila[1]);
			pedidoCtrlTienda.ActualizarEstadoPedido((int)idPedido, (int) estEnRutaDom , (int) estEntregaDom,usuario,true, Integer.parseInt(idUsuario), false);
		}
		//Luego de avanzar todos los pedidos damos la entrada al domiciliario
		capaControlador.EmpleadoCtrl empCtrl = new capaControlador.EmpleadoCtrl(false);
		empCtrl.entradaDomiciliario(Integer.parseInt(idUsuario));
		JSONObject resultado = new JSONObject();
		resultado.put("resultado", "exitoso");
		return(resultado.toString());
	}
	
	
	//Método que se encargará de dar llegada a los domiciliarios desde el sistema TABLET
		public String darSalidaDomicilios(String idUsuario, String usuario, String JSONidPedido)
		{
			//Creamos el JSONArray con el JSON que se recibe como parámetro
			JSONArray JSONPedidos = new JSONArray();
			try
			{
				JSONParser parser = new JSONParser();
				Object objParser = parser.parse(JSONidPedido);
				JSONPedidos = (JSONArray) objParser;
			}catch(Exception e)
			{
				System.out.println("Error parseando el JSON recibido por aplicación Tablet " + e.toString());
			}
			//Se traen el listado de pedidos 
			ArrayList pedidos = new ArrayList();
			capaControlador.PedidoCtrl pedidoCtrlTienda = new capaControlador.PedidoCtrl(false);
			//Realizamos un recorrido de cada uno de los pedidos para darle la llegada al domiciliario
			long estEnRutaDom;
			//variable que indica el estado cuando un domicilio es entregado
			long estEmpDom;
			
			ParametrosCtrl parCtrl = new ParametrosCtrl(false);
			Parametro parametro = parCtrl.obtenerParametro("EMPACADODOMICILIO");
			long valNum = 0;
			try
			{
				valNum = (long) parametro.getValorNumerico();
			}catch(Exception e)
			{
				System.out.println("SE TUVO ERROR TOMANDO LA CONSTANTE DE PEDIDOS EMPACADOS");
				valNum = 0;
			}
			estEmpDom = valNum;
			parametro = parCtrl.obtenerParametro("ENRUTADOMICILIO");
			try
			{
				valNum = (long) parametro.getValorNumerico();
			}catch(Exception e)
			{
				System.out.println("SE TUVO ERROR TOMANDO LA CONSTANTE DE PEDIDOS EN RUTA");
				valNum = 0;
			}
			estEnRutaDom = valNum;
					
			//recorremos el arreglo para dar salida a todos los pedidos
			for(int i = 0; i < JSONPedidos.size(); i++)
			{
				JSONObject pedido =(JSONObject) JSONPedidos.get(i);
				int idPedido = Integer.parseInt((String)pedido.get("idpedido"));
				//Realizamos la salida del domicilio en específico
				pedidoCtrlTienda.ActualizarEstadoPedido((int)idPedido, (int) estEmpDom , (int) estEnRutaDom,usuario,true, Integer.parseInt(idUsuario),true);
			}
			//Luego de avanzar todos los pedidos damos la entrada al domiciliario
			capaControlador.EmpleadoCtrl empCtrl = new capaControlador.EmpleadoCtrl(false);
			empCtrl.salidaDomiciliario(Integer.parseInt(idUsuario));
			
			JSONObject resultado = new JSONObject();
			resultado.put("resultado", "exitoso");
			return(resultado.toString());
		}
		
		public String cambiarEstadoDomiciliario(String idUsuario)
		{
			ArrayList pedidos = new ArrayList();
			capaControlador.EmpleadoCtrl empCtrl = new capaControlador.EmpleadoCtrl(false);
			empCtrl.entradaDomiciliario(Integer.parseInt(idUsuario));
			JSONObject resultado = new JSONObject();
			resultado.put("resultado", "exitoso");
			return(resultado.toString());
		}

}

package capaControladorServicios;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import capaDAOPixelpos.Main;
import capaModeloWeb.Cliente;
import interfazGraficaPOS.PrincipalLogueo;

public class ClienteCtrl {
	
	public String actualizarCliente(int idCliente,String telefono, String nombres, String apellidos, String nombreCompania, String direccion, String municipio, float latitud, float longitud, String zona,  String observacion, String tienda, int memcode, int idnomenclatura, String numNomenclatura, String numNomenclatura2, String num3, int pos, String dsnODBC, int idTienda, int idMunicipio, double distanciaTienda, String telefonoCelular, String email, String politicaDatos)
	{
		int idClienteTrabajado = 0;
		//Se define el comportamiento para POS Pizza Americana
		if(pos == 1)
		{
			//Creamos  el clienteCtrl del sistema cliente tienda CON JAR DE TIENDA
			capaControladorPOS.ClienteCtrl clienteCtrlTienda = new capaControladorPOS.ClienteCtrl(true);
			if(memcode == 0)
			{
				//Realizamos la creación del cliente capturando todos los valores enviados como parámetros
				capaModeloPOS.Cliente crearCliente = new capaModeloPOS.Cliente(0, telefono, nombres, apellidos, nombreCompania, direccion, "", idMunicipio,latitud, longitud, zona , observacion, tienda, idTienda, 0, idnomenclatura, numNomenclatura, numNomenclatura2, num3, "", distanciaTienda, telefonoCelular, email, politicaDatos);
				//Realizamos la inserción del cliente
				int idCliIns = clienteCtrlTienda.insertarCliente(crearCliente);
				//llevamos a la variable idClienteTienda el id del cliente insertado
				idClienteTrabajado = idCliIns;
			}else
			{
				//Creamos objeto para la actualización del cliente
				capaModeloPOS.Cliente actualizaCliente = new capaModeloPOS.Cliente(memcode, telefono, nombres, apellidos, nombreCompania, direccion, "", idMunicipio,latitud, longitud, zona , observacion, tienda, idTienda, 0, idnomenclatura, numNomenclatura, numNomenclatura2, num3, "", distanciaTienda, telefonoCelular, email, politicaDatos);
				clienteCtrlTienda.actualizarCliente(actualizaCliente);
				idClienteTrabajado = memcode;
			}
		}else if(pos == 2) //Se define el comportamiento par POS Pixel
		{
			Main operPixel = new Main();
			Cliente crearCliente = new Cliente(0, telefono, nombres, apellidos, nombreCompania, direccion, "", idMunicipio,latitud, longitud, zona , observacion, tienda, idTienda, 0, idnomenclatura, numNomenclatura, numNomenclatura2, num3, "", 0, "", "", "");
			idClienteTrabajado = operPixel.actualizarCrearCliente(memcode, crearCliente, dsnODBC);
		}
		//Formateamos la devolucion que requerimos
		JSONArray listJSON = new JSONArray();
		JSONObject respuesta = new JSONObject();
		respuesta.put("idclitienda", idClienteTrabajado);
		listJSON.add(respuesta);
		return(listJSON.toJSONString());
	}
}

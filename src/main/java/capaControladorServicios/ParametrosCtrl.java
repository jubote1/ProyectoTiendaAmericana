package capaControladorServicios;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import capaDAOPixelpos.ParametrosDAO;
import capaDAOPixelpos.TiendaDAO;
import capaDAOPixelpos.GeneralDAO;
import capaDAOPixelpos.TiempoPedidoDAO;
import capaModeloWeb.Correo;
import capaModeloWeb.Tienda;
import capaControladorPOS.OperacionesTiendaCtrl;
import capaModeloPOS.Parametro;
import interfazGraficaPOS.PrincipalLogueo;
import utilidades.ControladorEnvioCorreo;

public class ParametrosCtrl {
	
	public String retornarTiemposPedido()
	{
		JSONArray listJSON = new JSONArray();
		JSONObject Respuesta = new JSONObject();
		int tiempo = TiempoPedidoDAO.retornarTiempoPedido();
		Respuesta.put("tiempopedido", tiempo);
		listJSON.add(Respuesta);
		return(Respuesta.toString());
	}
	
	public String actualizarTiempoPedido(int nuevotiempo, String user, int pos)
	{
		JSONArray listJSON = new JSONArray();
		JSONObject Respuesta = new JSONObject();
		Tienda tienda = TiendaDAO.obtenerTienda();
		boolean respues = false;
		if(pos == 2)
		{
			respues = TiempoPedidoDAO.actualizarTiempoPedido(nuevotiempo, tienda.getIdTienda(), user);
		}else if(pos == 1)
		{
			respues = TiempoPedidoDAO.actualizarTiempoPedido(nuevotiempo, tienda.getIdTienda(), user);
		}
		String respuestaHTML = "";
		if(respues)
		{
			if (nuevotiempo > 70)
			{
				
				Correo correo = new Correo();
				correo.setAsunto("ALERTA TIEMPOS PEDIDO");
				ArrayList correos = GeneralDAO.obtenerCorreosParametro("TIEMPOPEDIDO");
				correo.setContrasena("Pizzaamericana2017");
				correo.setUsuarioCorreo("alertaspizzaamericana@gmail.com");
				correo.setMensaje("La tienda " + tienda.getNombretienda() + " está aumentando el tiempo de entrega a " + nuevotiempo + " minutos");
				ControladorEnvioCorreo contro = new ControladorEnvioCorreo(correo, correos);
				contro.enviarCorreo();
			}
			respuestaHTML = "<html><b> <h1 align='center'><i>EL TIEMPO SE HA ACTUALIZADO CORRECTAMENTE EN EL CONTACT CENTER</i></h1></b></html>";
		}
		Respuesta.put("resultado", respuestaHTML);
		listJSON.add(Respuesta);
		return(Respuesta.toString());
	}
	
	public String obtenerTienda(){
		JSONArray listJSON = new JSONArray();
		Tienda tienda = TiendaDAO.obtenerTienda();
		JSONObject objTienda = new JSONObject();
		objTienda.put("idtienda", tienda.getIdTienda());
		objTienda.put("nombre", tienda.getNombretienda());
		objTienda.put("urlcontact", tienda.getUrlContact());
		listJSON.add(objTienda);
		return listJSON.toJSONString();
	}
	
	public Parametro obtenerParametro(String valorParametro)
	{
		Parametro parametro = ParametrosDAO.obtenerParametro(valorParametro);
		return parametro;
	}
	
	//Para bloqueo y desbloqueo de tiendas en servicios
	
	public String activarEstadoTienda()
	{
		Tienda tienda = TiendaDAO.obtenerTienda();
		OperacionesTiendaCtrl operTiendaCtrl = new OperacionesTiendaCtrl(false);
		boolean respuesta = operTiendaCtrl.activarEstadoTienda(tienda.getNombretienda());
		JSONObject respJSON = new JSONObject();
		if(respuesta)
		{
			respJSON.put("resultado", "true");
		}
		else
		{
			respJSON.put("resultado", "false");
		}
		return(respJSON.toString());
	}
	
	public String desactivarEstadoTienda()
	{
		OperacionesTiendaCtrl operTiendaCtrl = new OperacionesTiendaCtrl(false);
		Tienda tienda = TiendaDAO.obtenerTienda();
		boolean respuesta = operTiendaCtrl.desactivarEstadoTienda(tienda.getNombretienda());
		JSONObject respJSON = new JSONObject();
		if(respuesta)
		{
			respJSON.put("resultado", "true");
		}
		else
		{
			respJSON.put("resultado", "false");
		}
		return(respJSON.toString());
	}
	

}

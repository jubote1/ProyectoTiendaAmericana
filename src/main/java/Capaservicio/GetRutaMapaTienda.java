package Capaservicio;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import capaControladorServicios.ParametrosCtrl;
import capaControladorPOS.OperacionesTiendaCtrl;

/**
 * Servlet implementation class GetTiendas
 * Servicio que se encarga de retornar las tiendas o puntos de venta para los cuales se podr� tomar un pedido, 
 * la informaci�n ser� retornada en formato JSON.
 */
@WebServlet("/GetRutaMapaTienda")
public class GetRutaMapaTienda extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetRutaMapaTienda() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 * Este servicio no recibe par�metros dado que no filtra la informaci�n, simplemente retorna en formato JSON 
	 * las tiendas o puntos de venta parametrizados en el sistema, invocando el m�todo obtenerTiendas de la capa Tienda Controlador.
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		try{
			response.addHeader("Access-Control-Allow-Origin", "*");
			response.setContentType("application/json");
			OperacionesTiendaCtrl operTienda = new OperacionesTiendaCtrl(false);
			String respuesta = operTienda.obtenerRutaMapa();
			PrintWriter out = response.getWriter();
			out.write(respuesta);
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
	
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}

package Capaservicio;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import capaControladorPOS.PedidoCtrl;

/**
 * Servlet implementation class ConsultarEstadosPedidoTienda
 */
@WebServlet("/ConsultaResumidaEstadoTienda")
public class ConsultaResumidaEstadoTienda extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ConsultaResumidaEstadoTienda() {
        super();
        // TODO Auto-generated constructor stub
    }

	/** Servicio que se encarga de retornar los pedidos de tipo domicilio actuales de la tienda y su estado
	 * Se retorna el resultado en un string en formato JSON.
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.addHeader("Access-Control-Allow-Origin", "*");
		HttpSession sesion = request.getSession();
		String fecha = request.getParameter("fecha");
		PedidoCtrl pedidoCtrl = new PedidoCtrl(false);
		String respuesta = pedidoCtrl.consultaResEstadoTienda(fecha);
		PrintWriter out = response.getWriter();
		out.write(respuesta);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}

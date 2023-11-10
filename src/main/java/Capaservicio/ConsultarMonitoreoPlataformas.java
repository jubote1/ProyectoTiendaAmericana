package Capaservicio;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import capaControladorServicios.PedidoCtrl;

/**
 * Servlet implementation class ConsultarMonitoreoPlataformas
 */
@WebServlet("/ConsultarMonitoreoPlataformas")
public class ConsultarMonitoreoPlataformas extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ConsultarMonitoreoPlataformas() {
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
		String dsnODBC = request.getParameter("dsnodbc");
		int pos = Integer.parseInt(request.getParameter("pos"));
		PedidoCtrl pedidoCtrl = new PedidoCtrl();
		String respuesta = pedidoCtrl.ConsultarMonitoreoPlataformas(dsnODBC,pos);
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

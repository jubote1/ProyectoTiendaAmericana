package Capaservicio;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import capaControladorServicios.PedidoCtrl;

/**
 * Servicio para consultar el conteo real de pedidos entregados y pendientes
 * de un domiciliario en el sistema POS de la tienda.
 */
@WebServlet("/ConsultarConteoPedidosDomiciliario")
public class ConsultarConteoPedidosDomiciliario extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public ConsultarConteoPedidosDomiciliario() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.setContentType("application/json;charset=UTF-8");
		request.setCharacterEncoding("UTF-8");
		Logger logger = Logger.getLogger("log_file");
		String claveUsuario = "";
		int idTienda = 0;
		try {
			claveUsuario = request.getParameter("claveusuario");
		} catch (Exception e) {
			claveUsuario = "";
		}
		try {
			idTienda = Integer.parseInt(request.getParameter("idtienda"));
		} catch (Exception e) {
			idTienda = 1;
		}
		String respuesta = "";
		try {
			PedidoCtrl pedCtrl = new PedidoCtrl();
			respuesta = pedCtrl.consultarConteoPedidosDomiciliario(idTienda, claveUsuario);
		} catch (Exception e) {
			logger.error("Error en ConsultarConteoPedidosDomiciliario: " + e.toString());
			respuesta = "{\"resultado\":\"error\",\"mensaje\":\"" + e.getMessage() + "\"}";
		}
        PrintWriter out = response.getWriter();
        out.write(respuesta);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}

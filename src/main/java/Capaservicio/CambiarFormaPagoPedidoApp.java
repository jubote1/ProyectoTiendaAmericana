package Capaservicio;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;

import capaControladorServicios.AutenticacionCtrl;
import capaControladorServicios.PedidoCtrl;
import capaModeloWeb.UsuarioAnt;

/**
 * Servlet implementation class ValidarUsuarioAplicacion
 * Servicio que es invocado siempre que es cargada una p�gina con el fin de validar si quien accede esta logueado en el sistema
 * en caso negativo se redirecciona a la URL de logueo a la aplicaci�.
 */
@WebServlet("/CambiarFormaPagoPedidoApp")
public class CambiarFormaPagoPedidoApp extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CambiarFormaPagoPedidoApp() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 * Este servicio retorna el atributo de tipo usuario y con base en este valida si el usuario si est� logueado.Se 
	 * retornan tres posibles valores NOK si la validaci�n del usuario no es correcta, OKA si es un usuario administrador
	 * y OK si es un usuario normal
	 */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.addHeader("Access-Control-Allow-Origin", "*");
        response.setContentType("application/json;charset=UTF-8");
        Logger logger = Logger.getLogger("log_file");
        request.setCharacterEncoding("UTF-8");
        String respuesta = "";

        try {
            // Obtener parámetros con método seguro
            String idPedidoTiendaStr = getParam(request, "idpedidotienda", "0");
            String claveUsuario      = getParam(request, "claveusuario", "");
            String idTiendaStr       = getParam(request, "idtienda", "0");
            String observacion       = getParam(request, "observacion", "");
            String idFormaPagoStr    = getParam(request, "idformapago", "0");

            // Decodificar observación
            observacion = URLDecoder.decode(observacion, StandardCharsets.UTF_8.toString());

            // Convertir parámetros numéricos
            int idPedidoTienda = parseIntSafe(idPedidoTiendaStr);
            int idTienda       = parseIntSafe(idTiendaStr);
            int idFormaPago    = parseIntSafe(idFormaPagoStr);

            // Ejecutar lógica de negocio
            PedidoCtrl pedCtrl = new PedidoCtrl();
            respuesta = pedCtrl.cambiarFormaPagoPedidoApp(idPedidoTienda, idTienda, claveUsuario, observacion, idFormaPago);

            // Si quieres concatenar observación (como lo hacías)
            respuesta += observacion;

        } catch (Exception e) {
            logger.error("Error en doGet cambiar forma de pago: " + e);
            respuesta = "error";
        }

        // Respuesta al cliente
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
	
	private String getParam(HttpServletRequest req, String name, String defecto) {
	    String val = req.getParameter(name);
	    return val != null ? val : defecto;
	}

	private int parseIntSafe(String val) {
	    try {
	        return Integer.parseInt(val);
	    } catch (Exception e) {
	        return 0;
	    }
	}


}

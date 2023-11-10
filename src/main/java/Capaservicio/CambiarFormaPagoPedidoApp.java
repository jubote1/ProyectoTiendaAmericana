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
		// TODO Auto-generated method stub
				response.addHeader("Access-Control-Allow-Origin", "*");
				Logger logger = Logger.getLogger("log_file");
				String idPedidoTiendaStr = "";
				String claveUsuario = "";
				int idPedidoTienda = 0;
				String idTiendaStr = "";
				String observacion = "";
				int idTienda = 0;
				try
				{
					idPedidoTiendaStr = request.getParameter("idpedidotienda");
				}catch(Exception e)
				{
					System.out.println("Error casteando el usuario");
					idPedidoTiendaStr = "1";
				}
				try
				{
					claveUsuario = request.getParameter("claveusuario");
				}catch(Exception e)
				{
					System.out.println("Error casteando clave usuario");
					claveUsuario = "";
				}
				try
				{
					idTiendaStr = request.getParameter("idtienda");
				}catch(Exception e)
				{
					System.out.println("Error casteando el usuario");
					idTiendaStr = "1";
				}
				idPedidoTienda = Integer.parseInt(idPedidoTiendaStr);
				idTienda = Integer.parseInt(idTiendaStr);
				String respuesta = "";
				try
				{
					observacion = request.getParameter("observacion");
				}catch(Exception e)
				{
					System.out.println("Error casteando clave usuario");
					observacion = "";
				}
				if(observacion == null)
				{
					observacion = "";
				}
				observacion = URLDecoder.decode(observacion, StandardCharsets.UTF_8.toString());
				//Tipo consulta busca diferenciar el tipo de consulta 1 es para domiciliario disponible y 2 para domiciliario en ruta
				//Al no existir el usuario logueado es posible que produza una excepcion
				try
				{
					//Debemos de validar la existencia del usuario
					PedidoCtrl pedCtrl = new PedidoCtrl();
					respuesta = pedCtrl.cambiarFormaPagoPedidoApp(idPedidoTienda, idTienda, claveUsuario,observacion);
					respuesta = respuesta + observacion;
				}catch(Exception e)
				{
					logger.error(e.toString());
					
				}
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

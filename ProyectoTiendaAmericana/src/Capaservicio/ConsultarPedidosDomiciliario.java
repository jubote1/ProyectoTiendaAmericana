package Capaservicio;

import java.io.IOException;
import java.io.PrintWriter;

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
 * Servicio que es invocado siempre que es cargada una página con el fin de validar si quien accede esta logueado en el sistema
 * en caso negativo se redirecciona a la URL de logueo a la aplicació.
 */
@WebServlet("/ConsultarPedidosDomiciliario")
public class ConsultarPedidosDomiciliario extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ConsultarPedidosDomiciliario() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 * Este servicio retorna el atributo de tipo usuario y con base en este valida si el usuario si está logueado.Se 
	 * retornan tres posibles valores NOK si la validación del usuario no es correcta, OKA si es un usuario administrador
	 * y OK si es un usuario normal
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
				response.addHeader("Access-Control-Allow-Origin", "*");
				Logger logger = Logger.getLogger("log_file");
				String idUsuario = "";
				try
				{
					idUsuario = request.getParameter("idusuario");
				}catch(Exception e)
				{
					System.out.println("Error casteando el usuario");
					idUsuario = "1";
				}
				System.out.println("USUARIO " + idUsuario);
				int tipoConsulta = 0;
				try
				{
					tipoConsulta = Integer.parseInt(request.getParameter("tipoconsulta"));
				}catch(Exception e)
				{
					System.out.println("Error casteando tipo consulta");
					tipoConsulta = 1;
				}
				System.out.println("TIPO CONSULTA " + tipoConsulta);
				String respuesta = "";
				//Tipo consulta busca diferenciar el tipo de consulta 1 es para domiciliario disponible y 2 para domiciliario en ruta
				//Al no existir el usuario logueado es posible que produza una excepcion
				try
				{
					logger.info("En proceso de extraer pedidos para el domiciliario  " + idUsuario);
					//Debemos de validar la existencia del usuario
					PedidoCtrl pedCtrl = new PedidoCtrl();
					respuesta = pedCtrl.consultarPedidosDomiciliario(idUsuario, tipoConsulta);
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

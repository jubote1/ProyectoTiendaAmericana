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
import org.json.simple.JSONObject;

import capaControladorServicios.AutenticacionCtrl;
import capaControladorServicios.PedidoCtrl;
import capaModeloWeb.UsuarioAnt;

/**
 * Servlet implementation class ValidarUsuarioAplicacion
 * Servicio que es invocado siempre que es cargada una p�gina con el fin de validar si quien accede esta logueado en el sistema
 * en caso negativo se redirecciona a la URL de logueo a la aplicaci�.
 */
@WebServlet("/AvanzarEstadoCocina")
public class AvanzarEstadoCocina extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AvanzarEstadoCocina() {
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
				int idPedido  = 0;
				String respuesta = "";
				try
				{
					idPedido = Integer.parseInt(request.getParameter("idPedido"));
				}catch(Exception e)
				{
					
					System.out.println(e.toString() + e.getMessage() + " el valor de pedido es " + idPedido);
					idPedido = 0;
					JSONObject jsonResp = new JSONObject();
					jsonResp.put("resultado", "NOK");
					jsonResp.put("causal", "No se pudo transformar el par�metro idPedido");
					respuesta = jsonResp.toJSONString();
				}
				if(respuesta.equals(new String("")))
				{
					capaControladorPOS.PedidoCtrl pedCtrl = new capaControladorPOS.PedidoCtrl(false);
					respuesta = pedCtrl.avanzarEstadoCocina(idPedido);
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

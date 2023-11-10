package Capaservicio;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import capaControladorServicios.ParametrosCtrl;
import capaModeloWeb.UsuarioAnt;

/**
 * Servlet implementation class CRUDFormaPago
 * Servicio que implementa el CRUD de la entidad Forma Pago.
 */
@WebServlet("/CRUDTiempoPedido")
public class CRUDTiempoPedido extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CRUDTiempoPedido() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 * Se recibir� como par�metro principal el idoperacion con base en los siguientes valores 
	 *  idoperacion 1 insertar 2 editar 3 Eliminar  4 Consultar, de acuerdo a la id operacion se pedir�n los otros par�metros y se invocar� el m�todo en la capa controlador.
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//Operaci�n idoperacion 1 actualizar 2 consultar
		response.addHeader("Access-Control-Allow-Origin", "*");		
		HttpSession sesion = request.getSession();
		response.addHeader("Access-Control-Allow-Origin", "*");
		UsuarioAnt usuario = (UsuarioAnt) sesion.getAttribute("usuario");
		String user = "" ;
		//Al no existir el usuario logueado es posible que produza una excepcion
		try
		{
			user = usuario.getNombreUsuario();
		}catch(Exception e)
		{
		
		}
		String idoperacion = request.getParameter("idoperacion");
		ParametrosCtrl ParametrosCtrl = new ParametrosCtrl();
		int operacion;
		String respuesta="";
		int pos;
		String strPos = request.getParameter("pos");
		try
		{
			pos = Integer.parseInt(strPos);
		}catch(Exception e){
			pos = 0;
		}
		
		
		try
		{
			operacion = Integer.parseInt(idoperacion);
		}catch(Exception e){
			operacion = 0;
		}
		if (operacion ==1)
		{
			int nuevotiempo = Integer.parseInt(request.getParameter("nuevotiempo"));
			respuesta = ParametrosCtrl.actualizarTiempoPedido(nuevotiempo,user,pos);
		}else if (operacion ==2)
		{
			respuesta = ParametrosCtrl.retornarTiemposPedido();
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

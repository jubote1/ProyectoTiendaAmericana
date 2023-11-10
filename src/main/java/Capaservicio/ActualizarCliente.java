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

import capaControladorServicios.ClienteCtrl;
import capaModeloWeb.UsuarioAnt;

/**
 * Servlet implementation class ActualizarCliente
 */
@WebServlet("/ActualizarCliente")
public class ActualizarCliente extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ActualizarCliente() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.addHeader("Access-Control-Allow-Origin", "*");
		Logger logger = Logger.getLogger("log_file");
		HttpSession sesion = request.getSession();
		//Final de captura del usuario
		String telefono = request.getParameter("telefono");
        String nombres = request.getParameter("nombres");
        String apellidos = request.getParameter("apellidos");
        String nombreCompania = request.getParameter("nombreCompania");
        String direccion = request.getParameter("direccion");
        String zona = request.getParameter("zona");
        String observacion = request.getParameter("observacion");
        String tienda = request.getParameter("tienda");
        String municipio = request.getParameter("municipio");
        String telefonoCelular = request.getParameter("telefonocelular");
        String email = request.getParameter("email");
        String politicaDatos = request.getParameter("politicadatos");
        float latitud;
        float longitud;
        int idCliente;
        int memcode;
        int idnomenclatura = 0;
        int pos = 0;
        String numNomenclatura = request.getParameter("numnomenclatura1");
        String numNomenclatura2 = request.getParameter("numnomenclatura2");
        String num3 = request.getParameter("num3");
        int idTienda = 0;
        int idMunicipio = 0;
        try{
        	latitud = Float.parseFloat(request.getParameter("latitud"));
        }catch(Exception e)
        {
        	latitud = 0;
        }
        try{
        	longitud = Float.parseFloat(request.getParameter("longitud"));
        }catch(Exception e)
        {
        	longitud = 0;
        }
        try{
        	memcode = Integer.parseInt(request.getParameter("memcode"));
        }catch(Exception e)
        {
        	memcode = 0;
        }
        try{
        	idCliente = Integer.parseInt(request.getParameter("idcliente"));
        }catch(Exception e)
        {
        	idCliente = 0;
        }
        try{
        	idnomenclatura = Integer.parseInt(request.getParameter("idnomenclatura"));
        }catch(Exception e)
        {
        	idnomenclatura = 0;
        }
        try{
        	pos = Integer.parseInt(request.getParameter("pos"));
        }catch(Exception e)
        {
        	pos = 0;
        }
        try{
        	idTienda = Integer.parseInt(request.getParameter("idtienda"));
        }catch(Exception e)
        {
        	idTienda = 0;
        }
        try{
        	idMunicipio = Integer.parseInt(request.getParameter("idmunicipio"));
        }catch(Exception e)
        {
        	idMunicipio = 0;
        }
        String dsnODBC = request.getParameter("dsnodbc");
        ClienteCtrl ClienCtrl = new ClienteCtrl();
        String respuesta = ClienCtrl.actualizarCliente(idCliente,telefono, nombres, apellidos, nombreCompania,  direccion, municipio, latitud, longitud, zona, observacion, tienda, memcode, idnomenclatura, numNomenclatura, numNomenclatura2, num3, pos,dsnODBC, idTienda, idMunicipio, 0, telefonoCelular, email, politicaDatos);
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

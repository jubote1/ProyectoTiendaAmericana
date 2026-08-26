package Capaservicio;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import capaDAOPixelpos.RecogidaTercerizadaDAO;

@WebServlet("/DesmarcarPedidoTercerizadoTienda")
public class DesmarcarPedidoTercerizadoTienda extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.addHeader("Access-Control-Allow-Origin", "*");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        JSONObject json = new JSONObject();

        try {
            long idPedidoTienda = Long.parseLong(request.getParameter("idPedidoTienda"));
            int idTienda = Integer.parseInt(request.getParameter("idTienda"));

            RecogidaTercerizadaDAO dao = new RecogidaTercerizadaDAO();

            boolean desmarcado = dao.desmarcarPedidoComoTercerizado(idPedidoTienda, idTienda);

            json.put("exito", desmarcado);
            json.put("mensaje", desmarcado
                    ? "Pedido desmarcado como tercerizado."
                    : "No se pudo desmarcar el pedido. Puede estar anulado o no estaba tercerizado.");

            response.getWriter().write(json.toJSONString());

        } catch (Exception e) {
            e.printStackTrace();

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            json.put("exito", false);
            json.put("mensaje", "Error desmarcando pedido tercerizado: " + e.getMessage());

            response.getWriter().write(json.toJSONString());
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
}
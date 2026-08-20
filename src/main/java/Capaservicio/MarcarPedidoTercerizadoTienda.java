package Capaservicio;


import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import org.json.simple.JSONObject;

import capaDAOPixelpos.RecogidaTercerizadaDAO;

@WebServlet("/MarcarPedidoTercerizadoTienda")
public class MarcarPedidoTercerizadoTienda extends HttpServlet {

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

            boolean marcado = dao.marcarPedidoComoTercerizado(idPedidoTienda, idTienda);

            json.put("exito", marcado);
            json.put("mensaje", marcado
                    ? "Pedido marcado como tercerizado en tienda."
                    : "No se marcó el pedido. Puede estar finalizado, anulado o ya marcado.");

            response.getWriter().write(json.toJSONString());

        } catch (Exception e) {
            e.printStackTrace();

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            json.put("exito", false);
            json.put("mensaje", "Error marcando pedido como tercerizado: " + e.getMessage());

            response.getWriter().write(json.toJSONString());
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
}
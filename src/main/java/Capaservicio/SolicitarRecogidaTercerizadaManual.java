package Capaservicio;


import capaControladorServicios.RecogidaTercerizadaCtrl;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

@WebServlet("/SolicitarRecogidaTercerizadaManual")
public class SolicitarRecogidaTercerizadaManual extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        JSONObject json = new JSONObject();

        String idPedidoParam = request.getParameter("idPedidoTienda");

        if (idPedidoParam == null || idPedidoParam.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            json.put("exito", false);
            json.put("mensaje", "idPedidoTienda es obligatorio");
            response.getWriter().write(json.toJSONString());
            return;
        }

        try {
            long idPedidoTienda = Long.parseLong(idPedidoParam);

            String mensaje = new RecogidaTercerizadaCtrl()
                    .procesarPedidoManual(idPedidoTienda);

            json.put("exito", true);
            json.put("mensaje", mensaje);

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(json.toJSONString());

        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            json.put("exito", false);
            json.put("mensaje", "idPedidoTienda no es numerico");
            response.getWriter().write(json.toJSONString());

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            json.put("exito", false);
            json.put("mensaje", e.getMessage());
            response.getWriter().write(json.toJSONString());
        }
    }
}
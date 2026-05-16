package Capaservicio;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import capaControladorPOS.OperacionesTiendaCtrl;
import capaModeloPOS.Notificaciones;


@WebServlet("/RegistrarNotificacionTienda")
public class RegistrarNotificacionTienda extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) {

        boolean ok = false;

        try {

            String mensaje = request.getParameter("mensaje");
            String idPedidoStr = request.getParameter("idpedido");
            String origen  = request.getParameter("origen");

            int idPedido = Integer.parseInt(idPedidoStr);

            Notificaciones notificacion = new Notificaciones(mensaje);
            notificacion.setOrigen(origen);
            notificacion.setIdpedido(idPedido);

            OperacionesTiendaCtrl operTiendaCtrl = new OperacionesTiendaCtrl(true);

            int idInsertado = operTiendaCtrl.insertarNotificaciones(notificacion);

            if (idInsertado > 0) {
                ok = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            response.getWriter().write(ok ? "OK" : "ERROR");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
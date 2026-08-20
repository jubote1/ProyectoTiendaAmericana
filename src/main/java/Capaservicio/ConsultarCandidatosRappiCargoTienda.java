package Capaservicio;

import java.io.IOException;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import capaDAOPOS.PedidoDAO;
import capaDAOPixelpos.PedidoPixelDAO;
import capaDAOPixelpos.RecogidaTercerizadaDAO;
import capaModeloWeb.PedidoCandidatoRappiCargo;

@WebServlet("/ConsultarCandidatosRappiCargoTienda")
public class ConsultarCandidatosRappiCargoTienda extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
    	

        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.addHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        JSONArray array = new JSONArray();

        try {
        	RecogidaTercerizadaDAO dao = new RecogidaTercerizadaDAO();

            List<PedidoCandidatoRappiCargo> pedidos =
                    dao.buscarCandidatosRappiCargo();

            for (PedidoCandidatoRappiCargo p : pedidos) {
                JSONObject obj = new JSONObject();

                obj.put("idpedidotienda", p.getIdPedidoTienda());
                obj.put("idtienda", p.getIdTienda());
                obj.put("fechainsercion", p.getFechaInsercion());
                obj.put("estadoActual", p.getEstadoActual());
                obj.put("minutosDesdeIngreso", p.getMinutosDesdeIngreso());
                obj.put("fechaCocina", p.getFechaCocina());
                obj.put("minutosCocina", p.getMinutosCocina());
                obj.put("nombreCompleto", p.getNombre() +" "+p.getApellido());
                obj.put("direccion", p.getDireccion());
                obj.put("telefono", p.getTelefono());
                obj.put("telefonoCelular", p.getTelefonoCelular());
                obj.put("email", p.getEmail());
                obj.put("latitud", p.getLatitud());
                obj.put("longitud", p.getLongitud());
                obj.put("observacion", p.getObservacion());
                obj.put("municipio", p.getMunicipio());
                obj.put("totalNeto", p.getTotalNeto());
                obj.put("idFormaPago", p.getIdFormaPago());
                obj.put("domicilioTercerizado", p.getDomicilioTercerizado());

                array.add(obj);
            }

            response.getWriter().write(array.toJSONString());

        } catch (Exception e) {
            e.printStackTrace();

            JSONObject error = new JSONObject();
            error.put("error", true);
            error.put("mensaje", e.getMessage());
            array.add(error);

            response.getWriter().write(array.toJSONString());
        }
    }
}

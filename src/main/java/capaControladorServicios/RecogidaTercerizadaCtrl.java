package capaControladorServicios;


import capaDAOPixelpos.RecogidaTercerizadaDAO;
import capaModeloWeb.RecogidaPendiente;
import capaModeloWeb.RespuestaNotificacionServidor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


public class RecogidaTercerizadaCtrl {

    
    private static String RAPPICARGO_URL_NOTIFICAR_PEDIDO_LISTO =
            "https://tiendapizzaamericana.co/ProyectoTiendaAmericana/NotificarPedidoListoRappiCargo";

    
    
    private final RecogidaTercerizadaDAO dao =
            new RecogidaTercerizadaDAO();

    public void ejecutarProceso() {
        try {
        	
        	int minutosMinimosHorno = ParametrosCtrl.obtenerParametroNumerico(
        	        "RAPPICARGO_MINUTOS_MINIMOS_HORNO", 10);

        	int maxIntentos = ParametrosCtrl.obtenerParametroNumerico(
        	        "RAPPICARGO_MAX_INTENTOS_RECOGIDA", 3);

        	int limitePorCiclo = ParametrosCtrl.obtenerParametroNumerico(
        	        "RAPPICARGO_LIMITE_PEDIDOS_POR_CICLO", 20);

        	RAPPICARGO_URL_NOTIFICAR_PEDIDO_LISTO = ParametrosCtrl.obtenerParametroTexto(
        	        "RAPPICARGO_URL_NOTIFICAR_PEDIDO_LISTO",
        	        "https://tiendapizzaamericana.co/ProyectoTiendaAmericana/NotificarPedidoListoRappiCargo"
        	);
        	
            dao.crearPendientesDesdeSalidaCocina();

            List<RecogidaPendiente> pendientes =
                    dao.buscarListasParaSolicitar(
                    		minutosMinimosHorno,
                    		maxIntentos,
                    		limitePorCiclo
                    );

            for (RecogidaPendiente pendiente : pendientes) {
                procesarPedido(pendiente,maxIntentos);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean procesarPedido(RecogidaPendiente pendiente, int maxIntentos) {
        try {
            boolean tomado = dao.tomarParaProcesar(pendiente.getId(), maxIntentos);

            if (!tomado) {
                return false;
            }

            RespuestaNotificacionServidor respuesta =
                    notificarPedidoListo(
                            (int) pendiente.getIdPedidoTienda(),
                            pendiente.getIdTienda()
                    );

            if (respuesta.isExito()) {
                dao.marcarEnviada(pendiente.getId());
                return true;
            }

            dao.marcarError(
                    pendiente.getId(),
                    "Servidor respondió HTTP " + respuesta.getHttpStatus()
                            + " - " + respuesta.getMensaje()
                            + " - Body: " + respuesta.getBody()
            );

            return false;

        } catch (Exception e) {
            try {
                dao.marcarError(pendiente.getId(), e.getMessage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return false;
        }
    }

    
    public String procesarPedidoManual(long idPedidoTienda) {
        try {
            int minutosMinimosHorno = ParametrosCtrl.obtenerParametroNumerico(
                    "RAPPICARGO_MINUTOS_MINIMOS_HORNO", 10);

            int maxIntentos = ParametrosCtrl.obtenerParametroNumerico(
                    "RAPPICARGO_MAX_INTENTOS_RECOGIDA", 3);

            RAPPICARGO_URL_NOTIFICAR_PEDIDO_LISTO = ParametrosCtrl.obtenerParametroTexto(
                    "RAPPICARGO_URL_NOTIFICAR_PEDIDO_LISTO",
                    "https://tiendapizzaamericana.co/ProyectoTiendaAmericana/NotificarPedidoListoRappiCargo"
            );

            dao.crearPendienteDesdeSalidaCocinaPorPedido(idPedidoTienda);

            RecogidaPendiente pendiente = dao.buscarPedidoListoParaSolicitar(
                    idPedidoTienda,
                    minutosMinimosHorno,
                    maxIntentos
            );

            if (pendiente == null) {
                return "El pedido no cumple condiciones o todavía no ha cumplido los minutos mínimos desde salida_cocina.";
            }

            boolean enviado = procesarPedido(pendiente, maxIntentos);

            if (enviado) {
                return "Pedido notificado correctamente a RappiCargo.";
            }

            return "Se intentó notificar el pedido, pero RappiCargo o el servidor respondió error.";

        } catch (Exception e) {
            e.printStackTrace();
            return "Error procesando pedido manual: " + e.getMessage();
        }
    }

    public RespuestaNotificacionServidor notificarPedidoListo(
            int idPedidoTienda,
            int idTienda
    ) throws Exception {

        String parametros =
                "idPedidoTienda=" + URLEncoder.encode(String.valueOf(idPedidoTienda), "UTF-8") +
                "&idTienda=" + URLEncoder.encode(String.valueOf(idTienda), "UTF-8");

        HttpURLConnection conn = null;

        try {
            URL url = new URL(RAPPICARGO_URL_NOTIFICAR_PEDIDO_LISTO);
            conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(30000);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

            try (OutputStream os = conn.getOutputStream()) {
                os.write(parametros.getBytes("UTF-8"));
            }

            int httpStatus = conn.getResponseCode();
            String body = leerRespuesta(conn, httpStatus);
            
            if (body == null || body.trim().isEmpty()) {
                return new RespuestaNotificacionServidor(
                        false,
                        httpStatus,
                        "El servidor no devolvio respuesta",
                        ""
                );
            }

            JSONObject json = (JSONObject) new JSONParser().parse(body);

            boolean exito = Boolean.TRUE.equals(json.get("exito"));
            String mensaje = json.get("mensaje") != null
                    ? json.get("mensaje").toString()
                    : "";

            return new RespuestaNotificacionServidor(
                    exito,
                    httpStatus,
                    mensaje,
                    body
            );

        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private String leerRespuesta(HttpURLConnection conn, int httpStatus) throws Exception {
        BufferedReader br;

        if (httpStatus >= 200 && httpStatus < 300) {
            br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
        } else if (conn.getErrorStream() != null) {
            br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"));
        } else {
            return "";
        }

        StringBuilder respuesta = new StringBuilder();
        String linea;

        while ((linea = br.readLine()) != null) {
            respuesta.append(linea);
        }

        br.close();
        return respuesta.toString();
    }
}
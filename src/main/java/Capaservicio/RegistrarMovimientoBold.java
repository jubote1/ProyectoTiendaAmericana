package Capaservicio;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import capaDAOPixelpos.ParametrosDAO;
import capaDAOPixelpos.SonoQrMovimientoDAO;

/**
 * Recibe del central un movimiento de Bold (SONO QR o datafono) de esta tienda
 * y lo guarda en la base local, para que el POS lo muestre en la conciliacion de
 * SONO QR. Lo invoca BoldEntregaTiendaCtrl del central, que ya resolvio que el
 * evento es de esta tienda.
 *
 * Responde "OK" si el movimiento quedo guardado (tambien si ya estaba), y
 * "ERROR: ..." con el motivo si no; el central solo da por entregado lo que
 * responda "OK" y reintenta el resto.
 *
 * Si el parametro TOKENEVENTOSBOLD de esta tienda tiene valor, se exige el mismo
 * token en la peticion: sin eso cualquiera con acceso a la red de la tienda
 * podria inventar pagos QR. Si el parametro no existe se acepta, para que el
 * flujo funcione antes de configurarlo, pero queda un aviso en el log.
 */
@WebServlet("/RegistrarMovimientoBold")
public class RegistrarMovimientoBold extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	@Override
	protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException {
		final Logger logger = Logger.getLogger("log_file");
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/plain");
		response.setCharacterEncoding("UTF-8");

		if (!tokenValido(request.getParameter("token"), logger)) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().write("ERROR: token");
			return;
		}

		final String paymentId = request.getParameter("paymentid");
		final String tipoEvento = request.getParameter("tipoevento");
		if (paymentId == null || paymentId.trim().isEmpty() || tipoEvento == null || tipoEvento.trim().isEmpty()) {
			response.getWriter().write("ERROR: faltan paymentid o tipoevento");
			return;
		}
		final long idBold;
		final BigDecimal monto;
		final Timestamp fecha;
		try {
			idBold = Long.parseLong(request.getParameter("idlog").trim());
			monto = new BigDecimal(request.getParameter("monto").trim());
			fecha = Timestamp.valueOf(LocalDateTime.parse(request.getParameter("fecha").trim(), FORMATO_FECHA));
		} catch (final Exception e) {
			response.getWriter().write("ERROR: idlog, monto o fecha invalidos");
			return;
		}

		final boolean guardado = SonoQrMovimientoDAO.guardar(idBold, paymentId.trim(), tipoEvento.trim(),
				request.getParameter("metodo"), monto, request.getParameter("moneda"), fecha,
				request.getParameter("referencia"));
		response.getWriter().write(guardado ? "OK" : "ERROR: no se pudo guardar en la tienda");
	}

	private static boolean tokenValido(final String recibido, final Logger logger) {
		final String esperado = ParametrosDAO.retornarValorAlfanumerico("TOKENEVENTOSBOLD");
		if (esperado == null || esperado.trim().isEmpty()) {
			logger.warn("RegistrarMovimientoBold: TOKENEVENTOSBOLD sin configurar en esta tienda, se acepta sin token");
			return true;
		}
		if (recibido == null) {
			return false;
		}
		return MessageDigest.isEqual(esperado.trim().getBytes(StandardCharsets.UTF_8),
				recibido.trim().getBytes(StandardCharsets.UTF_8));
	}

}

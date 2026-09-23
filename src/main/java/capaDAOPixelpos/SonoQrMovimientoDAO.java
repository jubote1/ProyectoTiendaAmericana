package capaDAOPixelpos;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Timestamp;

import org.apache.log4j.Logger;

import capaConexion.ConexionBaseDatos;

/**
 * Guarda en la base de la tienda los movimientos que Bold (SONO QR y datafonos)
 * notifica al central y este le entrega (tabla sonoqr_movimiento, ver
 * sql/2026_09_22_01_sonoqr_movimiento.sql en ProyectoTiendaAmericanaCliente).
 * El POS los lee de ahi en la conciliacion de SONO QR.
 */
public class SonoQrMovimientoDAO {

	/**
	 * Guarda un movimiento. Si ya estaba (el central reenvia cuando no recibio
	 * la respuesta) tambien es exito: lo que importa es que quede una sola vez,
	 * y eso lo garantiza la llave unica (payment_id, tipo_evento).
	 *
	 * @return true si el movimiento esta guardado al terminar
	 */
	public static boolean guardar(final long idBold, final String paymentId, final String tipoEvento,
			final String metodo, final BigDecimal monto, final String moneda, final Timestamp fechaEvento,
			final String referencia) {
		final Logger logger = Logger.getLogger("log_file");
		final Connection con1 = new ConexionBaseDatos().obtenerConexionBDLocal();
		if (con1 == null) {
			logger.error("SonoQrMovimientoDAO: sin conexion a la base de la tienda");
			return false;
		}
		try {
			final PreparedStatement ps = con1.prepareStatement(
					"insert into sonoqr_movimiento (id_bold, payment_id, tipo_evento, metodo, monto, moneda,"
							+ " fecha_evento, referencia) values (?, ?, ?, ?, ?, ?, ?, ?)");
			ps.setLong(1, idBold);
			ps.setString(2, paymentId);
			ps.setString(3, tipoEvento);
			ps.setString(4, metodo);
			ps.setBigDecimal(5, monto);
			ps.setString(6, moneda);
			ps.setTimestamp(7, fechaEvento);
			ps.setString(8, referencia);
			ps.executeUpdate();
			ps.close();
			con1.close();
			return true;
		} catch (final SQLIntegrityConstraintViolationException yaEstaba) {
			cerrar(con1);
			return true;
		} catch (final Exception e) {
			logger.error("SonoQrMovimientoDAO.guardar: " + e.toString());
			cerrar(con1);
			return false;
		}
	}

	private static void cerrar(final Connection con) {
		try {
			con.close();
		} catch (final Exception ignorada) {
		}
	}

}

package capaDAOPixelpos;

import capaConexion.ConexionBaseDatos;
import capaModeloWeb.PedidoCandidatoRappiCargo;
import capaModeloWeb.RecogidaPendiente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RecogidaTercerizadaDAO {

	public void crearPendientesDesdeSalidaCocina() throws SQLException {
	    String sql =
	        "INSERT IGNORE INTO programacion_recogida_tercerizada " +
	        "(idpedidotienda, fecha_salida_cocina) " +
	        "SELECT p.idpedidotienda, MAX(sc.fecha_hora) " +
	        "FROM salida_cocina sc " +
	        "JOIN pedido p ON p.idpedidotienda = sc.idpedidotienda " +
	        "WHERE p.domicilio_tercerizado = 'S' " +
	        "AND DATE(sc.fecha_hora) = CURDATE() " +
	        "AND p.idestado NOT IN (7, 8) " +
	        "AND p.idmotivoanulacion IS NULL " +
	        "GROUP BY p.idpedidotienda";

	    ConexionBaseDatos con = new ConexionBaseDatos();

	    try (Connection conn = con.obtenerConexionBDLocal();
	         PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.executeUpdate();
	    }
	}
	
	
	public void crearPendienteDesdeSalidaCocinaPorPedido(long idPedidoTienda) throws SQLException {
	    String sql =
	        "INSERT IGNORE INTO programacion_recogida_tercerizada " +
	        "(idpedidotienda, fecha_salida_cocina) " +
	        "SELECT p.idpedidotienda, MAX(sc.fecha_hora) " +
	        "FROM salida_cocina sc " +
	        "JOIN pedido p ON p.idpedidotienda = sc.idpedidotienda " +
	        "WHERE p.idpedidotienda = ? " +
	        "AND p.domicilio_tercerizado = 'S' " +
	        "AND DATE(sc.fecha_hora) = CURDATE() " +
	        "AND p.idestado NOT IN (7, 8) " +
	        "AND p.idmotivoanulacion IS NULL " +
	        "GROUP BY p.idpedidotienda";

	    ConexionBaseDatos con = new ConexionBaseDatos();

	    try (Connection conn = con.obtenerConexionBDLocal();
	         PreparedStatement ps = conn.prepareStatement(sql)) {

	        ps.setLong(1, idPedidoTienda);
	        ps.executeUpdate();
	    }
	}
	
	
    public List<RecogidaPendiente> buscarListasParaSolicitar(
            int minutosMinimosHorno,
            int maxIntentos,
            int limite
    ) throws SQLException {
    	
		ConexionBaseDatos con = new ConexionBaseDatos();


		String sql =
			    "SELECT prt.id, prt.idpedidotienda , p.idtienda  " +
			    "FROM programacion_recogida_tercerizada prt " +
			    "JOIN pedido p ON p.idpedidotienda = prt.idpedidotienda " +
			    "WHERE prt.estado IN ('PENDIENTE', 'ERROR') " +
			    "AND prt.fecha_salida_cocina <= DATE_SUB(NOW(), INTERVAL ? MINUTE) " +
			    "AND prt.intentos < ? " +
			    "AND p.domicilio_tercerizado = 'S' " +
			    "AND p.idestado NOT IN (7, 8) " +
			    "AND p.idmotivoanulacion IS NULL " +
			    "ORDER BY prt.fecha_salida_cocina ASC " +
			    "LIMIT ?";

        List<RecogidaPendiente> lista = new ArrayList<>();

        try (Connection conn = con.obtenerConexionBDLocal();PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, minutosMinimosHorno);
            ps.setInt(2, maxIntentos);
            ps.setInt(3, limite);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new RecogidaPendiente(
                        rs.getLong("id"),
                        rs.getLong("idpedidotienda"),
                        rs.getInt("idtienda")
                    ));
                }
            }
        }

        return lista;
    }
    
    public RecogidaPendiente buscarPedidoListoParaSolicitar(
            long idPedidoTienda,
            int minutosMinimosHorno,
            int maxIntentos
    ) throws SQLException {

        String sql =
            "SELECT prt.id, prt.idpedidotienda, p.idtienda " +
            "FROM programacion_recogida_tercerizada prt " +
            "JOIN pedido p ON p.idpedidotienda = prt.idpedidotienda " +
            "WHERE prt.idpedidotienda = ? " +
            "AND prt.estado IN ('PENDIENTE', 'ERROR') " +
            "AND prt.fecha_salida_cocina <= DATE_SUB(NOW(), INTERVAL ? MINUTE) " +
            "AND prt.intentos < ? " +
            "AND p.domicilio_tercerizado = 'S' " +
            "AND p.idestado NOT IN (7, 8) " +
            "AND p.idmotivoanulacion IS NULL " +
            "LIMIT 1";

        ConexionBaseDatos con = new ConexionBaseDatos();

        try (Connection conn = con.obtenerConexionBDLocal();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, idPedidoTienda);
            ps.setInt(2, minutosMinimosHorno);
            ps.setInt(3, maxIntentos);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new RecogidaPendiente(
                        rs.getLong("id"),
                        rs.getLong("idpedidotienda"),
                        rs.getInt("idtienda")
                    );
                }
            }
        }

        return null;
    }

    public boolean tomarParaProcesar(long id, int maxIntentos) throws SQLException {
    	
		ConexionBaseDatos con = new ConexionBaseDatos();

        String sql =
            "UPDATE programacion_recogida_tercerizada " +
            "SET estado = 'PROCESANDO', intentos = intentos + 1, ultimo_error = NULL " +
            "WHERE id = ? " +
            "AND estado IN ('PENDIENTE', 'ERROR') " +
            "AND intentos < ?";

        try (Connection conn = con.obtenerConexionBDLocal();PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.setInt(2, maxIntentos);

            return ps.executeUpdate() == 1;
        }
    }

    public void marcarEnviada(long id) throws SQLException {

        String sql =
            "UPDATE programacion_recogida_tercerizada " +
            "SET estado = 'ENVIADA', fecha_ejecucion = NOW() " +
            "WHERE id = ?";

        ejecutarUpdateSimple(sql, id);
    }

    public void marcarError(long id, String error) throws SQLException {
    	ConexionBaseDatos con = new ConexionBaseDatos();
        String sql =
            "UPDATE programacion_recogida_tercerizada " +
            "SET estado = 'ERROR', ultimo_error = ? " +
            "WHERE id = ?";

        try (Connection conn = con.obtenerConexionBDLocal(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, error);
            ps.setLong(2, id);
            ps.executeUpdate();
        }
    }

    public void marcarOmitida(long id, String motivo) throws SQLException {
    	ConexionBaseDatos con = new ConexionBaseDatos();
        String sql =
            "UPDATE programacion_recogida_tercerizada " +
            "SET estado = 'OMITIDA', ultimo_error = ? " +
            "WHERE id = ?";

        try (Connection conn = con.obtenerConexionBDLocal();PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, motivo);
            ps.setLong(2, id);
            ps.executeUpdate();
        }
    }

    private void ejecutarUpdateSimple(String sql, long id) throws SQLException {
		ConexionBaseDatos con = new ConexionBaseDatos();

        try (Connection conn = con.obtenerConexionBDLocal();PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }
    
    public List<PedidoCandidatoRappiCargo> buscarCandidatosRappiCargo() throws SQLException {

        String sql =
            "SELECT " +
            "p.idpedidotienda, " +
            "p.idtienda, " +
            "p.total_neto, " +
            "p.fechainsercion, " +
            "p.idestado, " +
            "e.descripcion_corta AS estado_actual, " +
            "p.domicilio_tercerizado, " +

            "c.nombre, " +
            "c.apellido, " +
            "c.telefono, " +
            "c.telefono_celular, " +
            "c.email, " +
            "c.direccion, " +
            "c.latitud, " +
            "c.longitud, " +
            "c.observacion, " +
            "m.nombre AS municipio, " +
            "fp.idforma_pago, " +

            "TIMESTAMPDIFF(MINUTE, p.fechainsercion, NOW()) AS minutos_desde_ingreso, " +

            "sc.fecha_cocina, " +
            "IF(sc.fecha_cocina IS NULL, 0, TIMESTAMPDIFF(MINUTE, sc.fecha_cocina, NOW())) AS minutos_cocina " +

            "FROM pedido p " +
            "INNER JOIN cliente c ON p.idcliente = c.idcliente " +
            "LEFT JOIN municipio m ON c.idmunicipio = m.idmunicipio " +
            "INNER JOIN estado e ON p.idestado = e.idestado " +
            "INNER JOIN tipo_pedido tp ON p.idtipopedido = tp.idtipopedido " +
            "LEFT JOIN pedido_forma_pago fp ON fp.idpedidotienda = p.idpedidotienda " +

            "LEFT JOIN ( " +
            "   SELECT idpedidotienda, MAX(fecha_hora) AS fecha_cocina " +
            "   FROM salida_cocina " +
            "   WHERE fecha_hora >= CURDATE() " +
            "   AND fecha_hora < CURDATE() + INTERVAL 1 DAY " +
            "   GROUP BY idpedidotienda " +
            ") sc ON sc.idpedidotienda = p.idpedidotienda " +

            "WHERE p.fechainsercion >= CURDATE() " +
            "AND p.fechainsercion < CURDATE() + INTERVAL 1 DAY " +
            "AND tp.esdomicilio = 1 " +
            "AND COALESCE(p.domicilio_tercerizado, 'N') IN ('N', 'S') " +
            //"AND COALESCE(p.domicilio_tercerizado, 'N') = 'N' " +
            "AND p.idmotivoanulacion IS NULL " +
            "AND p.idestado IN (2, 5) " +
            "ORDER BY p.idpedidotienda DESC";

        List<PedidoCandidatoRappiCargo> lista = new ArrayList<>();
        ConexionBaseDatos con = new ConexionBaseDatos();

        try (Connection conn = con.obtenerConexionBDLocal();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                PedidoCandidatoRappiCargo pedido = new PedidoCandidatoRappiCargo();

                pedido.setIdPedidoTienda(rs.getLong("idpedidotienda"));
                pedido.setIdTienda(rs.getInt("idtienda"));
                pedido.setTotalNeto(rs.getDouble("total_neto"));
                pedido.setFechaInsercion(rs.getString("fechainsercion"));
                pedido.setIdEstado(rs.getInt("idestado"));
                pedido.setEstadoActual(rs.getString("estado_actual"));
                pedido.setDomicilioTercerizado(rs.getString("domicilio_tercerizado"));

                pedido.setNombre(rs.getString("nombre"));
                pedido.setApellido(rs.getString("apellido"));
                pedido.setTelefono(rs.getString("telefono"));
                pedido.setTelefonoCelular(rs.getString("telefono_celular"));
                pedido.setEmail(rs.getString("email"));
                pedido.setDireccion(rs.getString("direccion"));
                pedido.setLatitud(rs.getString("latitud"));
                pedido.setLongitud(rs.getString("longitud"));
                pedido.setObservacion(rs.getString("observacion"));
                pedido.setMunicipio(rs.getString("municipio"));
                pedido.setIdFormaPago(rs.getInt("idforma_pago"));
                pedido.setMinutosDesdeIngreso(rs.getInt("minutos_desde_ingreso"));
                pedido.setFechaCocina(rs.getString("fecha_cocina"));
                pedido.setMinutosCocina(rs.getInt("minutos_cocina"));

                lista.add(pedido);
            }
        }

        return lista;
    }
    
    
    public boolean marcarPedidoComoTercerizado(long idPedidoTienda, int idTienda) throws SQLException {

        String sql =
            "UPDATE pedido " +
            "SET domicilio_tercerizado = 'S' " +
            "WHERE idpedidotienda = ? " +
            "AND idtienda = ? " +
            "AND COALESCE(domicilio_tercerizado, 'N') <> 'S' " +
            "AND idmotivoanulacion IS NULL " +
            "AND idestado NOT IN (7, 8)";

        ConexionBaseDatos con = new ConexionBaseDatos();

        try (Connection conn = con.obtenerConexionBDLocal();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, idPedidoTienda);
            ps.setInt(2, idTienda);

            return ps.executeUpdate() == 1;
        }
    }
}
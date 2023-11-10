package capaModeloWeb;

public class EstadoPedidoTienda {
	
	private String domiciliario;
	private String estatus;
	private int transaccion;
	private String horaPedido;
	private String tiempoTotal;
	private String direccion;
	private String telefono;
	private String nombreCompleto;
	private String tiempoEnRuta;
	private String tomadorDePedido;
	private String formaPago;
	private String estacion;
	private String idPedidoAlt;
	
	
	
	public String getEstacion() {
		return estacion;
	}
	public void setEstacion(String estacion) {
		this.estacion = estacion;
	}
	public String getIdPedidoAlt() {
		return idPedidoAlt;
	}
	public void setIdPedidoAlt(String idPedidoAlt) {
		this.idPedidoAlt = idPedidoAlt;
	}
	public String getFormaPago() {
		return formaPago;
	}
	public void setFormaPago(String formaPago) {
		this.formaPago = formaPago;
	}
	public String getTiempoEnRuta() {
		return tiempoEnRuta;
	}
	public void setTiempoEnRuta(String tiempoEnRuta) {
		this.tiempoEnRuta = tiempoEnRuta;
	}
	public String getTomadorDePedido() {
		return tomadorDePedido;
	}
	public void setTomadorDePedido(String tomadorDePedido) {
		this.tomadorDePedido = tomadorDePedido;
	}
	public String getDomiciliario() {
		return domiciliario;
	}
	public void setDomiciliario(String domiciliario) {
		this.domiciliario = domiciliario;
	}
	public String getEstatus() {
		return estatus;
	}
	public void setEstatus(String estatus) {
		this.estatus = estatus;
	}
	public int getTransaccion() {
		return transaccion;
	}
	public void setTransaccion(int transaccion) {
		this.transaccion = transaccion;
	}
	public String getHoraPedido() {
		return horaPedido;
	}
	public void setHoraPedido(String horaPedido) {
		this.horaPedido = horaPedido;
	}
	public String getTiempoTotal() {
		return tiempoTotal;
	}
	public void setTiempoTotal(String tiempoTotal) {
		this.tiempoTotal = tiempoTotal;
	}
	public String getDireccion() {
		return direccion;
	}
	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}
	public String getTelefono() {
		return telefono;
	}
	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}
	public String getNombreCompleto() {
		return nombreCompleto;
	}
	public void setNombreCompleto(String nombreCompleto) {
		this.nombreCompleto = nombreCompleto;
	}
	public EstadoPedidoTienda(String domiciliario, String estatus, int transaccion, String horaPedido,
			String tiempoTotal, String direccion, String telefono, String nombreCompleto, String tiempoEnRuta,
			String tomadorDePedido, String formaPago) {
		super();
		this.domiciliario = domiciliario;
		this.estatus = estatus;
		this.transaccion = transaccion;
		this.horaPedido = horaPedido;
		this.tiempoTotal = tiempoTotal;
		this.direccion = direccion;
		this.telefono = telefono;
		this.nombreCompleto = nombreCompleto;
		this.tiempoEnRuta = tiempoEnRuta;
		this.tomadorDePedido = tomadorDePedido;
		this.formaPago = formaPago;
	}
	
	
	
	
	

}

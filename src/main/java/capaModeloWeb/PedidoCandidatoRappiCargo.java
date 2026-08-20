package capaModeloWeb;

public class PedidoCandidatoRappiCargo {

    private long idPedidoTienda;
    private int idTienda;
    private double totalNeto;
    private String fechaInsercion;
    private int idEstado;
    private String estadoActual;
    private String domicilioTercerizado;

    private String nombre;
    private String apellido;
    private String telefono;
    private String telefonoCelular;
    private String email;
    private String direccion;
    private String latitud;
    private String longitud;
    private String observacion;
    private String municipio;
    private int idFormaPago;

    private int minutosDesdeIngreso;
    private String fechaCocina;
    private int minutosCocina;

    public long getIdPedidoTienda() {
        return idPedidoTienda;
    }

    public void setIdPedidoTienda(long idPedidoTienda) {
        this.idPedidoTienda = idPedidoTienda;
    }

    public int getIdTienda() {
        return idTienda;
    }

    public void setIdTienda(int idTienda) {
        this.idTienda = idTienda;
    }

    public double getTotalNeto() {
        return totalNeto;
    }

    public void setTotalNeto(double totalNeto) {
        this.totalNeto = totalNeto;
    }

    public String getFechaInsercion() {
        return fechaInsercion;
    }

    public void setFechaInsercion(String fechaInsercion) {
        this.fechaInsercion = fechaInsercion;
    }

    public int getIdEstado() {
        return idEstado;
    }

    public void setIdEstado(int idEstado) {
        this.idEstado = idEstado;
    }

    public String getEstadoActual() {
        return estadoActual;
    }

    public void setEstadoActual(String estadoActual) {
        this.estadoActual = estadoActual;
    }

    public String getDomicilioTercerizado() {
        return domicilioTercerizado;
    }

    public void setDomicilioTercerizado(String domicilioTercerizado) {
        this.domicilioTercerizado = domicilioTercerizado;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getTelefonoCelular() {
        return telefonoCelular;
    }

    public void setTelefonoCelular(String telefonoCelular) {
        this.telefonoCelular = telefonoCelular;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public int getIdFormaPago() {
        return idFormaPago;
    }

    public void setIdFormaPago(int idFormaPago) {
        this.idFormaPago = idFormaPago;
    }

    public int getMinutosDesdeIngreso() {
        return minutosDesdeIngreso;
    }

    public void setMinutosDesdeIngreso(int minutosDesdeIngreso) {
        this.minutosDesdeIngreso = minutosDesdeIngreso;
    }

    public String getFechaCocina() {
        return fechaCocina;
    }

    public void setFechaCocina(String fechaCocina) {
        this.fechaCocina = fechaCocina;
    }

    public int getMinutosCocina() {
        return minutosCocina;
    }

    public void setMinutosCocina(int minutosCocina) {
        this.minutosCocina = minutosCocina;
    }

    public String getNombreCompleto() {
        return ((nombre != null ? nombre : "") + " " + (apellido != null ? apellido : "")).trim();
    }
}
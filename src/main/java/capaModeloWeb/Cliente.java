package capaModeloWeb;


public class Cliente {

	private int idcliente;
	private String telefono;
	private String nombres;
	private String apellidos;
	private String nombreCompania;
	private String Direccion;
	private String Municipio;
	private int idMunicipio;
	private float latitud;
	private float lontitud;
	private String zonaDireccion;
	private String observacion;
	private String tienda;
	private int idtienda;
	private int memcode;
	private int idnomenclatura;
	private String numNomenclatura;
	private String numNomenclatura2;
	private String num3;
	private String nomenclatura;
	private double distanciaTienda;
	private String telefonoCelular;
	private String email;
	private String politicaDatos;
	
	
	
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



	public String getPoliticaDatos() {
		return politicaDatos;
	}



	public void setPoliticaDatos(String politicaDatos) {
		this.politicaDatos = politicaDatos;
	}



	public double getDistanciaTienda() {
		return distanciaTienda;
	}



	public void setDistanciaTienda(double distanciaTienda) {
		this.distanciaTienda = distanciaTienda;
	}



	public int getMemcode() {
		return memcode;
	}
	
	

	public String getNomenclatura() {
		return nomenclatura;
	}

	public void setNomenclatura(String nomenclatura) {
		this.nomenclatura = nomenclatura;
	}

	public void setMemcode(int memcode) {
		this.memcode = memcode;
	}

	public int getIdMunicipio() {
		return idMunicipio;
	}

	public void setIdMunicipio(int idMunicipio) {
		this.idMunicipio = idMunicipio;
	}

	public String getMunicipio() {
		return Municipio;
	}




	public int getIdnomenclatura() {
		return idnomenclatura;
	}

	public void setIdnomenclatura(int idnomenclatura) {
		this.idnomenclatura = idnomenclatura;
	}

	public String getNumNomenclatura() {
		return numNomenclatura;
	}

	public void setNumNomenclatura(String numNomenclatura) {
		this.numNomenclatura = numNomenclatura;
	}

	public String getNumNomenclatura2() {
		return numNomenclatura2;
	}

	public void setNumNomenclatura2(String numNomenclatura2) {
		this.numNomenclatura2 = numNomenclatura2;
	}

	public String getNum3() {
		return num3;
	}

	public void setNum3(String num3) {
		this.num3 = num3;
	}

	public void setMunicipio(String idMunicipio) {
		this.Municipio = idMunicipio;
	}




	public float getLatitud() {
		return latitud;
	}




	public void setLatitud(float latitud) {
		this.latitud = latitud;
	}




	public float getLontitud() {
		return lontitud;
	}




	public void setLontitud(float lontitud) {
		this.lontitud = lontitud;
	}




	public int getIdtienda() {
		return idtienda;
	}



	public void setIdtienda(int idtienda) {
		this.idtienda = idtienda;
	}



	public int getIdcliente() {
		return idcliente;
	}



	public void setIdcliente(int idcliente) {
		this.idcliente = idcliente;
	}



	public String getTienda() {
		if (this.tienda == null)
		{
			return("");
		}
		return tienda;
	}



	public void setTienda(String tienda) {
		this.tienda = tienda;
	}


	public String getTelefono() {
		return telefono;
	}



	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}



	public String getNombres() {
		return nombres;
	}



	public void setNombres(String nombres) {
		this.nombres = nombres;
	}



	public String getDireccion() {
		return Direccion;
	}



	public void setDireccion(String direccion) {
		this.Direccion = direccion;
	}



	public String getZonaDireccion() {
		if (this.zonaDireccion == null)
		{
			return("");
		}
		return zonaDireccion;
	}



	public void setZonaDireccion(String zonaDireccion) {
		this.zonaDireccion = zonaDireccion;
	}



	public String getObservacion() {
		if (this.observacion == null)
		{
			return("");
		}
		return observacion;
	}



	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}
	
	
	public String getApellidos() {
		if (this.apellidos == null)
		{
			return("");
		}
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public String getNombreCompania() {
		if (this.nombreCompania == null)
		{
			return("");
		}
		return nombreCompania;
	}

	public void setNombreCompania(String nombreCompania) {
		this.nombreCompania = nombreCompania;
	}

	public Cliente() {
		// TODO Auto-generated constructor stub
	}

		
	public Cliente(int idcliente, String telefono, String nombres, String apellidos, String nombreCompania,
			String direccion, String municipio, int idMunicipio, float latitud, float lontitud, String zonaDireccion,
			String observacion, String tienda, int idtienda, int memcode, int idnomenclatura, String numNomenclatura, String numNomenclatura2, String num3, String nomenclatura, double distanciaTienda, String telefonoCelular, String email, String politicaDatos) {
		super();
		this.idcliente = idcliente;
		this.telefono = telefono;
		this.nombres = nombres;
		this.apellidos = apellidos;
		this.nombreCompania = nombreCompania;
		this.Direccion = direccion;
		this.Municipio = municipio;
		this.idMunicipio = idMunicipio;
		this.latitud = latitud;
		this.lontitud = lontitud;
		this.zonaDireccion = zonaDireccion;
		this.observacion = observacion;
		this.tienda = tienda;
		this.idtienda = idtienda;
		this.memcode = memcode;
		this.idnomenclatura = idnomenclatura;
		this.numNomenclatura = numNomenclatura;
		this.numNomenclatura2 = numNomenclatura2;
		this.num3 = num3;
		this.nomenclatura = nomenclatura; 
		this.distanciaTienda = distanciaTienda;
		this.telefonoCelular = telefonoCelular;
		this.email = email;
		this.politicaDatos = politicaDatos;
	}



	

}

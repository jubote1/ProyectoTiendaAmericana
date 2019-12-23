package capaModeloWeb;

public class Tienda {
	
	private int idTienda;
	private String nombretienda;
	private String urlContact;
	
	public int getIdTienda() {
		return idTienda;
	}
	public void setIdTienda(int idTienda) {
		this.idTienda = idTienda;
	}
	public String getNombretienda() {
		return nombretienda;
	}
	public void setNombretienda(String nombretienda) {
		this.nombretienda = nombretienda;
	}
	public String getUrlContact() {
		return urlContact;
	}
	public void setUrlContact(String urlContact) {
		this.urlContact = urlContact;
	}
	public Tienda(int idTienda, String nombretienda, String urlContact) {
		super();
		this.idTienda = idTienda;
		this.nombretienda = nombretienda;
		this.urlContact = urlContact;
	}
	
	
	public Tienda()
	{
		
	}
	

}

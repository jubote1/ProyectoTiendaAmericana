package capaModeloWeb;

public class RecogidaPendiente {

    private long id;
    private long idPedidoTienda;
    private int idTienda;

    public int getIdTienda() {
		return idTienda;
	}

	public void setIdTienda(int idTienda) {
		this.idTienda = idTienda;
	}

	public RecogidaPendiente(long id, long idPedidoTienda ,  int idTienda) {
        this.id = id;
        this.idPedidoTienda = idPedidoTienda;
	    this.idTienda = idTienda;
    }

    public long getId() {
        return id;
    }

    public long getIdPedidoTienda() {
        return idPedidoTienda;
    }
}
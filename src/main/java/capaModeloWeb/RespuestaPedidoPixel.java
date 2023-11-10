package capaModeloWeb;

public class RespuestaPedidoPixel {
	
	private boolean clienteCreado;
	private int numeroFactura;
	private int membercode;
	private int idpedido;
	private int idcliente;
	
	
	
	
	public int getIdcliente() {
		return idcliente;
	}
	public void setIdcliente(int idcliente) {
		this.idcliente = idcliente;
	}
	public int getIdpedido() {
		return idpedido;
	}
	public void setIdpedido(int idpedido) {
		this.idpedido = idpedido;
	}
	public boolean getClienteCreado() {
		return clienteCreado;
	}
	public void setClienteCreado(boolean clienteCreado) {
		this.clienteCreado = clienteCreado;
	}
	public int getNumeroFactura() {
		return numeroFactura;
	}
	public void setNumeroFactura(int numeroFactura) {
		this.numeroFactura = numeroFactura;
	}
	public int getMembercode() {
		return membercode;
	}
	public void setMembercode(int membercode) {
		this.membercode = membercode;
	}
	public RespuestaPedidoPixel(boolean clienteCreado, int numeroFactura, int membercode, int idpedido, int idcliente) {
		super();
		this.clienteCreado = clienteCreado;
		this.numeroFactura = numeroFactura;
		this.membercode = membercode;
		this.idpedido = idpedido;
		this.idcliente = idcliente;
	}
	public RespuestaPedidoPixel() {
		super();
	}
	
	
	
	

}

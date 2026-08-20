package capaModeloWeb;


public class RespuestaNotificacionServidor {

    private boolean exito;
    private int httpStatus;
    private String mensaje;
    private String body;

    public RespuestaNotificacionServidor(boolean exito, int httpStatus, String mensaje, String body) {
        this.exito = exito;
        this.httpStatus = httpStatus;
        this.mensaje = mensaje;
        this.body = body;
    }

    public boolean isExito() {
        return exito;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public String getMensaje() {
        return mensaje;
    }

    public String getBody() {
        return body;
    }
}
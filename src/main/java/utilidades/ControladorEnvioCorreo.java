package utilidades;

import java.util.ArrayList;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import capaModeloWeb.Correo;

public class ControladorEnvioCorreo {

private Correo  c;
private ArrayList correos;

public ControladorEnvioCorreo(Correo co, ArrayList correosenv)
{
	this.c= co;
	this.correos = correosenv;
}

public boolean enviarCorreo()
{
	try
	{
		Properties p = new Properties();
		p.put("mail.smtp.host", "smtp.gmail.com");
		p.setProperty("mail.smtp.starttls.enable", "true");
		p.setProperty("mail.smtp.port", "587");
		p.setProperty("mail.smtp.user", c.getUsuarioCorreo());
		p.setProperty("mail.smtp.auth", "true");
		
		Session s = Session.getDefaultInstance(p, null);
		BodyPart texto = new MimeBodyPart();
		texto.setText(c.getMensaje());
		MimeMultipart m = new MimeMultipart();
		m.addBodyPart(texto);
		MimeMessage mensaje = new MimeMessage(s);
		mensaje.setFrom(new InternetAddress(c.getUsuarioCorreo()));
		for(int i = 0; i< correos.size(); i++)
		{
			mensaje.addRecipient(Message.RecipientType.TO, new InternetAddress((String)correos.get(i)));
		}
		mensaje.setSubject(c.getAsunto());
		mensaje.setContent(m);
		Transport t = s.getTransport("smtp");
		t.connect(c.getUsuarioCorreo(),c.getContrasena());
		t.sendMessage(mensaje, mensaje.getAllRecipients());
		t.close();
		return(true);
		
	}
	catch(Exception e)
	{
		System.out.println(e.toString());
		return(false);
	}
	
}
	
}

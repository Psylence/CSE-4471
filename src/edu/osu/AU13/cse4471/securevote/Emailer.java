package edu.osu.AU13.cse4471.securevote;

import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.Authenticator;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Emailer extends Authenticator {
	
	private final String HOST = "smtp.gmail.com";
	
	private PasswordAuthentication pa;
	private Session session;
	
	public Emailer(String username, String password) {
		pa = new PasswordAuthentication(username, password);
		
		// This is all blindly taken from here: http://stackoverflow.com/questions/2020088/sending-email-in-android-using-javamail-api-without-using-the-default-built-in-a
        // since I am not very experienced with the specific intricacies of email protocol
		Properties props = new Properties();   
        props.setProperty("mail.transport.protocol", "smtp");   
        props.setProperty("mail.host", HOST);   
        props.put("mail.smtp.auth", "true");   
        props.put("mail.smtp.port", "465");   
        props.put("mail.smtp.socketFactory.port", "465");   
        props.put("mail.smtp.socketFactory.class",   
                "javax.net.ssl.SSLSocketFactory");   
        props.put("mail.smtp.socketFactory.fallback", "false");   
        props.setProperty("mail.smtp.quitwait", "false");   

        session = Session.getDefaultInstance(props, this);
	}
	
	@Override
	protected PasswordAuthentication getPasswordAuthentication() {
		return pa;
	}
	
	public void sendEmail(Email email, String recipients) {
		try {
			MimeMessage message = new MimeMessage(session);
			
			message.setSender(new InternetAddress(email.getSender()));
			message.setSubject(email.getSubject());
			message.setText(email.getBody());
			message.setRecipients(RecipientType.TO, InternetAddress.parse(recipients));
			
			Transport.send(message);
			
		} catch(MessagingException ex) {
			// Something
		}
	}
}

package edu.osu.AU13.cse4471.securevote;

import java.util.List;

public class Email {
	private String subject;
	private String body;
	private String sender;
	
	public Email(String subject, String body, String sender) {
		this.subject = subject;
		this.body = body;
		this.sender = sender;
	}
	
	public String getSubject() {
		return subject;
	}
	
	public String getBody() {
		return body;
	}
	
	public String getSender() {
		return sender;
	}
}

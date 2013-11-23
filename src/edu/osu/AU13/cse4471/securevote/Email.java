package edu.osu.AU13.cse4471.securevote;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import android.content.Context;
import android.util.Log;

public class Email {
	private String subject;
	private String body;
	private String attachData;
	private File attachFile;

	public Email(String subject, String body) {
		this.subject = subject;
		this.body = body;
		this.attachData = null;
	}

	public Email(String subject, String body, String attach) {
		this.subject = subject;
		this.body = body;
		this.attachData = attach;
	}

	public String getSubject() {
		return subject;
	}

	public String getBody() {
		return body;
	}

	public File getAttach(Context con, Poll p) {
		if (attachData == null) {
			return null;
		}

		if (attachFile != null) {
			return attachFile;
		}

		Writer wr = null;
		try {
			File extDir = con.getExternalFilesDir(null);
			File extFile = File.createTempFile("securevote", ".svp", extDir);

			wr = new BufferedWriter(new FileWriter(extFile));
			wr.write(attachData);

			p.registerFileForDeletion(extFile);

			attachFile = extFile;
			return extFile;
		} catch (IOException e) {
			Log.e(Email.class.getSimpleName(),
					"Error writing attachment to file", e);
			throw new Error(e);
		} finally {
			if (wr != null) {
				try {
					wr.close();
				} catch (IOException e) {
					Log.e(Email.class.getSimpleName(), "WTF?", e);
				}
			}
		}
	}
}

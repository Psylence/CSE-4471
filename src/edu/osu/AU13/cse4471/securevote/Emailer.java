package edu.osu.AU13.cse4471.securevote;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

public class Emailer {
	private Emailer() {
	}

	public static void sendEmail(Email email, String[] recipients,
			Activity caller, Poll poll) {
		Intent intent = new Intent();

		intent.setAction(Intent.ACTION_SEND);

		intent.putExtra(Intent.EXTRA_EMAIL, recipients);
		intent.putExtra(Intent.EXTRA_SUBJECT, email.getSubject());
		intent.putExtra(Intent.EXTRA_TEXT, email.getBody());
		intent.putExtra(Intent.EXTRA_STREAM,
				Uri.fromFile(email.getAttach(caller, poll)));
		intent.setType("application/secure-vote-poll");
		caller.startActivity(intent);
	}
}

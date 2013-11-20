package edu.osu.AU13.cse4471.securevote;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

/**
 * Common base class for users and talliers.
 * 
 * Encapsulates their identfying information (ie, email address) and handles
 * sending communications to them.
 * 
 * @author andrew
 * 
 */
public abstract class User {
  /**
   * User's email address
   */
  private String email;

  /**
   * Each user's data is only stored in the context of a given Poll (the poll in
   * which he is a voter or a tallier. If the same user participates in multiple
   * polls, we'll allow ourselves to keep multiple records of him, so that we
   * can easily store Poll-specific data (like public keys, votes, etc) on the
   * User object. Furthermore, if the same person (same email address) is
   * participating as both a Voter and a Tallier, we'll keep two objects (
   * {@link Voter} and {@link Tallier}).
   */
  private Poll poll;

  /**
   * Initialize the fields in User
   * 
   * @param email
   *          user's email address
   */
  protected User(String email, Poll poll) {
    this.email = email;
    this.poll = poll;
  }

  /**
   * Retrieve the user's email address
   * 
   * @return the user's email address
   */
  public String getEmail() {
    return email;
  }

  /**
   * Retrieve the poll that this User object is participating in.
   * 
   * @return the poll in which this User is participating.
   */
  public Poll getPoll() {
    return poll;
  }

  /**
   * Send an email to this user
   * 
   * @param payload
   *          Data to attach t
   */
  public void sendEmail(final String subject, final String body,
      final String attachmentContents, final Activity fromActivity) {
    new Thread(new Runnable() {
      @Override
      public void run() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_EMAIL, email);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, body);

        Uri attachmentUri = User.createFile(attachmentContents, fromActivity);

        if (attachmentUri != null) {
          intent.putExtra(Intent.EXTRA_STREAM, attachmentUri);

          fromActivity.startActivity(intent);
        } else {
          Toast.makeText(fromActivity, "Error occurred", Toast.LENGTH_SHORT)
              .show();
        }

      }
    }).start();
  }

  static Uri createFile(String contents, Context context) {
    File publicStorage = context.getExternalFilesDir(null);
    if (publicStorage == null) {
      Log.e(User.class.getSimpleName(), "Could not access public storage");
      return null;
    }
    try {
      File temp = File.createTempFile("poll", ".svp", publicStorage);
      Writer w = new FileWriter(temp);
      w.write(contents);
      w.close();
      return Uri.fromFile(temp);
    } catch (IOException e) {
      Log.e(User.class.getSimpleName(), "Error creating file", e);
      return null;
    }
  }

  @Override
  public boolean equals(Object o) {
    if (o.getClass().equals(this.getClass())) {
      User u = (User) o;
      return email.equals(u.email) && poll.equals(u.poll);
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return this.getClass().hashCode() ^ email.hashCode() ^ poll.hashCode();
  }
}

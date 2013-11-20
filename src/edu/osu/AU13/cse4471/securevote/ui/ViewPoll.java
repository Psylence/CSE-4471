package edu.osu.AU13.cse4471.securevote.ui;

import java.util.UUID;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import edu.osu.AU13.cse4471.securevote.Constants;
import edu.osu.AU13.cse4471.securevote.Poll;
import edu.osu.AU13.cse4471.securevote.PollDB;
import edu.osu.AU13.cse4471.securevote.R;

public class ViewPoll extends Activity {
  private TextView mTitle;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_view_poll);
    // Show the Up button in the action bar.
    setupActionBar();

    Intent intent = getIntent();
    UUID id = null;
    Poll poll = null;
    try {
      id = UUID.fromString(intent.getStringExtra(Constants.DATA_NAME_POLL));
    } catch (NullPointerException e) {
    }

    if (id != null) {
      poll = PollDB.getInstance().getPoll(id);
    }

    if (id != null && poll != null) {
      mTitle = (TextView) findViewById(R.id.view_poll_title);

      mTitle.setText(poll.toString());

    } else {
      Log.e(ViewPoll.class.getSimpleName(), "Poll does not exist: ID " + id);
      finish();
    }
  }

  /**
   * Set up the {@link android.app.ActionBar}, if the API is available.
   */
  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  private void setupActionBar() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
      getActionBar().setDisplayHomeAsUpEnabled(true);
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.view_poll, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
    case android.R.id.home:
      // This ID represents the Home or Up button. In the case of this
      // activity, the Up button is shown. Use NavUtils to allow users
      // to navigate up one level in the application structure. For
      // more details, see the Navigation pattern on Android Design:
      //
      // http://developer.android.com/design/patterns/navigation.html#up-vs-back
      //
      NavUtils.navigateUpFromSameTask(this);
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

}

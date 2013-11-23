package edu.osu.AU13.cse4471.securevote.ui;

import java.util.UUID;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import edu.osu.AU13.cse4471.securevote.DiskPersister;
import edu.osu.AU13.cse4471.securevote.Poll;
import edu.osu.AU13.cse4471.securevote.R;
import edu.osu.AU13.cse4471.securevote.Tallier;
import edu.osu.AU13.cse4471.securevote.Voter;

public class ViewPoll extends Activity {
	private TextView mTitle;
	private TextView mDesc;
	private LinearLayout mContents;
	private UUID mId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_poll);
		// Show the Up button in the action bar.
		setupActionBar();

		mTitle = (TextView) findViewById(R.id.view_poll_title);
		mDesc = (TextView) findViewById(R.id.view_poll_desc);
		mContents = (LinearLayout) findViewById(R.id.view_poll_contents);

		Intent intent = getIntent();

		try {
			mId = UUID.fromString(intent
					.getStringExtra(MainActivity.DATA_NAME_POLL));
		} catch (NullPointerException e) {
			mId = null;
		}

		updateFields();
	}

	private void updateFields() {
		Poll p = DiskPersister.getInst().loadPoll(mId, this);
		Voter v = DiskPersister.getInst().loadVoter(mId, this);
		Tallier t = DiskPersister.getInst().loadTallier(mId, this);

		mTitle.setText(p.getTitle());
		mDesc.setText(p.getDesc());

		mContents.removeAllViews();
		if (t == null && v == null) {
			displayInfo(R.string.view_poll_not_participant);
		}

		if (v != null && !v.isReadyToVote()) {
			displayInfo(R.string.view_poll_need_keys);
		} else if (v != null && v.isReadyToVote() && !v.hasVoted()) {
			displayTwoButtons(R.string.no, new DoNothing(), R.string.yes,
					new DoNothing());
		} else if (v != null && v.hasVoted()) {
			displayOneButton(R.string.view_poll_resend_vote, new DoNothing());
		}

		if (t != null) {
			displayOneButton(R.string.view_poll_send_pubkey_label,
					new DoNothing());
		}

		if (t != null && !t.hasAllVotes()) {
			displayInfo(R.string.view_poll_need_votes);
		} else if (t != null && t.hasAllVotes() && !t.hasResults()) {
			displayOneButton(R.string.view_poll_send_point, new DoNothing());
		} else if (t != null && t.hasResults()) {
			displayOneButton(R.string.view_poll_count_votes, new DoNothing());
		}
	}

	private void displayOneButton(int buttonText, View.OnClickListener listener) {
		Button b = new Button(this);
		b.setText(buttonText);
		b.setOnClickListener(listener);

		mContents.addView(b);
	}

	private void displayTwoButtons(int button0Text, View.OnClickListener l0,
			int button1Text, View.OnClickListener l1) {
		LinearLayout horiz = new LinearLayout(this);
		horiz.setOrientation(LinearLayout.HORIZONTAL);
		Button b0 = new Button(this), b1 = new Button(this);
		b0.setText(button0Text);
		b0.setOnClickListener(l0);
		b1.setText(button1Text);
		b1.setOnClickListener(l1);
		mContents.addView(horiz);
	}

	private void displayInfo(int rId) {
		TextView tv = new TextView(this);
		tv.setText(rId);
		mContents.addView(tv);

	}

	@Override
	protected void onResume() {
		super.onResume();

		updateFields();
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

	private static class DoNothing implements View.OnClickListener {
		@Override
		public void onClick(View v) {
		}
	}
}

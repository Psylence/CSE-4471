package edu.osu.AU13.cse4471.securevote.ui;

import java.util.Collections;
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
import android.widget.TextView;
import edu.osu.AU13.cse4471.securevote.Poll;
import edu.osu.AU13.cse4471.securevote.R;
import edu.osu.AU13.cse4471.securevote.Voter;

public class ViewPoll extends Activity {
	private TextView mTitle;
	private TextView mDesc;
	private Button mVote0;
	private Button mVote1;
	private TextView mAlreadyVoted;
	private TextView mNeedKeys;
	private UUID mId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_poll);
		// Show the Up button in the action bar.
		setupActionBar();

		mTitle = (TextView) findViewById(R.id.view_poll_title);
		mDesc = (TextView) findViewById(R.id.view_poll_desc);
		mVote0 = (Button) findViewById(R.id.view_poll_vote_0);
		mVote1 = (Button) findViewById(R.id.view_poll_vote_1);
		mAlreadyVoted = (TextView) findViewById(R.id.view_poll_already_voted);
		mNeedKeys = (TextView) findViewById(R.id.view_poll_need_keys);

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
		Poll poll = new Poll(mId, "title", "desc",
				Collections.singletonList("krieger.63@osu.edu"),
				Collections.singletonList("krieger.63@osu.edu"));
		Voter voter = new Voter("foo@example.org", poll);

		mTitle.setText(poll.getTitle());
		mDesc.setText(poll.getDesc());

		if (!voter.isReadyToVote()) {
			mVote0.setVisibility(View.GONE);
			mVote1.setVisibility(View.GONE);
			mAlreadyVoted.setVisibility(View.GONE);
			mNeedKeys.setVisibility(View.VISIBLE);
		} else if (voter.isReadyToVote() && !voter.hasVoted()) {
			mVote0.setVisibility(View.VISIBLE);
			mVote1.setVisibility(View.VISIBLE);
			mAlreadyVoted.setVisibility(View.GONE);
			mNeedKeys.setVisibility(View.GONE);
		} else {
			mVote0.setVisibility(View.GONE);
			mVote1.setVisibility(View.GONE);
			mAlreadyVoted.setVisibility(View.VISIBLE);
			mNeedKeys.setVisibility(View.GONE);
		}
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

}

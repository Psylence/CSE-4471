package edu.osu.AU13.cse4471.securevote.ui;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import edu.osu.AU13.cse4471.securevote.Constants;
import edu.osu.AU13.cse4471.securevote.Poll;
import edu.osu.AU13.cse4471.securevote.PollDB;
import edu.osu.AU13.cse4471.securevote.R;

public class MainActivity extends Activity {
	private ListView mPollList;
	private ArrayAdapter<Poll> mPollAdapter;
	private Button mCreatePoll;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mPollList = (ListView) findViewById(R.id.poll_list);
		mCreatePoll = (Button) findViewById(R.id.create_poll);

		mPollList.setAdapter(mPollAdapter);
		mPollList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Object item = parent.getItemAtPosition(position);
				if (!(item instanceof Poll)) {
					Log.e(MainActivity.class.getSimpleName(),
							"Bad object in list of polls: class "
									+ item.getClass().getName());
					return;
				}

				Poll poll = (Poll) item;
				Intent intent = new Intent(MainActivity.this, ViewPoll.class);
				intent.putExtra(Constants.DATA_NAME_POLL, poll.getId());
				startActivity(intent);
			}
		});

		mCreatePoll.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, CreatePoll.class);
				startActivity(intent);
			}
		});

		Intent intent = getIntent();
		if (intent != null && intent.getAction().equals(Intent.ACTION_VIEW)) {
			Uri uri = intent.getData();
			if (uri != null && uri.getScheme().equals("file")) {
				File f = new File(uri.getPath());
				if (f.canRead()) {
					processEmailAttachment(f);
				}
			}
		}

		updatePollList();
	}

	private void processEmailAttachment(File f) {
		Toast.makeText(this, "Load file " + f.getAbsolutePath(),
				Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onResume() {
		super.onResume();
		updatePollList();
	}

	/**
	 * Update the list of polls. This is potentially slow, so we'll do it on a
	 * background thread
	 */
	private void updatePollList() {
		(new Thread(new Runnable() {
			// First, compute a new list of polls
			@Override
			public void run() {
				List<Poll> polls = PollDB.getInstance().getPolls();

				final ArrayAdapter<Poll> newAdapter = new ArrayAdapter<Poll>(
						MainActivity.this, android.R.layout.simple_list_item_1);

				// Save them into a new adapter (in our case, a special form of
				// List used to hold results for a ListView)
				for (Poll p : polls) {
					newAdapter.add(p);
				}

				// Go to the UI thread and update the list with the new results
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mPollAdapter = newAdapter;
						mPollList.setAdapter(newAdapter);
					}
				});
			}
		})).start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}

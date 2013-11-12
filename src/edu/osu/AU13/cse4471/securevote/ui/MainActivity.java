package edu.osu.AU13.cse4471.securevote.ui;

import java.io.File;
import java.math.BigInteger;
import java.util.Collections;
import java.util.UUID;

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
import edu.osu.AU13.cse4471.securevote.Poll;
import edu.osu.AU13.cse4471.securevote.PollDB;
import edu.osu.AU13.cse4471.securevote.R;
import edu.osu.AU13.cse4471.securevote.Tallier;
import edu.osu.AU13.cse4471.securevote.Voter;
import edu.osu.AU13.cse4471.securevote.math.CyclicGroup;
import edu.osu.AU13.cse4471.securevote.math.IntegersModM;

public class MainActivity extends Activity {
  public static final String DATA_NAME_POLL = "edu.osu.AU13.cse4471.securevote.POLL";
  private ListView mPollList;
  private Button mCreatePoll;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    mPollList = (ListView) findViewById(R.id.poll_list);
    mCreatePoll = (Button) findViewById(R.id.create_poll);
    final Activity context = this;

    ArrayAdapter<Poll> adapter = new ArrayAdapter<Poll>(this,
        android.R.layout.simple_list_item_1);
    for (Poll p : makeSamplePolls()) {
      adapter.add(p);
    }
    mPollList.setAdapter(adapter);
    mPollList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position,
          long id) {
        Object item = parent.getItemAtPosition(position);
        if (!(item instanceof Poll)) {
          Log.e(MainActivity.class.getSimpleName(),
              "Bad object in list of polls: class " + item.getClass().getName());
          return;
        }

        Poll poll = (Poll) item;
        Intent intent = new Intent(context, ViewPoll.class);
        intent.putExtra(MainActivity.DATA_NAME_POLL, poll.getId());
        startActivity(intent);
      }
    });

    mCreatePoll.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(context, CreatePoll.class);
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
  }

  private void processEmailAttachment(File f) {
    Toast
        .makeText(this, "Load file " + f.getAbsolutePath(), Toast.LENGTH_SHORT)
        .show();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  private Poll[] makeSamplePolls() {
    Poll[] ret = new Poll[16];

    for (int i = 0; i < 16; i++) {
      Poll p = new Poll(new UUID(0, i), "Poll " + (i + 1), "desc",
          Collections.<Voter> singletonList(null),
          Collections.<Tallier> singletonList(null));
      PollDB.getInstance().putPoll(p);
      ret[i] = p;
    }
    return ret;
  }
}

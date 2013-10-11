package edu.osu.AU13.cse4471.securevote.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import edu.osu.AU13.cse4471.securevote.Poll;
import edu.osu.AU13.cse4471.securevote.PollDB;
import edu.osu.AU13.cse4471.securevote.R;

public class MainActivity extends Activity {
  public static final String DATA_NAME_POLL =
      "edu.osu.AU13.cse4471.securevote.POLL";
  private ListView mPollList;
  private Button mCreatePoll;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    mPollList = (ListView) findViewById(R.id.poll_list);
    mCreatePoll = (Button) findViewById(R.id.create_poll);
    final Activity context = this;

    ArrayAdapter<Poll> adapter =
        new ArrayAdapter<Poll>(this, android.R.layout.simple_list_item_1);
    for (Poll p : makeSamplePolls())
      adapter.add(p);
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
        intent.putExtra(DATA_NAME_POLL, poll.getId());
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
      Poll p = new Poll(i, "Poll " + (i + 1));
      PollDB.getInstance(this).putPoll(p);
      ret[i] = p;
    }
    return ret;
  }
}

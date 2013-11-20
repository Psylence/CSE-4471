package edu.osu.AU13.cse4471.securevote.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import edu.osu.AU13.cse4471.securevote.EnterEmailActivity;
import edu.osu.AU13.cse4471.securevote.Poll;
import edu.osu.AU13.cse4471.securevote.R;
import edu.osu.AU13.cse4471.securevote.Tallier;
import edu.osu.AU13.cse4471.securevote.Voter;

public class CreatePoll extends FragmentActivity {
  private static final int EDIT_VOTER_EMAIL_REQUEST = 17;
  private static final int EDIT_TALLIER_EMAIL_REQUEST = 289;
  private EditText mTitle, mDesc;
  private ArrayAdapter<String> mVoterAdapter, mTallierAdapter;
  private ListView mVoterList, mTallierList;
  private Button mVoterAdd, mTallierAdd;
  private TextView mVoterEmpty, mTallierEmpty;
  private CheckBox mTallierSame;
  private Button mCancel, mOk;
  private int mErrorCode;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_create_poll);
    setupActionBar();

    // Ugly UI stuff ahead
    mTitle = (EditText) findViewById(R.id.create_poll_title);
    mDesc = (EditText) findViewById(R.id.create_poll_desc);
    mVoterList = (ListView) findViewById(R.id.create_poll_voter_list);
    mTallierList = (ListView) findViewById(R.id.create_poll_tallier_list);
    mTallierSame = (CheckBox) findViewById(R.id.create_poll_tallier_same);
    mVoterAdd = (Button) findViewById(R.id.create_poll_edit_voter);
    mTallierAdd = (Button) findViewById(R.id.create_poll_edit_tallier);
    mVoterEmpty = (TextView) findViewById(R.id.create_poll_voter_empty);
    mTallierEmpty = (TextView) findViewById(R.id.create_poll_tallier_empty);
    mCancel = (Button) findViewById(R.id.create_poll_cancel);
    mOk = (Button) findViewById(R.id.create_poll_ok);

    mVoterAdapter = new ArrayAdapter<String>(this,
        android.R.layout.simple_list_item_1);
    mTallierAdapter = new ArrayAdapter<String>(this,
        android.R.layout.simple_list_item_1);

    mVoterList.setAdapter(mVoterAdapter);
    mTallierList.setAdapter(mTallierAdapter);

    mTallierSame
        .setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {

          @Override
          public void onCheckedChanged(CompoundButton buttonView,
              boolean isChecked) {
            updateListViews();
          }
        });

    mVoterAdd.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        addVoter();
      }
    });
    mTallierAdd.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        addTallier();
      }
    });
    mCancel.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        setResult(Activity.RESULT_CANCELED);
        finish();
      }
    });

    mOk.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Poll p = gatherInputs();
        if (p == null) {
          Toast.makeText(CreatePoll.this, mErrorCode, Toast.LENGTH_SHORT)
              .show();
        } else {
          ProtocolHandler.getInst().sendRequestPublicKey(p,
              p.getTalliers().get(0), CreatePoll.this);
        }
      }
    });

    DataSetObserver obs = new DataSetObserver() {
      @Override
      public void onChanged() {
        updateListViews();
      }
    };

    mVoterAdapter.registerDataSetObserver(obs);
    mTallierAdapter.registerDataSetObserver(obs);

    updateListViews();
  }

  private void updateListViews() {
    if (mVoterAdapter.isEmpty()) {
      mVoterList.setVisibility(View.GONE);
      mVoterEmpty.setVisibility(View.VISIBLE);
    } else {
      mVoterList.setVisibility(View.VISIBLE);
      mVoterEmpty.setVisibility(View.GONE);
    }

    if (mTallierSame.isChecked()) {
      mTallierList.setVisibility(View.GONE);
      mTallierAdd.setVisibility(View.GONE);
      mTallierEmpty.setVisibility(View.GONE);
    } else {
      mTallierAdd.setVisibility(View.VISIBLE);
      if (mTallierAdapter.isEmpty()) {
        mTallierList.setVisibility(View.GONE);
        mTallierEmpty.setVisibility(View.VISIBLE);
      } else {
        mTallierList.setVisibility(View.VISIBLE);
        mTallierEmpty.setVisibility(View.GONE);
      }
    }
  }

  private void addVoter() {
    Intent i = new Intent(this, EnterEmailActivity.class);
    startActivityForResult(i, CreatePoll.EDIT_VOTER_EMAIL_REQUEST);
  }

  private void addTallier() {
    Intent i = new Intent(this, EnterEmailActivity.class);
    startActivityForResult(i, CreatePoll.EDIT_TALLIER_EMAIL_REQUEST);
  }

  private Poll gatherInputs() {
    String title = mTitle.getText().toString().trim();
    if (title.length() == 0) {
      mErrorCode = R.string.create_poll_error_no_title;
      return null;
    }
    String desc = mDesc.getText().toString().trim();

    int nVoters = mVoterAdapter.getCount();
    int nTalliers = mTallierAdapter.getCount();
    if (mTallierSame.isChecked()) {
      nTalliers = nVoters;
    }

    if (nVoters == 0) {
      mErrorCode = R.string.create_poll_error_no_voters;
      return null;
    }

    if (nTalliers == 0) {
      mErrorCode = R.string.create_poll_error_no_talliers;
      return null;
    }

    Poll p = new Poll(UUID.randomUUID(), title, desc);

    List<Voter> voters = new ArrayList<Voter>(nVoters);
    for (int i = 0; i < nVoters; i++) {
      voters.add(new Voter(mVoterAdapter.getItem(i), p));
    }

    List<Tallier> talliers = new ArrayList<Tallier>(nTalliers);
    ArrayAdapter<String> adapter = mTallierAdapter;
    if (mTallierSame.isChecked()) {
      adapter = mVoterAdapter;
    }
    for (int i = 0; i < nTalliers; i++) {
      talliers.add(new Tallier(adapter.getItem(i), p));
    }

    p.setVoters(voters);
    p.setTalliers(talliers);

    return p;
  }

  @Override
  protected void onActivityResult(int reqCode, int resCode, Intent intent) {
    if (resCode == Activity.RESULT_OK) {
      String[] res = intent.getExtras().getStringArray(
          EnterEmailActivity.EMAIL_LIST);
      if (res != null) {
        if (reqCode == CreatePoll.EDIT_VOTER_EMAIL_REQUEST) {
          mVoterAdapter.clear();
          for (String s : res) {
            mVoterAdapter.add(s);
          }
        } else if (reqCode == CreatePoll.EDIT_TALLIER_EMAIL_REQUEST) {
          mTallierAdapter.clear();
          for (String s : res) {
            mTallierAdapter.add(s);
          }
        }
      }
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.create_poll, menu);
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

  /**
   * Set up the {@link android.app.ActionBar}, if the API is available.
   */
  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  private void setupActionBar() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
      getActionBar().setDisplayHomeAsUpEnabled(true);
    }
  }
}

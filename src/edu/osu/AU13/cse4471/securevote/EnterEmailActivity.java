package edu.osu.AU13.cse4471.securevote;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class EnterEmailActivity extends Activity {
  public static final String EMAIL_LIST = "edu.osu.AU13.cse4471.securevote.emails";
  private ListView mList;
  private ArrayAdapter<String> mAdapter;
  private Button mCancel, mAdd, mOk;
  private TextView mError;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_enter_email);
    // Show the Up button in the action bar.
    setupActionBar();

    mList = (ListView) findViewById(R.id.email_list);
    mCancel = (Button) findViewById(R.id.email_cancel);
    mAdd = (Button) findViewById(R.id.email_add);
    mOk = (Button) findViewById(R.id.email_ok);
    mError = (TextView) findViewById(R.id.email_error);

    mAdapter = new ArrayAdapter<String>(this,
        android.R.layout.simple_list_item_1);
    mList.setAdapter(mAdapter);

    mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position,
          long id) {
        edit(position);
      }
    });

    mCancel.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        setResult(Activity.RESULT_CANCELED);
        finish();
      }
    });

    mAdd.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        add();
      }
    });

    mOk.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent data = new Intent();
        List<String> list = new ArrayList<String>(mAdapter.getCount());
        for (int i = 0; i < mAdapter.getCount(); i++) {
          list.add(mAdapter.getItem(i));
        }

        data.putExtra(EnterEmailActivity.EMAIL_LIST,
            list.toArray(new String[list.size()]));
        setResult(Activity.RESULT_OK, data);
        finish();
      }
    });
  }

  /**
   * Add a new email to the list
   */
  private void add() {
    doDialog(AdapterView.INVALID_POSITION);
  }

  /**
   * Edit the currently selected email
   */
  private void edit(int pos) {
    doDialog(pos);
  }

  private void doDialog(final int editIndex) {
    AlertDialog.Builder alert = new AlertDialog.Builder(this);

    if (editIndex >= 0) {
      alert
          .setTitle(getResources().getString(R.string.email_edit_dialog_title));
    } else {
      alert.setTitle(getResources().getString(R.string.email_add_dialog_title));
    }
    alert.setMessage(getResources()
        .getString(R.string.email_add_dialog_message));

    final EditText input = new EditText(this);
    input.setHint(getResources().getString(R.string.email_add_dialog_hint));
    if (editIndex != AdapterView.INVALID_POSITION) {
      input.setText(mAdapter.getItem(editIndex));
    }
    alert.setView(input);

    alert.setPositiveButton(android.R.string.ok,
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            String email = input.getText().toString();
            email = EnterEmailActivity.validateEmail(email);
            if (email != null) {
              EnterEmailActivity.setOrAdd(editIndex, mAdapter, email);
            } else {
              setError("Error: Invalid email");
            }
          }
        });

    if (editIndex != AdapterView.INVALID_POSITION) {
      alert.setNeutralButton(getResources().getString(R.string.delete),
          new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              EnterEmailActivity.delete(editIndex, mAdapter);
            }
          });
    }

    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int whichButton) {
      }
    });

    alert.show();
    setError(null);
  }

  private static <T> void setOrAdd(int idx, ArrayAdapter<T> adapter, T val) {
    if (idx < 0) {
      adapter.add(val);
    } else {
      List<T> list = EnterEmailActivity.adapterToList(adapter);
      list.set(idx, val);
      adapter.clear();
      for (T t : list) {
        adapter.add(t);
      }
    }
  }

  private static <T> void delete(int idx, ArrayAdapter<T> adapter) {
    List<T> list = EnterEmailActivity.adapterToList(adapter);
    adapter.clear();
    ListIterator<T> it = list.listIterator();
    while (it.hasNext()) {
      if (it.nextIndex() != idx) {
        adapter.add(it.next());
      } else {
        it.next();
      }
    }
  }

  private static <T> List<T> adapterToList(ArrayAdapter<T> adapter) {
    int l = adapter.getCount();
    ArrayList<T> list = new ArrayList<T>(l);
    for (int i = 0; i < l; i++) {
      list.add(adapter.getItem(i));
    }

    return list;
  }

  /**
   * Set an error message, to inform the luser of why they're stupid.
   */
  private void setError(String s) {
    if (s == null) {
      mError.setText("");
    } else {
      mError.setText(s);
    }
  }

  /**
   * Return a valid email (without leading whitespace), or null if the input is
   * not a salvageable email address. Currently does only the barest minimum of
   * validation.
   * 
   * @param email
   * @return
   */
  private static String validateEmail(String email) {
    if (email.indexOf('@') >= 0) {
      return email.trim();
    } else {
      return null;
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
    getMenuInflater().inflate(R.menu.enter_email, menu);
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

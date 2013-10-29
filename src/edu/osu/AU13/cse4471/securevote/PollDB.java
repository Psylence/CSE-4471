package edu.osu.AU13.cse4471.securevote;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * <p>
 * Note: Real database isn't implemented yet. This is a temporary "database" for
 * testing purposes only.
 * </p>
 * <p>
 * This class handles storing polls to the database and reading them back from
 * it. No other classes should modify the database directly; all access should
 * be encapsulated within this class.
 * </p>
 * <p>
 * This class supports asynchronous database access. As soon as the instance is
 * first access, a database connection is launched in a background thread.
 * Client classes can query whether the database is available yet via the
 * {@link #arePollsAvailable()} method. If the polls are available, clients may
 * access them via either {@link #getPolls()} or {@link #getPollById(int)}. All
 * of these methods may be called from any thread or activity.
 * </p>
 * <p>
 * Furthermore, if the polls aren't loaded yet (eg because of a long-running
 * database upgrade), clients can register callbacks that will execute once the
 * polls become available, using the {@link #onPollsAvailable} method. If the
 * polls are already loaded, this callback will be called immediately.
 * Otherwise, the system will delay until the polls are ready. In either case,
 * the callback will be called in the same thread that called
 * {@link #onPollsAvailable}. This thread is required to have an Android Looper
 * (eg, it must be an activity UI thread).
 * </p>
 * 
 * @author andrew
 * 
 */
public class PollDB {
  private static PollDB sInstance;
  private List<Poll> mPolls = new ArrayList<Poll>();

  private PollDB() {
  }

  /**
   * Singleton instance accessor. There's only ever one database connection,
   * which sticks around from the first time it's needed until the application
   * ends (and the connection is reclaimed by the system).
   * 
   * @param context
   *          Used to access the application context
   * @return the unique instance of the database connection
   */
  public static PollDB getInstance() {
    if (PollDB.sInstance == null) {
      PollDB.sInstance = new PollDB();
    }
    return PollDB.sInstance;
  }

  public List<Poll> getPolls() {
    return Collections.unmodifiableList(mPolls);
  }

  public Poll getPoll(int id) {
    List<Poll> list = getPolls();

    for (Poll p : list) {
      if (p.getId() == id) {
        return p;
      }
    }

    return null;
  }

  public void putPoll(Poll p) {
    Iterator<Poll> it = mPolls.iterator();
    while (it.hasNext()) {
      if (it.next().getId() == p.getId()) {
        it.remove();
        break;
      }
    }
    mPolls.add(p);
  }
}

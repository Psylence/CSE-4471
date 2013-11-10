package edu.osu.AU13.cse4471.securevote;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * Mock DB used to store polls. Will eventually save poll data to filesystem.
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

  public Poll getPoll(UUID id) {
    List<Poll> list = getPolls();

    for (Poll p : list) {
      if (p.getId().equals(id)) {
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

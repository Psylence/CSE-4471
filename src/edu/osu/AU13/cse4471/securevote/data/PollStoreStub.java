package edu.osu.AU13.cse4471.securevote.data;

import java.util.HashMap;
import java.util.Map;

public class PollStoreStub implements PollStore {
  private Map<Long, Poll> mMap = new HashMap<Long, Poll>();

  private static PollStoreStub inst = null;

  public static PollStoreStub getInstance() {
    if (inst == null)
      inst = new PollStoreStub();
    return inst;
  }

  private PollStoreStub() {
  }

  @Override
  public Poll getPoll(long id) {
    return mMap.get(id);
  }

  @Override
  public void putPoll(Poll poll) {
    mMap.put(poll.getId(), poll);
  }

}

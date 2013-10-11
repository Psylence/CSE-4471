package edu.osu.AU13.cse4471.securevote.data;

public interface PollStore {
  Poll getPoll(long id);

  void putPoll(Poll poll);
}

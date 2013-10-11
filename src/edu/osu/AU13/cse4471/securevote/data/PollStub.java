package edu.osu.AU13.cse4471.securevote.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PollStub implements Poll {
  private String mTitle, mDesc;
  private List<Option> mOpts;
  private long mId;

  public PollStub() {
  }

  public PollStub(String title, String desc, List<Option> opts, long id) {
    mTitle = title;
    mDesc = desc;
    mOpts = opts;
    mId = id;
  }

  @Override
  public String getTitle() {
    return mTitle;
  }

  @Override
  public void setTitle(String title) {
    mTitle = title;
  }

  @Override
  public String getDesc() {
    return mDesc;
  }

  @Override
  public void setDesc(String desc) {
    mDesc = desc;
  }

  @Override
  public List<Option> getOptions() {
    return mOpts;
  }

  @Override
  public void setOptions(List<Option> options) {
    mOpts = Collections.unmodifiableList(new ArrayList<Option>(options));
  }

  @Override
  public long getId() {
    return mId;
  }

  @Override
  public void setId(long mId) {
    this.mId = mId;
  }

  @Override
  public String toString() {
    return mTitle + " (" + mDesc + ")";
  }

}

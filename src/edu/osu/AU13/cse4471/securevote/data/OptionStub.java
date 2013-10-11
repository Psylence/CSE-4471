package edu.osu.AU13.cse4471.securevote.data;

public class OptionStub implements Option {
  private String mName, mDesc;
  private int mOrd;

  public OptionStub() {
  }

  public OptionStub(String name, String desc, int ord) {
    mName = name;
    mDesc = desc;
    mOrd = ord;
  }

  @Override
  public String getName() {
    return mName;
  }

  @Override
  public void setName(String name) {
    mName = name;
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
  public int getOrdinal() {
    return mOrd;
  }

  @Override
  public void setOrdinal(int ord) {
    mOrd = ord;
  }

  @Override
  public String toString() {
    return mName + " (" + mOrd + "; " + mDesc + ")";
  }

}

package edu.osu.AU13.cse4471.securevote.data;

public interface Option {
  String getName();

  void setName(String name);

  String getDesc();

  void setDesc(String desc);

  int getOrdinal();

  void setOrdinal(int ord);
}

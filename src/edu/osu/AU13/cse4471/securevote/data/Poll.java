package edu.osu.AU13.cse4471.securevote.data;

import java.util.List;

public interface Poll {
  String getTitle();

  void setTitle(String title);

  String getDesc();

  void setDesc(String desc);

  List<Option> getOptions();

  void setOptions(List<Option> options);

  long getId();

  void setId(long id);

}

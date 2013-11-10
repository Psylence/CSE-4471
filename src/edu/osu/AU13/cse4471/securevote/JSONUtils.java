package edu.osu.AU13.cse4471.securevote;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONUtils {
  private JSONUtils() {
  }

  public static interface JSONSerializable {
    public JSONObject toJson() throws JSONException;
  }

  public static interface JSONDeserializer<T> {
    public T fromJson(JSONObject obj) throws JSONException;
  }

  public static JSONArray toArray(Iterable<? extends JSONSerializable> coll)
      throws JSONException {
    JSONArray ret = new JSONArray();
    Iterator<? extends JSONSerializable> it = coll.iterator();

    while (it.hasNext()) {
      ret.put(it.next().toJson());
    }

    return ret;
  }

  public static <T> List<T> fromArray(JSONArray arr, JSONDeserializer<T> deser)
      throws JSONException {
    int len = arr.length();
    List<T> list = new ArrayList<T>(len);

    for (int i = 0; i < len; i++) {
      list.add(deser.fromJson(arr.getJSONObject(i)));
    }

    return list;
  }
}

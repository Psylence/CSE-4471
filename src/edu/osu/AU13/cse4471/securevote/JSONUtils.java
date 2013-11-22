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

	/**
	 * Marks a class that can be serialized to a JSONObject
	 * 
	 * @author andrew
	 * 
	 */
	public static interface JSONSerializable {
		/**
		 * Serialize this object into JSON
		 * 
		 * @return JSONObject
		 * @throws JSONException
		 */
		public JSONObject toJson() throws JSONException;
	}

	/**
	 * Type used to recreate objects from JSON
	 * 
	 * @author andrew
	 * 
	 * @param <T>
	 *            Type to deserialize
	 */
	public static interface JSONDeserializer<T> {
		/**
		 * Create an instance of type T, from the given JSONObject. Typically, T
		 * should be a type implementing {@link JSONSerializable}, and the
		 * {@code obj} supplied should be the return value of its
		 * {@link JSONSerializable#toJson()} method.
		 * 
		 * @param obj
		 * @return
		 * @throws JSONException
		 */
		public T fromJson(JSONObject obj) throws JSONException;
	}

	public static JSONArray toArray(List<String> coll) throws JSONException {
		String result = "[";

		for (String str : coll) {
			result += str + ", ";
		}

		result += "]";

		return new JSONArray(result);
	}

	/**
	 * Create a JSONArray from a List or other collection of
	 * {@link JsonSerializable} objects
	 * 
	 * @param coll
	 *            data to serialize
	 * @return serialized array
	 * @throws JSONException
	 *             only if one of the data throws JSONException
	 */
	public static JSONArray toArray(Iterable<? extends JSONSerializable> coll)
			throws JSONException {
		JSONArray ret = new JSONArray();
		Iterator<? extends JSONSerializable> it = coll.iterator();

		while (it.hasNext()) {
			ret.put(it.next().toJson());
		}

		return ret;
	}

	public static ArrayList<String> fromArray(JSONArray arr)
			throws JSONException {
		int len = arr.length();
		ArrayList<String> list = new ArrayList<String>(len);

		for (int i = 0; i < len; i++) {
			list.add(arr.getString(i));
		}

		return list;
	}

	/**
	 * Deserizializes a JSONArray to a Java List.
	 * 
	 * @param arr
	 *            data to deserialize
	 * @param deser
	 *            Deserialization handler
	 * @return a List<> of deserialized data
	 * @throws JSONException
	 *             if any of the deserialization calls throws JSONException
	 */
	public static <T> ArrayList<T> fromArray(JSONArray arr,
			JSONDeserializer<T> deser) throws JSONException {
		int len = arr.length();
		ArrayList<T> list = new ArrayList<T>(len);

		for (int i = 0; i < len; i++) {
			list.add(deser.fromJson(arr.getJSONObject(i)));
		}

		return list;
	}
}

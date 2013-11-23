package edu.osu.AU13.cse4471.securevote;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

/**
 * Persists state on disk
 * 
 * @author andrew
 * 
 */
public class DiskPersister {
	private DiskPersister() {
	}

	private static DiskPersister inst = new DiskPersister();
	private static final String JSON_POLL = "poll";
	private static final String JSON_VOTER = "voter";
	private static final String JSON_TALLIER = "tallier";

	private static final class Triple {
		public Triple(Poll p, Voter v, Tallier t) {
			this.p = p;
			this.v = v;
			this.t = t;
		}

		public Poll p;
		public Tallier t;
		public Voter v;
	}

	private Map<UUID, Triple> cache = new HashMap<UUID, Triple>();

	public static synchronized DiskPersister getInst() {
		return DiskPersister.inst;
	}

	public void save(Poll p, Voter v, Tallier t, Context con) {
		UUID id = p.getId();
		JSONObject obj = new JSONObject();
		try {
			obj.put(DiskPersister.JSON_POLL, p.toJson());
			if (v != null) {
				obj.put(DiskPersister.JSON_VOTER, v.toJson());
			}
			if (t != null) {
				obj.put(DiskPersister.JSON_TALLIER, t.toJson());
			}
		} catch (JSONException e) {
			Log.e(DiskPersister.class.getSimpleName(),
					"Error serializing to JSON", e);
			throw new RuntimeException(e);
		}

		try {
			OutputStream os = con.openFileOutput(id.toString(),
					Context.MODE_PRIVATE);
			Writer out = new OutputStreamWriter(os, Charset.defaultCharset());
			out.write(obj.toString());

			out.close();
		} catch (FileNotFoundException e) {
			Log.e(DiskPersister.class.getSimpleName(),
					"Error opening file for write", e);
			throw new RuntimeException(e);
		} catch (IOException e) {
			Log.e(DiskPersister.class.getSimpleName(), "Error writing file", e);
			throw new RuntimeException(e);
		}

		cache.put(id, new Triple(p, v, t));
	}

	public Poll loadPoll(UUID id, Context con) {
		Triple triple = loadTriple(id, con);
		return triple != null ? triple.p : null;
	}

	public Voter loadVoter(UUID id, Context con) {
		Triple triple = loadTriple(id, con);
		return triple != null ? triple.v : null;
	}

	public Tallier loadTallier(UUID id, Context con) {
		Triple triple = loadTriple(id, con);
		return triple != null ? triple.t : null;
	}

	public List<UUID> loadPolls(Context con) {
		Set<UUID> ret = new HashSet<UUID>(cache.keySet());

		File dir = con.getFilesDir();
		for (File file : dir.listFiles()) {
			try {
				UUID id = UUID.fromString(file.getName());
				ret.add(id);
			} catch (IllegalArgumentException e) {
				// suppress - just a bad file in the directory
			}
		}

		return new ArrayList<UUID>(ret);
	}

	private Triple loadTriple(UUID id, Context con) {
		Triple triple = cache.get(id);
		if (triple != null) {
			return triple;
		}

		triple = loadTripleFromFile(id, con);
		if (triple != null) {
			cache.put(id, triple);
		}
		return triple;
	}

	public Triple loadTripleFromFile(UUID id, Context con) {
		try {
			String str = readFile(id, con);
			if (str == null) {
				return null;
			}

			Poll p = null;
			Voter v = null;
			Tallier t = null;

			JSONObject fileObj = new JSONObject(str);
			JSONObject pollObj = fileObj.getJSONObject(DiskPersister.JSON_POLL);
			p = new Poll(pollObj);

			if (fileObj.has(DiskPersister.JSON_VOTER)) {
				JSONObject voterObj = fileObj
						.getJSONObject(DiskPersister.JSON_VOTER);
				v = (new Voter.VoterDeserializer(p)).fromJson(voterObj);
			}

			if (fileObj.has(DiskPersister.JSON_TALLIER)) {
				JSONObject tallierObj = fileObj
						.getJSONObject(DiskPersister.JSON_TALLIER);
				t = (new Tallier.TallierDeserializer(p)).fromJson(tallierObj);
			}

			return new Triple(p, v, t);
		} catch (JSONException e) {
			Log.e(DiskPersister.class.getSimpleName(), "Error parsing file", e);
			return null;
		}
	}

	private String readFile(UUID id, Context con) {
		Reader in = null;
		try {
			FileInputStream is = con.openFileInput(id.toString());
			in = new InputStreamReader(is, Charset.defaultCharset());

			char[] buf = new char[4096];
			int num;
			StringBuilder sb = new StringBuilder();
			while ((num = in.read(buf)) != -1) {
				sb.append(buf, 0, num);
			}

			in.close();
			return sb.toString();
		} catch (IOException e) {
			Log.e(DiskPersister.class.getSimpleName(), "Error reading file", e);
			return null;
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				Log.e(DiskPersister.class.getSimpleName(), "WTF?", e);
			}
		}
	}

	public void registerFileForDeletion(Poll p, File extFile) {

	}
}

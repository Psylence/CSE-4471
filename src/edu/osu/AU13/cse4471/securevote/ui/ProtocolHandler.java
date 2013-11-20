package edu.osu.AU13.cse4471.securevote.ui;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import edu.osu.AU13.cse4471.securevote.Constants;
import edu.osu.AU13.cse4471.securevote.Poll;
import edu.osu.AU13.cse4471.securevote.PrivateKey;
import edu.osu.AU13.cse4471.securevote.Tallier;
import edu.osu.AU13.cse4471.securevote.User;

public class ProtocolHandler {
  public void handle(JSONObject json) throws JSONException {
    if (!json.has(Constants.JSON_PHASE)) {
      throw new JSONException("Bad JSON: no phase");
    }
    if (!json.has(Constants.JSON_POLL)) {
      throw new JSONException("Bad JSON: no poll");
    }

    String phase = json.getString(Constants.JSON_PHASE);
    Poll p = new Poll(json.getJSONObject(Constants.JSON_POLL));

    if (Constants.PHASE_REQPUBKEY.equals(phase)) {
      int whichTallier = json.getInt(Constants.JSON_TALLIERNUM);

      if (!ProtocolHandler.confirmIdentity(p.getTalliers().get(whichTallier))) {
        throw new JSONException("Wrong user");
      }

      PrivateKey key = new PrivateKey(p.getGroup(), p.getg());
      p.setTallierSecretKey(key);
      p.getTalliers().get(whichTallier).setPublicKey(key.getPublicKey());

      if (whichTallier < p.getTalliers().size()) {
        sendRequestPublicKey(p.getTalliers().get(whichTallier + 1));
      } else {
        Log.i(ProtocolHandler.class.getSimpleName(),
            "Done collecting public keys: " + p.toJson());
      }
    } else {
      throw new JSONException("Unknown phase: " + phase);
    }
  }

  public void sendRequestPublicKey(Tallier t) {

  }

  private static boolean confirmIdentity(User user) {
    // Authenticate the current user
    return true;
  }

}

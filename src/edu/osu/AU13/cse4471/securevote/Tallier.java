package edu.osu.AU13.cse4471.securevote;

import org.json.JSONException;
import org.json.JSONObject;

import edu.osu.AU13.cse4471.securevote.JSONUtils.JSONDeserializer;
import edu.osu.AU13.cse4471.securevote.JSONUtils.JSONSerializable;
import edu.osu.AU13.cse4471.securevote.math.GroupElement;

public class Tallier extends User implements JSONSerializable {
  private static final String JSON_EMAIL = "email";
  private static final String JSON_PUBKEY = "pubkey";

  private GroupElement pubKey;

  public Tallier(String email, Poll poll) {
    super(email, poll);
    pubKey = null;
  }

  public Tallier(String email, Poll poll, GroupElement pubKey) {
    super(email, poll);
    this.pubKey = pubKey;
  }

  /**
   * Converts this object into a JSON representation
   */
  @Override
  public JSONObject toJson() throws JSONException {
    JSONObject obj = new JSONObject();

    obj.put(Tallier.JSON_EMAIL, getEmail());
    obj.put(Tallier.JSON_PUBKEY, pubKey.toString());

    return obj;
  }

  public static class TallierDeserializer implements JSONDeserializer<Tallier> {
    private Poll mPoll;

    public TallierDeserializer(Poll p) {
      mPoll = p;
    }

    @Override
    public Tallier fromJson(JSONObject obj) throws JSONException {
      String email = obj.getString(Tallier.JSON_EMAIL);
      GroupElement pubKey;
      if (obj.has(Tallier.JSON_PUBKEY)) {
        String pubkeyStr = obj.getString(Tallier.JSON_PUBKEY);
        pubKey = mPoll.getGroup().elementFromString(pubkeyStr);
      } else {
        pubKey = null;
      }

      return new Tallier(email, mPoll, pubKey);
    }
  }

}

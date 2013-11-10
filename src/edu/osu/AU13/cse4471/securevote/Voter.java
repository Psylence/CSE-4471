package edu.osu.AU13.cse4471.securevote;

import org.json.JSONException;
import org.json.JSONObject;

import edu.osu.AU13.cse4471.securevote.JSONUtils.JSONDeserializer;
import edu.osu.AU13.cse4471.securevote.JSONUtils.JSONSerializable;

public class Voter extends User implements JSONSerializable {
  private static final String JSON_EMAIL = "email";

  /**
   * Create a Voter, assigned the given email address
   * 
   * @param email
   */
  public Voter(String email, Poll poll) {
    super(email, poll);
  }

  @Override
  public JSONObject toJson() throws JSONException {
    JSONObject obj = new JSONObject();

    obj.put(Voter.JSON_EMAIL, getEmail());

    return obj;
  }

  public static class VoterDeserializer implements JSONDeserializer<Voter> {
    private final Poll mPoll;

    public VoterDeserializer(Poll p) {
      mPoll = p;
    }

    @Override
    public Voter fromJson(JSONObject obj) throws JSONException,
        IllegalArgumentException {
      String email = obj.getString(Voter.JSON_EMAIL);

      return new Voter(email, mPoll);
    }
  }
}

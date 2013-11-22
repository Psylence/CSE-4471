package edu.osu.AU13.cse4471.securevote;

import java.math.BigInteger;

import org.json.JSONException;
import org.json.JSONObject;

import edu.osu.AU13.cse4471.securevote.JSONUtils.JSONSerializable;
import edu.osu.AU13.cse4471.securevote.math.Group;
import edu.osu.AU13.cse4471.securevote.math.GroupElement;

public class PublicKey implements JSONSerializable {
  private GroupElement key;

  public PublicKey(GroupElement key) {
    this.key = key;
  }

  public GroupElement encode(BigInteger message) {
    return key.exp(message);
  }

  @Override
  public String toString() {
    return key.toString();
  }

  public static PublicKey fromString(Group group, String s) {
    return new PublicKey(group.elementFromString(s));
  }

@Override
public JSONObject toJson() throws JSONException {
	// TODO Auto-generated method stub
	return null;
}
}

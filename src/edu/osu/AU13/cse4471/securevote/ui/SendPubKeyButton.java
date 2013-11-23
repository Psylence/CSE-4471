package edu.osu.AU13.cse4471.securevote.ui;

import java.util.UUID;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import edu.osu.AU13.cse4471.securevote.DiskPersister;
import edu.osu.AU13.cse4471.securevote.Tallier;

public class SendPubKeyButton implements OnClickListener {
	private Activity caller;
	private Tallier t;

	public SendPubKeyButton(UUID id, Activity caller) {
		this.caller = caller;
		this.t = DiskPersister.getInst().loadTallier(id, caller);
	}

	@Override
	public void onClick(View v) {
		t.sendPublicKey(caller);
	}
}

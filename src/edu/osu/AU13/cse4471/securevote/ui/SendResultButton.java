package edu.osu.AU13.cse4471.securevote.ui;

import java.util.UUID;

import edu.osu.AU13.cse4471.securevote.DiskPersister;
import edu.osu.AU13.cse4471.securevote.Tallier;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;

public class SendResultButton implements OnClickListener {
	private Activity caller;
	private Tallier t;

	public SendResultButton(UUID id, Activity caller) {
		this.caller = caller;
		this.t = DiskPersister.getInst().loadTallier(id, caller);
	}

	@Override
	public void onClick(View arg0) {
		t.sendResult(caller);
	}

}

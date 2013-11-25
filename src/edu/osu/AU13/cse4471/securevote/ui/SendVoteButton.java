package edu.osu.AU13.cse4471.securevote.ui;

import java.util.UUID;

import edu.osu.AU13.cse4471.securevote.DiskPersister;
import edu.osu.AU13.cse4471.securevote.Tallier;
import edu.osu.AU13.cse4471.securevote.Voter;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;

public class SendVoteButton implements OnClickListener {
	private Activity caller;
	private Voter v;
	private Boolean choice;

	/**
	 * 
	 * @param id
	 *            The id of the poll that this listener should interact with.
	 * @param caller
	 *            The activity calling this.
	 * @param choice
	 *            Indicates whether this button encodes a yes/no choice. A null
	 *            value indicates it should just resend a vote.
	 */
	public SendVoteButton(UUID id, Activity caller, Boolean choice) {
		this.caller = caller;
		this.v = DiskPersister.getInst().loadVoter(id, caller);
		this.choice = choice;
	}

	@Override
	public void onClick(View view) {
		if (choice == null)
			v.vote(caller);
		else
			v.vote(caller, choice);
	}
}

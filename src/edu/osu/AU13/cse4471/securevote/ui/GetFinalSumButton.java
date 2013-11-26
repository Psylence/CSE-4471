package edu.osu.AU13.cse4471.securevote.ui;

import java.util.UUID;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import edu.osu.AU13.cse4471.securevote.DiskPersister;
import edu.osu.AU13.cse4471.securevote.Tallier;

public class GetFinalSumButton implements OnClickListener {
	private Tallier t;
	private Activity a;

	public GetFinalSumButton(UUID id, Activity a) {
		this.t = DiskPersister.getInst().loadTallier(id, a);
		this.a = a;
	}

	@Override
	public void onClick(View v) {
		int x = t.getFinalSum(a);
		Toast.makeText(a, "Total is " + x, Toast.LENGTH_LONG).show();
	}
}

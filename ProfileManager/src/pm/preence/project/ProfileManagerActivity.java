package pm.preence.project;

import android.app.Activity;
import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class ProfileManagerActivity extends Activity {
	private Intent i;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		i=new Intent(this,ProfileService.class);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	public void onStart(View arg) {
		ProfileService.running=true;
		Log.d("Service","Starting Service");
		startService(i);
	}
	
	public void onStop(View arg) {
		Log.d("Service","Stopping Service");
		ProfileService.running=false;
		stopService(i);
	}
	
}
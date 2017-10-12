package pm.preence.project;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.Toast;

public class SensorResult implements SensorEventListener{
	private Context appContext;
	private SensorManager mSensorManager;
	private Sensor mAccelorometer;
	private static SensorResult _orient;
	int count;
	
	private SensorResult() {
		
	}
	
	private SensorResult(Context c) {
		appContext=c;
		count=0;
		mSensorManager=(SensorManager) appContext.getSystemService(Context.SENSOR_SERVICE);
		mAccelorometer=(Sensor) mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mSensorManager.registerListener(this,mAccelorometer,SensorManager.SENSOR_DELAY_UI);
	}
	
	public static SensorResult getSensors(Context c) {
		if(_orient==null)
			_orient=new SensorResult(c);
		return _orient;
	}

	
	public void onAccuracyChanged(Sensor sensor, int accuracy) {		
	}
	

	public void onSensorChanged(SensorEvent event) {
		
	}
	
	
	
}
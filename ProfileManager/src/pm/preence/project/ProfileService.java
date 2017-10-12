package pm.preence.project;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

public class ProfileService extends IntentService{
	public static boolean running;
	public static int WAITING_TIME=10;
	public final Context mContext;
	public Handler mHandler;
	private SensorManager mSensorManager;
	private Sensor mProximity,mAccel;
	private AudioManager mAudioManager;
	private float CURR_PROX;
	private float LAST_PROX;
	//private float LAST_ACCEL_X,LAST_ACCEL_Y,LAST_ACCEL_Z;
	//private float CURR_ACCEL_X,CURR_ACCEL_Y,CURR_ACCEL_Z;
	private float CURR_ACCEL[],LAST_ACCEL[];
	private int curMode;
	public ProfileService() {
		super("Profile Manager");
		
		LAST_PROX=-1;
		CURR_PROX=0;
		CURR_ACCEL=new float[3];
		LAST_ACCEL=new float[3];
		for(int i=0;i<3;i++) {
			CURR_ACCEL[i]=0;
			LAST_ACCEL[i]=-1;
		}
		mHandler=new Handler(Looper.getMainLooper());
		mContext=this;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mSensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);
		mProximity=(Sensor) mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
		mAccel=(Sensor) mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mAudioManager=(AudioManager)getSystemService(AUDIO_SERVICE);
		curMode=mAudioManager.getRingerMode();
	}
	
	SensorEventListener proximityListener=new SensorEventListener() {
	
		public void onSensorChanged(SensorEvent event) {
			CURR_PROX=event.values[0];
		}
		
		
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}
	};
	SensorEventListener accelListener=new SensorEventListener() {
	
		public void onSensorChanged(SensorEvent event) {
			CURR_ACCEL=event.values;
		}
		
		public void onAccuracyChanged(Sensor sensor, int accuracy) {			
		}
	};
	
	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d("Info!!","Running!!");
		Thread t=new Thread(new Runnable() {
			public Boolean isStill(){
				for(int i=0;i<3;i++) {
					if(Math.abs(CURR_ACCEL[i]-LAST_ACCEL[i])>1) return false;
				}
				if(CURR_PROX!=LAST_PROX) return false;
				return true;
			}
			
			public Status getAcccelStatus() {
				if(CURR_ACCEL[2]>8) return Status.FLAT;
				else if(CURR_ACCEL[2]<-8) return Status.FLAT_FLIPPED;
				else if(CURR_ACCEL[1]>8) return Status.VERTICAL;
				return Status.INVALID;
			}
			
			
			public void run() {
				mSensorManager.registerListener(proximityListener, mProximity,SensorManager.SENSOR_DELAY_UI);
				mSensorManager.registerListener(accelListener, mAccel,SensorManager.SENSOR_DELAY_UI);
				while(ProfileService.running) {
					if(isStill()) {
						Log.d("Values",CURR_ACCEL[1]+" "+CURR_ACCEL[2]);
						//postToast(AudioManager.EXTRA_RINGER_MODE);
						postToast(CURR_ACCEL[1]+" "+CURR_ACCEL[2]);
						Status status=getAcccelStatus();
						if(status==Status.FLAT) {
							postToast("Device is Flat");
							Log.d("Info","Found Flat Device");
							mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
						}
						else if(status==Status.FLAT_FLIPPED && CURR_PROX==0) {
							postToast("Device is Flipped and Dark");
							Log.d("Info","Device is Flipped and Dark");
							mAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
						}
						else if(status==Status.VERTICAL && CURR_PROX==0) {
							postToast("Device is in Pocket");
							Log.d("Info","Device is in Pocket");
							mAudioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
						}
						
						/*
						postToast("Info");
						Log.d("Info","FLAT_PROX");
						if(CURR_PROX==0) {
							mAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
							//mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
						}
						else {
							mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
						}*/
					}
					LAST_PROX=CURR_PROX;
					LAST_ACCEL=CURR_ACCEL;
					try{
						Thread.sleep(ProfileService.WAITING_TIME*1000);
					}
					catch(Exception e) {
						Log.d("ExceptionLog","In exception of thread "+e.getMessage());
					}
				}
				mSensorManager.unregisterListener(proximityListener);
				mSensorManager.unregisterListener(accelListener);
			}
		});
		t.run();
	}
	
	void postToast(final String text) {
		mHandler.post(new Runnable() {
			
			public void run() {
				Toast.makeText(mContext,text , Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	@Override
	public void onDestroy() {
		Log.d("Info!!","Dead!!");
		Toast.makeText(this,"Dying!!", Toast.LENGTH_SHORT).show();
		super.onDestroy();
	}
}

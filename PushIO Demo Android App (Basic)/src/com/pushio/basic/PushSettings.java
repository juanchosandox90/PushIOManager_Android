package com.pushio.basic;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.pushio.basic.datasource.DataSource;
import com.pushio.manager.PushIOManager;
import com.pushio.manager.tasks.PushIOListener;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PushSettings extends Activity implements PushIOListener {

	private ListView mListView;
	private PushIOManager mPushIOManager; 
	private ProgressDialog mProgressDialog;
	private BroadcastReceiver mBroadcastReceiver;
	private Timer mTimeoutTimer;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_push_settings);
		mPushIOManager = new PushIOManager(getApplicationContext(), (PushIOListener) this, null);
		mPushIOManager.ensureRegistration();


		mListView = (ListView) findViewById(R.id.settingsList);
		mListView.setAdapter(new SettingsAdapter(this));
		//Storing the value of the three CheckBoxes in the DefaultSharedPreferences
		SharedPreferences mSettings = PreferenceManager.getDefaultSharedPreferences(this);
		final Editor mEditor = mSettings.edit();
		final Context mContext = this;



		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View thisView, int position,
					long arg3) {
				
				//Find the CheckBox from within the ListView Row
				CheckBox mCheck = (CheckBox) thisView.findViewById(R.list.checkBox);
				mCheck.setChecked(!mCheck.isChecked());

				//Show the Progress Alert
				showProgress();

				switch(position) {
				case 0: { //The check to turn on or off the Notifications in General
					//First, we save the check value in the DefaultSettings
					mEditor.putBoolean(mContext.getString(R.string.notification_preference_key),mCheck.isChecked());

					if	(mCheck.isChecked()){
						//They checked the box to turn on notifications, so register the device by registering for an empty category
						mPushIOManager.registerCategories(null, false);
					} else {
						//They are turning off the notifications

						//Save the check values in the DefaultSettings to false so they will get turned off
						mEditor.putBoolean(mContext.getString(R.string.us_news_preference_key),false);
						mEditor.putBoolean(mContext.getString(R.string.sports_preference_key),false);

						//Tell the server to unregister all categories
						mPushIOManager.unregisterAllCategories();
						// Unregister  the device
						mPushIOManager.unregisterDevice();
					}
					break;
				}
				case 1: {

					//Update the DefaultSettings with the new value for the CheckBox
					mEditor.putBoolean(mContext.getString(R.string.us_news_preference_key),mCheck.isChecked());

					//Create a key to pass to the pushIO server in the proper format
					List<String>lTags = new ArrayList<String>();
					lTags.add(mContext.getString(R.string.us_news_preference_key));

					if (mCheck.isChecked()) {
						//They checked the box, so register the category with the server
						mPushIOManager.registerCategories(lTags, false);
					} else {
						//They unchecked the box, so unregister the category with the server
						mPushIOManager.unregisterCategories(lTags);
					}
					break;
				}
				case 2: {

					//Update the DefaultSettings with the new value for the CheckBox
					mEditor.putBoolean(mContext.getString(R.string.sports_preference_key),mCheck.isChecked());

					//Create a key to pass to the pushIO server in the proper format
					List<String>lTags = new ArrayList<String>();
					lTags.add(mContext.getString(R.string.sports_preference_key));

					if (mCheck.isChecked()) {
						//They checked the box, so register the category with the server
						mPushIOManager.registerCategories(lTags, false);
					} else {
						//They unchecked the box, so unregister the category with the server
						mPushIOManager.unregisterCategories(lTags);
					}
					break;
				}
				}
				//We have updated the DefaultSettings, so save our changes
				mEditor.commit();

				//We now need to update the DataSource 
				DataSource localDataSource = DataSource.getDataSourceInstance(mContext);
				localDataSource.getmSettingsData().get(position).setmChecked(mCheck.isChecked());

				//Tell the SettingsAdapter to refresh itself with the updated DataSource
				SettingsAdapter adapter = (SettingsAdapter) parent.getAdapter();
				adapter.notifyDataSetChanged();


			}

		});
	}



	@Override
	public void onResume() {
		super.onResume();
		//When the activity is resumed, update the SettingsAdapter list with any changed Data
		SettingsAdapter adapter = (SettingsAdapter) mListView.getAdapter();
		adapter.notifyDataSetChanged();

		//With the activity starts, we want to handle any received pushes instead of letting the Notification
		//Service handle them.
		mBroadcastReceiver = new BroadcastReceiver() {
			@Override
			/**
			 * The push intent payload has the main "alert" in the Extra Bundle. If you have set any additional
			 * json keys you can also access them.
			 * 
			 */
			public void onReceive(Context context, Intent intent) {
				
				String alert = intent.getStringExtra("alert");
				Toast.makeText(context, alert, Toast.LENGTH_LONG).show();
				TextView mPushText = (TextView)findViewById(R.id.pushText);
				mPushText.setText(intent.getStringExtra("details"));
				
				Bundle extras = getResultExtras(true);
				//Pass the extras along to the PushIOManager so that they can be registered with
				//the server
				extras.putInt(PushIOManager.PUSH_STATUS, PushIOManager.PUSH_HANDLED_IN_APP);
				setResultExtras(extras);
				//Because we have handled the push, don't let it go to the Notification Center
				this.abortBroadcast();
			}

		};
		registerReceiver( mBroadcastReceiver, new IntentFilter("com.pushio.basic.PUSHIOPUSH") );
	}

	@Override
	public void onPause(){
		super.onPause();

		unregisterReceiver(mBroadcastReceiver);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		SettingsAdapter localAdapter = (SettingsAdapter) mListView.getAdapter();
		localAdapter.notifyDataSetChanged();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_push_settings, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent mIntent = new Intent(this, AboutThisApp.class);
		startActivity(mIntent);
		return true;
	}

	@Override
	protected void onStop(){
		super.onStop();
		mPushIOManager.resetEID();
	}

	private class TimeOutTimer extends AsyncTask<String, Void, String> {

		protected void onPostExecute(){
			onPushIOError("Timeout expired");
		}
		
		@Override
		protected String doInBackground(String... arg0) {
			SystemClock.sleep(45000);
			return null;
		}
		

	}
	private void showProgress(){
		new ProgressDialog(this);
		mProgressDialog = ProgressDialog.show(this, "Talking to push.io!", "Just a sec. We're saving your push.io settings.", true );
		
		new TimeOutTimer().execute("");
	}


	public void onPushIOSuccess() {
		mTimeoutTimer.cancel();
		mProgressDialog.dismiss();


	}
	public void onPushIOError(String aMessage) {
		mTimeoutTimer.cancel();
		mProgressDialog.dismiss();
		new AlertDialog.Builder(this).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogInterface, int i) {
				dialogInterface.dismiss();
			}
		}).setTitle("Whoa!").setMessage( aMessage).show();
	}
	

}

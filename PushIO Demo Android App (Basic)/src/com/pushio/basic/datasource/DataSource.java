package com.pushio.basic.datasource;

import java.util.ArrayList;

import com.pushio.basic.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class DataSource {
	private Context mContext;
	private ArrayList<DataSourceItem> mSettingsData;
	private static DataSource mDataSource;
	
	/**
	 * This is a shared instance that we will call
	 * @param context
	 * @return a DataSource item to use
	 */
	public static DataSource getDataSourceInstance(Context context) {
		if (mDataSource == null) {
			mDataSource = new DataSource(context);
		}
		return mDataSource;
	}
	
	public DataSource(Context context) {
		this.mContext = context;
		mSettingsData = new ArrayList<DataSourceItem>();
		
		SharedPreferences mPushSettings = PreferenceManager.getDefaultSharedPreferences(mContext);
		
		mSettingsData.add(new DataSourceItem(mContext.getString(R.string.DataSource_AllNotificationsTitle),mContext.getString(R.string.DataSource_AllNotificationsDescription),mPushSettings.getBoolean(mContext.getString(R.string.notification_preference_key), false)));
		mSettingsData.add(new DataSourceItem(mContext.getString(R.string.DataSource_USNewsTitle),mContext.getString(R.string.DataSource_USNewsDescription),mPushSettings.getBoolean(mContext.getString(R.string.us_news_preference_key), false))); 
		mSettingsData.add(new DataSourceItem(mContext.getString(R.string.DataSource_SportsTitle),mContext.getString(R.string.DataSource_SportsDescription),mPushSettings.getBoolean(mContext.getString(R.string.sports_preference_key), false))); 
	}

	public ArrayList<DataSourceItem> getmSettingsData() {
		return mSettingsData;
	}
	
	public int getDataSourceLength() {
		return mSettingsData.size();
	}
	
	
}

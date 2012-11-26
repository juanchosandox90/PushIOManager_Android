package com.pushio.basic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.pushio.basic.datasource.DataSource;
/**
 * 
 * @author pushIO
 * @see DataSource.java
 * @see DataSourceItem.java
 * This class simply populates the List View with the settings stored in the
 *
 */
public class SettingsAdapter extends BaseAdapter {
	private Context mContext;
	private LayoutInflater mInflater;
	private DataSource mDataSource;

	public SettingsAdapter(Context context) {
		mContext = context;
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mDataSource = DataSource.getDataSourceInstance(mContext);
	}

	/**
	 * This shows or hides the second and third row of the List based on the value of the Checked
	 * property of the first row.
	 * 
	 * @see DataSource.java
	 * @return the number of rows to display
	 */
	public int getCount() {
		if (mDataSource.getmSettingsData().get(0).getmChecked()) {
			return mDataSource.getDataSourceLength();
		} else {
			return 1;
		}
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		TextView mTitle;
		TextView mDetail;
		CheckBox mChecked;

		if(convertView == null) {
			convertView = mInflater.inflate(R.layout.push_settings_item, parent, false);
		}


		mTitle = (TextView)convertView.findViewById(R.list.titleView);
		mTitle.setText(mDataSource.getmSettingsData().get(position).getmTitle());

		mDetail = (TextView)convertView.findViewById(R.list.detailView);
		mDetail.setText(mDataSource.getmSettingsData().get(position).getmDetail());

		mChecked = (CheckBox)convertView.findViewById(R.list.checkBox);
		mChecked.setChecked(mDataSource.getmSettingsData().get(position).getmChecked());

		return convertView;
	}

}
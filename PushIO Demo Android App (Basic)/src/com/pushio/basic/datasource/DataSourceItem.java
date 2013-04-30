package com.pushio.basic.datasource;

public class DataSourceItem {

	private Boolean mChecked;
	private String	mTitle;
	private String	mDetail;
	
	/**
	 * @return the mChecked
	 */
	public Boolean getmChecked() {
		return mChecked;
	}
	/**
	 * @param mChecked the mChecked to set
	 */
	public void setmChecked(Boolean mChecked) {
		this.mChecked = mChecked;
	}
	/**
	 * @return the mTitle
	 */
	public String getmTitle() {
		return mTitle;
	}
	/**
	 * @param mTitle the mTitle to set
	 */
	public void setmTitle(String mTitle) {
		this.mTitle = mTitle;
	}
	/**
	 * @return the mDetail
	 */
	public String getmDetail() {
		return mDetail;
	}
	/**
	 * @param mDetail the mDetail to set
	 */
	public void setmDetail(String mDetail) {
		this.mDetail = mDetail;
	}
	
	/**
	 * 
	 */
	public DataSourceItem() {
		mTitle = "";
	}
	
	/**
	 * @param title the Title of the Row
	 * @param detail the Detail Text of the Row
	 * @param checked the value of the CheckBox
	 */
	public DataSourceItem(String title, String detail, Boolean checked) {
		if (title == null || checked == null) {
			throw new IllegalArgumentException();
		}
		
		mTitle = title;
		mDetail = detail;
		mChecked = checked;
	}
	
	
}

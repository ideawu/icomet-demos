package com.kyleduo.csclient.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kyleduo.csclient.R;
import com.kyleduo.csclient.obj.ChatListJsonBean.UserObj;

public class AllChatListAdapter extends BaseAdapter {

	private Context mContext;
	private List<UserObj> data;
	private ViewHolder mHolder;

	public AllChatListAdapter(Context mContext, List<UserObj> data) {
		super();
		this.mContext = mContext;
		this.data = data;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.all_chat_list_item, null);
			mHolder = new ViewHolder(convertView);
		} else {
			mHolder = (ViewHolder) convertView.getTag();
		}

		UserObj u = (UserObj) getItem(position);
		mHolder.nameView.setText(u.name);
		mHolder.onlineIndicator.setVisibility(u.unread == 1 ? View.VISIBLE : View.INVISIBLE);

		return convertView;
	}

	private class ViewHolder {
		TextView nameView;
		ImageView onlineIndicator;

		public ViewHolder(View convertView) {
			nameView = (TextView) convertView.findViewById(R.id.all_chat_list_item_name);
			onlineIndicator = (ImageView) convertView.findViewById(R.id.all_chat_list_item_online_indicator);
			convertView.setTag(this);
		}
	}

}

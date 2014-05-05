package com.kyleduo.csclient.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kyleduo.csclient.R;
import com.kyleduo.csclient.obj.MessageJsonBean.MessageObj;

public class ChatMessageListAdapter extends BaseAdapter {

	private Context mContext;
	private List<MessageObj> data;
	private String selfName;
	private ViewHolder mHolder;

	public ChatMessageListAdapter(Context mContext, List<MessageObj> data, String selfName) {
		super();
		this.mContext = mContext;
		this.data = data;
		this.selfName = selfName;
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
		MessageObj m = (MessageObj) getItem(position);

		if (m.from.equals(selfName)) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.chat_message_list_item_send, null);
			mHolder = new ViewHolder(convertView);
		} else {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.chat_message_list_item_receive, null);
			mHolder = new ViewHolder(convertView);
		}
		mHolder = (ViewHolder) convertView.getTag();
		mHolder.contentView.setText(m.text);

		return convertView;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getItemViewType(int position) {
		return super.getItemViewType(position);
	}

	private class ViewHolder {
		TextView timeView;
		TextView contentView;

		public ViewHolder(View convertView) {
			//timeView = (TextView) convertView.findViewById(R.id.chat_message_list_item_receive_time);
			contentView = (TextView) convertView.findViewById(R.id.chat_message_list_item_content);
			convertView.setTag(this);
		}
	}

}

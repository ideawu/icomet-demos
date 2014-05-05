package com.kyleduo.csclient.f;

import java.util.List;

import org.apache.http.Header;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.google.gson.Gson;
import com.kyleduo.csclient.CSConstant;
import com.kyleduo.csclient.R;
import com.kyleduo.csclient.a.ChatActivity;
import com.kyleduo.csclient.adapter.AllChatListAdapter;
import com.kyleduo.csclient.obj.ChatListJsonBean;
import com.kyleduo.csclient.obj.ChatListJsonBean.UserObj;
import com.kyleduo.csclient.utils.SPUtils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

public abstract class ContactFragment extends Fragment {

	private static final String TAG = "ContactFragment";

	private ListView mListView;
	private AsyncHttpClient mClient;
	protected List<UserObj> mUserList;
	protected AllChatListAdapter mAdapter;

	public ContactFragment() {
		mClient = new AsyncHttpClient();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		log("onCreateView");
		View view = inflater.inflate(getlayout(), null);
		mListView = (ListView) view.findViewById(R.id.all_chart_list);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				UserObj u = (UserObj) parent.getItemAtPosition(position);
				if (u.unread == 1) {
					u.unread = 0;
					mAdapter.notifyDataSetChanged();
				}
				Intent intent = new Intent(getActivity(), ChatActivity.class);
				intent.putExtra("with", ((UserObj) parent.getItemAtPosition(position)).name);
				getActivity().startActivity(intent);
			}
		});
		refresh();
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	public void refresh() {
		getList();
	}

	private void getList() {
		String url = CSConstant.BASE_URL + "/api/contacts.php?type=" + getType();
		String cookie = SPUtils.getString(getActivity(), "cookie");
		mClient.addHeader("Cookie", cookie);
		mClient.get(url, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				arg3.printStackTrace();
				super.onFailure(arg0, arg1, arg2, arg3);
			}

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				super.onSuccess(arg0, arg1, arg2);
				String rawResult = new String(arg2);
				log(rawResult);
				ChatListJsonBean cljb = null;
				try {
					cljb = new Gson().fromJson(rawResult, ChatListJsonBean.class);
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}
				refreshList(cljb.data);
			}

		});
	}

	private void refreshList(List<UserObj> newList) {

		if (mListView == null) {
			log("listview is null!!!!!");
			return;
		}

		if (newList == null || newList.size() < 1) {
			return;
		}
		if (mUserList == null) {
			mUserList = newList;
			prepareAdapter();
			log("listView is null? " + (mListView == null) + "  mAdapter is null? " + (mAdapter == null));
			mListView.setAdapter(mAdapter);
		} else {
			mUserList.clear();
			mUserList.addAll(0, newList);
			mAdapter.notifyDataSetChanged();
		}

	}

	/**
	 * 设置未读
	 * 
	 * @param from
	 */
	public boolean setUnread(String from) {
		if (mUserList == null) {
			Log.d(TAG, "mUserList is null");
			return false;
		}
		for (UserObj u : mUserList) {
			log("from: " + from + "   u.name: " + u.name);
			if (from.equals(u.name)) {
				u.unread = 1;
				mAdapter.notifyDataSetChanged();
				return true;
			}
		}
		return false;
	}

	/**
	 * 设置已读
	 * 
	 * @param from
	 */
	public void setRead(String from) {
		for (UserObj u : mUserList) {
			if (from.equals(u.name)) {
				u.unread = 0;
				break;
			}
		}
		mAdapter.notifyDataSetChanged();
	}

	private void log(String msg) {
		Log.d(TAG + "->" + getType(), msg);
	}

	public abstract int getlayout();

	public abstract String getType();

	public abstract void prepareAdapter();
}

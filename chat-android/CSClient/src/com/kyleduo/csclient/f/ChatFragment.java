package com.kyleduo.csclient.f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.http.Header;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.gson.Gson;
import com.kyleduo.csclient.CSApplication;
import com.kyleduo.csclient.CSConstant;
import com.kyleduo.csclient.R;
import com.kyleduo.csclient.a.LoginActivity;
import com.kyleduo.csclient.adapter.ChatMessageListAdapter;
import com.kyleduo.csclient.obj.MessageJsonBean;
import com.kyleduo.csclient.obj.MessageJsonBean.MessageObj;
import com.kyleduo.csclient.obj.SendResponseJsonBean;
import com.kyleduo.csclient.utils.SPUtils;
import com.kyleduo.csclient.utils.UIUtils;
import com.kyleduo.icomet.message.Message.Content;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class ChatFragment extends Fragment {

	private static final String TAG = "ChatFragment";

	private ListView mMessageList;
	private ChatMessageListAdapter mAdapter;
	private AsyncHttpClient mClient;
	private List<MessageObj> mMessages;
	private String mChatWith = "CSClient";
	private InnerMessageReceiver mReceiver;

	private ImageButton mSendButton;
	private EditText mInputEditText;

	public ChatFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		iniClient();
		mChatWith = getArguments().getString("with");

		View rootView = inflater.inflate(R.layout.fragment_chat, container, false);
		mMessageList = (ListView) rootView.findViewById(R.id.chat_message_list);
		mSendButton = (ImageButton) rootView.findViewById(R.id.chat_input_send);
		mInputEditText = (EditText) rootView.findViewById(R.id.chat_input_input);
		mSendButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String input = mInputEditText.getText().toString().trim();
				if (TextUtils.isEmpty(input)) {
					UIUtils.showToast(getActivity(), "Please input something");
					return;
				}
				mInputEditText.setText("");
				sendMessage(input);
			}
		});

		refresh();

		return rootView;
	}

	@Override
	public void onStart() {
		super.onStart();
		Log.d(TAG, "onStart");
		if (mReceiver == null) {
			mReceiver = new InnerMessageReceiver();
		}
		IntentFilter filter = new IntentFilter();
		filter.addAction(CSConstant.ACTION_MESSAGE_ARRIVED);
		filter.setPriority(200);
		getActivity().registerReceiver(mReceiver, filter);
	}

	@Override
	public void onStop() {
		super.onStop();
		getActivity().unregisterReceiver(mReceiver);
	}

	/**
	 * initial asyncHttpClient
	 */
	private void iniClient() {

		mClient = new AsyncHttpClient();

		String cookie = SPUtils.getString(getActivity(), "cookie");
		if (TextUtils.isEmpty(cookie)) {
			UIUtils.showToast(getActivity(), "Not login!");
			Intent intent = new Intent(getActivity(), LoginActivity.class);
			getActivity().startActivity(intent);
			getActivity().finish();
		}

		mClient.addHeader("Cookie", cookie);
	}

	public void refresh() {
		getMessageList();
	}

	private void refreshList(List<MessageObj> newList) {
		if (newList == null || newList.size() < 1) {
			return;
		}
		Collections.reverse(newList);
		if (mMessages == null) {
			mMessages = newList;
			mAdapter = new ChatMessageListAdapter(getActivity(), mMessages, ((CSApplication) getActivity().getApplication()).uname);
			mMessageList.setAdapter(mAdapter);
			mMessageList.setSelection(mAdapter.getCount() - 1);
		} else {
			mMessages.clear();
			mMessages.addAll(0, newList);
			mAdapter.notifyDataSetChanged();
			mMessageList.setSelection(mAdapter.getCount() - 1);
		}

	}

	private void getMessageList() {
		String url = CSConstant.BASE_URL + "/api/messages.php?with=" + mChatWith + "&size=&max_msg_id=";
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
				Log.d(TAG, rawResult);
				MessageJsonBean mjb = new Gson().fromJson(rawResult, MessageJsonBean.class);
				refreshList(mjb.data);
				super.onSuccess(arg0, arg1, arg2);
			}

		});
	}

	private void sendMessage(String text) {
		String urlString = CSConstant.BASE_URL + "/api/send.php";
		RequestParams params = new RequestParams();
		params.put("uid2", mChatWith);
		params.put("text", text);
		mClient.post(urlString, params, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				super.onFailure(arg0, arg1, arg2, arg3);
				arg3.printStackTrace();
			}

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				super.onSuccess(arg0, arg1, arg2);
				String rawResult = new String(arg2);
				SendResponseJsonBean srjb = new Gson().fromJson(rawResult, SendResponseJsonBean.class);
				MessageObj message = srjb.data;
				Log.d(TAG, srjb.toString());
				addMessageToList(message);
			}
		});
	}

	private void addMessageToList(MessageObj message) {
		if (mMessages == null) {
			mMessages = new ArrayList<MessageObj>();
			mAdapter = new ChatMessageListAdapter(getActivity(), mMessages, ((CSApplication) getActivity().getApplication()).uname);
			mMessageList.setAdapter(mAdapter);
		}
		mMessages.add(message);
		mAdapter.notifyDataSetChanged();
		mMessageList.setSelection(mAdapter.getCount() - 1);
	}

	private class InnerMessageReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			Content content = (Content) intent.getSerializableExtra("content");
			MessageObj message = new MessageObj(content);

			if (message.from.equals(mChatWith)) {
//				UIUtils.showToast(context, "message arrived in chat activity");
				addMessageToList(message);
				abortBroadcast();
			}
		}

	}

}

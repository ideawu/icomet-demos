package com.kyleduo.csclient.a;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.kyleduo.csclient.R;
import com.kyleduo.csclient.f.ChatFragment;

public class ChatActivity extends ActionBarActivity {

	private ChatFragment mFragment;
	private String mChatWith = "CSClient";

//	private static final String TAG = "ChatActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		mChatWith = getIntent().getStringExtra("with");

		setTitle(mChatWith);

		mFragment = new ChatFragment();
		mFragment.setArguments(getIntent().getExtras());
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction().add(R.id.container, mFragment).commit();
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mFragment != null) {
			mFragment.refresh();
		}
	}

}

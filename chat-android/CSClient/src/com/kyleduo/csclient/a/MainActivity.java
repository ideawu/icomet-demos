package com.kyleduo.csclient.a;

import java.util.Locale;

import org.apache.http.Header;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.kyleduo.csclient.CSConstant;
import com.kyleduo.csclient.R;
import com.kyleduo.csclient.comet.ICometService;
import com.kyleduo.csclient.f.AllChatFragment;
import com.kyleduo.csclient.f.ContactFragment;
import com.kyleduo.csclient.f.RecentChatFragment;
import com.kyleduo.csclient.obj.MessageJsonBean.MessageObj;
import com.kyleduo.csclient.utils.SPUtils;
import com.kyleduo.csclient.utils.UIUtils;
import com.kyleduo.icomet.message.Message.Content;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class MainActivity extends ActionBarActivity implements ActionBar.TabListener {

	private static final String TAG = "MainActivity";

	private boolean mTabInitialed = false;
	private ActionBar actionBar;

	SectionsPagerAdapter mSectionsPagerAdapter;
	private ContactFragment[] mFragments;

	ViewPager mViewPager;
	private InnerMessageReceiver mReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

	}

	private void iniTab() {
		actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				actionBar.setSelectedNavigationItem(position);
			}
		});
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			actionBar.addTab(actionBar.newTab().setText(mSectionsPagerAdapter.getPageTitle(i)).setTabListener(this));
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.d(TAG, "onStart");
		if (mReceiver == null) {
			mReceiver = new InnerMessageReceiver();
		}
		IntentFilter filter = new IntentFilter();
		filter.addAction(CSConstant.ACTION_MESSAGE_ARRIVED);
		filter.setPriority(100);
		registerReceiver(mReceiver, filter);

		if (!mTabInitialed) {
			iniTab();
			mTabInitialed = true;
		}

	}

	@Override
	protected void onStop() {
		super.onStop();
		if (mReceiver != null) {
			unregisterReceiver(mReceiver);
		}
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		Log.d(TAG, "onTabSelected");
		mViewPager.setCurrentItem(tab.getPosition());
		if (mTabInitialed) {
			ContactFragment currFragment = mFragments[tab.getPosition()];
			currFragment.refresh();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_logout) {
			SPUtils.putString(this, "cookie", "");

			logout();

			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void logout() {
		AsyncHttpClient mHttpClient = new AsyncHttpClient();
		mHttpClient.get(CSConstant.BASE_URL + "/login.php?do=logout", new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				super.onSuccess(arg0, arg1, arg2);
				ICometService.stopService();
				Intent service = new Intent(MainActivity.this, ICometService.class);
//				service.setFlags(Intent.)
				MainActivity.this.stopService(service);
				System.exit(0);
//				Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//				MainActivity.this.startActivity(intent);
//				MainActivity.this.finish();
			}
		});
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	}

	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			if (mFragments == null) {
				mFragments = new ContactFragment[getCount()];
			}
			ContactFragment fragment;
			if (position == 1) {
				fragment = new AllChatFragment();
			} else {
				fragment = new RecentChatFragment();
			}
			mFragments[position] = fragment;
			return fragment;
		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_recent).toUpperCase(l);
			case 1:
				return getString(R.string.title_all).toUpperCase(l);
			}
			return null;
		}
	}

	private class InnerMessageReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			UIUtils.showToast(context, "message arrived");

			Content content = (Content) intent.getSerializableExtra("content");
			MessageObj message = new MessageObj(content);

			boolean ok = false;

			for (ContactFragment cf : mFragments) {
				ok = cf.setUnread(message.from);
			}

			if (ok) {
				abortBroadcast();
			}
		}

	}

}

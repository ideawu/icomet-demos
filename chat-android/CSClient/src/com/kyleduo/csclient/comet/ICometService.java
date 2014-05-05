package com.kyleduo.csclient.comet;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.kyleduo.csclient.CSConstant;
import com.kyleduo.icomet.Channel;
import com.kyleduo.icomet.ChannelAllocator;
import com.kyleduo.icomet.ICometCallback;
import com.kyleduo.icomet.ICometClient;
import com.kyleduo.icomet.ICometConf;
import com.kyleduo.icomet.IConnCallback;
import com.kyleduo.icomet.message.Message;
import com.kyleduo.icomet.message.Message.Content;

public class ICometService extends Service {

	private static final String TAG = "SERVICE";

	private static final String COMET_HOST = "http://www.ideawu.com";

	private static final int WHAT_MESSAGE = 0;
	private static final int WHAT_CONNCTED = 1;
	private static final int WHAT_DISCONNECTED = 2;
	private static final int WHAT_DATAERROR = 3;

	private String uname;
	private static ICometClient mClient;

	private Handler mHanlder = new Handler() {

		@Override
		public void handleMessage(android.os.Message msg) {
			log("handle message, what=" + msg.what);
			switch (msg.what) {
			case WHAT_MESSAGE:
				Content content = (Content) msg.obj;
				Intent intent = new Intent(CSConstant.ACTION_MESSAGE_ARRIVED);
				intent.putExtra("content", content);
				sendOrderedBroadcast(intent, null);
				break;

			default:
				break;
			}
		}

	};

	private void log(String msg) {
		Log.d(TAG, msg);
	}

	public ICometService() {
		mClient = ICometClient.getInstance();
	}

	@Override
	public IBinder onBind(Intent intent) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		uname = intent.getStringExtra("uname");

		if (mClient.currStatus() == ICometClient.State.STATE_NEW) {
			ICometConf conf = new ICometConf();
//			conf.host = "10.0.2.2";
			conf.host = COMET_HOST;
			conf.port = "8100";
			conf.url = "stream";
			conf.iConnCallback = new MyConnCallback();
			conf.iCometCallback = new MyCometCallback();
			conf.channelAllocator = new MyChannelAllocator();
			new SubTask(conf).execute();
		}

		return Service.START_REDELIVER_INTENT;
	}

	public static void stopService() {
		mClient.stopComet();
		//mClient.stopConnect();
//		mClient = null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy");
		NotificationManager nm = (NotificationManager) this.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
		nm.cancel(0);
		if (mClient != null) {
			mClient.stopComet();
			mClient.stopConnect();
			mClient = null;
		}
	}

	class SubTask extends AsyncTask<Void, Void, Void> {

		ICometConf conf;

		public SubTask(ICometConf conf) {
			super();
			this.conf = conf;
		}

		@Override
		protected Void doInBackground(Void... params) {
			mClient.prepare(conf);
			mClient.connect();
			return null;
		}

	}

	class MyConnCallback implements IConnCallback {

		@Override
		public void onDisconnect() {
			log("disconnect");
		}

		@Override
		public void onFail(String arg0) {
			log(arg0);
		}

		@Override
		public boolean onReconnect(int arg0) {
			return true;
		}

		@Override
		public void onReconnectSuccess(int arg0) {
			log("reconnect, times: " + arg0);
		}

		@Override
		public void onStop() {
			log("stop");
		}

		@Override
		public void onSuccess() {
			log("success");
			mClient.comet();
		}

	}

	class MyCometCallback implements ICometCallback {

		@Override
		public void onDataMsgArrived(Content arg0) {
			log(arg0.toString());
			android.os.Message msg = mHanlder.obtainMessage();
			msg.what = WHAT_MESSAGE;
			msg.obj = arg0;
			mHanlder.sendMessage(msg);
		}

		@Override
		public void onErrorMsgArrived(Message arg0) {
			log(arg0.toString());
		}

		@Override
		public void onMsgArrived(Message arg0) {
			log(arg0.toString());
		}

		@Override
		public void onMsgFormatError() {
			log("format error");
		}

	}

	class MyChannelAllocator implements ChannelAllocator {

		@Override
		public Channel allocate() {
			Channel channel = new Channel();
			log("uname: " + uname);
			channel.cname = uname;
			channel.seq = 0;
			channel.token = "";
			return channel;
		}

	}
}

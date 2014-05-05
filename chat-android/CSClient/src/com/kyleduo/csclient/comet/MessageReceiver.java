package com.kyleduo.csclient.comet;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.kyleduo.csclient.CSConstant;
import com.kyleduo.csclient.R;
import com.kyleduo.csclient.a.ChatActivity;
import com.kyleduo.csclient.obj.MessageJsonBean.MessageObj;
import com.kyleduo.icomet.message.Message.Content;

public class MessageReceiver extends BroadcastReceiver {

	private static String TAG = "MessageReceiver";

	public MessageReceiver() {
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onReceive(Context context, Intent intent) {
		if (!intent.getAction().equals(CSConstant.ACTION_MESSAGE_ARRIVED)) {
			return;
		}
		Log.d(TAG, "message received in receiver");
		
		NotificationManager nm = (NotificationManager) context.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
		
		Content content = (Content) intent.getSerializableExtra("content");
		MessageObj message = new MessageObj(content);
		
		Notification notification = new Notification();
		notification.tickerText = "Message arrived";
		notification.when = System.currentTimeMillis();
		notification.icon = R.drawable.ic_launcher;
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		notification.defaults = Notification.DEFAULT_ALL;
		Intent i = new Intent(context.getApplicationContext(), ChatActivity.class);
		i.putExtra("with", message.from);
		PendingIntent pi = PendingIntent.getActivity(context, getResultCode(), i, Intent.FLAG_ACTIVITY_NEW_TASK);
		notification.setLatestEventInfo(context.getApplicationContext(), message.from, message.text, pi);
		nm.notify(0, notification);
		abortBroadcast();
	}
}

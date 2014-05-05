package com.kyleduo.csclient.a;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.kyleduo.csclient.CSApplication;
import com.kyleduo.csclient.CSConstant;
import com.kyleduo.csclient.R;
import com.kyleduo.csclient.comet.ICometService;
import com.kyleduo.csclient.utils.SPUtils;
import com.kyleduo.csclient.utils.UIUtils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class LoginActivity extends Activity {

	private static final String TAG = "LoginActivity";

	private Button mSubmitButton;
	private EditText mNameEditText;

	private ProgressDialog mProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);

		String cookie = SPUtils.getString(getApplicationContext(), "cookie");
		if (!TextUtils.isEmpty(cookie)) {
			String decoded = null;
			try {
				decoded = URLDecoder.decode(URLDecoder.decode(cookie, "utf-8"), "utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			if (!TextUtils.isEmpty(decoded)) {
				String s = decoded.substring(decoded.indexOf("s=") + 2, decoded.length());
				try {
					JSONObject jo = new JSONObject(s);
					String uname = jo.get("uname").toString();
					Log.d(TAG, "uanme from cookie: " + uname);
					((CSApplication) getApplication()).uname = uname;
					startICometService(uname);
					turnToMainActivity();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}

		mNameEditText = (EditText) findViewById(R.id.login_name);

		mSubmitButton = (Button) findViewById(R.id.login_submit_bt);
		mSubmitButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String uname = mNameEditText.getText().toString().trim();
				if (TextUtils.isEmpty(uname)) {
					UIUtils.showToast(LoginActivity.this, "Username can not be empty!");
					return;
				}
				doLogin(uname);
			}
		});

	}

	private void startICometService(String uname) {
		Intent service = new Intent(getApplicationContext(), ICometService.class);
		service.putExtra("uname", uname);
		getApplication().startService(service);
	}

	private void turnToMainActivity() {
		Intent intent = new Intent(getApplicationContext(), MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		getApplicationContext().startActivity(intent);
		LoginActivity.this.finish();
	}

	protected void doLogin(final String uname) {
		mProgressDialog = UIUtils.showProgressDialog(LoginActivity.this, "");
		AsyncHttpClient client = new AsyncHttpClient();
		client.setEnableRedirects(false);
		RequestParams params = new RequestParams();
		params.put("uname", uname);
		client.post(LoginActivity.this, CSConstant.BASE_URL + "/login.php", params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				super.onSuccess(arg0, arg1, arg2);
				dismissDialog();
				/*for (Header h : arg1) {
					Log.d("SU_HEADER: ", "name: " + h.getName() + "  value: " + h.getValue());
				}
				Log.d("success content: ", new String(arg2));*/
				Log.d(TAG, "success");
				String cookie;
				for (Header h : arg1) {
//					Log.d(TAG, "HEADER_NAME: " + h.getName());
					if (h.getName().equalsIgnoreCase("SET-COOKIE")) {
						String value = h.getValue();
						cookie = value.substring(0, value.indexOf(";")).trim();
						try {
							Log.d(TAG, URLDecoder.decode(URLDecoder.decode(cookie, "utf-8"), "utf-8"));
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
						SPUtils.putString(getApplicationContext(), "cookie", cookie);
						((CSApplication) getApplication()).uname = uname;
					}
				}
				startICometService(uname);
				turnToMainActivity();
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				super.onFailure(arg0, arg1, arg2, arg3);

				if (arg0 == 302) {
					onSuccess(arg0, arg1, arg2);
					return;
				}

				arg3.printStackTrace();
				dismissDialog();
				Log.d("FAIL", "FAIL " + arg0);
			}
		});

	}

	private void dismissDialog() {
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}
	}

}

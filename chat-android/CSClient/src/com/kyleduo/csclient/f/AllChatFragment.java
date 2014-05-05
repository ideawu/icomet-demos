package com.kyleduo.csclient.f;

import com.kyleduo.csclient.R;
import com.kyleduo.csclient.adapter.AllChatListAdapter;

public class AllChatFragment extends ContactFragment {

	@Override
	public void prepareAdapter() {
		mAdapter = new AllChatListAdapter(getActivity(), mUserList);
	}

	@Override
	public String getType() {
		return "all";
	}

	@Override
	public int getlayout() {
		return R.layout.fragment_all_chat;
	}

}

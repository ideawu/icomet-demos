package com.kyleduo.csclient.f;

import com.kyleduo.csclient.R;
import com.kyleduo.csclient.adapter.AllChatListAdapter;

public class RecentChatFragment extends ContactFragment {

	@Override
	public String getType() {
		return "recent";
	}

	@Override
	public void prepareAdapter() {
		mAdapter = new AllChatListAdapter(getActivity(), mUserList);
	}

	@Override
	public int getlayout() {
		return R.layout.fragment_all_chat;
	}
}

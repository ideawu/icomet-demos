package com.kyleduo.csclient.obj;

import java.io.Serializable;
import java.util.List;

public class ChatListJsonBean implements Serializable {

	private static final long serialVersionUID = -1944643092441379601L;
	public int errno;
	public String errmsg;
	public List<UserObj> data;

	public static class UserObj implements Serializable {

		private static final long serialVersionUID = 791214976989074690L;
		public String name;
		public int unread;

		@Override
		public boolean equals(Object o) {
			if (o instanceof UserObj) {
				return false;
			}
			if (((UserObj) o).name.equals(this.name)) {
				return true;
			}
			return false;
		}

		@Override
		public String toString() {
			return "UserObj [uname=" + name + ", unread=" + unread + "]";
		}

	}

	@Override
	public String toString() {
		return "ChatListJsonBean [errno=" + errno + ", errmsg=" + errmsg + ", data=" + data + "]";
	}

}

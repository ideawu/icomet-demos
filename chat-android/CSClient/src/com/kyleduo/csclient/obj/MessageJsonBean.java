package com.kyleduo.csclient.obj;

import java.io.Serializable;
import java.util.List;

import com.kyleduo.icomet.message.Message.Content;

public class MessageJsonBean implements Serializable {

	private static final long serialVersionUID = -2451630863892469576L;

	public int errno;
	public String errmsg;
	public List<MessageObj> data;

	public static class MessageObj implements Serializable {

		private static final long serialVersionUID = -9162099008091518534L;

		public String time;
		public String from;
		public String text;
		public String id;
		public int unread;
		
		public MessageObj(Content content) {
			this.time = content.time;
			this.from = content.from;
			this.text = content.text;
			this.id = content.id;
			this.unread = 1;
		}
		
		public MessageObj() {
			
		}

		@Override
		public boolean equals(Object o) {
			if (!(o instanceof MessageObj)) {
				return false;
			}
			if (((MessageObj) o).id.equals(this.id)) {
				return true;
			}

			return false;
		}

		@Override
		public String toString() {
			return "MessageObj [time=" + time + ", from=" + from + ", text=" + text + ", id=" + id + ", unread=" + unread + "]";
		}

	}

	@Override
	public String toString() {
		return "MessageJsonBean [errno=" + errno + ", errmsg=" + errmsg + ", data=" + data + "]";
	}

}

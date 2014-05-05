package com.kyleduo.csclient.obj;

import java.io.Serializable;

import com.kyleduo.csclient.obj.MessageJsonBean.MessageObj;

public class SendResponseJsonBean implements Serializable {

	private static final long serialVersionUID = -3299280905890406685L;

	public int errno;
	public String errmsg;
	public MessageObj data;

	@Override
	public String toString() {
		return "SendResponseJsonBean [errno=" + errno + ", errmsg=" + errmsg + ", data=" + data + "]";
	}

}

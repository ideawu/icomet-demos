<?php
require_once(dirname(__FILE__) . '/include/UserSvc.php');
$uc = new UserSvc();
$user = $uc->auth();
if(!$user || !$user['uname']){
	header('Location: ./login.php');
	die('Not login!');
}
?>
<?php include(dirname(__FILE__) . '/header.php'); ?>
<style>
#contacts{
	float: left;
	border: 1px solid #66f;
}
#contacts .tabs{
	height: 20px;
}
#contacts .tabs span{
	float: left;
	margin: 2px;
	padding: 4px;
	width: 70px;
	border: 1px solid #ccc;
	cursor: pointer;
	background: #eee;
}
#contacts .list{
	overflow: auto;
	height: 400px;
	width: 100%;
}
#contacts .list li:hover{
	cursor: pointer;
	background: #ffc;
}

#chat{
	float: left;
	margin-left: 10px;
	padding: 4px;
	border: 1px solid #66f;
}
#chat .talkwith .user{
	font-weight: bold;
}
#chat .messages_div{
	margin-top: 6px;
	width: 462px;
	height: 320px;
	border: 1px solid #999;
	overflow: auto;
}
#chat .messages{
	padding: 4px;
}
#chat .widget{
	margin-top: 6px;
}
#chat .widget .text{
	float: left;
	margin: 0;
	width: 380px;
	height: 60px;
	border: 1px solid #999;
}
#chat .widget .send:hover{
	background: #9f9;
}
#chat .widget .send{
	float: left;
	font-size: 16px;
	text-align: center;
	margin: 0 0 0 6px;
	padding: 20px 0;
	width: 76px;
	background: #cfc;
	border: 1px solid #999;
}

#chat .msg{
	margin: 0 0 15px 0;
}
#chat .sending{
	color: #666;
	background: #eee url('./img/loading.gif') no-repeat;
	border: 1px dashed #66f;
}
#chat .fail{
	color: #f00;
	background: #fff;
	border: 1px solid #f66;
}
#chat .msg .from{
	font-weight: bold;
}
#chat .msg .time{
	color: #3c3;
	margin-left: 10px;
}
#chat .msg .text{
	margin: 6px 0 0 10px;
}
</style>

<div id="contacts">
	<div class="tabs">
		<span class="all">All</span>
		<span class="recent">Recent</span>
	</div>
	<div class="list">
	</div>
</div>

<div id="chat">
	<div class="talkwith">
		Logged in as <b><?php echo $user['uname'];?></b>, <a href="login.php?act=logout">logout</a>
		<br/>
		chat with <span class="user">abc</span>:
	</div>
	<div class="messages_div"><div class="messages"></div></div>
	<div class="widget">
		<textarea class="text"></textarea>
		<button class="send">Send</button>
	</div>
</div>

<div style="float: left; margin-left: 20px;">
	<p>Download <a href="https://github.com/ideawu/icomet-demos/blob/master/chat-android/CSClient.apk?raw=true" target="_blank">Android APK</a>.</p>
	<div id="qrcode">
	</div>
</div>

<script>
$(function(){
	$('#qrcode').qrcode({
		width	: 150,
		height	: 150,
		text	: "https://github.com/ideawu/icomet-demos/blob/master/chat-android/CSClient.apk?raw=true"
	});	
});
</script>



<script>
var url_base;
var sub_url;
var n = location.href.match(/^(http[s]?:\/\/.*)\/[^\/]*/);
if (n && n.length == 2) {
	url_base = n[1];
	n = location.href.match(/^http[s]?:\/\/([^\/]*)\//);
	var ps = n[1].split(':');
	sub_url = 'http://' + ps[0] + ':8100/poll'
}

function MessageBox(dom){
	var self = this;
	self.dom = $(dom);
		
	self.clear = function(){
		self.dom.empty();
	}
		
	function msg_to_html(m){
		var html = '<div class="msg">';
		html += '<span class="from">' + m.from + '</span>';
		html += '<span class="time">' + m.time + '</span>';
		html += '<div class="text">' + m.text + '</div>';
		html += '</div>';
		return html;
	}
		
	self.append = function(m){
		self.dom.append(msg_to_html(m));
		self.dom.parent().scrollTop(self.dom[0].scrollHeight);
	}
	
	self.show = function(messages){
		self.clear();
		for(var i=0; i<messages.length; i++){
			var m = messages[i];
			msgBox.append(m);
		}
	}
	
	self.beginSend = function(msg){
		var html = $(msg_to_html(msg));
		html.addClass('sending');
		//html = '<div class="sending">' + html + '</div>';
		self.dom.append(html);
		self.dom.parent().scrollTop(self.dom[0].scrollHeight);
	}
	
	self.endSend = function(){
		self.dom.find('.sending').remove();
	}
	
	self.sendFail = function(id){
		self.dom.find('.sending').removeClass('sending').addClass('fail')
		.append('<div>(Failed to send this message)</div>');
	}
}
	
function ContactList(dom){
	var self = this;
	self.dom = $(dom);
	
	self.onchange = null;
	
	function make_html(user){
		var html = '';
		html += '<li user="' + user + '">';
		html += '<span>' + user + '</span>';
		html += '<span class="unread" count="0" style="color: #f00;"></span>';
		html += '</li>';
		return html;
	}
	
	self.append = function(user){
		var html = make_html(user);
		self.dom.find('ul').append(html);
		bind_event();
	}
	
	self.prepend = function(user){
		var html = make_html(user);
		self.dom.find('ul').prepend(html);
		bind_event();
	}
	
	function bind_event(){
		self.dom.find('li').click(function(){
			self.dom.find('li').removeClass('on').css('background', 'none');
			$(this).addClass('on').css('background', '#6cf');
			if(self.onchange){
				$(this).find('.unread').attr('count', 0).html('');
				var uid2 = $(this).attr('user');
				self.onchange(uid2);
			}
		});
	}
		
	self.show = function(contacts){
		var html = '<ul>';
		for(var i=0; i<contacts.length; i++){
			var c = contacts[i];
			html += make_html(c.name);
		}
		self.dom.html(html);
		bind_event();
		self.dom.find('li:first').click();
	}
	
	self.onNewMessage = function(msg){
		// 消息只显示在对应的聊天窗口
		var li = self.dom.find('li[user=' + msg.from + ']');
		if(li.length == 0){
			// refresh recent contacts list
			self.prepend(msg.from);
			li = self.dom.find('li[user=' + msg.from + ']');
		}
		var c = li.find('.unread');
		if(li.hasClass('on')){
			c.attr('count', 0);
			c.html('');
			//
			msgBox.append(msg);
		}else{
			var count = parseInt(c.attr('count')) + 1;
			c.attr('count', count);
			c.html('(' + count + ')');
		}
	}
}

$(function(){
	msgBox = new MessageBox('#chat .messages');
	contactList = new ContactList('#contacts .list');
	
	// 当激活聊天窗口时, 从持久化存储中(而不是从 icomet 的缓存队列中)获取历史消息
	contactList.onchange = function(uid2){
		$('#chat .talkwith .user').html(uid2);
		var url = url_base + '/api/messages.php';
		var params = {'with': uid2, 'size': 10, 'max_msg_id': ''};
		$.getJSON(url, params, function(resp){
			resp.data.reverse();
			msgBox.show(resp.data);
		});
	}

	// TODO: load unread messages on init

	$('#contacts .tabs span').click(function(){
		$('#contacts .tabs span').css('background', '#ddd');
		$(this).css('background', '#3cf');
	});
	$('#contacts .tabs .all').click(function(){
		var url = url_base + '/api/contacts.php?type=all';
		$.getJSON(url, function(resp){
			contactList.show(resp.data);
		});
	});
	$('#contacts .tabs .recent').click(function(){
		var url = url_base + '/api/contacts.php?type=recent';
		$.getJSON(url, function(resp){
			contactList.show(resp.data);
		});
	});
	
	$('#contacts .tabs .recent').click();
	
	
	$('#chat .widget .text').keypress(function (e) { 
		if(e.keyCode == 13 && !e.shiftKey){
			$('#chat .widget .send').click();
			return e.preventDefault();
		}
	});

	$('#chat .widget .send').click(function(){
		var text = $('#chat .widget .text').val();
		var uid2 = $('#chat .talkwith .user').html();
		if(text.length == 0 || uid2.length == 0){
			return false;
		}
		
		var time = new Date();
		var msg = {'from': uid2, 'time': time, 'text': text};
		
		msgBox.beginSend(msg);
		$('#chat .widget .text').val('');

		var url = url_base + '/api/send.php';
		var params = {'uid2': uid2, 'text': text};
		$.post(url, params, function(resp){
			if(resp.errno != 0){
				msgBox.sendFail();
				return;
			}
			msgBox.endSend();
			msgBox.append(resp.data);
		}, 'json');
	});
	
	var comet;
	var conf = {
		channel: <?php echo json_encode($user['uname']); ?>,
		signUrl: url_base + '/api/sign_comet.php',
		subUrl: sub_url,
		callback: function(content){
			var msg = JSON.parse(content);
			contactList.onNewMessage(msg);
		}
	};
	comet = new iComet(conf);
});
</script>


<div style="width:100px; clear: both;"></div>


<?php include(dirname(__FILE__) . '/footer.php'); ?>

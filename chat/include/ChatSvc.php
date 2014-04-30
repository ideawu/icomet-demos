<?php
require_once(dirname(__FILE__) . '/../include/SSDB.php');
class ChatSvc
{
	private $ssdb = null;

	function __construct(){
		$host = '127.0.0.1';
		$port = 8888;
		$this->ssdb = new SimpleSSDB($host, $port);
	}

	private function chat_key($u1, $u2){
		$arr = array($u1, $u2);
		sort($arr);
		return 'chat|' . join(',', $arr);
	}

	private function id_gen(){
		$new_id = $this->ssdb->incr('msg_id_gen');
		return time() . '_' . $new_id;
	}

	private function recent_contacts_key($uid){
		return 'recent_contacts|' . $uid;
	}

	private function talk($chat_key, $from, $text){
		$msg = array(
				'time' => time(),
				'from' => $from,
				'text' => $text,
				);
		$msg_id = $this->id_gen();
		$msg_str = json_encode($msg);
		$this->ssdb->hset($chat_key, $msg_id, $msg_str);

		$msg['id'] = $msg_id;
		return $msg;
	}

	function send($from, $to, $text){
		$chat_key = $this->chat_key($from, $to);
		$msg = $this->talk($chat_key, $from, $text);
		$this->ssdb->zset($this->recent_contacts_key($from), $to, time());
		$this->ssdb->zset($this->recent_contacts_key($to), $from, time());
		return $msg;
	}

	function setReadPosition($uid, $uid2, $msg_id){
		$chat_key = $this->chat_key($uid, $uid2);
		$pos_key = $chat_key . '|read_pos';
		$this->ssdb->hset($pos_key, $uid, $msg_id);
	}

	function getReadPosition($uid, $uid2){
		$chat_key = $this->chat_key($uid, $uid2);
		$pos_key = $chat_key . '|read_pos';
		return $this->ssdb->hget($pos_key, $uid);
	}

	/**
	 *  return messages that are before max_msg_id
	 */
	function listMessages($uid, $uid2, $size, $max_msg_id=''){
		$read_pos = $this->getReadPosition($uid, $uid2);
		$chat_key = $this->chat_key($uid, $uid2);
		$kvs = $this->ssdb->hrscan($chat_key, $max_msg_id, '', $size);
		$ret = array();
		foreach($kvs as $msg_id=>$str){
			$msg = json_decode($str, true);
			$msg['id'] = $msg_id;
			$msg['time'] = date('Y-m-d H:i:s', $msg['time']);
			$msg['unread'] = strcmp($read_pos, $msg_id) < 0? 1 : 0;
			$ret[] = $msg;
		}
		return $ret;
	}

	function listRecentContacts($uid, $size){
		$key = $this->recent_contacts_key($uid);
		$kvs = $this->ssdb->zrscan($key, '', '', '', $size);
		$keys = array_keys($kvs);
		return $keys;
	}

}



<?php
require_once(dirname(__FILE__) . '/SSDB.php');

class UserSvc
{
	private $ssdb = null;
	private $seed = 'dsf0$32_+[[;sa7239';
	private $domain = '';

	function __construct(){
		$host = '127.0.0.1';
		$port = 8888;
		$this->ssdb = new SimpleSSDB($host, $port);
		
		static $init = false;
		if(!$init){
			$init = true;
			if(get_magic_quotes_gpc()){
				foreach($_COOKIE as $k=>$v){
					$_COOKIE[$k] = stripslashes($v);
				}
			}
		}
	}
	
	private function encrypt($data){
		return json_encode($data);
	}   

	private function decrypt($data){
		return json_decode($data, true);
	}   
	
	//function register($uname, $password){
	//}
	
	function listUsers(){
		$uids = $this->ssdb->zrscan('all_users', '', '', '', 50);
		$uids = array_keys($uids);
		return $uids;
	}
	
	function login($uname, $password, $ttl=8640000){
		// add yo all_users list
		$this->ssdb->zset('all_users', $uname, time());
		
		$expire = time() + $ttl;
		$session = array(
			'uname' => $uname,
			'expire' => $expire,
			);
		$s = $this->encrypt($session);
		$e = $expire;
		$t = md5($this->seed . $e . $s);
		$str = http_build_query(array(
					'e' => $e,
					't' => $t,
					's' => $s
					));
		@setcookie('S', $str, $expire, '/', $this->domain);
		// 让 cookie 立即生效, 因为使用者在 saveSessionInCookie() 之后
		// 会立即调用 getSessionFromCookie.
		$_COOKIE['S'] = $str;
		return $session;
	}
	
	function logout(){
		@setcookie('S', '', 1, '/', $this->domain);
		unset($_COOKIE['S']);
	}
	
	function auth(){
		$str = $_COOKIE['S'];
		@parse_str($str, $arr);
		if(!is_array($arr) || !$arr['t'] || !$arr['s'] || !$arr['e']){
			return array();
		}
		if($arr['e'] < time()){
			return array();
		}
		$m = md5($this->seed . $arr['e'] . $arr['s']);
		if($m !== $arr['t']){
			return array();
		}
		$session = $this->decrypt($arr['s']);
		return $session;
	}

}

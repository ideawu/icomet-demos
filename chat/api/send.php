<?php
require_once(dirname(__FILE__) . '/../include/UserSvc.php');
$uc = new UserSvc();
$user = $uc->auth();
if(!$user || !$user['uname']){
	die('Not login!');
}
?>
<?php
require_once(dirname(__FILE__) . '/../include/ChatSvc.php');
$resp = array(
	'errno' => 0,
	'errmsg' => '',
	'data' => array(),
	);

$uid2 = $_REQUEST['uid2'];
$text = $_REQUEST['text'];
$uid2 = htmlspecialchars(trim($uid2));
$text = htmlspecialchars(trim($text));

if(!strlen($uid2) || strlen($uid2) > 32 || !strlen($text)){
	$resp['errno'] = 1;
	$resp['errmsg'] = 'Bad parameter!';
}else if(strlen($text) > 1000){
	$resp['errno'] = 1;
	$resp['errmsg'] = 'content too long!';
}else{
	$uid = $user['uname'];
	$svc = new ChatSvc();
	$msg = $svc->send($uid, $uid2, $text);
	
	$msg['time'] = date('Y-m-d H:i:s', $msg['time']);
	comet_push($uid2, json_encode($msg));
	
	$resp['data'] = $msg;
}

echo json_encode($resp);

function comet_push($cname, $content){
	$cname = urlencode($cname);
	$content = urlencode($content);
	$url = "http://127.0.0.1:8000/push?cname=$cname&content=$content";
	$ch = curl_init($url) ;
	curl_setopt($ch, CURLOPT_HEADER, 0);
	curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1) ;
	$result = curl_exec($ch) ;
	curl_close($ch) ;
	return $result;
}


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

$size = intval($_GET['size']);
if($size <= 0 || $size > 20){
	$size = 10;
}
$uid2 = htmlspecialchars($_GET['with']);

$uid = $user['uname'];
$svc = new ChatSvc();

$resp['data'] = $svc->listMessages($uid, $uid2, $size);

echo json_encode($resp);

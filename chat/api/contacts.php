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

$all_contacts = array(
	array('name' => 'kf_1', 'unread' => 0),
	array('name' => 'kf_2', 'unread' => 1),
	array('name' => 'kf_3', 'unread' => 0),
	);

$resp = array(
	'errno' => 0,
	'errmsg' => '',
	'data' => array(),
	);

if($_GET['type'] == 'all'){
	$uc = new UserSvc();
	$names = $uc->listUsers();
	$contacts = array();
	foreach($names as $name){
		$contacts[] = array(
			'name' => $name,
			'unread' => 0,
			);
	}
	$resp['data'] = $contacts;
}else{
	$uid = $user['uname'];
	$svc = new ChatSvc();
	$uids = $svc->listRecentContacts($uid, 20);
	$recent_contacts = array();
	foreach($uids as $uid){
		$recent_contacts[] = array(
			'name' => $uid,
			'unread' => 0,
		);
	}
	$resp['data'] = $recent_contacts;
}
#usort($resp['data'], 'sort_func');

echo json_encode($resp);

function sort_func($a, $b){
	return $b['unread'] - $a['unread'];
}

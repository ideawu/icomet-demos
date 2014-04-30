<?php
include('ChatSvc.php');

$svc = new ChatSvc();
$svc->send('a', 'kf_1', 'hi ' . date('Y-m-d'));
#$svc->send('b', 'kf_2', 'hi ' . date('Y-m-d'));
#$svc->send('b', 'kf_3', 'hi ' . date('Y-m-d'));

$ret = $svc->listMessages('a', 'kf_1', 10);
var_dump($ret);


<?php
$errmsg = '';
if($_POST){
	require_once(dirname(__FILE__) . '/include/UserSvc.php');
	$uname = trim($_POST['uname']);
	if(!preg_match('/^[0-9a-z_]+$/i', $uname)){
		$errmsg = 'bad user name!';
	}
	if(!$errmsg){
		$uc = new UserSvc();
		$uc->login($uname, '');
		header('Location: ./');
		die();
	}
}
?>
<?php include(dirname(__FILE__) . '/header.php'); ?>

<?php if($errmsg){ ?>
	<div style="color: #f00;">
		<?php echo $errmsg; ?>
	</div>
<?php } ?>

<form method="post">
	<table style="width: 240px; margin: 0 auto;">
		<tr>
			<td width="80">User Name:</td>
			<td><input type="text" name="uname" style="width: 120px;" /></td>
		</tr>
		<tr>
			<td></td>
			<td><input type="submit" value="Login" /></td>
		</tr>
	</table>
</form>

<?php include(dirname(__FILE__) . '/footer.php'); ?>

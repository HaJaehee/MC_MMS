<?php
$mrn = $_GET['mrn'];
$ip = $_GET['ip'];
$port = $_GET['port'];
$model = $_GET['model'];
$mms_url = $_GET['mms_url'];
$arrContextOptions=array(
    "ssl"=>array(
        "verify_peer"=>false,
        "verify_peer_name"=>false,
    ),
);  
$data = file_get_contents($mms_url.'/add-mns-entry?mrn='.$mrn.'&ip='.$ip.'&port='.$port.'&model='.$model, false, stream_context_create($arrContextOptions));
echo $data;
?>
<?php
$id = $_GET['id'];
$mms_url = $_GET['mms_url'];
$arrContextOptions=array(
    "ssl"=>array(
        "verify_peer"=>false,
        "verify_peer_name"=>false,
    ),
);  
$data = file_get_contents($mms_url.'/remove-id-realtime-log-ids?id='.$id, false, stream_context_create($arrContextOptions));
echo $data;
?>
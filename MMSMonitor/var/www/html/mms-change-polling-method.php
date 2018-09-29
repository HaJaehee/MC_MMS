<?php
$method = $_GET['method'];
$svc_mrn = $_GET['svcMRN'];
$mms_url = $_GET['mms_url'];
$arrContextOptions=array(
    "ssl"=>array(
        "verify_peer"=>false,
        "verify_peer_name"=>false,
    ),
);  
$data = file_get_contents($mms_url.'/polling?method='.$method.'&svcMRN='.$svc_mrn, false, stream_context_create($arrContextOptions));
echo $data;
?>
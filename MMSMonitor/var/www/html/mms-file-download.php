<?php
$directory = $_GET['directory'];
$file = $_REQUEST['file'];

$filepath = $directory.$file;
$path_parts = pathinfo($filepath);
$filename = $path_parts['basename'];
$filesize = filesize($filepath);


header("Pragma: public");
header("Expires: 0");
header("Content-Type: application/octet-stream");
header("Content-Disposition: attachment; filename=\"$filename\"");
header("Content-Transfer-Encoding: binary");
header("Content-Length: $filesize");

ob_clean();
flush();
readfile($filepath);
?>
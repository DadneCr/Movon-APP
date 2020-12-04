<?php
//Conexión a localhost
$conn = new mysqli('127.0.0.1', 'root', '', 'movon_tracking');

if($conn->connect_error){
  echo $error->$conn->connect_error;
}
$conn->set_charset("utf8");
//Conexión a servidor
// $conn = new mysqli('127.0.0.1', 'movoncom_admin_c', 't&!dCkUkf@6p', 'movoncom_movon_tracking');
//
// if($conn->connect_error){
//   echo $error->$conn->connect_error;
// }
?>

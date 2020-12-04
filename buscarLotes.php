<?php
include_once 'conexiones.php';
$registro = $_GET["registro"];
//$codigo= 123;
try{
    $sql = "SELECT COUNT(*) FROM lotes where id_registro = $registro " ;
    $consulta = $conn->query($sql);
   
}catch(Exception $e){
    echo $e->getMessage();
}
        while($row = $consulta->fetch_array()){
       
            $producto[] = array_map('utf8_encode', $row);
        }

echo json_encode($producto);
$consulta-> close();

?>


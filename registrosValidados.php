<?php
include_once 'conexiones.php';
$guia = $_GET["guia"];
//$codigo= 123;
$i = 0;
try{
    $sql = "SELECT COUNT(*) from registros WHERE registros.guia = '$guia' and registros.id_status =1" ;
    $consulta = $conn->query($sql);
   
}catch(Exception $e){
    echo $e->getMessage();
}
        while($row = $consulta->fetch_array()){
            //$lote[$i] =  $row[0];
            $resultado[] = array_map('utf8_encode', $row);

        }

//echo json_encode($lote);
echo json_encode($resultado);

$consulta-> close();

?>

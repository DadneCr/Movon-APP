<?php
include_once 'conexiones.php';
$guia = $_GET["guia"];
$lote = $_GET["lote"];
//$codigo= 123;
$i = 0;
try{
    $sql = "SELECT COUNT(*) FROM lotes INNER JOIN registros on registros.id_registro = lotes.id_registro
    where registros.guia = '$guia' and  lotes.validado=0 and num_serie = '$lote'" ;
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

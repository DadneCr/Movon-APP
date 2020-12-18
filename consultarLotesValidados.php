
<?php
include_once 'conexiones.php';
$guia = $_GET["guia"];
//$codigo= 123;
$i = 0;
try{
    $sql = "SELECT num_serie FROM lotes INNER JOIN registros on registros.id_registro = lotes.id_registro
    where registros.guia = '$guia' and  lotes.validado=1" ;
    $consulta = $conn->query($sql);
   
}catch(Exception $e){
    echo $e->getMessage();
}
        while($row = $consulta->fetch_array()){
            //$lote[$i] =  $row[0];
            $producto[] = array_map('utf8_encode', $row);

            $i++;
        }

//echo json_encode($lote);
echo json_encode($producto);

$consulta-> close();

?>


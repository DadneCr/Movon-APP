
<?php
include_once 'conexiones.php';
$guia = $_GET["guia"];
//$codigo= 123;
try{
    $sql = "SELECT COUNT(*) FROM lotes INNER JOIN registros on registros.id_registro = lotes.id_registro
    where registros.guia = '$guia' and  lotes.validado=0" ;
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


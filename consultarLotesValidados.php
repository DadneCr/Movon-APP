
<?php
include_once 'conexiones.php';
$guia = $_GET["guia"];
$num_serie = $_GET["lote"];
$item = $_GET['lote'];
$lotes = explode(" ", $item);
$validado = 1;
$datos_incorrectos = 0;
try{
    $sql = "SELECT id_registro from registros where registros.guia = '$guia'" ;
    $consulta = $conn->query($sql);
   
}catch(Exception $e){
    echo $e->getMessage();
}
        while($row = $consulta->fetch_array()){
       
            $id_registro= $row[0];
        }
try{

    $sql = "SELECT lotes.id_lote FROM lotes INNER JOIN
    registros on lotes.id_registro = registros.id_registro
    WHERE lotes.num_serie=TRIM('$num_serie') and lotes.id_registro=$id_registro and lotes.validado=0 LIMIT 1";

    $consulta = $conn->query($sql);



  }catch(Exception $e){
    echo $e->getMessage();
  }

  if($consulta and $consulta->num_rows==1){
    // el lote está en la base de datos
    //$resultados['loteNum'.$i] = "Validado";

    try{

      $stmt = $conn->prepare("UPDATE lotes SET validado=? WHERE num_serie=? and id_registro=?");
      $stmt->bind_param('isi', $validado, $num_serie, $id_registro);

      $stmt->execute();
      $stmt->close();
      $stmt=null;

    }catch(Exception $e){
      echo $e->getMessage();
    }

    if($i == (count($lotes)-1)){


      try{

        $sql = "SELECT COUNT(*) FROM lotes WHERE id_registro=$id_registro and datos=1 and validado=0";
        $consulta_validacion = $conn -> query($sql);

        while($row = $consulta_validacion->fetch_row()){

            if($row[0]==0){

                $id_status = 1;

                $stmt = $conn->prepare("UPDATE registros SET id_status=? WHERE id_registro=?");
                $stmt->bind_param('ii', $id_status, $id_registro);
                  echo $id_registro;
                $stmt->execute();
                $stmt->close();
                $stmt=null;

                $resultados['guia'] = $guia;

                $sql2 = "SELECT usuarios.nombres, usuarios.apellidos, usuarios.correo, registros.fecha
                FROM registros INNER JOIN usuarios on registros.id_usuario = usuarios.id_usuario
                WHERE registros.id_registro = $id_registro";

                $consulta_pcliente = $conn->query($sql2);

                while($row3 = $consulta_pcliente->fetch_row()){
                  $nombre_cliente = $row3[0];
                  $apellido_cliente = $row3[1];
                  $correo_cliente = $row3[2];
                  $fecha_cliente = $row3[3];
                }

                // Enviar correo al cliente
                include_once 'includes/funciones/mail_send.php';

                $alias_correo = 'Cliente Movon tracking';
                $title_text = 'Items recibidos';
                $nombre_completo = "$nombre_cliente $apellido_cliente";
                $main_text = "<img alt='header' src='cid:mi-fondo' width='300'><br><b style='color: #2883C5;'>Estimado</b> <br>
                <b style='color:#D61931;'>$nombre_completo.</b><br> tu registro se ha recibido con éxito el <b>$fecha_cliente</b>. <br>
                              El número de guia con el que podrás rastrear tu envío es el <b>$guia</b>";


                mandarMail($correo_cliente, $alias_correo, $title_text, $main_text);

            }

        }

      }catch(Exception $e){
        echo $e->getMessage();
      }

    }


  }
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


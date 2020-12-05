<?php
  include_once 'conexiones.php';

$id_registro = 20120419383;

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
?>
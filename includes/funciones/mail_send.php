<?php

//Secciones y archivos para el CORREO
use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\Exception;
include 'Exception.php';
include 'PHPMailer.php';
include 'SMTP.php';

  function mandarMail($correo, $alias_correo, $title_text, $main_text, $imagenes=0, $archivo_subir=NULL){
    $msg = '';
    $mail = new PHPMailer(true);

    try {
        //Server settings
        //Server settings
        $mail->SMTPDebug = 0;                                       // Enable verbose debug output
        $mail->isSMTP();                                            // Set mailer to use SMTP
        $mail->Host       = 'smtp.gmail.com';                       // Specify main and backup SMTP servers
        $mail->SMTPAuth   = true;                                   // Enable SMTP authentication
        $mail->Username   = 'funciones@carbe.mx';                     // SMTP username
        $mail->Password   = 'M3rcur10.1982+$';                               // SMTP password
        $mail->SMTPSecure = 'tls';                                  // Enable TLS encryption, `ssl` also accepted
        $mail->Port       = 587;                                    // TCP port to connect to
                                  // TCP port to connect to

        //Recipients
        $mail->setFrom('funciones@carbe.mx', 'Movon Tracking'); //Emisor
        $mail->addAddress($correo, $alias_correo);     // Receptor


        // Content
        $mail->isHTML(true);                                  // Set email format to HTML
        $mail->Subject = $title_text;
        ///$mail->AddEmbeddedImage("../../imagenes/logo_principal_mejorado.png", "mi-fondo", "Movon Tracking"); //meter imagen de header
        $mail->Body    = $main_text;

        if($imagenes!=0){
          //Attach multiple files one by one
          for ($ct = 0; $ct < count($archivo_subir['tmp_name']); $ct++) {
              $uploadfile = tempnam(sys_get_temp_dir(), hash('sha256', $archivo_subir['name'][$ct]));
              $filename = $archivo_subir['name'][$ct];
              //var_dump($archivo_subir['name']);
              if (move_uploaded_file($archivo_subir['tmp_name'][$ct], $uploadfile)) {
                  $mail->addAttachment($uploadfile, $filename);
              }
          }
        }

        // Activo condificación utf-8
        $mail->CharSet = 'UTF-8';

        $mail->send();
        //echo 'El mensaje se envío correctamente';
    } catch (Exception $e) {
        echo "No se ha podido enviar el correo. Mailer Error: {$mail->ErrorInfo}";
    }

  }



 ?>

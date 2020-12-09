<?php
  include_once 'conexiones.php';

  $correo = $_GET['correo'];
  $password = $_GET['password'];
  $password_segura = password_hash($password, PASSWORD_BCRYPT, ['cost'=>4]);


  $sql = "SELECT * FROM usuarios where correo='$correo' LIMIT 1";
  $login = $conn->query($sql);

  $sql1= "SELECT COUNT(*) FROM usuarios where correo='$correo' and 'password' = '$password_segura' LIMIT 1";
  $error = $conn->query($sql1);

  if($login and $login->num_rows == 1){
    $user = $login->fetch_assoc();
    $verify = password_verify($password, $user['password']);

    if($verify){
        
        $sql1 = "SELECT COUNT(*) FROM usuarios where correo='$correo' LIMIT 1";
        $consulta = $conn->query($sql1);
        while($row = $consulta->fetch_array()){
       
            $respuesta[] = array_map('utf8_encode', $row);
        }
        
    }else{
        while($row = $error->fetch_array()){
       
            $respuesta[] = array_map('utf8_encode', $row);
        }
    }

}else{
    while($row = $error->fetch_array()){
       
        $respuesta[] = array_map('utf8_encode', $row);
    }
}

echo json_encode($respuesta);



?>
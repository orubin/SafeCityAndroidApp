<?php
$con = mysql_connect("62.219.78.224","4779_karali","karali");
if (!$con)
  {
  die('Could not connect: ' . mysql_error());
  }

mysql_select_db("4779-karali", $con);

mysql_query("INSERT INTO Test (latitude, longtitude, id, lastUpdate, status)
VALUES ('$_POST['latitude']', '$_POST['longtitude']', '$_POST['id']', '$_POST['lastUpdate']', '$_POST['status']')");


mysql_close($con);
?>
<?php
	$con = mysql_connect("62.219.78.224","4779_karali","karali");
	if (!$con)
	{
	die('Could not connect: ' . mysql_error());
	}

	mysql_select_db("4779-karali", $con);
	$sql="INSERT INTO Incidents (pic, time, latitude, longtitude, details) VALUES ('$_POST[pic]', '$_POST[time]', '$_POST[latitude]', '$_POST[longtitude]', '$_POST[details]')";
	

	if (!mysql_query($sql,$con))
	{
		die('Error: ' . mysql_error());
	}
	echo "Incident saved";

	mysql_close($con);
	
?>
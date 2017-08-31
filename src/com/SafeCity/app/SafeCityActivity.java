package com.SafeCity.app;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.media.AudioManager;

public class SafeCityActivity extends Activity implements LocationListener {
    
	private LocationManager lm;
	
	private boolean alertOn = false;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sp = this.getSharedPreferences("prefile", 0);
        if(sp.getBoolean("firstTime", true)){
        	//first time!
        	
        	setContentView(R.layout.welcome);
        	SharedPreferences.Editor editor = sp.edit();
        	editor.putBoolean("firstTime", false);
        	editor.commit();
        }
        else{
        	setContentView(R.layout.main);
        }
        
    }
    
    public void alert(View view) throws ClassNotFoundException, SQLException{
    	
    	if(!alertOn){//turn it on
    		
    		alertOn = true;
	    	//start GPS thread
	
	    	ArrayList<String[]> al = new ArrayList<String[]>();
	    	lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
	        boolean gps_enabled = false;
	        try{gps_enabled=lm.isProviderEnabled(LocationManager.GPS_PROVIDER);}catch(Exception ex){}
	        if(gps_enabled)
	        	lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 100, this);
	        boolean network_enabled = false;
	        try{network_enabled=lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);}catch(Exception ex){}
	        if(network_enabled)
	            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
	        
	        //make the call
	        SharedPreferences sp = this.getSharedPreferences("prefile", 0);
	    	String number = sp.getString("phone", null);
	        
	    	
	    	AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
	    	//audioManager.setMode(AudioManager.MODE_IN_CALL);
	    	audioManager.setMode(AudioManager.MODE_NORMAL); 
	    	audioManager.setSpeakerphoneOn(true);
	        Intent call = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
	        startActivityForResult(call,0);
    	}
    	else{//turn it off
    		//request password
    		Intent stopAlert = new Intent(this, StopAlert.class);
	        startActivityForResult(stopAlert,0);
    		lm.removeUpdates(this);
    		
    	}

    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
    	if (requestCode==0){
    		TextView tv = (TextView) findViewById(R.id.textView1);
    		tv.setText("Reporting... Press again to stop");
    		if (data!=null){
    	    	Bundle b = data.getExtras();
        	}
    	}
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.layout.menu, menu);
        return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	case R.id.reportIncident:
    		Intent report = new Intent(this, ReportIncident.class);
        	startActivity(report);
    		return true;    		
    	case R.id.settings:
    		Intent settings = new Intent(this, Settings.class);
    		startActivity(settings);
    		return true;    		
    	case R.id.about:
    		Intent about = new Intent(this, About.class);
    		startActivity(about);
    		return true;    		
    	case R.id.help:
    		Intent help = new Intent(this, About.class);
        	startActivity(help);
    		return true;    
    	case R.id.exit:
    		AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
    		builder2.setMessage("Are you sure you want to exit?");
    		builder2.setCancelable(false);
    		builder2.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
    		           public void onClick(DialogInterface dialog, int id) {
    		        	   SafeCityActivity.this.finish();
    		           }
    		       })
    		       .setNegativeButton("No", new DialogInterface.OnClickListener() {
    		           public void onClick(DialogInterface dialog, int id) {
    		                dialog.cancel();
    		           }
    		       });
    		AlertDialog alert2 = builder2.create();
    		alert2.show();
    		return true;
    	}
    	return false;
	}
    
    
    public void SaveToDB(final String latitude, final String longtitude, final String id, final String lastUpdate, final String status){
    	final ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
    	Thread t = new Thread() {
            public void run() {
            	String result = "";
            	InputStream is = null;
            	//http post
            	try{
            		nameValuePairs.add(new BasicNameValuePair("latitude",latitude));
            		nameValuePairs.add(new BasicNameValuePair("longtitude",longtitude));
            		nameValuePairs.add(new BasicNameValuePair("id",id));
            		nameValuePairs.add(new BasicNameValuePair("lastUpdate",lastUpdate));
        	    	nameValuePairs.add(new BasicNameValuePair("status",status));
        	        HttpClient httpclient = new DefaultHttpClient();
        	        HttpPost httppost = new HttpPost("http://www.karali.co.il/enterData.php");
        	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        	        HttpResponse response = httpclient.execute(httppost);
        	        HttpEntity entity = response.getEntity();
        	        is = entity.getContent();
            	}catch(Exception e){
            		//Toast.makeText(getApplicationContext(), "Error in http connection "+e.toString(), 2).show();
            		e.printStackTrace();
            	}
            	//convert response to string
            	try{
            		BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
            	    StringBuilder sb = new StringBuilder();
            		String line = null;
            		while ((line = reader.readLine()) != null) {
            	                sb.append(line + "n");
            		}
            	    is.close();
            	    result=sb.toString();
            	    System.out.println(result);
            	}catch(Exception e){
            		System.out.println("Error converting result "+e.toString());
            	}
            }
        };
        t.start();
    }

	@Override
	public void onLocationChanged(Location location) {
		if (location != null) {
			double latitude = location.getLatitude();
			double longtitude = location.getLongitude();
			DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			String currentDateTimeString = dateFormat.format(new Date());
			TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
	    	//report location to server
			SaveToDB(Double.toString(latitude), Double.toString(longtitude), telephonyManager.getDeviceId(), currentDateTimeString, "1");
		}
		
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		Toast.makeText( getApplicationContext(), "Gps Disabled", Toast.LENGTH_LONG ).show();
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		
	}
	
//	@Override
//	public void onStop(){
//		lm.removeUpdates(this);
//        lm.removeUpdates(this);
//	}
//	
//	@Override
//	public void onPause(){
//		lm.removeUpdates(this);
//        lm.removeUpdates(this);
//	}
//	
//	@Override
//	public void onResume(){
//		boolean gps_enabled = false;
//        try{gps_enabled=lm.isProviderEnabled(LocationManager.GPS_PROVIDER);}catch(Exception ex){}
//        if(gps_enabled)
//        	lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 100, this);
//        boolean network_enabled = false;
//        try{network_enabled=lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);}catch(Exception ex){}
//        if(network_enabled)
//            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
//	}
	
	public void go(View view){
    	EditText et = (EditText) findViewById(R.id.editText1);
    	String password = et.getText().toString();
    	et = (EditText) findViewById(R.id.editText2);
    	String confirm = et.getText().toString();
    	if(password.length()<1){
    		errorAndClear("The password field is empty!");
    	}
    	else if(confirm.length()<1){
    		errorAndClear("The confirm password field is empty!");
    	}
    	else if(!password.equalsIgnoreCase(confirm)){
    		errorAndClear("The password does not match the confirm field!");
    	}
    	else{
    		SharedPreferences sp = this.getSharedPreferences("prefile", 0);
	    	SharedPreferences.Editor editor = sp.edit();
	    	editor.putString("password", password);
			editor.commit();
			setContentView(R.layout.main);
    	}
	}
	
    public void errorAndClear(String error){
    	Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
		//clear all the fields!
		EditText et1 = (EditText) findViewById(R.id.editText1);
		EditText et2 = (EditText) findViewById(R.id.editText2);
		et1.setText("");
		et2.setText("");
    }
    
}
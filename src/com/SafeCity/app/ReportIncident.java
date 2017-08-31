package com.SafeCity.app;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
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
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class ReportIncident extends Activity implements LocationListener{
	
	private static final int CAMERA_REQUEST = 1888;
	ArrayList<File> files = new ArrayList<File>();
	double latitude ;
	double longtitude ;
	LocationManager lm;
	boolean boo;
	
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reportincident);
    	lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        try{gps_enabled=lm.isProviderEnabled(LocationManager.GPS_PROVIDER);}catch(Exception ex){}
        if(gps_enabled)
        	lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 100, this);
        boolean network_enabled = false;
        try{network_enabled=lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);}catch(Exception ex){}
        if(network_enabled)
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
    	
    }
    
    public void attach(View view){
    	//launch menu
//    	Intent select = new Intent(this, MyCameraActivity.class);
//    	startActivityForResult(select,0);
    	Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE); 
        startActivityForResult(cameraIntent, CAMERA_REQUEST); 
    }
    
    public void send(View view){
    	EditText et = (EditText) findViewById(R.id.editText1);
    	String details = et.getText().toString();
        ImageView imageView = (ImageView)this.findViewById(R.id.imageView1);
        imageView.buildDrawingCache();
        Bitmap photo = (Bitmap) imageView.getDrawingCache();
        
    	sendReport(details, photo);
    	//while boo = null spinner, when done:
    	alert();
 	   this.finish();
    }

	private void sendReport(String details, Bitmap image) {
		ArrayList<NameValuePair> args = new ArrayList<NameValuePair>();
		if(image!=null){
			ByteArrayOutputStream full_stream = new ByteArrayOutputStream();
	        image.compress(Bitmap.CompressFormat.PNG, 100, full_stream);
	        byte[] full_bytes = full_stream.toByteArray();
	        String img_full = Base64.encodeToString(full_bytes, Base64.DEFAULT);
	        args.add(new BasicNameValuePair("pic", img_full));
		}
		else{
			args.add(new BasicNameValuePair("pic", ""));
		}
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		String currentDateTimeString = dateFormat.format(new Date());
        args.add(new BasicNameValuePair("time", currentDateTimeString));
        args.add(new BasicNameValuePair("latitude", Double.toString(latitude)));
        args.add(new BasicNameValuePair("longtitude", Double.toString(longtitude)));
        args.add(new BasicNameValuePair("details", details));
        lm.removeUpdates(this);
        httppost("http://karali.co.il/getIncident.php", args);
	}
	
	@Override
	public void onLocationChanged(Location location) {
		if (location != null) {
			latitude = location.getLatitude();
			longtitude = location.getLongitude();
		}
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
    	if(requestCode==0){
    		if (data!=null){
    			Bitmap bm = (Bitmap) data.getExtras().get("data");
    			OutputStream outStream = null;
    			File file = new File("image");
    			try {
				   outStream = new FileOutputStream(file);
				   bm.compress(Bitmap.CompressFormat.PNG, 100, outStream);
				   outStream.flush();
				   outStream.close();

				   System.out.println("height = "+ bm.getHeight() + ", width = " + bm.getWidth());
    			} 
    			catch (FileNotFoundException e) {
				   e.printStackTrace();
				   System.out.println("FileNotFoundException: "+ e.toString());
				} catch (IOException e) {
				   e.printStackTrace();
				   System.out.println("IOException: "+ e.toString());
				}
        	}
    	}
    	if (requestCode == CAMERA_REQUEST) {  
            Bitmap photo = (Bitmap) data.getExtras().get("data"); 
            ImageView imageView = (ImageView)this.findViewById(R.id.imageView1);
            imageView.setImageBitmap(photo);
            OutputStream outStream = null;
			File file = new File("image");
			try {
			   outStream = new FileOutputStream(file);
			   photo.compress(Bitmap.CompressFormat.PNG, 100, outStream);
			   outStream.flush();
			   outStream.close();

			   System.out.println("height = "+ photo.getHeight() + ", width = " + photo.getWidth());
			   files.add(file);
			} 
			catch (FileNotFoundException e) {
			   e.printStackTrace();
			   System.out.println("FileNotFoundException: "+ e.toString());
			} catch (IOException e) {
			   e.printStackTrace();
			   System.out.println("IOException: "+ e.toString());
			}
        }
    }
	
	private void httppost(final String url, final ArrayList<NameValuePair> args) {
		Thread t = new Thread() {
	        public void run() {
	        	/* Create the channel for communicaton */
	  		  InputStream is = null;

	  		  /* Send request to server */
	  		  try {
	  			    /* Create the POST */
	  			    HttpClient httpclient = new DefaultHttpClient();
	  			    HttpPost httppost = new HttpPost(url);
	  	
	  			    /* Add the login information "POST" variables in the php */
	  			    httppost.setEntity(new UrlEncodedFormEntity(args));
	  	
	  			    /* Execute the http POST and get the response */
	  			    HttpResponse response = httpclient.execute(httppost);
	  			    HttpEntity entity = response.getEntity();
	  			    is = entity.getContent();
	  		  } catch (Exception e) {
	  			    e.printStackTrace();
	  			    boo = false;
	  		  }

	  		  /* Read response from server */
	  		  try {
	  		    /* Read the response stream */
	  		    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);

	  		    /* Copy the response to StringBuilder */
	  		    StringBuilder sb = new StringBuilder();
	  		    String line = null;
	  		    while ((line = reader.readLine()) != null) {
	  		      sb.append(line + "\n");
	  		    }
	  		    is.close();
	  		    
	  		    if(sb.toString().contains("Incident saved")){
	  		    	boo = true;
	  		    }

	  		  } catch (Exception e) {
	  			  e.printStackTrace();
	  			  boo = false;
	  		  }
	        }
	    };
	    t.start();
	}
	
	public void alert(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setTitle("Your Report Has Been Succefully Sent!");
		builder.setPositiveButton("Great!", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   dialog.cancel();
		           }
		       })
	       	;
		AlertDialog alert = builder.create();
		alert.show();
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

}

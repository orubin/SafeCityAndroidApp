package com.SafeCity.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ChangePassword extends Activity {
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.changepassword);
    }
	
    public void save(View view){
    	SharedPreferences sp = this.getSharedPreferences("prefile", 0);
    	SharedPreferences.Editor editor = sp.edit();
    	EditText et = (EditText) findViewById(R.id.editText1);
    	String oldPassword = et.getText().toString();
    	et = (EditText) findViewById(R.id.editText2);
    	String newPassword = et.getText().toString();
    	et = (EditText) findViewById(R.id.editText3);
    	String confirmPassword = et.getText().toString();
    	if(!sp.getString("password", null).equalsIgnoreCase(oldPassword)){
    		errorAndClear("Error in confirming old password!");
    	}
    	else if(!newPassword.equalsIgnoreCase(confirmPassword)){
    		errorAndClear("The new password does not match the confirm field!");
    	}
    	else{
    		editor.putString("password", newPassword);
    		editor.commit();
    		Toast.makeText(getApplicationContext(), "Your password has been changed!", Toast.LENGTH_LONG).show();
    		this.finish();
    	}
    	
    }
    
    public void errorAndClear(String error){
    	Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
		//clear all the fields
		EditText et1 = (EditText) findViewById(R.id.editText1);
		EditText et2 = (EditText) findViewById(R.id.editText2);
		EditText et3 = (EditText) findViewById(R.id.editText3);
		et1.setText("");
		et2.setText("");
		et3.setText("");
    }
    
    public void reset(View view){
    	AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
    	builder2.setTitle("Reset To Factory Default?");
    	final SharedPreferences sp = this.getSharedPreferences("prefile", 0);
		builder2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		       		Toast.makeText(getApplicationContext(), "Your new password is now 1234!", Toast.LENGTH_LONG).show();
		    		SharedPreferences.Editor editor = sp.edit();
		    		editor.putString("password", "1234");
		    		editor.commit();
		        	   dialog.cancel();
		           }
		       })
	       	;
		AlertDialog alert2 = builder2.create();
		alert2.show();
    }
}

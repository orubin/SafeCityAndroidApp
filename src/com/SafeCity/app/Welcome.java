package com.SafeCity.app;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Welcome extends Activity {
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
    }
	
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
			this.finish();
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

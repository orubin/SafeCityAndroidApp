package com.SafeCity.app;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class StopAlert extends Activity {
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stopalert);
    }
    
    public void ok(View view){
    	EditText et = (EditText) findViewById(R.id.editText1);
    	String password = et.getText().toString();
    	SharedPreferences sp = this.getSharedPreferences("prefile", 0);
    	if(sp.getString("password", null).equalsIgnoreCase(password)){
    		this.finish();
    	}
    	else{
    		
    	}
    }
    
}

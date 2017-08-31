package com.SafeCity.app;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ForgotPassword extends Activity {
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgotpassword);
        SharedPreferences sp = this.getSharedPreferences("prefile", 0);
        TextView tv = (TextView) findViewById(R.id.textView2);
        tv.setText(sp.getString("question", ""));
    }
    
    public void reset(View view){
    	SharedPreferences sp = this.getSharedPreferences("prefile", 0);
    	EditText et = (EditText) findViewById(R.id.editText1);
    	if(!sp.getString("answer", "").equalsIgnoreCase(et.getText().toString())){
    		Toast.makeText(getApplicationContext(), "Your answer does not match the original answer to the question!", 3).show();
    		//clear all the fields
    		EditText et1 = (EditText) findViewById(R.id.editText1);
    		et1.setText("");
    	}
    	else{
    		Toast.makeText(getApplicationContext(), "Your new password is now 1234!", Toast.LENGTH_LONG).show();
    		SharedPreferences.Editor editor = sp.edit();
    		editor.putString("password", "1234");
    		editor.commit();
    		this.finish();
    	}
    	
    }
}

package com.SafeCity.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Settings extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
    	SharedPreferences sp = this.getSharedPreferences("prefile", 0);
    	String phone = sp.getString("phone", null);
    	String country = sp.getString("country", null);
    	String city = sp.getString("city", null);
    	if(phone!=null){
	    	EditText et = (EditText) findViewById(R.id.editText1);
			et.setText(phone);
    	}
    	if(country!=null){
	    	Button button = (Button) findViewById(R.id.button2);
	    	button.setText(country);
    	}
    	if(city!=null){
    		Button button = (Button) findViewById(R.id.button3);
    		button.setText(city);
    	}
    	
    }
    
    public void Save(View view){
    	SharedPreferences sp = this.getSharedPreferences("prefile", 0);
    	SharedPreferences.Editor editor = sp.edit();
    	EditText et;
    	et = (EditText) findViewById(R.id.editText1);
		editor.putString("phone", et.getText().toString());
		editor.commit();
		this.finish();
    }
    
    public void SelectCountry(View view){
    	Intent select = new Intent(this, CountryList.class);
    	startActivityForResult(select,0);
    }
    
    public void selectCity(View view){
    	Intent select = new Intent(this, CityList.class);
    	startActivityForResult(select,1);
    }
    
    public void changePassword(View view){
    	Intent select = new Intent(this, ChangePassword.class);
    	startActivityForResult(select,0);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
    	SharedPreferences sp = this.getSharedPreferences("prefile", 0);
    	SharedPreferences.Editor editor = sp.edit();

    	if (requestCode==1){
        	if (data!=null){
    	    	Bundle b = data.getExtras();
    	    	Button button = (Button) findViewById(R.id.button3);
    	    	button.setText(b.get("city").toString());
    	    	editor.putString("city", b.get("city").toString());
        	}
    	}
    	else if (requestCode==0){
    		if (data!=null){
    	    	Bundle b = data.getExtras();
    	    	Button button = (Button) findViewById(R.id.button2);
    	    	button.setText(b.get("country").toString());
    			editor.putString("country", b.get("country").toString());
        	}
    	}
    	editor.commit();
    	
    }
    
}

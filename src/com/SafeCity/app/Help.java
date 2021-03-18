package com.SafeCity.app;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.util.Linkify;
import android.widget.TextView;

public class Help extends Activity {
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help);
        TextView tv = (TextView) findViewById(R.id.textView1);
     	tv.setText("");
        tv = (TextView) findViewById(R.id.textView2);
        tv.setText(Html.fromHtml("<a href=''></a>\n"));
        tv.setAutoLinkMask(Linkify.WEB_URLS);
        tv = (TextView) findViewById(R.id.textView3);
        tv.setText("");    
   }
}

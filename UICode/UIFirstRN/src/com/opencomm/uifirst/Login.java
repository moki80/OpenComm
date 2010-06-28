package com.opencomm.uifirst;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * Application Name: Open Comm UI First Draft
 * Created by: Risa Naka
 */

public class Login extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // load the layout
        setContentView(R.layout.main);
        
     // get login button in the xml file and assign it to a local variable of type Button
        Button login = (Button)findViewById(R.id.login);
        
        login.setOnClickListener(new OnClickListener()
        {
        	public void onClick(View viewParam)
        	{
        		// get user and pwd edittexts in the xml file and assign to a local variable of type EditText
                EditText usernameEditText = (EditText) findViewById(R.id.entry_user);
                EditText passwordEditText = (EditText) findViewById(R.id.entry_pwd);
                
                // the getText() gets the current value of the text box
                // the toString() converts the value to String data type
                // then assigns it to a variable of type String
                String sUserName = usernameEditText.getText().toString();
                String sPassword = passwordEditText.getText().toString();
        		
                if (sUserName.equals("") || sPassword.equals("")){
                	Toast.makeText(Login.this, "Both Username and Password required!",
                			Toast.LENGTH_SHORT).show();
                }
               else if (sUserName.equals("opencomm") && sPassword.equals("bailey2010")){
            	    Toast.makeText(Login.this, "Welcome, "+ sUserName, Toast.LENGTH_SHORT).show();
                	
                	setContentView(R.layout.buddy);
                	
                	GridView gridview = (GridView) findViewById(R.id.buddygrid);
                    gridview.setAdapter(new ImageAdapter(Login.this));

                    gridview.setOnItemClickListener(new OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                        	Toast.makeText(Login.this, "" + position, Toast.LENGTH_SHORT).show();
                        }
                    });

                }       
               else{
                	// display the username and the password in string format
                	Toast.makeText(Login.this, "Username and password not valid.",
                			Toast.LENGTH_SHORT).show();
                	}
        	}
        }); // end of launch.setOnclickListener
    }
        /** super.onRestart();
        
        super.onStart();

        super.onResume();

        super.onPause();

        super.onStop();

       super.onDestroy(); */


}
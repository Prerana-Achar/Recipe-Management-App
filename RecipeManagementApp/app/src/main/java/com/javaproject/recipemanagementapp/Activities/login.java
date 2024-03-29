package com.javaproject.recipemanagementapp.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Database;

import com.javaproject.recipemanagementapp.DatabaseHelper;
import com.javaproject.recipemanagementapp.R;

public class login extends AppCompatActivity {
    TextView forgot;
    EditText e5, e6;
    Button b2;
    CheckBox remCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        e5 = findViewById(R.id.login_email);
        e6 = findViewById(R.id.login_password);
        b2 = findViewById(R.id.login_btn1);
        forgot = findViewById(R.id.forgot_password);
        remCheck = findViewById(R.id.RememberMe_checkbox);

        remCheck.setOnClickListener(view -> {
            DatabaseHelper.setRemStatus(e5.getText().toString().trim());
        });

        forgot.setOnClickListener(v1 -> {
            Intent goToResetPassword = new Intent(login.this, forgot_password.class);
            startActivity(goToResetPassword);
                });


        b2.setOnClickListener(view -> {
            String e=e5.getText().toString().trim();
            String p=e6.getText().toString().trim();

//                User user = DatabaseHelper.getUserByEmail(e);
//                DatabaseHelper.setCurrentUser(user);
//                DatabaseHelper.getUserByEmail(e);

            if(e.equals("")||p.equals("")) {
                Toast.makeText(getApplicationContext(), "Fields are empty", Toast.LENGTH_SHORT).show();
            }

            else if(e.contains("@")){
                Boolean chk = DatabaseHelper.checklogin(e, p);
                if(chk){
                    Toast.makeText(getApplicationContext(), "Login Successful.", Toast.LENGTH_SHORT).show();
                    DatabaseHelper.checkemail(e);
                    DatabaseHelper.getAllRecipe();
                    Intent intent = new Intent(login.this, landing_page.class);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(getApplicationContext(), "Wrong Email or Password", Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Please enter a valid Email!", Toast.LENGTH_SHORT).show();
            }
            /*Boolean Checkemailpassword = db.emailpassword(email, password);

            if(Checkemailpassword==true){

                Toast.makeText(getApplicationContext(),"Login successful",Toast.LENGTH_SHORT).show();

                Intent intent1 = new Intent(login.this, landing_page.class);
                startActivity(intent1);
            }
            else{
                Toast.makeText(getApplicationContext(), "Wrong email or password", Toast.LENGTH_SHORT).show();
            }
*/
        });
    }

    @Override
    public void onBackPressed(){
        Intent toLandingPage = new Intent(this, ExitPage.class);
        startActivity(toLandingPage);
//        super.onBackPressed();
    }
}
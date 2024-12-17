package com.example.app_asistencia;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class Registro extends AppCompatActivity implements View.OnClickListener {
    EditText editnombre, editapellido, editedad, editnusuario, editcontrasena;
    Button btnregistro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        editnombre =(EditText) findViewById(R.id.editnombre);
        editapellido =(EditText) findViewById(R.id.editapellido);
        editedad =(EditText) findViewById(R.id.editedad);
        editnusuario =(EditText) findViewById(R.id.editnusuario);
        editcontrasena =(EditText) findViewById(R.id.editcontrasena);
        btnregistro =(Button) findViewById(R.id.btnregistro);

        btnregistro.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        final String name=editnombre.getText().toString();
        final String ap=editapellido.getText().toString();
        final int age=Integer.parseInt(editedad.getText().toString());
        final String username=editnusuario.getText().toString();
        final String password=editcontrasena.getText().toString();

        Response.Listener<String> respoListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success= jsonResponse.getBoolean("success");

                    if(success){
                        Intent intent = new Intent(Registro.this, MainActivity.class);
                        Registro.this.startActivity(intent);
                    }else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(Registro.this);
                        builder.setMessage("Error registro")
                                .setNegativeButton("Retry", null)
                                .create().show();
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
        };
        RegisterRequest registerRequest = new RegisterRequest(name, ap, age, username, password, respoListener);
        RequestQueue queue = Volley.newRequestQueue(Registro.this);
        queue.add(registerRequest);
    }
}
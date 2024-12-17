package com.example.app_asistencia;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL;
import androidx.biometric.BiometricPrompt;
import androidx.biometric.BiometricManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Executor;

public class Asistencia extends AppCompatActivity {
    private Executor executor;
    private androidx.biometric.BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    Button btnsalir;
    ImageView imagefinger;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asistencia);
    imagefinger = findViewById(R.id.imagefinger);
        Button btnsalir = findViewById(R.id.btnsalir);

        username = getIntent().getStringExtra("username");

        if (username != null) {
            Toast.makeText(this, "Usuario: " + username, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No se encontró el nombre de usuario", Toast.LENGTH_SHORT).show();
        }

        btnsalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Asistencia.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG |
                BiometricManager.Authenticators.DEVICE_CREDENTIAL)){
            case BiometricManager.BIOMETRIC_SUCCESS:
                Toast.makeText(this, "SE PUEDE USAR EL BIOMETRICO", Toast.LENGTH_LONG).show();
                break;

            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Toast.makeText(this, "NO HAY DISPOSITIVO BIOMETRICO", Toast.LENGTH_LONG).show();
                break;

            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Toast.makeText(this, "DISPOSITIVO BIOMETRICO NO DISPONIBLE", Toast.LENGTH_LONG).show();
                break;

            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                Toast.makeText(this, "CREE CREDENCIALES NECESARIAS", Toast.LENGTH_LONG).show();
                break;
        }
        lanzar_Biometrico();
    }

    private void lanzar_Biometrico() {
        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new androidx.biometric.BiometricPrompt(this, executor, new androidx.biometric.BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(Asistencia.this, "ERROR EN EL EXECUTOR BIOMETRICO",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull androidx.biometric.BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(Asistencia.this, "ASISTENCIA REGISTRADA",Toast.LENGTH_LONG).show();
                registrarAsistencia();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(Asistencia.this, "ERROR EN LA AUTENTICACION",Toast.LENGTH_LONG).show();
            }
        });
        promptInfo = new androidx.biometric.BiometricPrompt.PromptInfo.Builder()
                .setTitle("INICIO BIOMETRICO")
                .setSubtitle("USANDO CREDENCIALES BIOMETRICAS")
                .setNegativeButtonText("USE PASSWORD PARA VALIDAR")
                .build();
        imagefinger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                biometricPrompt.authenticate(promptInfo);
            }
        });
    }
    private void registrarAsistencia() {
        Response.Listener<String> responseListener = response -> {
            try {
                JSONObject jsonResponse = new JSONObject(response);
                boolean success = jsonResponse.getBoolean("success");

                if (success) {
                    Toast.makeText(this, "Asistencia registrada correctamente", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Error: " + jsonResponse.getString("message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error en el servidor", Toast.LENGTH_SHORT).show();
            }
        };

        AsistenciaRequest asistenciaRequest = new AsistenciaRequest(username, responseListener);
        RequestQueue queue = Volley.newRequestQueue(Asistencia.this);
        queue.add(asistenciaRequest);
    }
}
package com.example.app_asistencia;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class AsistenciaRequest extends StringRequest {
    private static final String ASISTENCIA_REQUEST_URL = "http://192.168.0.220/RegistrarAsistencia.php";
    private final Map<String, String> params;

    public AsistenciaRequest(String username, Response.Listener<String> listener) {
        super(Method.POST, ASISTENCIA_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("username", username);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}


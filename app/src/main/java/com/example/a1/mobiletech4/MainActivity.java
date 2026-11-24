package com.example.a1.mobiletech4;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1;
    private String passwordStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        passwordStatus = new String("changed");

        // Проверка и запрос разрешения на доступ к файловой системе
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
        } else {
            proceedWithAppLogic();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                proceedWithAppLogic();
            } else {
                // Разрешение не предоставлено, обработка отказа
            }
        }
    }

    private void proceedWithAppLogic() {
        String code = PreferencesSettings.getCurrentCode(this);

        if (code.length() > 0) {
            startEnterPassActivity();
        } else {
            passwordStatus = "not_changed";
            startCreatePassActivity();
        }
    }

    private void startCreatePassActivity() {
        Intent intent = new Intent(this, CreatePassActivity.class);
        finish();
        startActivity(intent);
    }

    private void startEnterPassActivity() {
        Intent intent = new Intent(this, EnterPassActivity.class);
        finish();
        intent.putExtra("password", passwordStatus);
        startActivity(intent);
    }
}

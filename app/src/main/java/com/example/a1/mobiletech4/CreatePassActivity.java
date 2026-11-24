package com.example.a1.mobiletech4;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class CreatePassActivity extends AppCompatActivity {

    // Поле для ввода нового пароля
    EditText passwordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Вызываем метод суперкласса для выполнения стандартной инициализации
        super.onCreate(savedInstanceState);
        // Устанавливаем макет интерфейса для этой активности
        setContentView(R.layout.activity_create_pass);

        // Инициализируем поле ввода пароля из макета
        passwordText = findViewById(R.id.createPassEnterPass);
        // Устанавливаем метод для скрытия вводимых символов (режим пароля)
        passwordText.setTransformationMethod(PasswordTransformationMethod.getInstance());

        // Инициализируем чекбокс для показа/скрытия пароля
        CheckBox checkBox = findViewById(R.id.createPassTick);
        // Устанавливаем слушатель для изменения состояния чекбокса
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Если чекбокс отмечен, скрываем вводимые символы
                if (isChecked) {
                    passwordText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } 
                // Если чекбокс не отмечен, показываем вводимые символы
                else {
                    passwordText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });

        // Инициализируем кнопку для подтверждения создания пароля
        Button acceptButton = findViewById(R.id.createPassBtn);
        // Устанавливаем слушатель нажатия на кнопку
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Сохраняем введенный пароль, хэшируя его с помощью MD5
                savePass(passwordText.getText().toString());
                // Переходим на главную активность
                startMainActivity();
            }
        });
    }

    // Метод для сохранения пароля
    void savePass(String pass) {
        // Хэшируем пароль и сохраняем его в настройках приложения
        PreferencesSettings.saveToPref(this, PreferencesSettings.md5(pass));
    }

    // Метод для перехода на главную активность
    void startMainActivity() {
        // Создаем Intent для запуска MainActivity
        Intent intent = new Intent(this, MainActivity.class);
        // Завершаем текущую активность
        finish();
        // Запускаем главную активность
        startActivity(intent);
    }
}

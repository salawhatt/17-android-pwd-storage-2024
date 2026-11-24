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

public class EnterPassActivity extends AppCompatActivity {

    // Поле для ввода пароля
    EditText passwordText;
    // Переменная для хранения статуса пароля (например, изменён или нет)
    String passwordStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Устанавливаем макет интерфейса для этой активности
        setContentView(R.layout.activity_enter_pass);

        // Получаем переданные данные (например, статус пароля) через Intent
        Bundle arguments = getIntent().getExtras();
        // Присваиваем переданный статус пароля переменной passwordStatus
        passwordStatus = new String(arguments.get("password").toString());

        // Находим элемент для ввода пароля на экране
        passwordText = findViewById(R.id.createPassEnterPass2);
        // По умолчанию скрываем вводимые символы (режим пароля)
        passwordText.setTransformationMethod(PasswordTransformationMethod.getInstance());

        // Обработка чекбокса для показа/скрытия пароля
        CheckBox checkBox = findViewById(R.id.createPassTick2);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Если чекбокс отмечен, скрываем пароль
                if (isChecked) {
                    passwordText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } 
                // Если чекбокс не отмечен, показываем пароль
                else {
                    passwordText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });

        // Обработка нажатия на кнопку "Принять"
        Button acceptButton = findViewById(R.id.createPassBtn2);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Проверяем введённый пароль
                if (checkPass() == 1) {
                    // Если пароль верный, запускаем следующую активность
                    startAnotherActivity();
                }
            }
        });
    }

    // Метод для перехода на другую активность после успешной проверки пароля
    private void startAnotherActivity() {
        // Создаём Intent для запуска WorkActivity
        Intent intent = new Intent(this, WorkActivity.class);
        // Завершаем текущую активность
        finish();
        // Передаём статус пароля в следующую активность
        intent.putExtra("password", passwordStatus);
        // Запускаем новую активность
        startActivity(intent);
    }

    // Метод для проверки введённого пароля
    int checkPass() {
        // Получаем введённый пароль и вычисляем его MD5-хэш
        String enteredPass = PreferencesSettings.md5(passwordText.getText().toString());
        // Получаем сохранённый хэш пароля из настроек приложения
        String defaultPass = PreferencesSettings.getCurrentCode(this);

        // Сравниваем введённый хэш с сохранённым
        if (enteredPass.equals(defaultPass)) {
            // Если хэши совпадают, пароль правильный
            return 1;
        }
        // Если не совпадают, пароль неверный
        return 0;
    }
}

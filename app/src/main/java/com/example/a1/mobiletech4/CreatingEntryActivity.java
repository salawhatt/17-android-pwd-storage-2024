package com.example.a1.mobiletech4;

import android.content.ClipData; // Импортируем класс для работы с данными буфера обмена
import android.content.ClipboardManager; // Импортируем класс для работы с буфером обмена
import android.content.Intent; // Импортируем класс для создания новых интентов
import android.os.Bundle; // Импортируем класс для работы с параметрами активности
import android.text.method.HideReturnsTransformationMethod; // Импортируем класс для отображения текста
import android.text.method.PasswordTransformationMethod; // Импортируем класс для скрытия текста (пароля)
import android.view.View; // Импортируем класс для работы с представлениями
import android.widget.Button; // Импортируем класс для кнопок
import android.widget.CheckBox; // Импортируем класс для чекбоксов
import android.widget.CompoundButton; // Импортируем класс для работы с переключаемыми кнопками
import android.widget.EditText; // Импортируем класс для текстовых полей
import android.widget.Toast; // Импортируем класс для отображения сообщений

import androidx.appcompat.app.AppCompatActivity; // Импортируем класс для создания активностей

import com.google.android.material.snackbar.Snackbar; // Импортируем класс для отображения снэкбаров

public class CreatingEntryActivity extends AppCompatActivity {

    // Экземпляр базы данных
    Db database;

    // Поля для ввода данных
    EditText resourceText; // Поле для ввода имени ресурса
    EditText loginText; // Поле для ввода логина
    EditText passwordText; // Поле для ввода пароля
    EditText noteText; // Поле для ввода заметок

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Вызываем метод суперкласса для выполнения стандартной инициализации
        super.onCreate(savedInstanceState);
        // Устанавливаем макет интерфейса для этой активности
        setContentView(R.layout.activity_creating_entry);

        // Инициализируем базу данных и устанавливаем ключ шифрования
        database = new Db(this);
        database.setKey(PreferencesSettings.getCurrentCode(this).substring(0, 32));

        // Инициализируем поля ввода из макета
        resourceText = findViewById(R.id.resourceText);
        loginText = findViewById(R.id.loginText);
        passwordText = findViewById(R.id.passwordText);
        noteText = findViewById(R.id.noteText);

        // Устанавливаем метод для скрытия вводимых символов (режим пароля)
        passwordText.setTransformationMethod(PasswordTransformationMethod.getInstance());

        // Инициализируем чекбокс для показа/скрытия пароля
        CheckBox checkBox = findViewById(R.id.checkBox);
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

        // Инициализируем кнопку "Копировать пароль"
        Button copePassButton = findViewById(R.id.copyPassBtn2);
        copePassButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                // Получаем текст пароля для копирования
                String srcText = passwordText.getText().toString();

                // Получаем сервис для работы с буфером обмена
                Object clipboardService = getSystemService(CLIPBOARD_SERVICE);
                final ClipboardManager clipboardManager = (ClipboardManager)clipboardService;
                // Создаем новый ClipData для копирования текста в буфер обмена
                ClipData clipData = ClipData.newPlainText("Source Text", srcText);
                // Устанавливаем его как основной клип для копирования
                clipboardManager.setPrimaryClip(clipData);
                // Отображаем уведомление о копировании текста
                Snackbar snackbar = Snackbar.make(v, "Source text has been copied to system clipboard.", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });

        // Инициализируем кнопку "Сохранить"
        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                // Сохраняем введенные данные в базу данных
                long result = database.createItem(
                        resourceText.getText().toString(),
                        loginText.getText().toString(),
                        passwordText.getText().toString(),
                        noteText.getText().toString()
                );
                // Проверяем результат сохранения
                if(result < 0) {
                    Toast.makeText(CreatingEntryActivity.this, "[-] createItem returned -1", Toast.LENGTH_SHORT).show();
                }

                // Закрываем базу данных
                database.close();
                // Переходим к основной активности
                startWorkActivity();
            }
        });

        // Инициализируем кнопку "Отмена"
        Button cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                // Закрываем базу данных
                database.close();
                // Переходим к основной активности
                startWorkActivity();
            }
        });
    }

    // Метод для перехода к основной активности
    void startWorkActivity()
    {
        // Создаем Intent для запуска WorkActivity
        Intent intent = new Intent(this, WorkActivity.class);
        // Завершаем текущую активность
        finish();
        // Передаем статус пароля в следующую активность
        intent.putExtra("password", "not_changed");
        // Запускаем основную активность
        startActivity(intent);
    }
}

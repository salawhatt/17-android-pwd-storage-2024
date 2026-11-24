package com.example.a1.mobiletech4;
import android.content.ClipData;
import android.content.ClipboardManager;
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
import com.google.android.material.snackbar.Snackbar;

public class ChangingEntryActivity extends AppCompatActivity {
    // Поля для ввода данных
    EditText resourceText; // Поле для ввода имени ресурса
    EditText loginText; // Поле для ввода логина
    EditText passwordText; // Поле для ввода пароля
    EditText noteText; // Поле для ввода заметок

    // Переменная для хранения имени ресурса, который нужно изменить
    String checkedResource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Вызываем метод суперкласса для выполнения стандартной инициализации
        super.onCreate(savedInstanceState);
        // Устанавливаем макет интерфейса для этой активности
        setContentView(R.layout.activity_changing_entry);

        // Получаем переданные данные (выбранный ресурс) через Intent
        Bundle arguments = getIntent().getExtras();
        checkedResource = arguments.get("checked_resource").toString();

        // Инициализируем поля ввода из макета
        resourceText = findViewById(R.id.resourceText);
        loginText = findViewById(R.id.loginText);
        passwordText = findViewById(R.id.passwordText);
        noteText = findViewById(R.id.noteText);

        // Устанавливаем метод для скрытия вводимых символов (режим пароля)
        passwordText.setTransformationMethod(PasswordTransformationMethod.getInstance());

        // Заполняем поля ввода текущими значениями для выбранного ресурса
        Db database = new Db(this);
        database.setKey(PreferencesSettings.getCurrentCode(this).substring(0, 32));
        loginText.setText(database.getItemInfoByResourceName(checkedResource, DbHelper.KEY_LOGIN));
        passwordText.setText(database.getItemInfoByResourceName(checkedResource, DbHelper.KEY_PASSWORD));
        noteText.setText(database.getItemInfoByResourceName(checkedResource, DbHelper.KEY_NOTE));
        database.close();

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

        // Инициализируем кнопку "Отмена"
        Button cancelButton = findViewById(R.id.cancelButton2);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Переходим обратно к основной активности
                startWorkActivity();
            }
        });

        // Инициализируем кнопку "Копировать пароль"
        Button copePassButton = findViewById(R.id.copyPassBtn);
        copePassButton.setOnClickListener(new View.OnClickListener() {
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
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Получаем значения из полей ввода
                String resource = ((EditText)findViewById(R.id.resourceText)).getText().toString();
                String login = ((EditText)findViewById(R.id.loginText)).getText().toString();
                String password = ((EditText)findViewById(R.id.passwordText)).getText().toString();
                String note = ((EditText)findViewById(R.id.noteText)).getText().toString();
                // Обновляем поля в базе данных
                updateFields(resource, login, password, note);
                // Переходим к основной активности
                startWorkActivity();
            }
        });
    }

    // Метод для обновления записей в базе данных
    void updateFields(String resource, String login, String password, String note) {
        Db database = new Db(this);
        database.setKey(PreferencesSettings.getCurrentCode(this).substring(0, 32));
        // Обновляем записи в базе данных
        database.updateLogin(checkedResource, login);
        database.updatePassword(checkedResource, password);
        database.updateNote(checkedResource, note);
        database.updateResource(checkedResource, resource);
        // Закрываем базу данных
        database.close();
    }

    // Метод для перехода к основной активности
    void startWorkActivity() {
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
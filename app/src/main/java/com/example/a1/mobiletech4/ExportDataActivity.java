package com.example.a1.mobiletech4;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ExportDataActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 1; // Код запроса разрешений
    String key; // Ключ для шифрования данных

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Вызываем метод суперкласса для выполнения стандартной инициализации
        super.onCreate(savedInstanceState);
        // Устанавливаем макет интерфейса для этой активности
        setContentView(R.layout.activity_export_data);

        // Проверяем наличие разрешений на запись во внешнее хранилище
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Запрашиваем разрешения, если они не предоставлены
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
        }

        // Проверяем доступ к внешнему хранилищу
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "Storage is not available", Toast.LENGTH_SHORT).show();
            return; // Выходим из метода
        }

        // Проверяем и создаем папку для базы данных, если она не существует
        checkDatabaseFolder();

        // Получаем текущий код для шифрования
        key = PreferencesSettings.getCurrentCode(this).substring(0, 32);

        // Инициализируем кнопку для экспорта данных
        Button exportButton = findViewById(R.id.exportToFileButton);
        exportButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                // Получаем имя файла для экспорта из текстового поля
                String filename = ((EditText)findViewById(R.id.filenameToExport)).getText().toString();
                // Экспортируем базу данных в файл
                exportDatabaseToFile(filename);
                // Переход к основной активности
                startWorkActivity();
            }
        });

        // Инициализируем кнопку "Отмена"
        Button cancelButton = findViewById(R.id.cancelButton4);
        cancelButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                // Переход к основной активности
                startWorkActivity();
            }
        });
    }

    // Метод для экспорта базы данных в файл
    void exportDatabaseToFile(String filename) {
        // Создаем файл для сохранения данных
        File databaseFile = new File(Environment.getExternalStorageDirectory() + "/AppDatabases/", filename);
        BufferedWriter bw;

        try {
            // Создаем BufferedWriter для записи в файл
            bw = new BufferedWriter(new FileWriter(databaseFile));
        }
        catch(IOException e) {
            Toast.makeText(this, "FileWriter() failed", Toast.LENGTH_SHORT).show(); // Сообщаем об ошибке
            return; // Выходим из метода
        }

        Db database = new Db(this); // Инициализируем базу данных
        database.setKey(key); // Устанавливаем ключ шифрования

        // Получаем все ресурсы из базы данных
        String[] resources = database.getAllResources();

        // Проходим по всем ресурсам и записываем их в файл
        for(int i = 0; i < database.getItemCount(); i++) {
            writeEntryToFile(resources[i], bw, database); // Записываем текущий ресурс
        }

        database.close(); // Закрываем базу данных

        try {
            bw.close(); // Закрываем BufferedWriter
        }
        catch(IOException e) {
            Toast.makeText(this, "bw.close() failed", Toast.LENGTH_SHORT).show(); // Сообщаем об ошибке
            return; // Выходим из метода
        }
    }

    // Метод для записи записи в файл
    void writeEntryToFile(String resource, BufferedWriter bw, Db database) {
        // Получаем данные о ресурсе
        String login = database.getItemInfoByResourceName(resource, DbHelper.KEY_LOGIN);
        String password = database.getItemInfoByResourceName(resource, DbHelper.KEY_PASSWORD);
        String note = database.getItemInfoByResourceName(resource, DbHelper.KEY_NOTE);

        try {
            // Записываем зашифрованные данные в файл
            bw.write(encrypt(resource) + " " +
                    encrypt(login) + " " +
                    encrypt(password) + " " +
                    encrypt(note) + '\n');
        }
        catch(IOException e) {
            Toast.makeText(this, "bw.write() failed", Toast.LENGTH_SHORT).show(); // Сообщаем об ошибке
            return; // Выходим из метода
        }
    }

    // Метод для перехода к основной активности
    void startWorkActivity() {
        Intent intent = new Intent(this, WorkActivity.class);
        finish(); // Завершаем текущую активность
        intent.putExtra("password", "not_changed"); // Передаем статус пароля
        startActivity(intent); // Запускаем новую активность
    }

    // Метод для проверки и создания папки для базы данных
    void checkDatabaseFolder() {
        File databaseFolder = new File(Environment.getExternalStorageDirectory() + "/AppDatabases/");
        if (!databaseFolder.exists())
            databaseFolder.mkdirs(); // Создаем папку, если она не существует
    }

    // Метод для шифрования текста
    String encrypt(String openedText) {
        CryptoProvider cp = new CryptoProvider();
        String cipherText = "";

        try {
            // Шифруем текст с использованием текущего ключа
            cipherText = cp.encryptMessage(openedText, key);
        }
        catch (Exception e) {
            Toast.makeText(this, "encryptMessage failed", Toast.LENGTH_SHORT).show(); // Сообщаем об ошибке
        }

        // Возвращаем зашифрованный текст без последнего символа
        return cipherText.substring(0, cipherText.length() - 1);
    }
}
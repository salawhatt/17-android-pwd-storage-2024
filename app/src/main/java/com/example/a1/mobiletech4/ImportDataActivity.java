package com.example.a1.mobiletech4;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.material.snackbar.Snackbar;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


public class ImportDataActivity extends AppCompatActivity {

    // Константа для запроса разрешений на запись во внешнее хранилище
    private static final int REQUEST_CODE = 1;

    // Переменная для хранения выбранного файла
    String checkedFile;

    // Массив строк для хранения списка файлов
    String[] fileList;

    // Элемент интерфейса для отображения списка файлов
    ListView listView;

    // Переменная для хранения ключа шифрования
    String key;

    // Поле для ввода пароля
    EditText passwordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_data); // Устанавливаем макет интерфейса активности

        // Проверяем наличие разрешения на запись во внешнее хранилище. Если разрешение не предоставлено, запрашиваем его.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
        }

        // Проверяем доступность SD-карты. Если недоступна, выводим сообщение.
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "SD is not available", Toast.LENGTH_SHORT).show();
            return;
        }

        // Создаем папку для базы данных, если она не существует
        checkDatabaseFolder();

        // Инициализируем поле для ввода пароля
        passwordText = findViewById(R.id.createPassEnterPass3);

        // Устанавливаем режим скрытия вводимого пароля
        passwordText.setTransformationMethod(PasswordTransformationMethod.getInstance());

        // Инициализируем чекбокс для показа/скрытия пароля
        CheckBox checkBox = findViewById(R.id.checkBox3);
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
        Button copePassButton = findViewById(R.id.copyPassBtn4);
        copePassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Получаем текст пароля для копирования
                String srcText = passwordText.getText().toString();

                // Используем системный сервис для работы с буфером обмена
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

        // Инициализируем переменную для хранения выбранного файла
        checkedFile = new String();

        // Получаем список файлов из папки AppDatabases
        fileList = getFileListFromFolder();

        // Инициализируем ListView для отображения списка файлов
        listView = findViewById(R.id.fileListForImport);
        listView.setChoiceMode(listView.CHOICE_MODE_SINGLE); // Устанавливаем одиночный выбор

        // Обрабатываем клик по элементу списка для выбора файла
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Сохраняем выбранный файл
                checkedFile = fileList[listView.getCheckedItemPosition()];
            }
        });

        // Настраиваем адаптер для списка файлов
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_single_choice, fileList);
        listView.setAdapter(adapter);

        // Инициализируем кнопку "Импортировать"
        Button importButton = findViewById(R.id.importFromFileButton);
        importButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Если не выбран файл для импорта, выводим сообщение
                if (checkedFile.length() == 0) {
                    Toast.makeText(ImportDataActivity.this, "No resources selected", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Импортируем данные из выбранного файла
                importDatabaseFromFile(checkedFile);

                // Переход к основной активности
                startWorkActivity();
            }
        });

        // Инициализируем кнопку "Отмена"
        Button cancelButton = findViewById(R.id.cancelButton3);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Переход к основной активности
                startWorkActivity();
            }
        });
    }

    // Метод для импорта данных из выбранного файла
    void importDatabaseFromFile(String filename) {
        File databaseFile = new File(Environment.getExternalStorageDirectory() + "/AppDatabases/", filename);
        BufferedReader br;

        try {
            // Открываем файл для чтения
            br = new BufferedReader(new FileReader(databaseFile));
        }
        catch (IOException e) {
            Toast.makeText(this, "FileReader() failed", Toast.LENGTH_SHORT).show();
            return;
        }

        // Инициализируем базу данных
        Db database = new Db(this);
        key = PreferencesSettings.getCurrentCode(this).substring(0, 32);
        database.setKey(key);

        // Удаляем все записи из базы данных перед импортом
        String[] resources = database.getAllResources();
        int N = database.getItemCount();
        for (int i = 0; i < N; i++) {
            database.deleteItem(resources[i]);
        }

        String line;

        try {
            // Читаем файл построчно
            while ((line = br.readLine()) != null) {
                // Разделяем строку на параметры
                String[] parameters = line.split(" ");
                if (parameters.length < 4) {
                    continue;
                }

                // Дешифруем параметры
                key = PreferencesSettings.md5(passwordText.getText().toString());
                database.setKey(key);

                String resource = decrypt(parameters[0]);
                String login = decrypt(parameters[1]);
                String password = decrypt(parameters[2]);
                String note = decrypt(parameters[3]);

                // Сохраняем данные в базу с новым ключом
                key = PreferencesSettings.getCurrentCode(this).substring(0, 32);
                database.setKey(key);
                database.createItem(resource, login, password, note);
            }
        } catch (IOException e) {
            Toast.makeText(this, "readLine() failed", Toast.LENGTH_SHORT).show();
            return;
        }

        // Закрываем базу данных
        database.close();

        try {
            // Закрываем BufferedReader
            br.close();
        } catch (IOException e) {
            Toast.makeText(this, "br.close() failed", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    // Метод для получения списка файлов из папки
    String[] getFileListFromFolder() {
        File sdCardRoot = Environment.getExternalStorageDirectory();
        File yourDir = new File(sdCardRoot, "AppDatabases");
        File[] filesArray = yourDir.listFiles();
        String[] list = new String[filesArray.length];

        // Заполняем список имен файлов
        for (int i = 0; i < filesArray.length; i++) {
            list[i] = filesArray[i].getName();
        }

        return list;
    }

    // Метод для перехода к основной активности
    void startWorkActivity() {
        Intent intent = new Intent(this, WorkActivity.class);
        finish(); // Завершаем текущую активность
        intent.putExtra("password", "not_changed"); // Передаем статус пароля
        startActivity(intent); // Запускаем новую активность
    }

    // Метод для создания папки для базы данных, если она не существует
    void checkDatabaseFolder() {
        File databaseFolder = new File(Environment.getExternalStorageDirectory() + "/AppDatabases/");
        if (!databaseFolder.exists())
            databaseFolder.mkdirs();
    }

    // Метод для дешифрования зашифрованного текста
    String decrypt(String cipherText) {
        CryptoProvider cp = new CryptoProvider();
        String openedText = "";

        try {
            // Дешифруем сообщение с использованием ключа
            openedText = cp.decryptMessage(cipherText, key);
        } catch (Exception e) {
            Toast.makeText(this, "decryptMessage failed", Toast.LENGTH_SHORT).show();
        }

        return openedText;
    }
}

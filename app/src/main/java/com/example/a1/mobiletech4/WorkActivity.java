package com.example.a1.mobiletech4;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
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

import com.google.android.material.snackbar.Snackbar;

public class WorkActivity extends AppCompatActivity {

    // Переменные для хранения ресурсов и состояния выбранного ресурса
    String resources[];
    String checkedResource;
    ListView listView; // Список для отображения ресурсов
    EditText passwordText; // Поле для ввода пароля

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Вызываем метод суперкласса для выполнения стандартной инициализации
        super.onCreate(savedInstanceState);
        // Устанавливаем макет интерфейса для этой активности
        setContentView(R.layout.activity_work);

        checkedResource = new String(); // Инициализация переменной выбранного ресурса

        // Инициализируем ListView для отображения ресурсов
        listView = findViewById(R.id.resourceList);
        listView.setChoiceMode(listView.CHOICE_MODE_SINGLE); // Устанавливаем одиночный выбор

        // Получаем переданные данные (статус пароля) из предыдущей активности
        Bundle arguments = getIntent().getExtras();
        String passwordStatus = new String(arguments.get("password").toString());

        // Получаем текущий и предыдущий коды пароля
        String currentCode = new String(PreferencesSettings.getCurrentCode(this));
        String previousCode = new String(PreferencesSettings.getPreviousCode(this));

        // Проверяем старый код пароля, если он существует
        String oldCode = PreferencesSettings.getOldCode(this);
        if (oldCode.length() > 0) {
            PreferencesSettings.deleteOldCode(this); // Удаляем старый код
            recryptAllEntries(oldCode); // Перешифровываем все записи
        }

        refreshResourcesList(); // Обновляем список ресурсов

        // Устанавливаем слушатель для обработки кликов по элементам списка
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Сохраняем выбранный ресурс
                checkedResource = resources[listView.getCheckedItemPosition()];
            }
        });

        // Инициализируем поле для ввода пароля
        passwordText = findViewById(R.id.createPassEnterPass4);
        passwordText.setTransformationMethod(PasswordTransformationMethod.getInstance()); // Скрываем вводимые символы

        // Инициализируем чекбокс для показа/скрытия пароля
        CheckBox checkBox = findViewById(R.id.checkBox2);
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
        Button copePassButton = findViewById(R.id.copyPassBtn3);
        copePassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Получаем текст пароля для копирования
                String srcText = passwordText.getText().toString();
                // Получаем сервис для работы с буфером обмена
                Object clipboardService = getSystemService(CLIPBOARD_SERVICE);
                final ClipboardManager clipboardManager = (ClipboardManager) clipboardService;
                // Создаем новый ClipData для копирования текста в буфер обмена
                ClipData clipData = ClipData.newPlainText("Source Text", srcText);
                // Устанавливаем его как основной клип для копирования
                clipboardManager.setPrimaryClip(clipData);
                // Отображаем уведомление о копировании текста
                Snackbar snackbar = Snackbar.make(v, "Source text has been copied to system clipboard.", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });

        // Инициализируем кнопку "Добавить"
        Button addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Переход к активности создания новой записи
                startCreatingEntryActivity();
            }
        });

        // Инициализируем кнопку "Изменить"
        Button changeButton = findViewById(R.id.changeButton);
        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Проверяем, выбран ли ресурс для изменения
                if (checkedResource.length() == 0) {
                    Toast.makeText(WorkActivity.this, "No resources selected", Toast.LENGTH_SHORT).show();
                    return; // Выход, если ничего не выбрано
                }

                // Переход к активности изменения записи
                startChangingEntryActivity();
            }
        });

        // Инициализируем кнопку "Удалить"
        Button deleteButton = findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Проверяем, выбран ли ресурс для удаления
                if (checkedResource.length() == 0) {
                    Toast.makeText(WorkActivity.this, "No resources selected", Toast.LENGTH_SHORT).show();
                    return; // Выход, если ничего не выбрано
                }

                // Удаляем выбранный ресурс из базы данных
                deleteDatabaseElement(checkedResource);
                refreshResourcesList(); // Обновляем список ресурсов
            }
        });

        // Инициализируем кнопку "Изменить пароль"
        Button changePasswordButton = findViewById(R.id.changePasswordButton);
        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Получаем введённый старый пароль и хэшируем его
                String oldPass = PreferencesSettings.md5(passwordText.getText().toString());
                // Проверяем старый пароль перед изменением
                if (checkOldPassToChangeIt(oldPass)) {
                    deleteCode(); // Удаляем старый код
                    saveOldPassword(oldPass); // Сохраняем старый пароль
                    startPasswordChangingActivity(); // Переход к активности изменения пароля
                }
            }
        });

        // Инициализируем кнопку "Экспорт данных"
        Button exportButton = findViewById(R.id.exportDataButton);
        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Переход к активности экспорта данных
                startExportDataActivity();
            }
        });

        // Инициализируем кнопку "Импорт данных"
        Button importButton = findViewById(R.id.importDataButton);
        importButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Переход к активности импорта данных
                startImportDataActivity();
            }
        });
    }

    // Метод для перешифровки всех записей с использованием старого кода
    void recryptAllEntries(String previousCode) {
        String currentCode = PreferencesSettings.getCurrentCode(this).substring(0, 32);
        Db database = new Db(this);
        database.setKey(previousCode);
        int nResources = database.getItemCount(); // Получаем количество ресурсов
        String[] resources = database.getAllResources(); // Получаем все ресурсы

        String[] logins = new String[nResources];
        String[] passwords = new String[nResources];
        String[] notes = new String[nResources];

        // Перешифровка всех записей
        for (int i = 0; i < nResources; i++) {
            // Получаем информацию о текущем ресурсе
            database.setKey(previousCode);
            logins[i] = database.getItemInfoByResourceName(resources[i], DbHelper.KEY_LOGIN);
            passwords[i] = database.getItemInfoByResourceName(resources[i], DbHelper.KEY_PASSWORD);
            notes[i] = database.getItemInfoByResourceName(resources[i], DbHelper.KEY_NOTE);

            database.deleteItem(resources[i]); // Удаляем старую запись

            database.setKey(currentCode); // Устанавливаем новый код
            // Создаем новую запись с зашифрованными данными
            database.createItem(resources[i], logins[i], passwords[i], notes[i]);
        }

        database.close(); // Закрываем базу данных
    }

    // Метод для удаления элемента из базы данных
    void deleteDatabaseElement(String resourceName) {
        Db database = new Db(this);
        database.setKey(PreferencesSettings.getCurrentCode(this).substring(0, 32));
        database.deleteItem(resourceName); // Удаляем элемент по имени ресурса
        database.close(); // Закрываем базу данных
    }

    // Метод для обновления списка ресурсов
    void refreshResourcesList() {
        Db database = new Db(this);
        database.setKey(PreferencesSettings.getCurrentCode(this).substring(0, 32));
        resources = database.getAllResources(); // Получаем все ресурсы
        database.close(); // Закрываем базу данных

        // Проверяем, есть ли ресурсы
        if (resources.length == 0) {
            Toast.makeText(WorkActivity.this, "Resource list is empty", Toast.LENGTH_SHORT).show();
        }

        // Настраиваем адаптер для отображения ресурсов в ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_single_choice, resources);
        listView.setAdapter(adapter); // Устанавливаем адаптер для списка
    }

    // Метод для перехода к активности создания новой записи
    void startCreatingEntryActivity() {
        Intent intent = new Intent(this, CreatingEntryActivity.class);
        finish(); // Завершаем текущую активность
        startActivity(intent); // Запускаем новую активность
    }

    // Метод для перехода к активности изменения записи
    void startChangingEntryActivity() {
        Intent intent = new Intent(this, ChangingEntryActivity.class);
        finish(); // Завершаем текущую активность
        intent.putExtra("checked_resource", checkedResource); // Передаем выбранный ресурс
        startActivity(intent); // Запускаем новую активность
    }

    // Метод для перехода к активности импорта данных
    void startImportDataActivity() {
        Intent intent = new Intent(this, ImportDataActivity.class);
        finish(); // Завершаем текущую активность
        startActivity(intent); // Запускаем новую активность
    }

    // Метод для перехода к активности экспорта данных
    void startExportDataActivity() {
        Intent intent = new Intent(this, ExportDataActivity.class);
        finish(); // Завершаем текущую активность
        startActivity(intent); // Запускаем новую активность
    }

    // Метод для перехода к активности изменения пароля
    void startPasswordChangingActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        finish(); // Завершаем текущую активность
        startActivity(intent); // Запускаем новую активность
    }

    // Метод для удаления кода пароля из настроек
    void deleteCode() {
        PreferencesSettings.deleteCode(this); // Удаляем код пароля
    }

    // Метод для проверки старого пароля перед изменением
    boolean checkOldPassToChangeIt(String oldPass) {
        // Сравниваем введенный старый пароль с сохраненным
        return oldPass.equals(PreferencesSettings.getCurrentCode(this).toString());
    }

    // Метод для сохранения старого пароля
    void saveOldPassword(String oldPass) {
        PreferencesSettings.saveOldCode(this, oldPass); // Сохраняем старый пароль
    }
}
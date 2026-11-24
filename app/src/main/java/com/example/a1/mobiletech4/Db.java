package com.example.a1.mobiletech4;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

public class Db {
    // Экземпляр вспомогательного класса для управления базой данных
    DbHelper dbHelper;
    // Контекст приложения для работы с ресурсами и базой данных
    Context context;
    // Курсор для перемещения по результатам запросов к базе данных
    Cursor cursor;
    // Экземпляр базы данных SQLite для выполнения операций
    SQLiteDatabase database;

    // Строка для хранения ключа шифрования
    String key;
    // Массив для хранения списка ресурсов (после дешифровки)
    String[] resourcesList;

    // Конструктор класса Db, инициализирует базу данных и DbHelper
    public Db(Context context) {
        this.context = context;
        dbHelper = new DbHelper(context);
        database = dbHelper.getWritableDatabase(); // Открываем базу данных на запись
    }

    // Метод для установки ключа шифрования
    public void setKey(String key_) {
        key = new String(key_); // Копируем строку ключа
    }

    // Метод для получения количества записей в базе данных
    public int getItemCount() {
        // Выполняем запрос ко всей таблице для получения всех записей
        cursor = database.query(dbHelper.TABLE_NAME, null, null, null, null, null, null);
        // Получаем количество записей
        int itemCount = cursor.getCount();
        cursor.close(); // Закрываем курсор

        return itemCount; // Возвращаем количество записей
    }

    // Метод для получения информации о записи по имени ресурса
    public String getItemInfoByResourceName(String resourceName, String columnName) {
        // Выполняем запрос для поиска строки по зашифрованному имени ресурса
        cursor = database.query(dbHelper.TABLE_NAME, null,
                dbHelper.KEY_RESOURCE + " = ?",
                new String[] {encrypt(resourceName)}, // Зашифрованное имя ресурса
                null, null, null);

        cursor.moveToFirst(); // Переходим к первой найденной записи
        int columnIndex = cursor.getColumnIndex(columnName); // Находим нужный столбец
        return decrypt(cursor.getString(columnIndex)); // Дешифруем и возвращаем значение столбца
    }

    // Метод для создания новой записи (ресурс, логин, пароль, заметка)
    public long createItem(String resourceName, String login, String password, String note) {

        // Если имя ресурса пустое, возвращаем 0 (ошибка)
        if(resourceName.length() == 0)
            return 0;

        // Создаем объект ContentValues для вставки данных в таблицу
        ContentValues cv = new ContentValues();
        // Зашифровываем и добавляем данные в соответствующие столбцы
        cv.put(dbHelper.KEY_RESOURCE, encrypt(resourceName));
        cv.put(dbHelper.KEY_LOGIN, encrypt(login));
        cv.put(dbHelper.KEY_PASSWORD, encrypt(password));
        cv.put(dbHelper.KEY_NOTE, encrypt(note));

        // Вставляем данные в таблицу и возвращаем ID новой записи
        return database.insert(dbHelper.TABLE_NAME, null, cv);
    }

    // Метод для удаления записи по имени ресурса
    public void deleteItem(String resourceName) {
        // Удаляем запись с указанным зашифрованным именем ресурса
        database.delete(
                dbHelper.TABLE_NAME,
                dbHelper.KEY_RESOURCE + " = ?",
                new String[] {encrypt(resourceName)});
    }

    // Метод для обновления имени ресурса
    public void updateResource(String resourceName, String newResource){
        ContentValues cv = new ContentValues();
        // Зашифровываем новое имя ресурса и обновляем его в таблице
        cv.put(DbHelper.KEY_RESOURCE, encrypt(newResource));
        String[] args = new String[]{encrypt(resourceName)}; // Старое имя ресурса
        database.update(DbHelper.TABLE_NAME, cv, "resource = ?", args); // Выполняем обновление
    }

    // Метод для обновления логина
    public void updateLogin(String resourceName, String newLogin){
        ContentValues cv = new ContentValues();
        // Зашифровываем новый логин и обновляем его в таблице
        cv.put(DbHelper.KEY_LOGIN, encrypt(newLogin));
        String[] args = new String[]{encrypt(resourceName)};
        database.update(DbHelper.TABLE_NAME, cv, "resource = ?", args);
    }

    // Метод для обновления пароля
    public void updatePassword(String resourceName, String newPassword){
        ContentValues cv = new ContentValues();
        // Зашифровываем новый пароль и обновляем его в таблице
        cv.put(DbHelper.KEY_PASSWORD, encrypt(newPassword));
        String[] args = new String[]{encrypt(resourceName)};
        database.update(DbHelper.TABLE_NAME, cv, "resource = ?", args);
    }

    // Метод для обновления заметки
    public void updateNote(String resourceName, String newNote){
        ContentValues cv = new ContentValues();
        // Зашифровываем новую заметку и обновляем ее в таблице
        cv.put(DbHelper.KEY_NOTE, encrypt(newNote));
        String[] args = new String[]{encrypt(resourceName)};
        database.update(DbHelper.TABLE_NAME, cv, "resource = ?", args);
    }

    // Метод для получения всех ресурсов (после дешифровки)
    public String[] getAllResources() {
        // Инициализируем массив для хранения всех ресурсов
        resourcesList = new String[this.getItemCount()];
        // Выполняем запрос ко всей таблице для получения всех записей
        cursor = database.query(dbHelper.TABLE_NAME, null, null, null, null, null, null);
        
        // Если записи найдены, начинаем их обработку
        if (cursor.getCount() > 0) {
            cursor.moveToFirst(); // Переходим к первой записи
            int nameColInd = cursor.getColumnIndex(dbHelper.KEY_RESOURCE); // Индекс столбца ресурса

            int i = 0;
            // Проходим по всем записям и дешифруем имена ресурсов
            do {
                String resourceName = decrypt(cursor.getString(nameColInd)); // Дешифруем имя ресурса
                resourcesList[i] = resourceName; // Сохраняем его в массив
                i++;
            } while (cursor.moveToNext()); // Переходим к следующей записи
        }

        cursor.close(); // Закрываем курсор
        return resourcesList; // Возвращаем массив ресурсов
    }

    // Метод для закрытия базы данных и вспомогательного класса
    public void close() {
        database.close(); // Закрываем базу данных
        dbHelper.close(); // Закрываем DbHelper
    }

    // Метод для шифрования текста
    String encrypt(String openedText) {

        CryptoProvider cp = new CryptoProvider(); // Экземпляр класса CryptoProvider для шифрования
        String cipherText = "";

        try {
            // Шифруем сообщение с использованием текущего ключа
            cipherText = cp.encryptMessage(openedText, key);
        }
        catch (Exception e) {
            // В случае ошибки выводим сообщение
            Toast.makeText(this.context, "encryptMessage failed", Toast.LENGTH_SHORT).show();
        }

        return cipherText; // Возвращаем зашифрованный текст
    }

    // Метод для дешифровки текста
    String decrypt(String cipherText) {

        CryptoProvider cp = new CryptoProvider(); // Экземпляр класса CryptoProvider для дешифровки
        String openedText = "";

        try {
            // Дешифруем сообщение с использованием текущего ключа
            openedText = cp.decryptMessage(cipherText, key);
        }
        catch (Exception e) {
            // В случае ошибки выводим сообщение
            Toast.makeText(this.context, "decryptMessage failed", Toast.LENGTH_SHORT).show();
        }

        return openedText; // Возвращаем дешифрованный текст
    }
}

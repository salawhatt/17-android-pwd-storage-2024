package com.example.a1.mobiletech4;

import android.content.Context;
import android.content.ContentValues;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {

    // Имя базы данных
    private static final String DATABASE_NAME = "urlsDB";
    // Имя таблицы, в которой будут храниться данные
    public static final String TABLE_NAME = "urls";

    // Названия столбцов таблицы
    public static final String KEY_ID = "_id"; // Уникальный идентификатор (первичный ключ)
    public static final String KEY_RESOURCE = "resource"; // Название ресурса (например, сайт или приложение)
    public static final String KEY_LOGIN = "login"; // Логин для ресурса
    public static final String KEY_PASSWORD = "password"; // Пароль для ресурса
    public static final String KEY_NOTE = "note"; // Дополнительные заметки

    // Версия базы данных (при обновлении структуры базы увеличиваем номер версии)
    private static final int DATABASE_VERSION = 1;

    // Конструктор DbHelper, передаем контекст и устанавливаем базовые параметры базы данных
    public DbHelper(Context context){
        // Вызов конструктора суперкласса SQLiteOpenHelper
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Метод, который вызывается при создании базы данных (например, при первом запуске приложения)
    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL-запрос для создания таблицы с указанными столбцами
        db.execSQL("CREATE TABLE " + TABLE_NAME + " ("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " // Уникальный идентификатор с автоинкрементом
                + KEY_RESOURCE + " TEXT, " // Столбец для ресурса (тип данных TEXT)
                + KEY_LOGIN + " TEXT, " // Столбец для логина
                + KEY_PASSWORD + " TEXT, " // Столбец для пароля
                + KEY_NOTE + " TEXT);"); // Столбец для заметок
    }

    // Метод для обновления структуры базы данных (вызывается при изменении версии базы)
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Удаляем старую таблицу, если она существует
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        // Создаем новую таблицу заново
        this.onCreate(db);
    }
}

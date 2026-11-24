package com.example.a1.mobiletech4;

import android.content.Context;
import android.content.SharedPreferences;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PreferencesSettings {

    // Имена файлов для хранения текущих и предыдущих настроек
    private static final String CURRENT_PREF_FILE = "settings_pref"; // Файл для текущих настроек
    private static final String PREVIOUS_PREF_FILE = "settings_pref_previous"; // Файл для предыдущих настроек

    // Метод для сохранения текущего кода (пароля) в SharedPreferences
    static void saveToPref(Context context, String str) {
        SharedPreferences.Editor editor; // Объект для редактирования SharedPreferences
        // Получаем доступ к текущим настройкам
        SharedPreferences sharedPref = context.getSharedPreferences(CURRENT_PREF_FILE, Context.MODE_PRIVATE);
        // Проверяем, запускалось ли приложение ранее
        boolean hasVisited = sharedPref.getBoolean("hasVisited", false);

        // Если приложение запускается впервые
        if (!hasVisited) {
            editor = sharedPref.edit(); // Получаем редактор
            editor.putBoolean("hasVisited", true); // Устанавливаем, что приложение было запущено
            editor.commit(); // Применяем изменения
        } else {
            // Если приложение уже запускалось, получаем доступ к предыдущим настройкам
            sharedPref = context.getSharedPreferences(PREVIOUS_PREF_FILE, Context.MODE_PRIVATE);
            editor = sharedPref.edit(); // Получаем редактор для предыдущих настроек
            // Сохраняем текущий код в предыдущие настройки
            editor.putString("code", getCurrentCode(context));
            editor.putBoolean("hasVisited", true); // Устанавливаем, что приложение было запущено
            editor.commit(); // Применяем изменения
        }

        // Сохраняем новый код в текущие настройки
        sharedPref = context.getSharedPreferences(CURRENT_PREF_FILE, Context.MODE_PRIVATE);
        editor = sharedPref.edit(); // Получаем редактор для текущих настроек
        editor.putString("code", str); // Сохраняем код
        editor.commit(); // Применяем изменения
    }

    // Метод для получения текущего кода (пароля) из текущих настроек
    static String getCurrentCode(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(CURRENT_PREF_FILE, Context.MODE_PRIVATE);
        String defaultValue = ""; // Значение по умолчанию, если код не найден
        return sharedPref.getString("code", defaultValue); // Возвращаем текущий код
    }

    // Метод для получения предыдущего кода (пароля) из предыдущих настроек
    static String getPreviousCode(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREVIOUS_PREF_FILE, Context.MODE_PRIVATE);
        String defaultValue = ""; // Значение по умолчанию

        // Проверяем, были ли ранее установлены текущие настройки
        boolean hasVisitedCurrent = sharedPref.getBoolean("hasVisited", false);
        if(!hasVisitedCurrent) {
            return new String(""); // Если нет, возвращаем пустую строку
        }

        return sharedPref.getString("code", defaultValue); // Возвращаем предыдущий код
    }

    // Метод для генерации MD5-хэша строки (используется для хэширования паролей)
    public static final String md5(final String s) {
        final String MD5 = "MD5"; // Алгоритм хэширования
        try {
            // Создаем объект для вычисления MD5-хэша
            MessageDigest digest = java.security.MessageDigest.getInstance(MD5);
            digest.update(s.getBytes()); // Обрабатываем строку
            byte messageDigest[] = digest.digest(); // Получаем хэш в виде массива байт

            // Преобразуем массив байт в шестнадцатеричную строку
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h; // Дополняем нулями, если необходимо
                hexString.append(h); // Добавляем хэш к строке
            }
            return hexString.toString(); // Возвращаем хэш-строку

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace(); // Обработка исключений
        }
        return ""; // В случае ошибки возвращаем пустую строку
    }

    // Метод для удаления текущего кода (пароля) из настроек
    static int deleteCode(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(CURRENT_PREF_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear(); // Очищаем все данные из файла настроек
        editor.commit(); // Применяем изменения
        return 1; // Возвращаем успех операции
    }

    // Метод для сохранения старого кода (пароля) перед изменением
    static void saveOldCode(Context context, String oldCode) {
        SharedPreferences.Editor editor; // Объект для редактирования
        SharedPreferences sharedPref; // Объект для хранения настроек

        sharedPref = context.getSharedPreferences(CURRENT_PREF_FILE, Context.MODE_PRIVATE);
        editor = sharedPref.edit(); // Получаем редактор
        editor.putString("code_old", oldCode); // Сохраняем старый код
        editor.commit(); // Применяем изменения
    }

    // Метод для удаления старого кода
    static void deleteOldCode(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(CURRENT_PREF_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove("code_old"); // Удаляем старый код из настроек
        editor.commit(); // Применяем изменения
    }

    // Метод для получения старого кода из настроек
    static String getOldCode(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(CURRENT_PREF_FILE, Context.MODE_PRIVATE);
        String defaultValue = ""; // Значение по умолчанию
        return sharedPref.getString("code_old", defaultValue); // Возвращаем старый код
    }
}
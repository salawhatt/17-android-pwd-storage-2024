package com.example.a1.mobiletech4;

import android.util.Base64;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;

public class CryptoProvider {
    // Метод для шифрования сообщения
    public String encryptMessage(String message, String key)
            throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException,
            BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        
        // Вектор инициализации (IV) должен быть 16 байт для AES
        String IV = "!a3edr45!a3edr45"; 
        
        // Преобразуем строку ключа в массив байт
        final byte[] keyBytes = key.getBytes();
        // Преобразуем строку сообщения в массив байт
        final byte[] messageBytes = message.getBytes();

        // Создаем спецификацию секретного ключа для AES
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
        // Создаем объект Cipher для алгоритма AES в режиме CBC с PKCS5 паддингом
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        
        // Инициализируем шифратор в режиме шифрования с заданным ключом и вектором инициализации
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(IV.getBytes()));
        // Выполняем шифрование и получаем зашифрованный массив байт
        final byte[] resultBytes = cipher.doFinal(messageBytes);
        // Кодируем зашифрованный массив байт в строку Base64 и возвращаем
        return Base64.encodeToString(resultBytes, Base64.DEFAULT);
    }

    // Метод для дешифрования сообщения
    public String decryptMessage(String message, String key)
            throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException,
            BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        
        // Вектор инициализации (IV) должен быть 16 байт для AES
        String IV = "!a3edr45!a3edr45"; 
        
        // Преобразуем строку ключа в массив байт
        final byte[] keyBytes = key.getBytes();
        // Декодируем строку Base64 в массив зашифрованных байт
        final byte[] encryptedBytes = Base64.decode(message, Base64.DEFAULT);
        
        // Создаем спецификацию секретного ключа для AES
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
        // Создаем объект Cipher для алгоритма AES в режиме CBC с PKCS5 паддингом
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        
        // Инициализируем дешифратор в режиме дешифрования с заданным ключом и вектором инициализации
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(IV.getBytes()));
        // Выполняем дешифрование и получаем массив байт результата
        final byte[] resultBytes = cipher.doFinal(encryptedBytes);
        // Преобразуем массив байт в строку и возвращаем
        return new String(resultBytes);
    }
}
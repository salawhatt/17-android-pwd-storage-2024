import os

# Имя выходного файла
output_file = 'all_java_files.txt'

# Открываем выходной файл для записи
with open(output_file, 'w', encoding='utf-8') as outfile:
    # Проходим по всем файлам в текущей директории
    for filename in os.listdir('.'):
        # Проверяем, что файл имеет расширение .java
        if filename.endswith('.java'):
            # Записываем в выходной файл название исходного файла в виде комментария
            outfile.write(f"// ========== {filename} ========== //\n\n")
            
            # Открываем java файл и копируем его содержимое
            with open(filename, 'r', encoding='utf-8') as infile:
                outfile.write(infile.read())
            
            # Добавляем разделитель между файлами
            outfile.write("\n\n// ============================= //\n\n")

print(f"Все файлы .java собраны в {output_file}")

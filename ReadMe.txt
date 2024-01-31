sudo systemctl start mongod.service - запуск MongoDB
sudo systemctl start postgresql.service - запуск PostgreSQL

Excel файл с исходными данными можно расположить по следующему пути: src/main/resources/excel/

Команды для запуска приложения:
java -DconfigPath=<путь к файлу со свойствами environment.properties> -Dlog4jPath=<путь к файлу конфигураций логгера log4j2.properties> -jar <путь к jar файлу> -dType <датапровайдер (CSV, XML, Postgres)> <операция> <данные>

Если не указывать configPath и log4jPath, то будут использованы настройки по умолчанию.

Options:

-cms - Создание основного расписания с занятиями.
                                                                              
-crs <start_date end_date export_to_excel send_email> - Создание расписания пересдач для студентов.
                                                                              
-d <entity_type entity_id> - Удаление записи по указанному идентификатору и типу сущности. Entity_type: student, teacher, group, subject.
                                                                              
-dsu <type schedule_unit_id> - Удаление ячейки расписания по указанному идентификатору и типу расписания (MAIN or RETAKE).
                                                                              
-dt <path_to_file> - Выполнение преобразования данных из Excel формата в другой (CSV, XML, Postgres).
                                                                              
-dType <data_type> - Указание типа данных (CSV, XML, Postgres). По умолчанию XML     
                                                                                                                                    
-help - Вспомогательная информация по использованию данного сервиса
                                                                              
-ng <group_number course level_of_training busy_day students> - Создание новой записи о группе студентов.
                
-ns <subject_name control_type> - Создание новой записи о предмете.   
                                           
-nst <last_name first_name patronymic email average_score> - Создание новой записи о студенте.
                  
-nsu <schedule_type date_time subject_id location teacher_id group_number> - Создание новой записи о занятии в расписании.   
                                                                             
-nt <last_name first_name patronymic email busy_day> - Создание новой записи о преподавателе.
  
-pg - Вывод на экран всех групп.

-ps - Вывод на экран всех предметов.

-pst - Вывод на экран всех студентов.

-pt - Вывод на экран всех преподавателей. 

-psu <schedule_type> - Вывод на экран всех ячеек расписания (schedule_type = MAIN or RETAKE).
                                                                             
------------------------------------------------------------------------------------------------------------------------------------------
Примеры команд:

Добавление студента:

java -DconfigPath="src/main/resources/environment.properties" -Dlog4jPath="src/main/resources/log4j2.properties" -jar out/artifacts/RetakeScheduler_jar/RetakeScheduler.jar -dType Postgres -nst Ivanov Ivan Ivanovich ivanov@mail.ru 100 

Добавление преподавателя:

java -DconfigPath="src/main/resources/environment.properties" -Dlog4jPath="src/main/resources/log4j2.properties" -jar out/artifacts/RetakeScheduler_jar/RetakeScheduler.jar -dType Postgres -nt Ivanov Ivan Ivanovich ivanov@mail.ru 2023-12-12

Добавление группы:

java -DconfigPath="src/main/resources/environment.properties" -Dlog4jPath="src/main/resources/log4j2.properties" -jar out/artifacts/RetakeScheduler_jar/RetakeScheduler.jar -dType Postgres -ng testNumber 3 Bachelor 2023-12-01 "{Ivanov Ivan Ivanovich ivan@mail.ru 100},{Petrov Petr Petrovich petrov@mail.ru 85.5},{Sidorov Pavel Vasilievich sidorov@mail.ru 76.3}"

Студенты при добавлении группы выделяются кавычками; каждый студент в фигурных скобках; аргументы студентов пишутся через пробел.

Добавление предмета:

java -DconfigPath="src/main/resources/environment.properties" -Dlog4jPath="src/main/resources/log4j2.properties" -jar out/artifacts/RetakeScheduler_jar/RetakeScheduler.jar -dType Postgres -ns Math Exam

Добавление ячейки расписания:

java -DconfigPath="src/main/resources/environment.properties" -Dlog4jPath="src/main/resources/log4j2.properties" -jar out/artifacts/RetakeScheduler_jar/RetakeScheduler.jar -dType Postgres -nsu MAIN '2023-12-20 15:55' d28ca691-388f-4535-bd10-5289ec12b38b IVTiPT b1681b53-ad10-4c55-b234-1312f3f01acb testNumber

Удаление записи:

java -DconfigPath="src/main/resources/environment.properties" -Dlog4jPath="src/main/resources/log4j2.properties" -jar out/artifacts/RetakeScheduler_jar/RetakeScheduler.jar -dType Postgres -d student 97b364a2-f45d-495a-9d71-0fec97cc2992

Удаление ячейки расписания:

java -DconfigPath="src/main/resources/environment.properties" -Dlog4jPath="src/main/resources/log4j2.properties" -jar out/artifacts/RetakeScheduler_jar/RetakeScheduler.jar -dType Postgres -dsu MAIN aff580a8-f50b-4cd6-9eb9-604a2dfbd149

Преобразование данных:

java -DconfigPath="src/main/resources/environment.properties" -Dlog4jPath="src/main/resources/log4j2.properties" -jar out/artifacts/RetakeScheduler_jar/RetakeScheduler.jar -dType Postgres -dt 'src/main/resources/excel/Задолжники лето 2022-2023 .xls.xlsx'

Составление расписания пересдач:

java -DconfigPath="src/main/resources/environment.properties" -Dlog4jPath="src/main/resources/log4j2.properties" -jar out/artifacts/RetakeScheduler_jar/RetakeScheduler.jar -dType Postgres -crs 2023-12-01 2023-12-25 true false

При сохранении расписания в Excel файл, он сохраняется в директорию output

Составление основного расписания:

java -DconfigPath="src/main/resources/environment.properties" -Dlog4jPath="src/main/resources/log4j2.properties" -jar out/artifacts/RetakeScheduler_jar/RetakeScheduler.jar -dType Postgres -cms

Вывод информации по использованию системы:

java -DconfigPath="src/main/resources/environment.properties" -Dlog4jPath="src/main/resources/log4j2.properties" -jar out/artifacts/RetakeScheduler_jar/RetakeScheduler.jar -help

Вывод всех студентов:

java -DconfigPath="src/main/resources/environment.properties" -Dlog4jPath="src/main/resources/log4j2.properties" -jar out/artifacts/RetakeScheduler_jar/RetakeScheduler.jar -dType Postgres -pst

Вывод всех преподавателей:

java -DconfigPath="src/main/resources/environment.properties" -Dlog4jPath="src/main/resources/log4j2.properties" -jar out/artifacts/RetakeScheduler_jar/RetakeScheduler.jar -dType Postgres -pt

Вывод всех предметов:

java -DconfigPath="src/main/resources/environment.properties" -Dlog4jPath="src/main/resources/log4j2.properties" -jar out/artifacts/RetakeScheduler_jar/RetakeScheduler.jar -dType Postgres -ps

Вывод всех групп:

java -DconfigPath="src/main/resources/environment.properties" -Dlog4jPath="src/main/resources/log4j2.properties" -jar out/artifacts/RetakeScheduler_jar/RetakeScheduler.jar -dType Postgres -pg

Вывод всех ячеек расписания:

java -DconfigPath="src/main/resources/environment.properties" -Dlog4jPath="src/main/resources/log4j2.properties" -jar out/artifacts/RetakeScheduler_jar/RetakeScheduler.jar -dType Postgres -psu



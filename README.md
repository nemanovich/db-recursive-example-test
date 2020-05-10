#Работа с иерархическими запросами Oracle

## Навигация
Вариант с использованием connect-by: [select_cfo_using_connect_by.sql](https://github.com/nemanovich/db-recursive-example-test/blob/master/src/main/resources/select_cfo_using_connect_by.sql)

Вариант с with: [select_cfo_using_with.sql](https://github.com/nemanovich/db-recursive-example-test/blob/master/src/main/resources/select_cfo_using_with.sql)

Процедуры для 2 части лежат в [test/resources/db]([select_cfo_using_with.sql](https://github.com/nemanovich/db-recursive-example-test/blob/master/src/main/resources/select_cfo_using_with.sql))

[Тесты](https://github.com/nemanovich/db-recursive-example-test/tree/master/src/test/java/com/github/nemanovich/db/oracle/recursive/tests)

## Ограничения на исходные данные
- консистентность
- отсутствие циклов
- корневые узлы имеют родителя с id 0 или NULL
- корневые узлы имеют CFO
- для tricky варианта с connect-by - отсутствие в значениях cfo разделителя ("|")
 и нестрогое ограничение на вложенность/длину значений CFO (из-за max размера SYS_CONNECT_BY_PATH в 4000 символов)  
 
 Для запуска требуется Docker.
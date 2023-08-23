# java-filmorate
Template repository for Filmorate project.

Ссылка на файл со схемой базы данных:
https://github.com/Kolobok37/java-filmorate/blob/8daf9d20786fc7534dfe0ec0eed55292035d9edc/filmorateDiagramSQL.PNG

Пояснение к запросам бизнес-логики:

Запрос на общих друзей: запрос искользует таблицу friends, сначала извлекает из неё 2 таблицы
(все друзья 1 пользователя и все друзья 2 пользователя), а потом ищет пересечения.

Запрос на n популярных фильмов: таблица films сортируеться по полю rate

Всё взоиможействие между пользовтелями идёт через таблицы friends и request_friendship

Всё взоимодействие между пользователем и фильмом(лайки) идёт через балицу like_films
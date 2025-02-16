Разработано 2 сервиса:

Первый (*Порт 8081*):
1) Получает на вход ссылку на rtsp-видеопоток
2) Осуществлять нарезку кадров
3) Осуществлять кодирование кадра с целью дальнейшей отправки на второй сервис

Второй (*Порт 8082*):

Отправляет в ответ на кадр json, содержащий время принятия кадра, вес, ширину, высоту и 1 строку. Эти данные сохраняются в fileLog*текущая дата и время*.txt.


Оба сервиса разворачиваются в docker-контейнере. Общий вес двух образов - 2.2GB. Требуется наличие docker на устройстве. 
Для создания контейнера требуется ввести команду *docker-compose up --build* в корневой директории проекта. 

Для удобного взаимодействия создать веб-интерфейс (*http://localhost:8081/api/stream/*) с выводом информации о каждом обработанном кадре. Имеется возможность остановить обработку потока.

Небольшой список ссылок на rtsp-видеопотоки для тестирования:

1) rtsp://188.170.41.78:554

2) rtsp://37.230.146.42:554

3) rtsp://185.46.46.29:554

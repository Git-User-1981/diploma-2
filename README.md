# Интернет-магазин книг
1. [X] Добавление, редактирование и продажа книг, личный кабинет покупателя
2. [X] Интерфейс на двух языках, русском и английском, с возможностью выбора
3. [X] Интеграция с платежной системой ЮKassa
4. [X] Интеграция с СМС-сервисом SMSC
5. [X] Интеграция с почтовым сервисом Яндекс
6. [X] Интеграция с google, для регистрации и входа пользователей

### Используемые технологии
Maven, Java Spring Boot Framework, Spring security/data/test/aop, JPA, JSOUP, Postgres, Hibernate, Liquibase, JWT, OAuth2.0, Thymeleaf, REST Api, Swagger, JUnit, Mockito, Selenium 

### Требования
* СУБД Postgres, версии не ниже 15
* Java, версии не ниже 17;

### Запуск книжного магазина
Для запуска необходимо выполнить команду:

`java -jar BookShop.jar`

> Если в консоль выводятся не читаемые символы, то используйте эту команду для запуска: `java -Dfile.encoding=cp866 -jar SearchEngine.jar`

>  Исполняемый файл [BookShop.jar](https://github.com/Git-User-1981/diploma-2/raw/main/dist/BookShop.jar) находится в папке [dist](https://github.com/Git-User-1981/diploma-2/tree/main/dist) данного репозитория.

##### Требуемые переменные окружения для запуска магазина:
<table>
<thead>
<tr>
<th>Название</th>
<th>Значение</th>
<th>Описание</th>
</tr>
</thead>
<tbody>
<tr>
<td>PG_HOST</td>
<td>HOST:PORT</td>
<td>Адрес и порт подключения к СУБД Postgres</td>
</tr>
<tr>
<td>PG_USER</td>
<td>USER_NAME</td>
<td>Имя пользователя</td>
</tr>
<tr>
<td>PG_PASSWORD</td>
<td>PASSWORD</td>
<td>Пароль</td>
</tr>
<tr>
<td>LIQUIBASE_START</td>
<td>true/false</td>
<td>Запуск версирования СУБД Liquibase</td>
</tr>
<tr>
<td>JWT_KEY</td>
<td>KEY</td>
<td>Ключ шифрования токена</td>
</tr>
<tr>
<td>GOOGLE_ID</td>
<td>Client ID</td>
<td>Идентификатор клиента google</td>
</tr>
<tr>
<td>GOOGLE_SECRET</td>
<td>Client secret</td>
<td>Секретный код клиента google</td>
</tr>
<tr>
<td>MAIL_HOST</td>
<td>HOST</td>
<td>Адрес почтового сервера</td>
</tr>
<tr>
<td>MAIL_PORT</td>
<td>PORT</td>
<td>Порт почтового сервера</td>
</tr>
<tr>
<td>MAIL_USERNAME</td>
<td>USERNAME</td>
<td>Имя пользователя (адрес почты)</td>
</tr>
<tr>
<td>MAIL_PASSWORD</td>
<td>PASSWORD</td>
<td>Пароль почтового сервиса</td>
</tr>
<tr>
<td>MAIL_DEBUG</td>
<td>true/false</td>
<td>Вывод отладочных сообщений в лог</td>
</tr>
<tr>
<td>PAYMENT_SHOP_ID</td>
<td>SHOP_ID</td>
<td>Платежный идентификатор</td>
</tr>
<tr>
<td>PAYMENT_KEY</td>
<td>KEY</td>
<td>Секретный ключ</td>
</tr>
<tr>
<td>SMSC_LOGIN</td>
<td>LOGIN</td>
<td>Логин в сервисе SMSC</td>
</tr>
<tr>
<td>SMSC_PASSWORD</td>
<td>PASSWORD</td>
<td>Пароль в сервисе SMSC</td>
</tr>
<tr>
<td>SMSC_DEBUG</td>
<td>true/false</td>
<td>Вывод отладочных сообщений в лог</td>
</tr>
</tbody>
</table>

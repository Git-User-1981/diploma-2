package ru.book_shop.services.sms;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.io.*;

@Slf4j
@Service
public class SmsService {
    @Value("${smsc.login}")
    private String smscLogin;                                   // логин клиента

    @Value("${smsc.password}")
    private String smscPassword;                               // пароль

    @Value("${smsc.debug}")
    private boolean smscDebug;                                 // флаг отладки

    private static final String SMSC_CHARSET = "utf-8";        // кодировка сообщения: koi8-r, windows-1251 или utf-8 (по умолчанию)
    private static final boolean SMSC_HTTPS   = false;         // использовать HTTPS протокол
    private static final boolean SMSC_POST    = false;         // Использовать метод POST

    /**
     * Отправка SMS
     *
     * @param phones   - список телефонов через запятую или точку с запятой
     * @param message  - отправляемое сообщение
     */
    public void sendSms(String phones, String message) {
        String[] m = {};

        try {
            m = smscSendCmd("cost=3&phones=" + URLEncoder.encode(phones, SMSC_CHARSET)
                    + "&mes=" + URLEncoder.encode(message, SMSC_CHARSET)
            );
        }
        catch (UnsupportedEncodingException e) {
            log.error("Указанная кодировка символов не поддерживается!\n" + e + "\n");
        }

        if (m.length > 1) {
            if (smscDebug) {
                if (Integer.parseInt(m[1]) > 0) {
                    log.debug("Сообщение отправлено успешно. ID: " + m[0] + ", всего SMS: " + m[1] + ", стоимость: " + m[2] + ", баланс: " + m[3]);
                }
                else {
                    log.debug("Ошибка №" + Math.abs(Integer.parseInt(m[1])));
                    log.debug(Integer.parseInt(m[0])>0 ? (", ID: " + m[0]) : "");
                }
            }
        }
        else {
            log.error("Не получен ответ от сервера.");
        }

    }

    /**
     * Формирование и отправка запроса
     * @param arg - дополнительные параметры
     */

    private String[] smscSendCmd(String arg) {
        String ret = ",";

        try {
            String tmpUrl = (SMSC_HTTPS ? "https" : "http") + "://smsc.ru/sys/send.php?login=" + URLEncoder.encode(smscLogin, SMSC_CHARSET)
                    + "&psw=" + URLEncoder.encode(smscPassword, SMSC_CHARSET)
                    + "&fmt=1&charset=" + SMSC_CHARSET + "&" + arg;

            String url = tmpUrl;
            int i = 0;
            do {
                if (i++ > 0) {
                    url = tmpUrl;
                    url = url.replace("://smsc.ru/", "://www" + (i) + ".smsc.ru/");
                }
                ret = smscReadUrl(url);
            }
            while (ret.isEmpty() && i < 5);
        }
        catch (UnsupportedEncodingException e) {
            log.error("Указанная кодировка символов не поддерживается!\n" + e + "\n");
        }

        return ret.split(",");
    }

    /**
     * Чтение URL
     * @param url - ID сообщения
     * @return line - ответ сервера
     */
    private String smscReadUrl(String url) {

        StringBuilder line = new StringBuilder();
        String realUrl = url;
        String[] param = {};
        boolean isPost = (SMSC_POST || url.length() > 2000);

        if (isPost) {
            param = url.split("\\?",2);
            realUrl = param[0];
        }

        try {
            InputStreamReader reader = getInputStreamReader(realUrl, isPost, param);

            int ch;
            while ((ch = reader.read()) != -1) {
                line.append((char) ch);
            }

            reader.close();
        }
        catch (MalformedURLException e) { // Неверный урл, протокол...
            log.error("Ошибка при обработке URL-адреса!\n" + e + "\n");
        }
        catch (IOException e) {
            log.error("Ошибка при операции передачи/приема данных!\n" + e + "\n");
        }

        return line.toString();
    }

    private static InputStreamReader getInputStreamReader(String realUrl, boolean isPost, String[] param) throws IOException {
        URL u = new URL(realUrl);
        InputStream is;

        if (isPost){
            URLConnection conn = u.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter os = new OutputStreamWriter(conn.getOutputStream(), StandardCharsets.UTF_8);
            os.write(param[1]);
            os.flush();
            os.close();

            is = conn.getInputStream();
        }
        else {
            is = u.openStream();
        }

        return new InputStreamReader(is, StandardCharsets.UTF_8);
    }
}

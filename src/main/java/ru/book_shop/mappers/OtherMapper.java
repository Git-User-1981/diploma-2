package ru.book_shop.mappers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.book_shop.dto.DocumentDTO;
import ru.book_shop.dto.FaqDTO;
import ru.book_shop.entities.other.Document;
import ru.book_shop.entities.other.Faq;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class OtherMapper {
    private static String documentsURI;
    private static String documentsCoverPath;

    @Value("${app.link.documents}")
    public void setDocumentsPath(String path){
        OtherMapper.documentsURI = path + "/";
    }

    @Value("${app.path.documents.cover}")
    public void setDocumentsCoverPath(String path){
        OtherMapper.documentsCoverPath = path;
    }

    private OtherMapper(){}

    public static FaqDTO toFaqDTO(Faq faq) {
        return FaqDTO
                .builder()
                .answer(faq.getAnswer())
                .question(faq.getQuestion())
                .build();
    }

    public static DocumentDTO toDocumentDTO(Document document) {
        return DocumentDTO
                .builder()
                .slug(documentsURI + document.getSlug())
                .title(document.getTitle())
                .shortText(createShortText(document.getText()))
                .text(document.getText())
                .img(documentsCoverPath + document.getSlug() + ".jpg")
                .build();
    }

    private static String createShortText(String text) {
        Matcher m = Pattern.compile("<p>(.*)</p>").matcher(text);
        if (m.find()) {
            final String result = m.group(1);
            return result.length() > 808 ? result.substring(0, 808) : result;
        }
        return text;
    }
}

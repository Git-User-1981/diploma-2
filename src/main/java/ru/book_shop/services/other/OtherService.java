package ru.book_shop.services.other;

import ru.book_shop.dto.DocumentDTO;
import ru.book_shop.dto.FaqDTO;
import ru.book_shop.dto.FeedbackRequestDTO;

import java.security.Principal;
import java.util.List;

public interface OtherService {
    List<DocumentDTO> getListDocuments();
    DocumentDTO getDocument(String slug);
    List<FaqDTO> geFaqList();
    void feedback(Principal principal, FeedbackRequestDTO form);
}

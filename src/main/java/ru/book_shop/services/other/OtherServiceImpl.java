package ru.book_shop.services.other;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.book_shop.dto.DocumentDTO;
import ru.book_shop.dto.FaqDTO;
import ru.book_shop.dto.FeedbackRequestDTO;
import ru.book_shop.entities.other.Document;
import ru.book_shop.entities.other.Message;
import ru.book_shop.exceptions.PageCallException;
import ru.book_shop.mappers.OtherMapper;
import ru.book_shop.repositories.DocumentsRepository;
import ru.book_shop.repositories.FaqRepository;
import ru.book_shop.repositories.MessagesRepository;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OtherServiceImpl implements OtherService {

    private final DocumentsRepository documentsRepository;
    private final FaqRepository faqRepository;
    private final MessagesRepository messagesRepository;

    @Override
    public List<DocumentDTO> getListDocuments() {
        return documentsRepository
                .findAllByOrderBySortIndexAsc()
                .stream()
                .map(OtherMapper::toDocumentDTO)
                .toList();
    }

    @Override
    public DocumentDTO getDocument(String slug) {
        final Document document = documentsRepository
                .findBySlug(slug)
                .orElseThrow(() -> new PageCallException("page.error.document.not-found"));

        return DocumentDTO
                .builder()
                .title(document.getTitle())
                .text(document.getText())
                .build();
    }

    @Override
    public List<FaqDTO> geFaqList() {
        return faqRepository
                .findAllByOrderBySortIndexAsc()
                .stream()
                .map(OtherMapper::toFaqDTO)
                .toList();
    }

    @Override
    public void feedback(Principal principal, FeedbackRequestDTO form) {
        final Message message = Message
                .builder()
                .subject(form.getTopic())
                .text(form.getMessage())
                .time(LocalDateTime.now())
                .build();

        if (principal == null) {
            if (form.getName().isBlank()) {
                throw new PageCallException("page.contacts.form.name.error");
            }
            if (form.getMail().isBlank()) {
                throw new PageCallException("page.contacts.form.mail.error");
            }

            message.setUserId(null);
            message.setName(form.getName());
            message.setEmail(form.getMail());
        }
        else {
            message.setUserId(Integer.parseInt(principal.getName()));
        }

        messagesRepository.save(message);
    }
}

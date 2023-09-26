package ru.book_shop.services.storage;

import org.springframework.web.multipart.MultipartFile;
import ru.book_shop.dto.BookFileDTO;
import ru.book_shop.dto.BookFilesDTO;

import java.util.List;

public interface FileStorageService {
    String saveFile(MultipartFile file, StorageType type);
    List<BookFilesDTO> getBookFiles(Integer bookId);
    BookFileDTO getBookFileByHash(String hash, Integer userId);
}

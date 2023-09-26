package ru.book_shop.services.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.book_shop.dto.BookFileDTO;
import ru.book_shop.dto.BookFilesDTO;
import ru.book_shop.entities.book.file.FileDownload;
import ru.book_shop.exceptions.PageCallException;
import ru.book_shop.mappers.BooksFilesMapper;
import ru.book_shop.entities.book.file.BookFile;
import ru.book_shop.repositories.BooksFilesRepository;
import ru.book_shop.repositories.FileDownloadRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileStorageServiceImpl implements FileStorageService {
    @Value("${app.path.upload.covers}")
    private String coversPath;

    @Value("${app.path.upload.authors}")
    private String photosPath;

    @Value("${app.path.download.books}")
    private String booksLocationPath;

    private final BooksFilesRepository filesRepository;
    private final FileDownloadRepository fileDownloadRepository;

    @Override
    public String saveFile(MultipartFile file, StorageType type) {
        if (file.isEmpty() || file.getSize() == 0) {
            throw new PageCallException("page.error.upload.file.empty");
        }

        final String uploadPath = switch (type) {
            case PHOTO -> photosPath;
            case FILE -> booksLocationPath;
            default -> coversPath;
        };

        try {
            Files.createDirectories(Paths.get(uploadPath));
            file.transferTo(Paths.get(uploadPath, file.getOriginalFilename()));
        }
        catch (IOException e) {
            throw new PageCallException("page.error.upload.file.save", e);
        }

        return file.getOriginalFilename();
    }

    @Override
    public List<BookFilesDTO> getBookFiles(Integer bookId) {
        return BooksFilesMapper.toDTO(filesRepository.getBookFiles(bookId));
    }

    @Override
    public BookFileDTO getBookFileByHash(String hash, Integer userId) {
        final BookFile bookFile = filesRepository.getUserBookFile(hash, userId).orElseThrow(() -> new PageCallException("page.error.book.not-found"));

        if (!Files.exists(Paths.get(booksLocationPath, bookFile.getPath()))) {
            throw new PageCallException("page.error.book.file-not-found");
        }

        try {
            final File file = new File(booksLocationPath, bookFile.getPath());
            final InputStream inputStream = new FileInputStream(file);
            final String mimeType = Files.probeContentType(file.toPath());

            BookFileDTO dto = new BookFileDTO();
            dto.setFileName(bookFile.getPath());
            dto.setMimeType(mimeType != null ? MediaType.parseMediaType(mimeType) : MediaType.APPLICATION_OCTET_STREAM);
            dto.setData(new InputStreamResource(inputStream));
            dto.setLength(file.length());

            userBookDownloadLog(bookFile.getBook().getId(), userId);

            return dto;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void userBookDownloadLog(Integer bookId, Integer userId) {
        final FileDownload fileDownload = fileDownloadRepository
                .findByBookIdAndUserId(bookId, userId)
                .orElse(FileDownload.builder().bookId(bookId).userId(userId).build());

        fileDownload.setCount(fileDownload.getCount() + 1);
        fileDownloadRepository.save(fileDownload);
    }
}

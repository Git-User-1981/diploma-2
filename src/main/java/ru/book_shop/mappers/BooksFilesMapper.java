package ru.book_shop.mappers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.book_shop.dto.BookFilesDTO;
import ru.book_shop.dto.IBookFilesDTO;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@Component
public class BooksFilesMapper {
    private static String booksLocationPath;
    private static String booksDownloadPath;

    @Value("${app.path.download.books}")
    public void setBooksPath(String booksLocationPath) {
        BooksFilesMapper.booksLocationPath = booksLocationPath;
    }
    @Value("${app.path.books.download}")
    public void setBooksDownloadPath(String booksDownloadPath) {
        BooksFilesMapper.booksDownloadPath = booksDownloadPath;
    }

    private BooksFilesMapper(){}

    public static List<BookFilesDTO> toDTO(List<IBookFilesDTO> iFiles) {
        List<BookFilesDTO> list = new ArrayList<>();
        for (IBookFilesDTO iFile : iFiles) {
            if (Files.exists(Paths.get(booksLocationPath + iFile.getPath()))) {
                BookFilesDTO dto = new BookFilesDTO();
                dto.setDownloadPath(booksDownloadPath + iFile.getHash());
                dto.setFileName(iFile.getPath());
                dto.setType(iFile.getType());
                dto.setTypeDescription(iFile.getDescription());
                dto.setSize(getFileSize(Paths.get(booksLocationPath + iFile.getPath())));
                list.add(dto);
            }
        }
        return list;
    }

    private static String getFileSize(Path fullFilePath) {
        try {
            long size = Files.size(fullFilePath);

            if (size <= 0) {
                return "0";
            }

            final String[] units = new String[] {"B", "kB", "MB", "GB", "TB"};
            int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
            return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

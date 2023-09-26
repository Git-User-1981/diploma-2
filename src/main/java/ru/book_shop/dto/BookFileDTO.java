package ru.book_shop.dto;

import lombok.Data;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;

@Data
public class BookFileDTO {
    private String fileName;
    private InputStreamResource data;
    private MediaType mimeType;
    private long length;
}

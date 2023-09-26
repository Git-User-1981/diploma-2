package ru.book_shop.dto;

import lombok.Data;

@Data
public class BookFilesDTO {
    private String downloadPath;
    private String fileName;
    private String type;
    private String typeDescription;
    private String size;
}

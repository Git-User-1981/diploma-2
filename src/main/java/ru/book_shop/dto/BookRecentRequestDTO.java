package ru.book_shop.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
public class BookRecentRequestDTO extends BookRequestDTO {
    @Schema(description = "Дата начала периода", format = "dd.mm.yyyy", pattern = "dd.mm.yyyy")
    @DateTimeFormat(pattern="dd.MM.yyyy")
    @PastOrPresent(message = "api.error.book.from")
    private LocalDate from;

    @Schema(description = "Дата окончания периода", format = "dd.mm.yyyy", pattern = "dd.mm.yyyy")
    @DateTimeFormat(pattern="dd.MM.yyyy")
    @PastOrPresent(message = "api.error.book.to")
    private LocalDate to;
}

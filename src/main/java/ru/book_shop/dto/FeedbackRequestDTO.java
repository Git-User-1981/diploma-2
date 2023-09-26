package ru.book_shop.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FeedbackRequestDTO {
    private String name;
    private String mail;

    @NotBlank(message = "page.contacts.form.topic.error")
    private String topic;

    @NotBlank(message = "page.contacts.form.message.error")
    private String message;
}

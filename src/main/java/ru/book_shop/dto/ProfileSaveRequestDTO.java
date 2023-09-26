package ru.book_shop.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ProfileSaveRequestDTO {
    @NotBlank(message = "page.signup.form.name.error")
    String name;

    @NotBlank(message = "page.signup.form.phone.error")
    String phone;

    @NotBlank(message = "page.signup.form.mail.error")
    String mail;

    String password;

    String passwordReply;
}

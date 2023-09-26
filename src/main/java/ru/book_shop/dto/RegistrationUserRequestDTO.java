package ru.book_shop.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RegistrationUserRequestDTO {
    @NotBlank(message = "page.signup.form.name.error")
    String name;

    @NotBlank(message = "page.signup.form.phone.error")
    String phone;

    @NotBlank(message = "page.signup.form.phone.code.error")
    String phoneCode;

    @NotBlank(message = "page.signup.form.mail.error")
    String mail;

    @NotBlank(message = "page.signup.form.mail.code.error")
    String mailCode;

    @NotBlank(message = "page.signup.form.password.error")
    String password;
}

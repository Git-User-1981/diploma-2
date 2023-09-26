package ru.book_shop.mappers;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import ru.book_shop.dto.*;
import ru.book_shop.entities.enums.ContactType;
import ru.book_shop.entities.user.User;

import java.util.Optional;

@Component
public class UsersMapper {
    private UsersMapper(){}

    public static UserShortDTO toShortDTO(User user) {
        return Optional.ofNullable(user)
            .map(u -> UserShortDTO
                .builder()
                .id(u.getId())
                .hash(u.getHash())
                .name(u.getName())
                .role(u.getRole())
                .balance(u.getBalance())
                .build()
            )
            .orElse(null);
    }

    public static UserProfileDTO toProfileDTO(User user) {
        return Optional.ofNullable(user)
            .map(u -> {
                final UserProfileDTO dto = UserProfileDTO.builder().id(u.getId()).name(u.getName()).build();
                u.getContacts().forEach(contact -> {
                    if ((contact.getType().equals(ContactType.EMAIL))) {
                        dto.setEmail(contact.getContact());
                        dto.setEmailApproved(contact.getApproved());
                    }
                    else {
                        dto.setPhone(contact.getContact());
                        dto.setPhoneApproved(contact.getApproved());
                    }
                });
                return dto;
            })
            .orElse(null);
    }

    public static UsersPageDTO toUsersPageDTO(Page<IUsersDTO> usersPage) {
        return UsersPageDTO
                .builder()
                .count(usersPage.getTotalElements())
                .users(usersPage.getContent().stream().map(UsersMapper::toUsersDTO).toList())
                .build();
    }

    public static UsersDTO toUsersDTO(IUsersDTO user) {
        return UsersDTO
                .builder()
                .id(user.getId())
                .name(user.getName())
                .regTime(user.getRegTime())
                .role(user.getRole())
                .phone(StringUtils.defaultIfEmpty(user.getPhone(), ""))
                .email(StringUtils.defaultIfEmpty(user.getEmail(), ""))
                .balance(user.getBalance())
                .build();
    }
}

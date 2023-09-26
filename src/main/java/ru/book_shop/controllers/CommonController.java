package ru.book_shop.controllers;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import ru.book_shop.annotations.NotLogExecutionTime;
import ru.book_shop.dao.MenuItem;
import ru.book_shop.dto.UserShortDTO;
import ru.book_shop.mappers.UsersMapper;
import ru.book_shop.services.menu.MenuService;
import ru.book_shop.services.users.UserService;

import java.security.Principal;
import java.util.List;

@NotLogExecutionTime
@ControllerAdvice(basePackages={"ru.book_shop.controllers.web"})
public class CommonController {
    private final MenuService menuService;
    private final UserService userService;

    public CommonController(MenuService menuService, UserService userService) {
        this.menuService = menuService;
        this.userService = userService;
    }

    @ModelAttribute("menuItems")
    public List<MenuItem> menuItems() {
        return menuService.getMenuItems();
    }

    @ModelAttribute("menuBookCounts")
    public int[] menuBookCounts(Principal principal) {
        return principal == null ? menuService.getReservedBooksCount() : userService.getUserBooksCount();
    }

    @ModelAttribute("currentUser")
    public UserShortDTO getCurrentUser(Principal principal) {
        return principal == null ? null : UsersMapper.toShortDTO(userService.getCurrentUser(principal));
    }
}

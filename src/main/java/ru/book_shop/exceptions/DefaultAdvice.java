package ru.book_shop.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import ru.book_shop.dto.ApiErrorResponseDTO;
import ru.book_shop.services.menu.MenuService;
import ru.book_shop.services.users.UserService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class DefaultAdvice {
    @Value("${app.link.signin}")
    String signinUrl;

    @Value("${app.link.signup}")
    String signupUrl;

    @Value("${app.link.refresh-token}")
    String refreshTokenUrl;

    @Value("${app.link.registration}")
    String registrationUrl;

    @Value("${app.link.main}")
    String mainUrl;

    @Value("${app.auth.jwt.refresh-token.name}")
    String refreshTokenName;

    private final MessageSource messageSource;
    private final MenuService menuService;
    private final UserService userService;

    public DefaultAdvice(MessageSource messageSource, MenuService menuService, UserService userService) {
        this.messageSource = messageSource;
        this.menuService = menuService;
        this.userService = userService;
    }

    @ExceptionHandler(ApiCallException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleException(ApiCallException e) {
        String errorMessage = getErrorMessage(e.getMessage());

        log.warn(errorMessage, e);
        if (e.getOriginalError() != null) {
            log.warn(e.getOriginalError().getLocalizedMessage(), e.getOriginalError());
        }
        return ResponseEntity.status(e.getStatusCode()).body(new ApiErrorResponseDTO(errorMessage));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleException(RuntimeException e) {
        log.error(e.getLocalizedMessage(), e);
        return ResponseEntity.internalServerError().body(new ApiErrorResponseDTO(getErrorMessage("api.error.unexpected")));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleException(BindException e) {
        List<FieldError> fieldErrors = e.getFieldErrors();
        StringJoiner errorMessage = new StringJoiner("; ");

        if (fieldErrors.isEmpty()) {
            errorMessage.add(getErrorMessage("api.error.unexpected"));
        }
        else {
            for (FieldError fieldError : fieldErrors) {
                String errMsgKey = fieldError.getDefaultMessage();
                if (errMsgKey == null) {
                    errorMessage.add(getErrorMessage("api.error.key.not.set"));
                }
                else {
                    String errCode = fieldError.getCode();
                    if (errCode != null && errCode.contains("typeMismatch")) {
                        errMsgKey = "api.error.mismatch";
                    }
                    errorMessage.add(getErrorMessage(errMsgKey, new Object[]{fieldError.getField()}));
                }
            }
        }

        log.info(errorMessage.toString(), e);
        return ResponseEntity.badRequest().body(new ApiErrorResponseDTO(errorMessage.toString()));
    }

    @ExceptionHandler(PageCallException.class)
    public String handleException(PageCallException e, Model model) {
        String errorMessage = getErrorMessage(e.getMessage());

        log.warn(errorMessage, e);
        if (e.getOriginalError() != null) {
            log.warn(e.getOriginalError().getLocalizedMessage(), e.getOriginalError());
        }

        model.addAttribute("fragment", "errorPage");
        model.addAttribute("menuItems", menuService.getMenuItems());
        model.addAttribute("menuBookCounts", userService.getUserBooksCount());
        model.addAttribute("error", errorMessage);
        return "index";
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleException(MethodArgumentTypeMismatchException e) {
        String errorMessage = getErrorMessage("api.error.mismatch-field", new Object[]{e.getName()});

        log.warn(errorMessage, e);
        return ResponseEntity.badRequest().body(new ApiErrorResponseDTO(errorMessage));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleException(ConstraintViolationException e) {
        String errorMessage = e.getConstraintViolations()
            .stream()
            .map(c -> getErrorMessage(c.getMessage(), pathToArgs(c.getPropertyPath())))
            .collect(Collectors.joining("; "));

        log.warn(errorMessage, e);
        return ResponseEntity.badRequest().body(new ApiErrorResponseDTO(errorMessage));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleException(HttpMessageNotReadableException e) {
        String errorMessage = getErrorMessage("api.error.mismatch");

        log.warn(errorMessage, e);
        return ResponseEntity.badRequest().body(new ApiErrorResponseDTO(errorMessage));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleException(MaxUploadSizeExceededException e, Model model) {
        String errorMessage = getErrorMessage("page.error.upload.file.maximum");

        log.error(errorMessage, e);

        model.addAttribute("fragment", "errorPage");
        model.addAttribute("menuItems", menuService.getMenuItems());
        model.addAttribute("menuBookCounts", userService.getUserBooksCount());
        model.addAttribute("error", errorMessage);
        return "index";
    }

    @ExceptionHandler(AccessDeniedException.class)
    public void handleException(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        final String path = request.getServletPath();
        if (path.startsWith("/api")) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().print(new ObjectMapper().writeValueAsString(new ApiErrorResponseDTO(getErrorMessage("api.error.access-denied"))));
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
        else if ((path.equals(signinUrl) || path.equals(signupUrl) || path.equals(registrationUrl)) && request.getUserPrincipal() != null) {
            response.sendRedirect(mainUrl);
        }
        else if (userService.getCookieValueByName(refreshTokenName) != null) {
            request.setAttribute("ref", path);
            RequestDispatcher dispatcher = request.getServletContext().getRequestDispatcher(refreshTokenUrl);
            dispatcher.forward(request, response);
        }
        else {
            response.sendRedirect(signinUrl);
        }
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleException(BadCredentialsException e) {
        String errorMessage = getErrorMessage("api.error.bad-credential");

        log.warn(errorMessage, e);
        return ResponseEntity.badRequest().body(new ApiErrorResponseDTO(errorMessage));
    }

    private String getErrorMessage(String msgKey) {
        return getErrorMessage(msgKey, null);
    }

    private String getErrorMessage(String msgKey, Object[] args) {
        String message;
        try {
            message = messageSource.getMessage(msgKey, args, LocaleContextHolder.getLocale());
        }
        catch (NoSuchMessageException err) {
            message = getErrorMessage("api.error.unknown.key");
        }
        return message;
    }

    private Object[] pathToArgs(Path propertyPath) {
        List<String> result = new ArrayList<>();
        for (Path.Node node: propertyPath) {
            if (!node.getName().isEmpty()) {
                result.add(node.getName());
            }
        }
        Collections.reverse(result);
        return result.toArray();
    }
}

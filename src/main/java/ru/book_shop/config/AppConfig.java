package ru.book_shop.config;

import com.transferwise.icu.ICUReloadableResourceBundleMessageSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import java.time.Duration;

@Configuration
public class AppConfig implements WebMvcConfigurer {
    @Value("${app.path.book.cover}")
    private String coversPath;

    @Value("${app.path.upload.covers}")
    private String coversLocationPath;

    @Value("${app.path.authors.photo}")
    private String photosPath;

    @Value("${app.path.upload.authors}")
    private String photosLocationPath;

    @Value("${app.path.documents.cover}")
    private String documentsPath;

    @Value("${app.path.upload.documents}")
    private String documentsLocationPath;

    @Bean
    public LocaleResolver localeResolver(){
        final CookieLocaleResolver localeResolver = new CookieLocaleResolver("lang");
        localeResolver.setCookieMaxAge(Duration.ofDays(365));
        return localeResolver;
    }

    @Bean
    public MessageSource messageSource() {
        ICUReloadableResourceBundleMessageSource messageSource = new ICUReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:lang/messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(coversPath + "**").addResourceLocations("file:" + coversLocationPath);
        registry.addResourceHandler(photosPath + "**").addResourceLocations("file:" + photosLocationPath);
        registry.addResourceHandler(documentsPath + "**").addResourceLocations("file:" + documentsLocationPath);
    }
}

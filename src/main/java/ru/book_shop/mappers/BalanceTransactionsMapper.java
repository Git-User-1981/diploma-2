package ru.book_shop.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import ru.book_shop.dto.BalanceTransactionDTO;
import ru.book_shop.dto.BalanceTransactionPageDTO;
import ru.book_shop.dto.IBalanceTransactionDTO;

import java.time.format.DateTimeFormatter;

@Component
public class BalanceTransactionsMapper {
    private static String booksPath;
    private static MessageSource messageSource;

    @Value("${app.path.books}")
    public void setBooksPath(String path){
        BalanceTransactionsMapper.booksPath = path;
    }

    @Autowired
    public void setMessageSource(MessageSource messageSource) {
        BalanceTransactionsMapper.messageSource = messageSource;
    }

    private BalanceTransactionsMapper(){}

    public static BalanceTransactionPageDTO toDTO(Page<IBalanceTransactionDTO> transactions) {
        final BalanceTransactionPageDTO dto = new BalanceTransactionPageDTO();
        dto.setCount(transactions.getTotalElements());
        dto.setTransactions(transactions.getContent().stream().map(BalanceTransactionsMapper::toBalanceTransactionDTO).toList());

        return dto;
    }

    private static BalanceTransactionDTO toBalanceTransactionDTO(IBalanceTransactionDTO transaction) {
        final String transactionAmount = messageSource.getMessage(
                "page.profile.transactions.amount",
                new Object[]{transaction.getValue()},
                LocaleContextHolder.getLocale()
        );

        String description;
        if (transaction.getValue() > 0) {
            description = messageSource.getMessage("page.profile.transactions.top-up", null, LocaleContextHolder.getLocale());
        }
        else if (transaction.getValue() == 0) {
            description = messageSource.getMessage(
                    "page.profile.transactions.gift",
                    new Object[]{booksPath + transaction.getSlug(), transaction.getTitle()},
                    LocaleContextHolder.getLocale()
            );
        }
        else {
            description = messageSource.getMessage(
                    "page.profile.transactions.buy",
                    new Object[]{booksPath + transaction.getSlug(), transaction.getTitle()},
                    LocaleContextHolder.getLocale()
            );
        }

        return BalanceTransactionDTO
                .builder()
                .value(transaction.getValue() > 0 ? "+" + transactionAmount : transactionAmount)
                .time(transaction.getTime().format(DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm", LocaleContextHolder.getLocale())))
                .description(description)
                .build();
    }
}

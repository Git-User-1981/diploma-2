package ru.book_shop.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.book_shop.dto.IBalanceTransactionDTO;
import ru.book_shop.entities.payments.BalanceTransaction;

@Repository
public interface BalanceTransactionsRepository extends JpaRepository<BalanceTransaction, Integer> {
    @Query(value = "select b.slug, " +
                          "b.title, " +
                          "bt.time, " +
                          "bt.value " +
                     "from balance_transactions bt " +
                     "left join books b on bt.book_id = b.id " +
                    "where bt.user_id = :userId",
            countQuery = "select count(1) from balance_transactions where user_id = :userId",
            nativeQuery = true
    )
    Page<IBalanceTransactionDTO> getBalanceTransactions(Integer userId, Pageable nextPage);
}

package com.gpw.radar.repository;

import com.gpw.radar.domain.User;
import com.gpw.radar.domain.stock.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the User entity.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findOneByActivationKey(String activationKey);

    List<User> findAllByActivatedIsFalseAndCreatedDateBefore(ZonedDateTime dateTime);

    List<User> findAllByStocks(Stock stock);

    Optional<User> findOneByResetKey(String resetKey);

    Optional<User> findOneByEmail(String email);

    Optional<User> findOneByLogin(String login);

    Optional<User> findOneById(Long userId);

    @Query(value = "from User u join fetch u.stocks where u.login = :login")
    User findOneByLoginFetchStocks(@Param("login") String login);

    @Modifying
    @Query(value = "insert into USER_STOCKS (user_id, stock_id) values ((SELECT id from USERS where login= :login), :stockId)", nativeQuery = true)
    void createAssociationWithStock(@Param("login") String login, @Param("stockId") Long stockId);

    @Modifying
    @Query(value = "delete from USER_STOCKS where user_id= (SELECT id from USERS where login= :login) and stock_id= :stockId", nativeQuery = true)
    void deleteAssociationWithStock(@Param("login") String login, @Param("stockId") Long stockId);

    @Override
    void delete(User t);
}

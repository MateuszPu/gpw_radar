package com.gpw.radar.repository;

import com.gpw.radar.config.CacheConfiguration;
import com.gpw.radar.domain.User;
import org.springframework.cache.annotation.Cacheable;
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
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findOneByActivationKey(String activationKey);

    List<User> findAllByActivatedIsFalseAndCreatedDateBefore(ZonedDateTime dateTime);

    @Query(value = "select * from users u " +
        "inner join user_stocks usSt on u.id = usSt.user_id " +
        "inner join stock st on usSt.stock_id = st.id where st.ticker = :stockTicker", nativeQuery = true)
    List<User> findAllByStockTicker(@Param("stockTicker") String stockTicker);

    Optional<User> findOneByResetKey(String resetKey);

    Optional<User> findOneByEmail(String email);

    @Cacheable(cacheNames = CacheConfiguration.USER_INFO_CACHE)
    @Query(value = "from User u join fetch u.authorities where u.login = :login")
    Optional<User> findOneByLogin(@Param("login") String login);

    Optional<User> findOneById(String userId);

    @Modifying
    @Query(value = "insert into USER_STOCKS (user_id, stock_id) values (:userId, :stockId)", nativeQuery = true)
    void createAssociationWithStock(@Param("userId") String userId, @Param("stockId") String stockId);

    @Modifying
    @Query(value = "delete from USER_STOCKS where user_id= :userId and stock_id= :stockId", nativeQuery = true)
    void deleteAssociationWithStock(@Param("userId") String userId, @Param("stockId") String stockId);

    @Override
    void delete(User t);
}

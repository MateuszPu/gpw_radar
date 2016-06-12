package com.gpw.radar.repository;

import com.gpw.radar.config.CacheConfiguration;
import com.gpw.radar.config.JHipsterProperties;
import com.gpw.radar.domain.User;
import com.gpw.radar.domain.stock.Stock;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
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
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findOneByActivationKey(String activationKey);

    List<User> findAllByActivatedIsFalseAndCreatedDateBefore(ZonedDateTime dateTime);

    List<User> findAllByStocks(Stock stock);

    Optional<User> findOneByResetKey(String resetKey);

    Optional<User> findOneByEmail(String email);

    @Cacheable(cacheNames = CacheConfiguration.USER_INFO_CACHE)
    @Query(value = "from User u join fetch u.authorities where u.login = :login")
    Optional<User> findOneByLogin(@Param("login") String login);

    Optional<User> findOneById(Long userId);

    @Query(value = "from User u join fetch u.stocks where u.login = :login")
    User findOneByLoginFetchStocks(@Param("login") String login);

    @Modifying
    @Query(value = "insert into USER_STOCKS (user_id, stock_id) values (:userId, :stockId)", nativeQuery = true)
    @CacheEvict(cacheNames = CacheConfiguration.STOCKS_FOLLOWED_BY_USER_CACHE, key = "#p0")
    void createAssociationWithStock(@Param("userId") Long userId, @Param("stockId") Long stockId);

    @Modifying
    @Query(value = "delete from USER_STOCKS where user_id= :userId and stock_id= :stockId", nativeQuery = true)
    @CacheEvict(cacheNames = CacheConfiguration.STOCKS_FOLLOWED_BY_USER_CACHE, key = "#p0")
    void deleteAssociationWithStock(@Param("userId") Long userId, @Param("stockId") Long stockId);

    @Override
    void delete(User t);
}

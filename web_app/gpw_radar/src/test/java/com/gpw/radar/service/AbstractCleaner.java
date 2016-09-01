package com.gpw.radar.service;

import com.gpw.radar.Application;
import org.junit.After;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public abstract class AbstractCleaner {

    @Inject
    private CacheManager cacheManager;

    @After
    public void clean() {
        for (String cache : cacheManager.getCacheNames()) {
            cacheManager.getCache(cache).clear();
        }
    }

    public void cleanRepo(JpaRepository... repositories) {
        for (JpaRepository repo : repositories) {
            repo.deleteAll();
        }
    }
}

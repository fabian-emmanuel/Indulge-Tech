package com.indulgetech.services.system.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static java.util.Optional.ofNullable;

@RequiredArgsConstructor
@Service
@Profile("!test")
public class CacheServiceImpl implements CacheService{

    private final CacheManager cacheManager;

    @Override
    public void clearCache() {
        cacheManager.getCacheNames()
                .stream()
                .map(cacheName->ofNullable(cacheManager.getCache(cacheName)))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(Cache::clear);
    }
}

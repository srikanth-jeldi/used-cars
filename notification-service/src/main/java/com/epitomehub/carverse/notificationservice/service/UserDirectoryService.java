package com.epitomehub.carverse.notificationservice.service;

import com.epitomehub.carverse.notificationservice.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDirectoryService {

    private final RestTemplate restTemplate;
    private final InternalTokenProvider internalTokenProvider;

    @Value("${services.auth.base-url}")
    private String authBaseUrl;

    @Cacheable(cacheNames = "usersById", key = "#userId", unless = "#result == null")
    public UserDto getUserById(Long userId) {

        // prints only on cache miss
        log.info("CACHE MISS -> calling auth-service for userId={}", userId);

        String url = authBaseUrl + "/api/internal/users/" + userId;

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-INTERNAL-TOKEN", internalTokenProvider.getToken());

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<UserDto> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                UserDto.class
        );

        return response.getBody();
    }
}

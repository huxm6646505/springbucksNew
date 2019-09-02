package com.example.demo.service;

import com.example.demo.mapper.CoffeeNewMapper;
import com.example.demo.model.Coffee;
import com.example.demo.model.CoffeeNew;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.exact;

/**
 * Created by huxm on 2019/8/26
 */
@Slf4j
@Service
public class CoffeeNewService {
    private static final String CACHE = "springbucks-coffee";
    @Autowired(required = false)
    private RedisTemplate<String, CoffeeNew> redisTemplate;
    @Autowired(required = false)
    private CoffeeNewMapper coffeeNewMapper;

    public List<CoffeeNew> findAllCoffee() {
        return coffeeNewMapper.findAllWithRowBounds(new RowBounds(1, 0));
    }

    public Optional<CoffeeNew> findOneCoffee(String name) {
        HashOperations<String, String, CoffeeNew> hashOperations = redisTemplate.opsForHash();
        if (redisTemplate.hasKey(CACHE) && hashOperations.hasKey(CACHE, name)) {
            log.info("Get coffee {} from Redis.", name);
            return Optional.of(hashOperations.get(CACHE, name));
        }
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("name", exact().ignoreCase());
        Optional<CoffeeNew> coffee = coffeeNewMapper.findOneByName(name);


        log.info("Coffee Found: {}", coffee);
        if (coffee.isPresent()) {
            log.info("Put coffee {} to Redis.", name);
            hashOperations.put(CACHE, name, coffee.get());
            redisTemplate.expire(CACHE, 1, TimeUnit.MINUTES);
        }
        return coffee;
    }

}

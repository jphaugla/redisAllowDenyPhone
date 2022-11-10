package com.redis.allowDeny.service;

import com.redis.allowDeny.domain.FromTo;
import com.redis.allowDeny.strategy.ScanStrategy;
import com.redis.allowDeny.strategy.impl.Scan;
import com.redis.allowDeny.repository.FromToRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import redis.clients.jedis.*;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.*;

@Slf4j
@Service
public class AllowDenyService {

    @Autowired
    private Environment env;
    @Autowired
    private FromToRepository fromToRepository;

    Jedis  jedis;
    JedisPool jedisPool;
    String redisUrl = "redis://localhost:6379"; // default named
    HashMap<String, String> allFromTo = new HashMap<String, String>();
    @Value("${server.port}")
    private int serverPort;

    public FromTo returnFromTo(String from, String to, String product) {
        FromTo fromTo = fromToRepository.get(from, to, product, jedis);
        return fromTo;
    }
    public String createFromTo(FromTo fromTo) {
        fromToRepository.create(fromTo, jedis);
        addToAllFromTo(fromTo.getKey(), fromTo.getValue());
        return "Success\n";
    }

    public RedisIterator iterator(int initialScanCount, String pattern, ScanStrategy strategy) {
        return new RedisIterator(jedisPool, initialScanCount, pattern, strategy);
    }

    @PostConstruct
    private void init() throws URISyntaxException, UnknownHostException {
        log.info("Init AllowDeny Service");

        redisUrl =  env.getProperty("redis.url");
        URI uri = new URI(redisUrl);
        jedis = new Jedis(uri);
        jedisPool = new JedisPool(uri);
        //  add entry to sorted set for this IP address
        //   with timestamp from before kicking off the reload
        Double timestamp = (double) System.currentTimeMillis();
        String ip = String.valueOf(InetAddress.getLocalHost().getHostAddress());
        String returnVal =  this.reloadData(100,FromTo.getPREFIX() + '*', Boolean.FALSE);
        final long zadd = jedis.zadd("redis-instance-set", timestamp,
                ip + ':' + serverPort);

    }


    public String reloadData(int loopSize, String prefix, Boolean clearExisting) {
        ScanStrategy<String> scanStrategy = new Scan();
        RedisIterator iterator = iterator(loopSize, prefix + "*", scanStrategy);
        List<String> results = new LinkedList<String>();
        if (clearExisting && (allFromTo.size() > 0)) {
            allFromTo.clear();
        }

        while (iterator.hasNext()) {
            results.addAll(iterator.next());
            Map<String, String> hash = fromToRepository.get(results, jedis);
            addToAllFromTo(hash);
            results.clear();
        }
        log.info("fromTo");
        log.info(allFromTo.toString());

        return "Success\n";
    }
    public void addToAllFromTo(Map<String, String> hash) {
        allFromTo.putAll(hash);
    }
    public void addToAllFromTo(String key, String value) {
        allFromTo.put(key, value);
    }

    public String getAllHash() {

        return allFromTo.toString();
    }

    public HashMap<String, String> checkCache() {
        HashMap<String, String> statistics = new HashMap<String, String>();
        int sizeHash = allFromTo.size();
        statistics.put("Hash Size", String.valueOf(sizeHash));
        return statistics;
    }

    public HashMap<String, String> getCache(String key) {
        HashMap<String, String> hash = new HashMap<String, String>();
        String value = allFromTo.get(key);
        hash.put(key, value);
        return hash;
    }
}

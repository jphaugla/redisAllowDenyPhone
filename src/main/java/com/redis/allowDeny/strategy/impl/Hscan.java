package com.redis.allowDeny.strategy.impl;

import com.redis.allowDeny.strategy.ScanStrategy;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.resps.ScanResult;

import java.util.Map;
import java.util.Map.Entry;

public class Hscan implements ScanStrategy<Map.Entry<String, String>> {

    private String key;

    public Hscan(String key) {
        super();
        this.key = key;
    }

    @Override
    public ScanResult<Entry<String, String>> scan(Jedis jedis, String cursor, ScanParams scanParams) {
        return jedis.hscan(key, cursor, scanParams);
    }

}

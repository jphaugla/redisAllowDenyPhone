package com.redis.allowDeny.strategy.impl;

import com.redis.allowDeny.strategy.ScanStrategy;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.resps.ScanResult;

public class Scan implements ScanStrategy<String> {


    public ScanResult<String> scan(Jedis jedis, String cursor, ScanParams scanParams) {
        return jedis.scan(cursor, scanParams);
    }
}

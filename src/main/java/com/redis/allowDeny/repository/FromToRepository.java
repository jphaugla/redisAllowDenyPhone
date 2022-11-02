package com.redis.allowDeny.repository;

import org.springframework.stereotype.Repository;
import com.redis.allowDeny.domain.FromTo;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;

@Repository
public class FromToRepository {

    public String create(FromTo fromTo, Jedis jedis) {
        HashMap<String, String> hash = new HashMap<String,String>();
        hash.put("decision", fromTo.getDecision());
        hash.put("ruleId",fromTo.getRuleId());
        hash.put("reason",fromTo.getReason());
        jedis.hset(fromTo.getKey(), hash);
        return "Success\n";
    }
    public FromTo get(String from, String to, String product, Jedis jedis) {
        String fromToKey = FromTo.generateKey(from, to, product);
        Map<String, String> hash_value = jedis.hgetAll(fromToKey);
        FromTo fromTo = new FromTo(from, to, product, hash_value.get("decision"), hash_value.get("ruleId"), hash_value.get("reason"));
        return fromTo;
    }
    public FromTo get(String fromToKey, Jedis jedis) {
        Map<String, String> hash_value = jedis.hgetAll(fromToKey);
        FromTo fromTo = new FromTo(FromTo.getFromKey(fromToKey), FromTo.getToKey(fromToKey), FromTo.getProductKey(fromToKey),
                hash_value.get("decision"), hash_value.get("ruleId"), hash_value.get("reason"));
        return fromTo;
    }

}

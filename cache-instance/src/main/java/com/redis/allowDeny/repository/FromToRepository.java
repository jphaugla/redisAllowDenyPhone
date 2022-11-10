package com.redis.allowDeny.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Repository;
import com.redis.allowDeny.domain.FromTo;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Repository
@Slf4j
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
        FromTo fromTo = new FromTo(from, to, product, hash_value.get("decision"), hash_value.get("ruleId"),
                hash_value.get("reason"));
        return fromTo;
    }
    public Map<String, String> get(List<String> results, Jedis jedis) {
        Pipeline pipeline = jedis.pipelined();
        ArrayList<FromTo> fromToArray = new ArrayList<FromTo>();
        Map<String, String> allFromTo = new HashMap<String, String>();
        ArrayList<Response<Map<String, String>>> responses = new ArrayList<>();
        for (int i =0; i < results.size(); i++) {
            String key = results.get(i);
            log.info("key is " + key);
            responses.add(pipeline.hgetAll(key));
        }
        pipeline.sync();
        int x = 0;
        for (Response<Map<String, String>> response_map : responses) {
            Map<String, String> hash_value = response_map.get();
            allFromTo.put(results.get(x), FromTo.generateValue(hash_value.get("decision"), hash_value.get("ruleId"), hash_value.get("reason")));
            x += 1;
        }
        return allFromTo;
    }

}

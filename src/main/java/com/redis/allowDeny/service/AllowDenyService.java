package com.redis.allowDeny.service;

import com.redis.allowDeny.domain.FromTo;
import com.redis.allowDeny.strategy.ScanStrategy;
import com.redis.allowDeny.strategy.impl.Scan;
import com.redis.allowDeny.repository.FromToRepository;
import redis.clients.jedis.*;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
    ArrayList<FromTo> allFromTo;
    public FromTo returnFromTo(String from, String to, String product) {
        FromTo fromTo = fromToRepository.get(from, to, product, jedis);
        return fromTo;
    }
    public String createFromTo(FromTo fromTo) {
        fromToRepository.create(fromTo, jedis);
        return "Success\n";
    }
/*
    private static void loadJSONfile()
    private static void loadData(String from,String to, String Destination, String rule_id){
        long startRange = startValue; // something like 4000000000000000000l
        long endRangeDelta = (int)Math.floor(Math.random()*((10000/deltaBase)-10+1)+10);//at least 10 cards in a range
        float protocol1Base = 1.2f;
        float protocol2Base = 1.2f;
        String BIN_BASE = null;
        String cardType = null;
        Pipeline pipelinedJedis = jedis.pipelined();
        for(int x=0;x<5000000;x++){
            BIN_BASE=Long.toString(startRange).substring(0,1); // to calculate cardType
            cardType= Integer.parseInt(BIN_BASE)%2==1 ? "Mastercard" : "Visa";
            startRange+=x;
            BIN_BASE = Long.toString(startRange).substring(0,bin_base_upper_bound); //to allow for filtering results better
            HashMap<String,String> values = new HashMap<String,String>();
            values.put("startRange",startRange+"");
            values.put("endRange",startRange+endRangeDelta+"");
            values.put("actionInd","A");
            values.put("dsAlias", cardType);
            values.put("acsStartProtocolVersion", ""+protocol1Base+(x%4));
            values.put("acsEndProtocolVersion", ""+protocol1Base+(x%4));
            values.put("dsStartProtocolVersion",""+protocol2Base+(x%3));
            values.put("dsEndProtocolVersion", ""+protocol2Base+(x%3));
            values.put("BIN_BASE", BIN_BASE);
            values.put("acsInfoInd", "04,02,03,01");
            pipelinedJedis.hset("CARD:RANGE:"+keyBatchPrefix+":"+startRange,values);
            x+=endRangeDelta;//for loop will add an additional 1 to x
        }
        pipelinedJedis.sync();
    }
    */
    public RedisIterator iterator(int initialScanCount, String pattern, ScanStrategy strategy) {
        return new RedisIterator(jedisPool, initialScanCount, pattern, strategy);
    }

    @PostConstruct
    private void init() throws URISyntaxException {
        log.info("Init RediSearchService");

        redisUrl =  env.getProperty("redis.url");
        URI uri = new URI(redisUrl);
        jedis = new Jedis(uri);
        jedisPool = new JedisPool(uri);
        allFromTo = new ArrayList<FromTo>();

    }


    public String reloadData(int loopSize, String prefix) {
        ScanStrategy<String> scanStrategy = new Scan();
        RedisIterator iterator = iterator(loopSize, prefix + "*", scanStrategy);
        List<String> results = new LinkedList<String>();
        if (allFromTo.size() > 0 ) {
            allFromTo.clear();
        }
        while (iterator.hasNext()) {
           results.addAll(iterator.next());
        }
        for (int i =0; i < results.size(); i++) {
            String key = results.get(i);
            log.info("key is " + key);
            FromTo nextFromTo = fromToRepository.get(key, jedis);
            allFromTo.add(nextFromTo);
        }
        log.info("number of elements " + results.size());
        log.info("results");
        log.info(results.toString());
        log.info("fromTo");
        log.info(allFromTo.toString());

        return "Success\n";
    }
}

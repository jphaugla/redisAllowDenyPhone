package com.redis.allowDeny.config;

import com.redis.allowDeny.domain.FromTo;

import com.redis.allowDeny.service.AllowDenyService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import javax.inject.Inject;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
@RequiredArgsConstructor

public class FromToEventConsumer implements StreamListener<String, ObjectRecord<String, FromTo>> {

	private AtomicInteger atomicInteger = new AtomicInteger(0);

	private final ReactiveRedisTemplate<String, String> redisTemplate;
	@Inject
	AllowDenyService allowDenyService;

	@Override
	@SneakyThrows

	public void onMessage(ObjectRecord<String, FromTo> record) {
		log.info(InetAddress.getLocalHost().getHostName() + " - consumed :" + record.getValue());
		FromTo fromTo = record.getValue();
		String key = fromTo.getKey();
		HashMap<String, String> hash = new HashMap<String, String>();

		hash.put("decision", fromTo.getDecision());
		hash.put("reason", fromTo.getReason());
		hash.put("ruleId", fromTo.getRuleId());
			this.redisTemplate
					.opsForHash()
					.putAll(key, hash)
					.subscribe();


		atomicInteger.incrementAndGet();
		//  add new message to the cache
		allowDenyService.addToAllFromTo(key, fromTo.getValue());

		/*  this is getting the list of cache instances which is not needed
		Long isZero = Long.valueOf(0);
		Long isNegOne = Long.valueOf(-1);
		Range<Long> range = Range.closed(isZero, isNegOne);

		Flux<String> allInstances = this.redisTemplate.opsForZSet().
				range("redis-instance-set", range);
		for (String oneInstance: allInstances.toIterable()) {
			log.info(oneInstance);
			 String prefix = "http://";
			String postfix = "/status";
			URL url = new URL(prefix + oneInstance + postfix);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");


		}
		 */
		}

	@Scheduled(fixedRate = 10000)
	public void showPublishedEventsSoFar(){
		log.info("Total Consumer :: " + atomicInteger.get());
	}

}
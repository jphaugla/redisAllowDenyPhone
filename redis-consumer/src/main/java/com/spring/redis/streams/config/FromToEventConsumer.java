package com.spring.redis.streams.config;

import com.spring.redis.streams.domain.FromTo;

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

import java.net.InetAddress;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
@RequiredArgsConstructor

public class FromToEventConsumer implements StreamListener<String, ObjectRecord<String, FromTo>> {

	private AtomicInteger atomicInteger = new AtomicInteger(0);

	private final ReactiveRedisTemplate<String, String> redisTemplate;


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
		Long isZero = Long.valueOf(0);
		Long isNegOne = Long.valueOf(-1);
		Range<Long> range = Range.closed(isZero, isNegOne);
		// Range<Long> range = Range.from(0).to(-1);
		Flux<String> allInstances = this.redisTemplate.opsForZSet().
				range("redis-instance-set", range);
		for (String oneInstance: allInstances.toIterable()) {
			log.info(oneInstance);
		}
		}

	@Scheduled(fixedRate = 10000)
	public void showPublishedEventsSoFar(){
		log.info("Total Consumer :: " + atomicInteger.get());
	}

}
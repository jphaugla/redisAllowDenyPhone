package com.spring.redis.streams.publisher;


import lombok.extern.slf4j.Slf4j;
import com.spring.redis.streams.domain.FromTo;
import com.spring.redis.streams.repository.FromToPublisherRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class FromToPublisher {

	private AtomicInteger atomicInteger = new AtomicInteger(0);
    FromToPublisherRepository fromToPublisherRepository;
	@Value("${stream.trimSize}")
	private int trimSize;
	@Value("${stream.key}")
	private String streamKey;



	private final ReactiveRedisTemplate<String, String> redisTemplate;

	public FromToPublisher(FromToPublisherRepository repository,
						   ReactiveRedisTemplate<String, String> redisTemplate) {
		this.fromToPublisherRepository = repository;
		this.redisTemplate = redisTemplate;
	}

	@Scheduled(fixedRateString= "${publish.rate}")
	public void publishEvent(){
		FromTo fromTo = this.fromToPublisherRepository.getRandomFromTo();
		// set the ruleId to atomic integer
		fromTo.setRuleId(String.valueOf(atomicInteger));
		log.info("From To :: "+fromTo);
		ObjectRecord<String, FromTo> record = StreamRecords.newRecord()
		                                                         .ofObject(fromTo)
		                                                         .withStreamKey(streamKey);
		this.redisTemplate
				.opsForStream()
				.add(record)
				.subscribe(System.out::println);
		atomicInteger.incrementAndGet();
		//  every 100 messages check to trim the stream to 500
		if(atomicInteger.get() % 100 == 0) {
			this.redisTemplate
					.opsForStream()
					.trim(streamKey, trimSize, true);
		}
	}

	@Scheduled(fixedRate = 10000)
	public void showPublishedEventsSoFar(){
		log.info("Total Events :: " +atomicInteger.get());
	}

}
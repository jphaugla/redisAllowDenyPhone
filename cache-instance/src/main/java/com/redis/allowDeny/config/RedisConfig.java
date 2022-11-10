package com.redis.allowDeny.config;

import com.redis.allowDeny.domain.FromTo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.Subscription;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class RedisConfig {

	@Value("${stream.key}")
	private String streamKey;
	@Value("${server.port}")
	private int serverPort;

	private final StreamListener<String, ObjectRecord<String, FromTo>> streamListener;


	@Bean
	public Subscription subscription(RedisConnectionFactory redisConnectionFactory) throws UnknownHostException {
		StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, ObjectRecord<String, FromTo>> options = StreamMessageListenerContainer
				.StreamMessageListenerContainerOptions.builder().pollTimeout(Duration.ofSeconds(1)).targetType(FromTo.class).build();
		StreamMessageListenerContainer<String, ObjectRecord<String, FromTo>>  listenerContainer = StreamMessageListenerContainer
				.create(redisConnectionFactory, options);
		String ip = String.valueOf(InetAddress.getLocalHost().getHostAddress());
		String serverKey = ip + ':' + String.valueOf(serverPort);
		String groupName = streamKey + serverKey;
		try {
			log.info("before xgroup create with serverkey " + serverKey);
			redisConnectionFactory.getConnection()
			                      .xGroupCreate(streamKey.getBytes(), groupName, ReadOffset.from("0-0"), true);
		} catch (RedisSystemException exception) {
			log.info("in exception handler fro xgroupcreate");
			log.warn(exception.getCause().getMessage());
		}
		log.info("before create subscription");
		Subscription subscription = listenerContainer.receive(Consumer.from(groupName, InetAddress.getLocalHost().getHostName()),
		                                                      StreamOffset.create(streamKey, ReadOffset.lastConsumed()), streamListener);
		listenerContainer.start();
		log.info("after start listenerContainer");
		return subscription;
	}
}
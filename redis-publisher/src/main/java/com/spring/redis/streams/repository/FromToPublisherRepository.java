package com.spring.redis.streams.repository;

import com.github.javafaker.service.FakeValuesService;
import com.github.javafaker.service.RandomService;
import com.spring.redis.streams.domain.FromTo;

import org.springframework.stereotype.Repository;

import java.util.Locale;


@Repository
public class FromToPublisherRepository {
	FakeValuesService fakeValuesService = new FakeValuesService(
			new Locale("en-us"), new RandomService());

	public FromTo getRandomFromTo() {
		String from = fakeValuesService.bothify("############");
		String to = fakeValuesService.bothify("############");
		String product = fakeValuesService.bothify("??????");
		String decision = fakeValuesService.bothify("Decision##");
		String ruleId = fakeValuesService.bothify("##");
		String reason = fakeValuesService.bothify("###");
		return new FromTo(from, to, product, decision, ruleId, reason);
	}


}
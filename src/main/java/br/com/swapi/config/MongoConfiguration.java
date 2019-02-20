package br.com.swapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

import com.mongodb.MongoClient;

@Configuration
public class MongoConfiguration extends AbstractMongoConfiguration {
	
	public static final String DB_NAME="starwarsapi";
	
	@Value("${starwarsapi.mongodb.host}")
	private String HOST;
	
	@Value("${starwarsapi.mongodb.port}")
	private int PORT;

	@Override
	@Bean
	@ConditionalOnMissingBean(MongoClient.class)
	public MongoClient mongoClient() {
		return new MongoClient(HOST, PORT);
	}

	@Override
	protected String getDatabaseName() {
		return DB_NAME;
	}

}

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mongodb.MongoClient;

/**
 * Create a embended mongodb client for tests execution

 * @author fabiosilva
 *
 */
@Configuration
public class MongoEmbendedForTests {
	
	@Value("${starwarsapi.mongoJunit.hostname}")
	private String HOSTNAME;
	
	@Value("${starwarsapi.mongoJunit.port}")
	private int PORT;

	@Bean
	public MongoClient mongoClient() {
		return new MongoClient(HOSTNAME, PORT);
	}
}

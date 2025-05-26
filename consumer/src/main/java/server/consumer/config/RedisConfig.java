package server.consumer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class RedisConfig {

	@Value("${spring.data.redis.host}")
	private String redisHost;

	@Value("${spring.data.redis.port:6379}")
	private int redisPort;

	@Value("${spring.data.redis.password}")
	private String redisPassword;

	@Bean
	public LettuceConnectionFactory redisConnectionFactory() {
		// RedisStandaloneConfiguration을 사용하여 비밀번호 설정
		RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
		config.setHostName(redisHost);
		config.setPort(redisPort);
		config.setPassword(redisPassword); // 비밀번호 설정
		return new LettuceConnectionFactory(config);
	}

	@Bean
	public StringRedisTemplate stringRedisTemplate(LettuceConnectionFactory connectionFactory) {
		return new StringRedisTemplate(connectionFactory);
	}
}
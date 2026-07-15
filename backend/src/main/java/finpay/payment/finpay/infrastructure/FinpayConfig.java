package finpay.payment.finpay.infrastructure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import tools.jackson.databind.json.JsonMapper;

@Configuration
public class FinpayConfig {

	@Bean
	RestClient.Builder restClientBuilder() {
		return RestClient.builder();
	}

	@Bean
	JsonMapper jsonMapper() {
		return JsonMapper.builder().build();
	}
}

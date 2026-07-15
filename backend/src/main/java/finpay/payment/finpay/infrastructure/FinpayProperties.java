package finpay.payment.finpay.infrastructure;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "finpay")
public class FinpayProperties {

	private String merchantId;
	private String merchantKey;
	private String initiateUrl;
	private String checkUrl;
	private String successUrl;
	private String failUrl;
	private String callbackUrl;
}

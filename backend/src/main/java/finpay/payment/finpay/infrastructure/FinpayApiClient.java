package finpay.payment.finpay.infrastructure;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import finpay.payment.shared.infrastructure.exception.FinpayApiException;
import finpay.payment.finpay.dto.FinpayCheckStatusResponseDTO;
import finpay.payment.finpay.dto.FinpayInitiateRequestDTO;
import finpay.payment.finpay.dto.FinpayInitiateResponseDTO;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FinpayApiClient {

	private final FinpayProperties properties;
	private final RestClient.Builder restClientBuilder;

	public FinpayInitiateResponseDTO initiate(FinpayInitiateRequestDTO requestBody) {
		return post(properties.getInitiateUrl(), requestBody, FinpayInitiateResponseDTO.class);
	}

	public FinpayCheckStatusResponseDTO checkStatus(String orderId) {
		String url = properties.getCheckUrl().replace("{orderId}", orderId);
		try {
			FinpayCheckStatusResponseDTO response = finpayClient()
					.get()
					.uri(url)
					.retrieve()
					.body(FinpayCheckStatusResponseDTO.class);
			if (response == null) {
				throw new FinpayApiException("Empty response from Finpay check status API");
			}
			return response;
		} catch (RestClientResponseException exception) {
			throw FinpayApiException.fromResponseBody(exception.getResponseBodyAsString());
		}
	}

	private <T> T post(String url, Object requestBody, Class<T> responseType) {
		try {
			T response = finpayClient()
					.post()
					.uri(url)
					.body(requestBody)
					.retrieve()
					.body(responseType);
			if (response == null) {
				throw new FinpayApiException("Empty response from Finpay API");
			}
			return response;
		} catch (RestClientResponseException exception) {
			throw FinpayApiException.fromResponseBody(exception.getResponseBodyAsString());
		}
	}

	private RestClient finpayClient() {
		String credentials = properties.getMerchantId() + ":" + properties.getMerchantKey();
		String authorization = "Basic "
				+ Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

		return restClientBuilder
				.defaultHeader(HttpHeaders.AUTHORIZATION, authorization)
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
				.build();
	}
}

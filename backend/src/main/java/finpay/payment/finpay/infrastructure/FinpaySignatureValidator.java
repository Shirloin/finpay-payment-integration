package finpay.payment.finpay.infrastructure;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Component;

import finpay.payment.shared.infrastructure.exception.InvalidPaymentSignatureException;
import finpay.payment.finpay.dto.FinpayCallbackRequestDTO;
import lombok.RequiredArgsConstructor;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ObjectNode;

@Component
@RequiredArgsConstructor
public class FinpaySignatureValidator {

	private final JsonMapper jsonMapper;

	public FinpayCallbackRequestDTO validateAndParse(String rawJson, String merchantKey) {
		try {
			JsonNode root = jsonMapper.readTree(rawJson);
			String sentSignature = root.path("signature").asString(null);
			if (sentSignature == null || sentSignature.isBlank()) {
				throw new InvalidPaymentSignatureException();
			}
			if (!(root instanceof ObjectNode payload)) {
				throw new InvalidPaymentSignatureException();
			}

			payload.remove("signature");
			String calculatedSignature = hmacSha512Hex(jsonMapper.writeValueAsString(payload), merchantKey);
			if (!MessageDigest.isEqual(
					calculatedSignature.getBytes(StandardCharsets.UTF_8),
					sentSignature.getBytes(StandardCharsets.UTF_8))) {
				throw new InvalidPaymentSignatureException();
			}

			return jsonMapper.readValue(rawJson, FinpayCallbackRequestDTO.class);
		} catch (InvalidPaymentSignatureException exception) {
			throw exception;
		} catch (Exception exception) {
			throw new InvalidPaymentSignatureException();
		}
	}

	String sign(String canonicalJson, String merchantKey) {
		return hmacSha512Hex(canonicalJson, merchantKey);
	}

	private static String hmacSha512Hex(String data, String secret) {
		try {
			Mac mac = Mac.getInstance("HmacSHA512");
			mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA512"));
			return HexFormat.of().formatHex(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
		} catch (Exception exception) {
			throw new InvalidPaymentSignatureException();
		}
	}
}

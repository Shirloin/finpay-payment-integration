package finpay.payment.finpay.infrastructure;

import finpay.payment.finpay.dto.FinpayCallbackSignaturePayloadDTO;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ObjectNode;

public final class FinpayCallbackTestSupport {

	private static final JsonMapper JSON_MAPPER = JsonMapper.builder().build();
	private static final FinpaySignatureValidator VALIDATOR = new FinpaySignatureValidator(JSON_MAPPER);

	private FinpayCallbackTestSupport() {
	}

	public static String signedJson(FinpayCallbackSignaturePayloadDTO payload, String merchantKey) throws Exception {
		ObjectNode root = JSON_MAPPER.valueToTree(payload);
		String canonical = JSON_MAPPER.writeValueAsString(root);
		root.put("signature", VALIDATOR.sign(canonical, merchantKey));
		return JSON_MAPPER.writeValueAsString(root);
	}
}

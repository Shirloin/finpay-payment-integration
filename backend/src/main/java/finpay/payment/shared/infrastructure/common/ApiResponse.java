package finpay.payment.shared.infrastructure.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ApiResponse<T> {

	private final T data;
	private final String message;
	private final ApiError error;

	public static <T> ApiResponse<T> success(T data, String message) {
		return ApiResponse.<T>builder()
				.data(data)
				.message(message)
				.build();
	}

	public static <T> ApiResponse<T> failure(String message, ApiError error) {
		return ApiResponse.<T>builder()
				.message(message)
				.error(error)
				.build();
	}
}

package finpay.payment.user.domain.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class CreateUserRequestDTOValidationTests {

	private static ValidatorFactory validatorFactory;
	private static Validator validator;

	@BeforeAll
	static void setUpValidator() {
		validatorFactory = Validation.buildDefaultValidatorFactory();
		validator = validatorFactory.getValidator();
	}

	@AfterAll
	static void closeValidator() {
		validatorFactory.close();
	}

	@Test
	void acceptsAValidRequestWithoutChangingTheUsername() {
		CreateUserRequestDTO request = new CreateUserRequestDTO("Alice", "secret1");

		assertTrue(validator.validate(request).isEmpty());
		assertEquals("Alice", request.getUsername());
	}

	@Test
	void rejectsShortUsernameAndPassword() {
		Set<ConstraintViolation<CreateUserRequestDTO>> violations =
				validator.validate(new CreateUserRequestDTO("ab", "12345"));

		assertEquals(2, violations.size());
		assertTrue(violations.stream().anyMatch(violation ->
				violation.getPropertyPath().toString().equals("username")));
		assertTrue(violations.stream().anyMatch(violation ->
				violation.getPropertyPath().toString().equals("password")));
	}
}

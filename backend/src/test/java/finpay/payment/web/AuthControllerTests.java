package finpay.payment.web;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import finpay.payment.shared.infrastructure.common.GlobalExceptionHandler;
import finpay.payment.shared.infrastructure.exception.DuplicateUsernameException;
import finpay.payment.user.application.UserService;
import finpay.payment.user.domain.dto.UserResponseDTO;

class AuthControllerTests {

	private MockMvc mockMvc;
	private UserService service;

	@BeforeEach
	void setUp() {
		service = mock(UserService.class);
		when(service.register("alice", "secret1")).thenReturn(userResponse("alice"));
		when(service.register(" Alice ", "secret1")).thenReturn(userResponse(" Alice "));
		LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
		validator.afterPropertiesSet();
		mockMvc = MockMvcBuilders
				.standaloneSetup(new AuthController(service))
				.setControllerAdvice(new GlobalExceptionHandler())
				.setValidator(validator)
				.build();
	}

	@Test
	void returnsStandardCreatedResponseWithoutPassword() throws Exception {
		mockMvc.perform(post("/api/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"username":"alice","password":"secret1"}
								"""))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.data.id").isNotEmpty())
				.andExpect(jsonPath("$.data.username").value("alice"))
				.andExpect(jsonPath("$.data.createdAt").isNotEmpty())
				.andExpect(jsonPath("$.message").value("User registered successfully"))
				.andExpect(jsonPath("$.error").isEmpty())
				.andExpect(content().string(not(containsString("password"))));
	}

	@Test
	void preservesUsernameExactly() throws Exception {
		mockMvc.perform(post("/api/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"username":" Alice ","password":"secret1"}
								"""))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.data.username").value(" Alice "));
	}

	@Test
	void returnsStandardBadRequestResponse() throws Exception {
		mockMvc.perform(post("/api/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"username":"ab","password":"12345"}
								"""))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.data").isEmpty())
				.andExpect(jsonPath("$.message").value("Request validation failed"))
				.andExpect(jsonPath("$.error.fieldErrors.username").exists())
				.andExpect(jsonPath("$.error.fieldErrors.password").exists());
	}

	@Test
	void returnsStandardConflictResponse() throws Exception {
		String request = """
				{"username":"alice","password":"secret1"}
				""";
		when(service.register("alice", "secret1"))
				.thenReturn(userResponse("alice"))
				.thenThrow(new DuplicateUsernameException("alice"));
		mockMvc.perform(post("/api/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(request));

		mockMvc.perform(post("/api/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content(request))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.data").isEmpty())
				.andExpect(jsonPath("$.message").value("Username 'alice' is already registered"))
				.andExpect(jsonPath("$.error").exists());
	}

	private UserResponseDTO userResponse(String username) {
		return UserResponseDTO.builder()
				.id(UUID.randomUUID())
				.username(username)
				.createdAt(Instant.now())
				.build();
	}
}

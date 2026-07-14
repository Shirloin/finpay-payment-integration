package finpay.payment.web;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import finpay.payment.auth.application.AuthService;
import finpay.payment.auth.domain.dto.AuthResponseDTO;
import finpay.payment.shared.infrastructure.common.GlobalExceptionHandler;
import finpay.payment.shared.infrastructure.exception.DuplicateUsernameException;
import finpay.payment.shared.infrastructure.exception.IncorrectPasswordException;
import finpay.payment.shared.infrastructure.exception.InvalidCredentialsException;
import finpay.payment.shared.infrastructure.exception.UsernameNotFoundException;
import finpay.payment.user.application.UserService;
import finpay.payment.user.domain.dto.UserResponseDTO;

class AuthControllerTests {

	private MockMvc mockMvc;
	private AuthService authService;
	private UserService service;
	private Jwt jwt;

	@BeforeEach
	void setUp() {
		authService = mock(AuthService.class);
		service = mock(UserService.class);
		UUID subject = UUID.randomUUID();
		jwt = Jwt.withTokenValue("jwt-token")
				.header("alg", "HS256")
				.subject(subject.toString())
				.build();
		when(service.register("alice", "secret1")).thenReturn(userResponse("alice"));
		when(service.register(" Alice ", "secret1")).thenReturn(userResponse(" Alice "));
		when(authService.login("alice", "secret1")).thenReturn(authResponse("alice"));
		when(authService.verify(subject.toString())).thenReturn(userResponse(subject, "alice"));
		LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
		validator.afterPropertiesSet();
		mockMvc = MockMvcBuilders
				.standaloneSetup(new AuthController(authService, service))
				.setControllerAdvice(new GlobalExceptionHandler())
				.setCustomArgumentResolvers(jwtArgumentResolver())
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

	@Test
	void returnsTokenAndUserWithoutPassword() throws Exception {
		mockMvc.perform(post("/api/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"username":"alice","password":"secret1"}
								"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.accessToken").value("jwt-token"))
				.andExpect(jsonPath("$.data.tokenType").value("Bearer"))
				.andExpect(jsonPath("$.data.expiresAt").isNotEmpty())
				.andExpect(jsonPath("$.data.user.username").value("alice"))
				.andExpect(jsonPath("$.message").value("Login successful"))
				.andExpect(jsonPath("$.error").isEmpty())
				.andExpect(content().string(not(containsString("password"))));
	}

	@Test
	void returnsClearUnauthorizedResponseForIncorrectPassword() throws Exception {
		when(authService.login("alice", "wrong-password")).thenThrow(new IncorrectPasswordException());

		mockMvc.perform(post("/api/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"username":"alice","password":"wrong-password"}
								"""))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.data").isEmpty())
				.andExpect(jsonPath("$.message").value("Incorrect password"))
				.andExpect(jsonPath("$.error.status").value(401))
				.andExpect(jsonPath("$.error.path").value("/api/auth/login"));
	}

	@Test
	void returnsClearUnauthorizedResponseWhenUsernameIsMissing() throws Exception {
		when(authService.login("missing", "secret1")).thenThrow(new UsernameNotFoundException());

		mockMvc.perform(post("/api/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"username":"missing","password":"secret1"}
								"""))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.data").isEmpty())
				.andExpect(jsonPath("$.message").value("Username not found"))
				.andExpect(jsonPath("$.error.status").value(401))
				.andExpect(jsonPath("$.error.path").value("/api/auth/login"));
	}

	@Test
	void validatesLoginRequest() throws Exception {
		mockMvc.perform(post("/api/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"username":"","password":""}
								"""))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error.fieldErrors.username").exists())
				.andExpect(jsonPath("$.error.fieldErrors.password").exists());
	}

	@Test
	void returnsVerifiedCurrentUserWithoutPassword() throws Exception {
		mockMvc.perform(get("/api/auth/verify"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.id").value(jwt.getSubject()))
				.andExpect(jsonPath("$.data.username").value("alice"))
				.andExpect(jsonPath("$.data.createdAt").isNotEmpty())
				.andExpect(jsonPath("$.message").value("Token verified successfully"))
				.andExpect(jsonPath("$.error").isEmpty())
				.andExpect(content().string(not(containsString("password"))));
	}

	@Test
	void returnsStandardUnauthorizedResponseWhenVerifiedUserIsMissing() throws Exception {
		when(authService.verify(jwt.getSubject())).thenThrow(new InvalidCredentialsException());

		mockMvc.perform(get("/api/auth/verify"))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.data").isEmpty())
				.andExpect(jsonPath("$.message").value("Invalid username or password"))
				.andExpect(jsonPath("$.error.status").value(401))
				.andExpect(jsonPath("$.error.path").value("/api/auth/verify"));
	}

	private UserResponseDTO userResponse(String username) {
		return userResponse(UUID.randomUUID(), username);
	}

	private UserResponseDTO userResponse(UUID id, String username) {
		return UserResponseDTO.builder()
				.id(id)
				.username(username)
				.createdAt(Instant.now())
				.build();
	}

	private AuthResponseDTO authResponse(String username) {
		return AuthResponseDTO.builder()
				.accessToken("jwt-token")
				.tokenType("Bearer")
				.expiresAt(Instant.now().plusSeconds(3600))
				.user(userResponse(username))
				.build();
	}

	private HandlerMethodArgumentResolver jwtArgumentResolver() {
		return new HandlerMethodArgumentResolver() {
			@Override
			public boolean supportsParameter(MethodParameter parameter) {
				return parameter.hasParameterAnnotation(AuthenticationPrincipal.class)
						&& parameter.getParameterType().equals(Jwt.class);
			}

			@Override
			public Object resolveArgument(
					MethodParameter parameter,
					ModelAndViewContainer mavContainer,
					NativeWebRequest webRequest,
					WebDataBinderFactory binderFactory) {
				return jwt;
			}
		};
	}
}

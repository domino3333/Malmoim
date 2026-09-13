package com.malmoim.controller.auth;

import com.malmoim.controller.common.ApiExceptionHandler;
import com.malmoim.domain.Member;
import com.malmoim.dto.auth.SignUpRequest;
import com.malmoim.mapper.MemberMapper;
import com.malmoim.security.jwt.JwtTokenProvider;
import com.malmoim.service.member.impl.MemberServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.json.JsonMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SignUpValidationTest {

    private static final Map<String, Object> VALID_BODY = Map.of(
            "email", "member@example.test", "name", "Member", "password", "secret");
    private static final JsonMapper JSON = JsonMapper.builder().build();
    private MemberMapper memberMapper;
    private BCryptPasswordEncoder passwordEncoder;
    private MemberServiceImpl memberService;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        memberMapper = mock(MemberMapper.class);
        passwordEncoder = spy(new BCryptPasswordEncoder(4));
        memberService = new MemberServiceImpl(memberMapper, passwordEncoder);
        AuthController controller = new AuthController(mock(AuthenticationManager.class),
                mock(JwtTokenProvider.class), memberService);
        mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new ApiExceptionHandler()).build();
    }

    @ParameterizedTest
    @MethodSource("invalidRequests")
    void invalidSignUpReturns400WithoutHashingOrInserting(Map<String, Object> body) throws Exception {
        mvc.perform(post("/api/auth/signUp").contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(not(emptyOrNullString())));
        verifyNoInteractions(memberMapper, passwordEncoder);
    }

    static Stream<Arguments> invalidRequests() {
        List<Arguments> cases = new ArrayList<>();
        for (String field : List.of("email", "name", "password")) {
            for (String value : new String[]{null, "", " \t\n"}) {
                cases.add(Arguments.of(with(field, value)));
            }
            Map<String, Object> missingField = new HashMap<>(VALID_BODY);
            missingField.remove(field);
            cases.add(Arguments.of(missingField));
        }
        cases.add(Arguments.of(with("email", "not-an-email")));
        cases.add(Arguments.of(with("email", "member@@example.test")));
        cases.add(Arguments.of(with("email", "a".repeat(38) + "@example.test")));
        cases.add(Arguments.of(with("name", "a".repeat(21))));
        cases.add(Arguments.of(with("password", "a".repeat(73))));
        cases.add(Arguments.of(with("password", "가".repeat(25))));
        return cases.stream();
    }

    @ParameterizedTest
    @MethodSource("validPasswords")
    void maximumLengthFieldsAreAcceptedAndPasswordIsHashedWithoutTrimming(String password) throws Exception {
        String email = "a".repeat(37) + "@example.test";
        String name = "가".repeat(20);
        mvc.perform(post("/api/auth/signUp").contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.writeValueAsString(Map.of("email", email, "name", name, "password", password))))
                .andExpect(status().isOk());
        var saved = ArgumentCaptor.forClass(Member.class);
        verify(memberMapper).insertMember(saved.capture());
        assertThat(saved.getValue().getEmail()).isEqualTo(email);
        assertThat(saved.getValue().getName()).isEqualTo(name);
        assertThat(saved.getValue().getPassword()).isNotEqualTo(password);
        assertThat(passwordEncoder.matches(password, saved.getValue().getPassword())).isTrue();
        verify(passwordEncoder).encode(password);
    }

    static Stream<String> validPasswords() {
        return Stream.of("a".repeat(72), "가".repeat(24), " secret ");
    }

    @ParameterizedTest
    @MethodSource("invalidPasswords")
    void serviceRejectsInvalidPasswordBeforeHashing(String password) {
        SignUpRequest request = new SignUpRequest();
        request.setEmail("member@example.test");
        request.setName("Member");
        request.setPassword(password);
        assertThatThrownBy(() -> memberService.signUp(request))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        exception -> assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST));
        verifyNoInteractions(memberMapper, passwordEncoder);
    }

    static Stream<Arguments> invalidPasswords() {
        return Stream.of(Arguments.of((Object) null), Arguments.of(""), Arguments.of(" \t\n"),
                Arguments.of("a".repeat(73)), Arguments.of("가".repeat(25)));
    }

    private static Map<String, Object> with(String field, Object value) {
        Map<String, Object> body = new HashMap<>(VALID_BODY);
        body.put(field, value);
        return body;
    }
}

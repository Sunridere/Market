package org.sunrider.market.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.sunrider.market.security.JwtService;
import org.sunrider.market.user.dto.JwtAuthenticationResponse;
import org.sunrider.market.user.dto.SignInRequest;
import org.sunrider.market.user.dto.SignUpRequest;
import org.sunrider.market.user.service.AuthenticationService;
import org.sunrider.market.user.service.UserService;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserService userService;

    @Test
    void signUp_success() throws Exception {
        SignUpRequest request = new SignUpRequest("testuser", "test@test.com", "password123", "Test", "User");
        JwtAuthenticationResponse response = new JwtAuthenticationResponse("jwt-token");

        when(authenticationService.signUp(any(SignUpRequest.class))).thenReturn(response);

        mockMvc.perform(post("/auth/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    @Test
    void signIn_success() throws Exception {
        SignInRequest request = new SignInRequest("testuser", "password123");
        JwtAuthenticationResponse response = new JwtAuthenticationResponse("jwt-token");

        when(authenticationService.signIn(any(SignInRequest.class), any(String.class))).thenReturn(response);

        mockMvc.perform(post("/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    @Test
    void signUp_invalidRequest_returnsBadRequest() throws Exception {
        SignUpRequest request = new SignUpRequest("", "", "", "", "");

        mockMvc.perform(post("/auth/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void signIn_invalidRequest_returnsBadRequest() throws Exception {
        SignInRequest request = new SignInRequest("", "");

        mockMvc.perform(post("/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }
}

package com.example.project_security;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.hasItem;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.example.project_security.dto.UserDTO;
import com.example.project_security.dto.request.LoginRequestDTO;
import com.example.project_security.dto.request.UserRegistrationDTO;
import com.example.project_security.dto.request.UserUpdateDTO;
import com.example.project_security.dto.response.AuthResponse;
import com.example.project_security.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private UserDTO testUserDTO;
    private UserRegistrationDTO registrationDTO;
    private LoginRequestDTO loginRequestDTO;
    private UserUpdateDTO updateDTO;
    private AuthResponse authResponse;

    @BeforeEach
    void setUp() {
        // Setup test data
        testUserDTO = UserDTO.builder()
                .id(1L)
                .firstName("Mario")
                .lastName("Rossi")
                .email("mario.rossi@email.com")
                .address("Via Roma 123")
                .telephone("+393331234567")
                .authorities(Set.of("ROLE_USER"))
                .orderCount(0)
                .hasActiveCart(true)
                .build();

        registrationDTO = UserRegistrationDTO.builder()
                .firstName("Mario")
                .lastName("Rossi")
                .email("mario.rossi@email.com")
                .password("Password123!")
                .address("Via Roma 123")
                .telephone("+393331234567")
                .build();

        loginRequestDTO = new LoginRequestDTO();
        loginRequestDTO.setEmail("mario.rossi@email.com");
        loginRequestDTO.setPassword("Password123!");

        updateDTO = UserUpdateDTO.builder()
                .firstName("Mario")
                .lastName("Verdi")
                .address("Via Milano 456")
                .build();

        authResponse = AuthResponse.builder()
                .token("jwt-token")
                .refreshToken("refresh-token")
                .build();
    }

    // ===== AUTHENTICATION ENDPOINTS TESTS =====

    @Test
    @DisplayName("Should register a new user successfully")
    void testRegisterUser_Success() throws Exception {
        when(userService.registerUser(any(UserRegistrationDTO.class))).thenReturn(testUserDTO);

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("mario.rossi@email.com"))
                .andExpect(jsonPath("$.firstName").value("Mario"))
                .andExpect(jsonPath("$.lastName").value("Rossi"));

        verify(userService, times(1)).registerUser(any(UserRegistrationDTO.class));
    }

    @Test
    @DisplayName("Should fail registration with invalid data")
    void testRegisterUser_InvalidData() throws Exception {
        UserRegistrationDTO invalidDTO = new UserRegistrationDTO();
        // Missing required fields

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).registerUser(any());
    }

    @Test
    @DisplayName("Should login user successfully")
    void testLoginUser_Success() throws Exception {
        when(userService.loginUser(any(LoginRequestDTO.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"));

        verify(userService, times(1)).loginUser(any(LoginRequestDTO.class));
    }

    // ===== USER PROFILE ENDPOINTS TESTS =====

    @Test
    @WithMockUser(username = "mario.rossi@email.com", roles = "USER")
    @DisplayName("Should get current user profile")
    void testGetCurrentUserProfile_Success() throws Exception {
        when(userService.getUserByEmail("mario.rossi@email.com")).thenReturn(testUserDTO);

        mockMvc.perform(get("/api/users/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("mario.rossi@email.com"))
                .andExpect(jsonPath("$.firstName").value("Mario"));

        verify(userService, times(1)).getUserByEmail("mario.rossi@email.com");
    }

    @Test
    @DisplayName("Should fail to get profile when not authenticated")
    void testGetCurrentUserProfile_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/users/profile"))
                .andExpect(status().isUnauthorized());

        verify(userService, never()).getUserByEmail(anyString());
    }

    @Test
    @WithMockUser(username = "mario.rossi@email.com", roles = "USER")
    @DisplayName("Should update current user profile")
    void testUpdateCurrentUserProfile_Success() throws Exception {
        UserDTO updatedUser = UserDTO.builder()
                .id(1L)
                .firstName("Mario")
                .lastName("Verdi")
                .email("mario.rossi@email.com")
                .address("Via Milano 456")
                .build();

        when(userService.getUserByEmail("mario.rossi@email.com")).thenReturn(testUserDTO);
        when(userService.updateUser(eq(1L), any(UserUpdateDTO.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/api/users/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("Verdi"))
                .andExpect(jsonPath("$.address").value("Via Milano 456"));

        verify(userService, times(1)).updateUser(eq(1L), any(UserUpdateDTO.class));
    }

    @Test
    @WithMockUser(username = "mario.rossi@email.com", roles = "USER")
    @DisplayName("Should change password successfully")
    void testChangePassword_Success() throws Exception {
        Map<String, String> passwordData = new HashMap<>();
        passwordData.put("currentPassword", "OldPassword123!");
        passwordData.put("newPassword", "NewPassword123!");

        when(userService.getUserByEmail("mario.rossi@email.com")).thenReturn(testUserDTO);
        doNothing().when(userService).changePassword(eq(1L), anyString(), anyString());

        mockMvc.perform(post("/api/users/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(passwordData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password cambiata con successo"));

        verify(userService, times(1)).changePassword(eq(1L), eq("OldPassword123!"), eq("NewPassword123!"));
    }

    @Test
    @DisplayName("Should check email availability")
    void testCheckEmailAvailability() throws Exception {
        when(userService.isEmailAvailable("test@email.com")).thenReturn(true);

        mockMvc.perform(get("/api/users/check-email")
                .param("email", "test@email.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(true));

        verify(userService, times(1)).isEmailAvailable("test@email.com");
    }

    // ===== ADMIN ENDPOINTS TESTS =====

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should get all users as admin")
    void testGetAllUsers_AsAdmin() throws Exception {
        List<UserDTO> users = Arrays.asList(testUserDTO);
        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].email").value("mario.rossi@email.com"));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should fail to get all users without admin role")
    void testGetAllUsers_Forbidden() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isForbidden());

        verify(userService, never()).getAllUsers();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should get user by ID as admin")
    void testGetUserById_AsAdmin() throws Exception {
        when(userService.getUserById(1L)).thenReturn(testUserDTO);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("mario.rossi@email.com"));

        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should update user as admin")
    void testUpdateUser_AsAdmin() throws Exception {
        UserDTO updatedUser = UserDTO.builder()
                .id(1L)
                .firstName("Mario")
                .lastName("Verdi")
                .email("mario.rossi@email.com")
                .build();

        when(userService.updateUser(eq(1L), any(UserUpdateDTO.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("Verdi"));

        verify(userService, times(1)).updateUser(eq(1L), any(UserUpdateDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should delete user as admin")
    void testDeleteUser_AsAdmin() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Utente eliminato con successo"));

        verify(userService, times(1)).deleteUser(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should add authority to user as admin")
    void testAddAuthority_AsAdmin() throws Exception {
        Map<String, String> authorityData = new HashMap<>();
        authorityData.put("authority", "ROLE_ADMIN");

        UserDTO updatedUser = UserDTO.builder()
                .id(1L)
                .authorities(Set.of("ROLE_USER", "ROLE_ADMIN"))
                .build();

        when(userService.addAuthority(1L, "ROLE_ADMIN")).thenReturn(updatedUser);

        mockMvc.perform(post("/api/users/1/authorities")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authorityData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authorities").isArray())
                .andExpect(jsonPath("$.authorities").value(hasItem("ROLE_ADMIN")));

        verify(userService, times(1)).addAuthority(1L, "ROLE_ADMIN");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should remove authority from user as admin")
    void testRemoveAuthority_AsAdmin() throws Exception {
        UserDTO updatedUser = UserDTO.builder()
                .id(1L)
                .authorities(Set.of("ROLE_USER"))
                .build();

        when(userService.removeAuthority(1L, "ROLE_ADMIN")).thenReturn(updatedUser);

        mockMvc.perform(delete("/api/users/1/authorities/ROLE_ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authorities").isArray())
                .andExpect(jsonPath("$.authorities").value(hasItem("ROLE_USER")));

        verify(userService, times(1)).removeAuthority(1L, "ROLE_ADMIN");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should search users by name as admin")
    void testSearchUsers_AsAdmin() throws Exception {
        List<UserDTO> users = Arrays.asList(testUserDTO);
        when(userService.searchUsersByName("Mario")).thenReturn(users);

        mockMvc.perform(get("/api/users/search")
                .param("name", "Mario"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("Mario"));

        verify(userService, times(1)).searchUsersByName("Mario");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should get users by authority as admin")
    void testGetUsersByAuthority_AsAdmin() throws Exception {
        List<UserDTO> users = Arrays.asList(testUserDTO);
        when(userService.getUsersByAuthority("ROLE_USER")).thenReturn(users);

        mockMvc.perform(get("/api/users/by-authority/ROLE_USER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].authorities").value(hasItem("ROLE_USER")));

        verify(userService, times(1)).getUsersByAuthority("ROLE_USER");
    }
}
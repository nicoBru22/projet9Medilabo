package com.medilabo.microService.utilisateur.controller;

import org.springframework.security.core.Authentication;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medilabo.microService.utilisateur.config.JwtUtil;
import com.medilabo.microService.utilisateur.model.User;
import com.medilabo.microService.utilisateur.model.UserDto;
import com.medilabo.microService.utilisateur.service.IUserService;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.eq;


import java.util.List;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private IUserService userService;
    
    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtUtil jwtUtil;



    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    public void getAllUserControllerTest() throws Exception{
        User userTest1 = new User("1", "nicolasb", "Brunet", "Nicolas", "Password123@", "USER");
        User userTest2 = new User("2", "sarahPiet", "Piet", "Sarah", "Password123@", "ADMIN");
        List<User> listUserMocked = List.of(userTest1, userTest2);
        
        when(userService.getAllUser()).thenReturn(listUserMocked);

        mockMvc.perform(get("/utilisateur/list"))
            .andExpect(status().isOk());
        
        verify(userService, times(1)).getAllUser();
    }
    
    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    public void addUserControllerTest() throws Exception {
        User userTest1 = new User(null, "nicolasb", "Brunet", "Nicolas", "Password123@", "USER");
        User userAdded = new User("1", "nicolasb", "Brunet", "Nicolas", "Password123@", "USER");

        when(userService.addUser(any(User.class))).thenReturn(userAdded);

        ObjectMapper objectMapper = new ObjectMapper();
        String userJson = objectMapper.writeValueAsString(userTest1);

        mockMvc.perform(post("/utilisateur/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
            .andExpect(status().isCreated());

        verify(userService, times(1)).addUser(any(User.class));
    }
    
    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    public void deleteUserControllerTest() throws Exception {
        String userIdToDelete = "1";

        doNothing().when(userService).deleteUser(userIdToDelete);

        mockMvc.perform(delete("/utilisateur/delete/{id}", userIdToDelete))
               .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(userIdToDelete);
    }

    
    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void updateUserControllerTest() throws Exception {
        User userTest1 = new User("1", "nicolasb", "Brunet", "Nicolas", "Password123@", "USER");

        when(userService.updateUser(any(User.class), eq("1"))).thenReturn(userTest1);

        ObjectMapper objectMapper = new ObjectMapper();
        String userJson = objectMapper.writeValueAsString(userTest1);

        mockMvc.perform(put("/utilisateur/update/{id}", userTest1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value("1"))
            .andExpect(jsonPath("$.username").value("nicolasb"));

        verify(userService, times(1)).updateUser(any(User.class), eq("1"));
    }

    
    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    public void loginControllerTest() throws Exception {
        UserDto userDto = new UserDto("nicolasb", "Password123@");
        User user = new User("1", "nicolasb", "Brunet", "Nicolas", "Password123@", "USER");
        String tokenMock = "mocked.jwt.token";

        // Mock Authentication local
        Authentication auth = Mockito.mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        when(userService.getUserByUsername("nicolasb")).thenReturn(user);
        when(jwtUtil.generateToken(user.getUsername(), user.getRole(), user.getPrenom(), user.getNom())).thenReturn(tokenMock);

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(userDto);

        mockMvc.perform(post("/utilisateur/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("success"))
            .andExpect(jsonPath("$.token").value(tokenMock));

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userService, times(1)).getUserByUsername("nicolasb");
        verify(jwtUtil, times(1)).generateToken(user.getUsername(), user.getRole(), user.getPrenom(), user.getNom());
    }


    

    
    

}



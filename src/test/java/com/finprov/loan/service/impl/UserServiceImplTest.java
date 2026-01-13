package com.finprov.loan.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.finprov.loan.dto.UserDto;
import com.finprov.loan.entity.User;
import com.finprov.loan.repository.UserRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private UserServiceImpl userService;

  private User user;

  @BeforeEach
  void setUp() {
    user = User.builder()
        .id(1L)
        .username("testuser")
        .email("test@example.com")
        .password("password")
        .isActive(true)
        .build();
  }

  @Test
  void testCreateUser() {
    when(userRepository.save(any(User.class))).thenReturn(user);

    User createdUser = userService.createUser(user);

    assertNotNull(createdUser);
    assertEquals("testuser", createdUser.getUsername());
    verify(userRepository, times(1)).save(any(User.class));
  }

  @Test
  void testGetAllUsers() {
    when(userRepository.findAll()).thenReturn(Arrays.asList(user));

    List<UserDto> users = userService.getAllUsers(null);

    assertNotNull(users);
    assertEquals(1, users.size());
    assertEquals("testuser", users.get(0).getUsername());
    verify(userRepository, times(1)).findAll();
  }

  @Test
  void testDeleteUser() {
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    doNothing().when(userRepository).delete(user);

    userService.deleteUser(1L);

    verify(userRepository, times(1)).findById(1L);
    verify(userRepository, times(1)).delete(user);
  }
}

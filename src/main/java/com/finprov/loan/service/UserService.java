package com.finprov.loan.service;

import com.finprov.loan.dto.UserDto;
import com.finprov.loan.entity.User;
import java.util.List;

public interface UserService {
  User createUser(User user);

  List<UserDto> getAllUsers(Long branchId);

  void deleteUser(Long id);

  void updateStatus(Long id, Boolean isActive);

  User updateUser(Long id, User user);

  User assignBranches(Long userId, List<Long> branchIds);
}

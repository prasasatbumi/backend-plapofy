package com.finprov.loan.service.impl;

import com.finprov.loan.entity.Menu;
import com.finprov.loan.repository.MenuRepository;
import com.finprov.loan.service.MenuService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

  private final MenuRepository menuRepository;

  @Override
  public Menu create(Menu menu) {
    return menuRepository.save(menu);
  }

  @Override
  public Menu update(Long id, Menu menu) {
    Menu existing =
        menuRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Menu not found"));
    existing.setCode(menu.getCode());
    existing.setName(menu.getName());
    existing.setPath(menu.getPath());
    return menuRepository.save(existing);
  }

  @Override
  public void delete(Long id) {
    menuRepository.deleteById(id);
  }

  @Override
  public List<Menu> list() {
    return menuRepository.findAll();
  }
}

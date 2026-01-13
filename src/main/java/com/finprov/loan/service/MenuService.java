package com.finprov.loan.service;

import com.finprov.loan.entity.Menu;
import java.util.List;

public interface MenuService {
  Menu create(Menu menu);

  Menu update(Long id, Menu menu);

  void delete(Long id);

  List<Menu> list();
}

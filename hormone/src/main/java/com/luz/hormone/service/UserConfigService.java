package com.luz.hormone.service;

import com.luz.hormone.entity.Node;

public interface UserConfigService {

    public void saveRoute(int userId);
    public void delRoute(int userId);
    public boolean checkRoute(int userId);
}

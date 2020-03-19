package com.luz.route.service;

import com.luz.route.enetity.Node;

import java.util.List;

public interface UserConfigService {
    public List<Node> getRoute();
    public void delRoute(int userId);
    public Node checkUserRoute(int userId);
}

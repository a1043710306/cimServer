package com.luz.route.service.impl;

import com.luz.route.cache.UserCache;
import com.luz.route.enetity.Node;
import com.luz.route.service.UserConfigService;
import com.luz.route.utils.ZkitUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserConfigServiceImpl implements UserConfigService {
    @Autowired
    UserCache userCache;
    @Override
    public List<Node> getRoute() {
        return ZkitUtils.getAllNode();
    }

    @Override
    public void delRoute(int userId) {
        //清除路由
        userCache.delRoute(userId);
    }

    @Override
    public Node checkUserRoute(int userId) {
        return userCache.getUserRoute(userId);
    }
}

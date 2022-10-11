package com.talodu.taloduspringboot.server.interfaces;

import com.talodu.taloduspringboot.server.model.Server;

import java.io.IOException;
import java.util.Collection;

public interface ServerInterface {
    Server create(Server server);
    Server ping(String ipAddress) throws IOException;
    Collection<Server> list(int limit);
    Server getServer(Long id);
    Server update(Server server);
    Boolean delete(Long id);
}

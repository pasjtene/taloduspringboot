package com.talodu.taloduspringboot.server.repository;

import com.talodu.taloduspringboot.server.model.Server;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServerRepository extends JpaRepository<Server, Long> {
    Server findByIpAddress(String ipAddress);
    //if name was unique, we could also do ..
    //Server findByName(String name);
}

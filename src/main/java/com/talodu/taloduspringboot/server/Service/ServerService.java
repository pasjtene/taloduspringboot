package com.talodu.taloduspringboot.server.Service;

import com.talodu.taloduspringboot.server.enumeration.Status;
import com.talodu.taloduspringboot.server.interfaces.ServerInterface;
import com.talodu.taloduspringboot.server.model.Server;
import com.talodu.taloduspringboot.server.repository.ServerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.transaction.Transactional;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Collection;
import java.util.Random;

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class ServerService implements ServerInterface {
    private final ServerRepository serverRepository;

    @Override
    public Server create(Server server) {
        log.info("Saving server: {}", server.getName());
        server.setImageUrl(setServerImageUrl());
        return serverRepository.save(server);
    }

    private String setServerImageUrl() {
        String[] imageNames = {"server1.png", "server2.png",
                "server3.png", "server4.png"};
        return ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/server/image/"+ imageNames[new Random().nextInt(4)]).toString();
    }

    @Override
    public Server ping(String ipAddress) throws IOException {
        log.info("Pinging server ip: {}", ipAddress);
        Server server = serverRepository.findByIpAddress(ipAddress);
        InetAddress address = InetAddress.getByName(ipAddress);
        server.setStatus(address.isReachable(10000)? Status.SERVER_UP: Status.SERVER_DOWN);
        serverRepository.save(server);

        return server;
    }

    @Override
    public Collection<Server> list(int limit) {
        log.info("Fetching all servers");
        return serverRepository.findAll(PageRequest.of(0, limit)).toList();
    }

    @Override
    public Server getServer(Long id) {
        log.info("Fetching server by id {}", id);


        return serverRepository.findById(id).get();
    }

    @Override
    public Server update(Server server) {
        log.info("updating server {}", server);
        //The provided server must have an id, then it will be updated
        return serverRepository.save(server);
    }

    @Override
    public Boolean delete(Long id) {
        log.info("Deleting server with id {}", id);
        serverRepository.deleteById(id);
        //The provided server must have an id, then it will be updated
        return Boolean.TRUE;
    }
}

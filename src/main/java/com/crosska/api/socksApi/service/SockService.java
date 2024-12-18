package com.crosska.api.socksApi.service;

import com.crosska.api.socksApi.model.Sock;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface SockService {

    ResponseEntity<?> addSock(Sock sock);

    ResponseEntity<?> removeSock(Sock sock);

    List<Sock> readAll();

    boolean updateSock(Sock sock, int id);

    //void uploadSockData();

}

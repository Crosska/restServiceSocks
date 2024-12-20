package com.crosska.api.socksApi.service;

import com.crosska.api.socksApi.model.Sock;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface SockService {

    ResponseEntity<?> addSock(Sock sock);

    ResponseEntity<?> removeSock(Sock sock);

    ResponseEntity<?> getSocksAmount(String[] parameters);

    List<Sock> getAllSocks();

    boolean updateSock(Sock sock, int id);

    ResponseEntity<?> getSocksFilter(String sortBy, int[] betweenParameters);

    //void uploadSockData();

}

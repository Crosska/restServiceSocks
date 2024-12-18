package com.crosska.api.socksApi.service;

import com.crosska.api.socksApi.model.Sock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class SockServiceImpl implements SockService {

    private static final Map<Integer, Sock> SOCKS_REPOSITORY_MAP = new HashMap<>();

    private static final AtomicInteger SOCKS_ID_HOLDER = new AtomicInteger();

    @Override
    public ResponseEntity<?> addSock(Sock sock) {
        for (Map.Entry<Integer, Sock> entry : SOCKS_REPOSITORY_MAP.entrySet()) {
            if (entry.getValue().getColor().equals(sock.getColor()) &&
                    entry.getValue().getCotton() == sock.getCotton()) {
                entry.getValue().setAmount(entry.getValue().getAmount() + sock.getAmount());
                System.out.println("Increased existing sock amount");
                return ResponseEntity.status(HttpStatus.ACCEPTED).body("Увеличены существующие носки на указанное количество");
            }
        }
        final int socksId = SOCKS_ID_HOLDER.incrementAndGet();
        sock.setId(socksId);
        SOCKS_REPOSITORY_MAP.put(socksId, sock);
        System.out.println("Created new sock with parameters");
        return ResponseEntity.status(HttpStatus.CREATED).body("Созданы новые носки с указанными параметрами");
    }

    @Override
    public ResponseEntity<?> removeSock(Sock sock) {
        for (Map.Entry<Integer, Sock> entry : SOCKS_REPOSITORY_MAP.entrySet()) {
            if (entry.getValue().getColor().equals(sock.getColor()) &&
                    entry.getValue().getCotton() == sock.getCotton()) {
                int amount = entry.getValue().getAmount();
                if (amount >= sock.getAmount()) {
                    System.out.println("Decremented");
                    entry.getValue().setAmount(amount - sock.getAmount());
                    return ResponseEntity.status(HttpStatus.ACCEPTED).body("Успешно отобраны носки");
                } else {
                    System.out.println("Not enough socks");
                    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Недостаточно носков для отбора");
                }
            }
        }
        System.out.println("Not found in map to decrement");
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Не найдены носки по заданным параметрам");
    }

    @Override
    public List<Sock> readAll() {
        return new ArrayList<>(SOCKS_REPOSITORY_MAP.values());
    }

    @Override
    public boolean updateSock(Sock sock, int id) {
        if (SOCKS_REPOSITORY_MAP.containsKey(id)) {
            sock.setId(id);
            SOCKS_REPOSITORY_MAP.put(id, sock);
            return true;
        }
        return false;
    }

}

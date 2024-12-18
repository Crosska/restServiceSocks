package com.crosska.api.socksApi.service;

import com.crosska.api.socksApi.model.Sock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class SockServiceImpl implements SockService {

    //TODO change to repository (hibernate)
    private static final Map<Integer, Sock> SOCKS_REPOSITORY_MAP = new HashMap<>();

    //TODO remove due to future DB
    private static final AtomicInteger SOCKS_ID_HOLDER = new AtomicInteger();

    @Override
    public ResponseEntity<?> addSock(Sock sock) {
        for (Map.Entry<Integer, Sock> entry : SOCKS_REPOSITORY_MAP.entrySet()) {
            if (entry.getValue().getColor().equals(sock.getColor()) &&
                    entry.getValue().getCotton() == sock.getCotton()) {
                entry.getValue().setAmount(entry.getValue().getAmount() + sock.getAmount());
                //TODO change to log
                System.out.println("Increased existing sock amount");
                return ResponseEntity.status(HttpStatus.ACCEPTED).body("Увеличены существующие носки на указанное количество");
            }
        }
        final int socksId = SOCKS_ID_HOLDER.incrementAndGet();
        sock.setId(socksId);
        SOCKS_REPOSITORY_MAP.put(socksId, sock);
        //TODO change to log
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
                    //TODO change to log
                    System.out.println("Decremented");
                    entry.getValue().setAmount(amount - sock.getAmount());
                    return ResponseEntity.status(HttpStatus.ACCEPTED).body("Успешно отобраны носки");
                } else {
                    //TODO change to log
                    System.out.println("Not enough socks");
                    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Недостаточно носков для отбора");
                }
            }
        }
        //TODO change to log
        System.out.println("Not found in map to decrement");
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Не найдены носки по заданным параметрам");
    }

    @Override
    public List<Sock> getAllSocks() {
        //TODO add log
        return SOCKS_REPOSITORY_MAP.values().stream().toList();
    }

    @Override
    public ResponseEntity<?> getSocksAmount(String[] parameters) {
        int amount = 0, cotton;
        if (parameters[0] != null && parameters[1] != null && parameters[2] != null) { // All parameters
            try {
                cotton = Integer.parseInt(parameters[2]);
            } catch (Exception e) {
                //TODO add log
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("Вы указали неправильное число для сравнения");
            }
            //TODO add log for each yield
            return switch (parameters[1]) {
                case "moreThan" -> {
                    for (Map.Entry<Integer, Sock> entry : SOCKS_REPOSITORY_MAP.entrySet()) {
                        if (entry.getValue().getCotton() > cotton && entry.getValue().getColor().equals(parameters[0])) {
                            amount = amount + entry.getValue().getAmount();
                        }
                    }
                    yield ResponseEntity.status(HttpStatus.OK).body(amount);
                }
                case "lessThan" -> {
                    for (Map.Entry<Integer, Sock> entry : SOCKS_REPOSITORY_MAP.entrySet()) {
                        if (entry.getValue().getCotton() < cotton && entry.getValue().getColor().equals(parameters[0])) {
                            amount = amount + entry.getValue().getAmount();
                        }
                    }
                    yield ResponseEntity.status(HttpStatus.OK).body(amount);
                }
                case "equal" -> {
                    for (Map.Entry<Integer, Sock> entry : SOCKS_REPOSITORY_MAP.entrySet()) {
                        if (entry.getValue().getCotton() == cotton && entry.getValue().getColor().equals(parameters[0])) {
                            amount = amount + entry.getValue().getAmount();
                        }
                    }
                    yield ResponseEntity.status(HttpStatus.OK).body(amount);
                }
                default ->
                        ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("Вы неправильно указали параметр сравнения (moreThan, lessThan, equal)");
            };
        } else if (parameters[0] == null && parameters[1] != null && parameters[2] != null) { // No color, comparison and cotton
            try {
                cotton = Integer.parseInt(parameters[2]);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("Вы указали неправильное число для сравнения");
            }
            return switch (parameters[1]) {
                case "moreThan" -> {
                    for (Map.Entry<Integer, Sock> entry : SOCKS_REPOSITORY_MAP.entrySet()) {
                        if (entry.getValue().getCotton() > cotton) {
                            amount = amount + entry.getValue().getAmount();
                        }
                    }
                    yield ResponseEntity.status(HttpStatus.OK).body(amount);
                }
                case "lessThan" -> {
                    for (Map.Entry<Integer, Sock> entry : SOCKS_REPOSITORY_MAP.entrySet()) {
                        if (entry.getValue().getCotton() < cotton) {
                            amount = amount + entry.getValue().getAmount();
                        }
                    }
                    yield ResponseEntity.status(HttpStatus.OK).body(amount);
                }
                case "equal" -> {
                    for (Map.Entry<Integer, Sock> entry : SOCKS_REPOSITORY_MAP.entrySet()) {
                        if (entry.getValue().getCotton() == cotton) {
                            amount = amount + entry.getValue().getAmount();
                        }
                    }
                    yield ResponseEntity.status(HttpStatus.OK).body(amount);
                }
                default ->
                        ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("Вы неправильно указали параметр сравнения (moreThan, lessThan, equal)");
            };
        } else if (parameters[0] != null) { // Color, no comparison or cotton
            for (Map.Entry<Integer, Sock> entry : SOCKS_REPOSITORY_MAP.entrySet()) {
                if (Objects.equals(entry.getValue().getColor(), parameters[0])) {
                    amount = amount + entry.getValue().getAmount();
                }
            }
            return ResponseEntity.status(HttpStatus.OK).body(amount);
        }
        for (Map.Entry<Integer, Sock> entry : SOCKS_REPOSITORY_MAP.entrySet()) { // No parameters
            amount = amount + entry.getValue().getAmount();
        }
        return ResponseEntity.status(HttpStatus.OK).body(amount);
    }

    @Override
    public boolean updateSock(Sock sock, int id) {
        //TODO add log
        if (SOCKS_REPOSITORY_MAP.containsKey(id)) {
            sock.setId(id);
            SOCKS_REPOSITORY_MAP.put(id, sock);
            return true;
        }
        return false;
    }

}

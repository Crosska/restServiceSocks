package com.crosska.api.socksApi.service;

import com.crosska.api.socksApi.dao.DAOImpl;
import com.crosska.api.socksApi.model.Sock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SockServiceImpl implements SockService {

    private final DAOImpl daoImpl;

    @Autowired
    public SockServiceImpl(DAOImpl daoImpl) {
       this.daoImpl = daoImpl;
    }

    @Override
    public ResponseEntity<?> addSock(Sock sock) {
        Sock existSock = daoImpl.findFirstByColorAndCotton(sock.getColor(), sock.getCotton());
        if (existSock != null) {
            existSock.setAmount(existSock.getAmount() + sock.getAmount());
            daoImpl.update(existSock);
            System.out.println("Increased existing sock amount");
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("Увеличены существующие носки на указанное количество");
        } else {
            Sock newSock = new Sock();
            newSock.setColor(sock.getColor());
            newSock.setCotton(sock.getCotton());
            newSock.setAmount(sock.getAmount());
            daoImpl.save(newSock);
            System.out.println("Created new sock with parameters");
            return ResponseEntity.status(HttpStatus.CREATED).body("Созданы новые носки с указанными параметрами");
        }
    }

    @Override
    public ResponseEntity<?> removeSock(Sock sock) {
        Sock existSock = daoImpl.findFirstByColorAndCotton(sock.getColor(), sock.getCotton());
        if (existSock != null) {
            if (existSock.getAmount() > sock.getAmount()) {
                existSock.setAmount(existSock.getAmount() - sock.getAmount());
                daoImpl.update(existSock);
                System.out.println("Decreased existing sock amount");
                return ResponseEntity.status(HttpStatus.ACCEPTED).body("Успешно отобраны носки");
            } else {
                System.out.println("Not enough socks");
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Недостаточно носков для отбора");
            }
        } else {
            System.out.println("Sock not found in map to decrement");
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Не найдены носки по заданным параметрам");
        }
    }

    @Override
    public List<Sock> getAllSocks() {
        //TODO add log
        return daoImpl.findAll();
    }

    @Override
    public ResponseEntity<?> getSocksAmount(String[] parameters) {
        int cotton;
        if (parameters[0] != null && parameters[1] != null && parameters[2] != null) { // All parameters
            try {
                cotton = Integer.parseInt(parameters[2]);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("Вы указали неправильное число для сравнения");
            }
            long sumAmount = daoImpl.findManyWithAllParameters(parameters[0], parameters[1], cotton);
            return ResponseEntity.status(HttpStatus.OK).body(sumAmount);
        } else if (parameters[0] == null && parameters[1] != null && parameters[2] != null) { // No color, comparison and cotton
            try {
                cotton = Integer.parseInt(parameters[2]);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("Вы указали неправильное число для сравнения");
            }
            long sumAmount = daoImpl.findManyWithCottonParameters(parameters[1], cotton);
            return ResponseEntity.status(HttpStatus.OK).body(sumAmount);
        } else if (parameters[0] != null) { // Color, no comparison or cotton
            long sumAmount = daoImpl.findManyWithColorParameters(parameters[0]);
            return ResponseEntity.status(HttpStatus.OK).body(sumAmount);
        }
        long sumAmount = daoImpl.findManyWithoutParameters();
        return ResponseEntity.status(HttpStatus.OK).body(sumAmount);
    }

    @Override
    public ResponseEntity<?> getSocksFilter(String sortBy, int[] betweenParameters) {
        if (sortBy == null && (betweenParameters[0] > 0 && betweenParameters[1] > 0)) {
            return ResponseEntity.status(HttpStatus.OK).body(daoImpl.findBetween(betweenParameters[0], betweenParameters[1]));
        } else if (sortBy != null && (betweenParameters[0] <= 0 || betweenParameters[1] <= 0)) {
            return ResponseEntity.status(HttpStatus.OK).body(daoImpl.findSort(sortBy));
        } else if (sortBy != null) {
            return ResponseEntity.status(HttpStatus.OK).body(daoImpl.findBetweenSort(betweenParameters[0], betweenParameters[1], sortBy));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Параметры фильтров не указаны");
    }

    @Override
    public boolean updateSock(Sock sock, int id) {
        //TODO add log
        Sock existSock = daoImpl.findById(id);
        if (existSock != null) {
            existSock.setAmount(sock.getAmount());
            existSock.setColor(sock.getColor());
            existSock.setCotton(sock.getCotton());
            daoImpl.update(existSock);
            return true;
        }
        return false;
    }

}

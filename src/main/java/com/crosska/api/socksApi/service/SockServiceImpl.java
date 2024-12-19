package com.crosska.api.socksApi.service;

import com.crosska.api.socksApi.dao.DAOImpl;
import com.crosska.api.socksApi.model.Sock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SockServiceImpl implements SockService {

    private final DAOImpl daoImpl = new DAOImpl();

    @Override
    public ResponseEntity<?> addSock(Sock sock) {
        Sock existSock = daoImpl.findSingleByColorAndCotton(sock.getColor(), sock.getCotton());
        if (existSock != null) {
            existSock.setColor(sock.getColor());
            existSock.setCotton(sock.getCotton());
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
        Sock existSock = daoImpl.findSingleByColorAndCotton(sock.getColor(), sock.getCotton());
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
        int amount = 0, cotton;
        if (parameters[0] != null && parameters[1] != null && parameters[2] != null) { // All parameters
            try {
                cotton = Integer.parseInt(parameters[2]);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("Вы указали неправильное число для сравнения");
            }
            ArrayList<Sock> selectedSocks = (ArrayList<Sock>) daoImpl.findManyWithAllParameters(parameters[0], parameters[1], cotton);
            try {
                for (Sock sock : selectedSocks) amount = amount + sock.getAmount();
                return ResponseEntity.status(HttpStatus.OK).body(amount);
            } catch (NullPointerException e) {
                return ResponseEntity.status(HttpStatus.OK).body(0);
            }
        } else if (parameters[0] == null && parameters[1] != null && parameters[2] != null) { // No color, comparison and cotton
            try {
                cotton = Integer.parseInt(parameters[2]);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("Вы указали неправильное число для сравнения");
            }
            ArrayList<Sock> selectedSocks = (ArrayList<Sock>) daoImpl.findManyWithCottonParameters(parameters[1], cotton);
            for (Sock sock : selectedSocks) amount = amount + sock.getAmount();
            return ResponseEntity.status(HttpStatus.OK).body(amount);
        } else if (parameters[0] != null) { // Color, no comparison or cotton
            ArrayList<Sock> selectedSocks = (ArrayList<Sock>) daoImpl.findManyWithColorParameters(parameters[0]);
            try {
                for (Sock sock : selectedSocks) amount = amount + sock.getAmount();
                return ResponseEntity.status(HttpStatus.OK).body(amount);
            } catch (NullPointerException e) {
                return ResponseEntity.status(HttpStatus.OK).body(0);
            }
        }
        ArrayList<Sock> selectedSocks = (ArrayList<Sock>) daoImpl.findAll();
        try {
            for (Sock sock : selectedSocks) amount = amount + sock.getAmount();
            return ResponseEntity.status(HttpStatus.OK).body(amount);
        } catch (NullPointerException e) {
            return ResponseEntity.status(HttpStatus.OK).body(0);
        }
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

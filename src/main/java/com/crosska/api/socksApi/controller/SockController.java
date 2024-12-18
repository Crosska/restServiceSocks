package com.crosska.api.socksApi.controller;

import com.crosska.api.socksApi.model.Sock;
import com.crosska.api.socksApi.service.SockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Говорим спрингу, что данный класс это REST контроллер (логика обработки клиентских запросов)
public class SockController {

    private final SockService sockService;

    @Autowired
    public SockController(SockService sockService) { // Внедряем зависимости через конструктор и аннотацию @Autowired
        this.sockService = sockService;
    }

    @PostMapping(value = "/api/socks/income")
    public ResponseEntity<?> add(@RequestBody Sock sock) {
        return  sockService.addSock(sock);
    }

    @PostMapping(value = "/api/socks/outcome")
    public ResponseEntity<?> remove(@RequestBody Sock sock) {
        return sockService.removeSock(sock);
    }

    @GetMapping(value = "/api/socks")
    public ResponseEntity<List<Sock>> read() {
        final List<Sock> clients = sockService.readAll();
        return clients != null && !clients.isEmpty()
                ? new ResponseEntity<>(clients, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping(value = "/api/socks/{id}")
    public ResponseEntity<?> update(@PathVariable(name = "id") int id, @RequestBody Sock sock) {
        if (sockService.updateSock(sock, id)) {
            return ResponseEntity.status(HttpStatus.OK).body("Успешно обновлены параметры носка");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body("Не найден такой вид носков");
        }
    }

}

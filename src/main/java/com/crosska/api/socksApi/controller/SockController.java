package com.crosska.api.socksApi.controller;

import com.crosska.api.socksApi.model.Sock;
import com.crosska.api.socksApi.service.SockService;
import org.apache.commons.csv.CSVParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.SQLOutput;

@RestController // Говорим спрингу, что данный класс это REST контроллер (логика обработки клиентских запросов)
public class SockController {

    private final SockService sockService;

    @Autowired
    public SockController(SockService sockService) { // Внедряем зависимости через конструктор и аннотацию @Autowired
        this.sockService = sockService;
    }

    @PostMapping(value = "/api/socks/income")
    public ResponseEntity<?> add(@RequestBody Sock sock) {
        if (sock.getCotton() < 0 || sock.getCotton() > 100) {
            return new ResponseEntity<>("Invalid cotton", HttpStatus.BAD_REQUEST);
        } else if (sock.getAmount() < 0) {
            return new ResponseEntity<>("Invalid amount", HttpStatus.BAD_REQUEST);
        }
        return sockService.addSock(sock);
    }

    @PostMapping(value = "/api/socks/outcome")
    public ResponseEntity<?> remove(@RequestBody Sock sock) {
        if (sock.getCotton() < 0 || sock.getCotton() > 100) {
            return new ResponseEntity<>("Invalid cotton", HttpStatus.BAD_REQUEST);
        } else if (sock.getAmount() < 0) {
            return new ResponseEntity<>("Invalid amount", HttpStatus.BAD_REQUEST);
        }
        return sockService.removeSock(sock);
    }

    @GetMapping(value = "/api/socks")
    public ResponseEntity<?> read(
            @RequestParam(required = false) String color,
            @RequestParam(required = false) String comparison,
            @RequestParam(required = false) String cotton) {
        //TODO change to custom class?
        String[] searchParameters = new String[3];
        if (color != null) searchParameters[0] = color;
        if (comparison != null) searchParameters[1] = comparison;
        if (cotton != null) searchParameters[2] = cotton;
        return sockService.getSocksAmount(searchParameters);
    }

    @GetMapping(value = "/api/socks/all")
    public ResponseEntity<?> read() {
        return ResponseEntity.status(HttpStatus.OK).body(sockService.getAllSocks());
    }

    @PutMapping(value = "/api/socks/{id}")
    public ResponseEntity<?> update(@PathVariable(name = "id") int id, @RequestBody Sock sock) {
        if (sock.getCotton() < 0 || sock.getCotton() > 100) {
            return new ResponseEntity<>("Invalid cotton", HttpStatus.BAD_REQUEST);
        } else if (sock.getAmount() < 0) {
            return new ResponseEntity<>("Invalid amount", HttpStatus.BAD_REQUEST);
        }
        if (sockService.updateSock(sock, id)) {
            return ResponseEntity.status(HttpStatus.OK).body("Sock data updated");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body("No such sock");
        }
    }

    @PostMapping("/api/socks/batch")
    public ResponseEntity<?> batchRead(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            CSVParser records = CSVFormat.DEFAULT.builder().setHeader("color", "cotton", "amount").setSkipHeaderRecord(false).build().parse(reader);
            boolean withErrors = false;
            for (CSVRecord record : records) {
                String color = record.get("color");
                int cotton;
                int amount;
                try {
                    cotton = Integer.parseInt(record.get("cotton"));
                    amount = Integer.parseInt(record.get("amount"));
                } catch (NumberFormatException e) {
                    withErrors = true;
                    System.out.println("Error with parsing INT");
                    continue;
                }

                if (cotton < 0 || cotton > 100) {
                    return new ResponseEntity<>("Invalid cotton", HttpStatus.EXPECTATION_FAILED);
                } else if (amount < 0) {
                    return new ResponseEntity<>("Invalid amount", HttpStatus.EXPECTATION_FAILED);
                }

                Sock sock = new Sock();
                sock.setCotton(cotton);
                sock.setColor(color);
                sock.setAmount(amount);
                sockService.addSock(sock);

                System.out.println("Added - color: " + color + ", cotton: " + cotton + ", amount: " + amount);
            }
            if (withErrors) {
                return ResponseEntity.status(HttpStatus.OK).body("Sock data updated, but invalid line were skipped");
            } else {
                return ResponseEntity.status(HttpStatus.OK).body("Sock data updated");
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Error reading file");
        }
    }
}

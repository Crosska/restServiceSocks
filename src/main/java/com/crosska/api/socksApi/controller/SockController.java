package com.crosska.api.socksApi.controller;

import com.crosska.api.socksApi.model.Sock;
import com.crosska.api.socksApi.service.SockService;
import org.apache.commons.csv.CSVParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@RestController // Говорим спрингу, что данный класс это REST контроллер (логика обработки клиентских запросов)
public class SockController {

    private final SockService sockService;

    private final Logger logger = LoggerFactory.getLogger(SockController.class);

    @Autowired
    public SockController(SockService sockService) { // Внедряем зависимости через конструктор и аннотацию @Autowired
        this.sockService = sockService;
    }

    @PostMapping(value = "/api/socks/income")
    public ResponseEntity<?> add(@RequestBody Sock sock) {
        if (sock.getCotton() < 0 || sock.getCotton() > 100) {
            logger.error("POST request /api/socks/income received incorrect parameter cotton");
            return new ResponseEntity<>("Invalid cotton", HttpStatus.BAD_REQUEST);
        } else if (sock.getAmount() < 0) {
            logger.error("POST request /api/socks/income received incorrect parameter amount");
            return new ResponseEntity<>("Invalid amount", HttpStatus.BAD_REQUEST);
        }
        logger.info("POST request /api/socks/income passed the execution on");
        return sockService.addSock(sock);
    }

    @PostMapping(value = "/api/socks/outcome")
    public ResponseEntity<?> remove(@RequestBody Sock sock) {
        if (sock.getCotton() < 0 || sock.getCotton() > 100) {
            logger.error("POST request /api/socks/outcome received incorrect parameter cotton");
            return new ResponseEntity<>("Invalid cotton", HttpStatus.BAD_REQUEST);
        } else if (sock.getAmount() < 0) {
            logger.error("POST request /api/socks/outcome received incorrect parameter amount");
            return new ResponseEntity<>("Invalid amount", HttpStatus.BAD_REQUEST);
        }
        logger.info("POST request /api/socks/outcome passed the execution on");
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
        logger.info("GET request /api/socks passed the execution on");
        return sockService.getSocksAmount(searchParameters);
    }

    @GetMapping(value = "/api/socks/all")
    public ResponseEntity<?> readAll() {
        logger.info("GET request /api/socks/all passed the execution on");
        return ResponseEntity.status(HttpStatus.OK).body(sockService.getAllSocks());
    }

    @PutMapping(value = "/api/socks/{id}")
    public ResponseEntity<?> update(@PathVariable(name = "id") int id, @RequestBody Sock sock) {
        if (sock.getCotton() < 0 || sock.getCotton() > 100) {
            logger.error("PUT request /api/socks/{id}} received incorrect parameter cotton");
            return new ResponseEntity<>("Invalid cotton", HttpStatus.BAD_REQUEST);
        } else if (sock.getAmount() < 0) {
            logger.error("PUT request /api/socks/{id}} received incorrect parameter amount");
            return new ResponseEntity<>("Invalid amount", HttpStatus.BAD_REQUEST);
        }
        logger.info("PUT request /api/socks/{id} passed the execution on");
        if (sockService.updateSock(sock, id)) {
            logger.info("DB data updated successfully");
            return ResponseEntity.status(HttpStatus.OK).body("Sock data updated");
        } else {
            logger.info("DB data updated partially successfully");
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body("No such sock");
        }
    }

    @PostMapping("/api/socks/batch")
    public ResponseEntity<?> batchRead(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            logger.error("POST request /api/socks/batch} received incorrect file");
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
                    logger.error("POST request /api/socks/batch} encountered line with incorrect cotton");
                    return new ResponseEntity<>("Invalid cotton", HttpStatus.EXPECTATION_FAILED);
                } else if (amount < 0) {
                    logger.error("POST request /api/socks/batch} encountered line with incorrect amount");
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
                logger.warn("POST request /api/socks/batch sock data was added but some lines were skipped");
                return ResponseEntity.status(HttpStatus.OK).body("Sock data updated, but invalid line were skipped");
            } else {
                logger.warn("POST request /api/socks/batch sock data was added");
                return ResponseEntity.status(HttpStatus.OK).body("Sock data updated");
            }
        } catch (IOException e) {
            logger.error("POST request /api/socks/batch catch an error while reading file");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Error reading file");
        }
    }
}

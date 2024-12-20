package com.crosska.api.socksApi.controller;

import com.crosska.api.socksApi.model.Sock;
import com.crosska.api.socksApi.service.SockServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Sock Controller", description = "REST api controller for Socks")
@RestController // Говорим спрингу, что данный класс это REST контроллер (логика обработки клиентских запросов)
public class SockController {

    private final SockServiceImpl sockService;

    private final Logger logger = LoggerFactory.getLogger(SockController.class);

    @Autowired
    public SockController(SockServiceImpl sockServiceImpl) { // Внедряем зависимости через конструктор и аннотацию @Autowired
        this.sockService = sockServiceImpl;
    }

    @Operation(summary = "Post socks income", description = "Add new sock object with data into DataBase table socks, if that sock existed - add amount, if not - create new")
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

    @Operation(summary = "Post socks outcome", description = "Remove amount from existed sock if there are enough amount")
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

    @Operation(summary = "Get socks sum of amount", description = "Sum amount of socks, received with or without parameters filter")
    @GetMapping(value = "/api/socks")
    public ResponseEntity<?> read(
            @Parameter(description = "String value of color name", example = "red")
            @RequestParam(required = false) String color,
            @Parameter(description = "String value of type of comparison", example = "moreThan | lessThan | equal")
            @RequestParam(required = false) String comparison,
            @Parameter(description = "Integer value using in comparison", example = "10")
            @RequestParam(required = false) String cotton) {
        //TODO change to custom class?
        String[] searchParameters = new String[3];
        if (color != null) searchParameters[0] = color;
        if (comparison != null) searchParameters[1] = comparison;
        if (cotton != null) searchParameters[2] = cotton;
        logger.info("GET request /api/socks passed the execution on");
        return sockService.getSocksAmount(searchParameters);
    }

    @Operation(summary = "Get socks filtered list", description = "Get list of socks, received with or without parameters filter")
    @GetMapping(value = "/api/socks/filter")
    public ResponseEntity<?> readFilter(
            @Parameter(description = "Integer value from 1 to 100, means start of between condition", example = "15")
            @RequestParam(required = false) Integer betweenStart,
            @Parameter(description = "Integer value from 1 to 100, means end of between condition", example = "95")
            @RequestParam(required = false) Integer betweenEnd,
            @Parameter(description = "String value indicating which column to sort the result by", example = "Amount")
            @RequestParam(required = false) String sortBy) {
        try {
            String parameterSortBy = null;
            int[] betweenParameters = new int[2];
            if (sortBy != null && !sortBy.isEmpty()) {
                parameterSortBy = sortBy;
            }
            if (betweenStart != null && betweenEnd != null) {
                if ((betweenStart > 0 && betweenEnd > 0) && (betweenStart <= 100 && betweenEnd <= 100)) {
                    betweenParameters[0] = betweenStart;
                    betweenParameters[1] = betweenEnd;
                }
            } else {
                betweenParameters[0] = -1;
                betweenParameters[1] = -1;
            }
            logger.info("GET request /api/socks/filter passed the execution on");
            return sockService.getSocksFilter(parameterSortBy, betweenParameters);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Необходимые значения не были переданы");
        }
    }

    @Operation(summary = "Get all socks", description = "Get list of all socks")
    @GetMapping(value = "/api/socks/all")
    public ResponseEntity<?> readAll() {
        logger.info("GET request /api/socks/all passed the execution on");
        return ResponseEntity.status(HttpStatus.OK).body(sockService.getAllSocks());
    }

    @Operation(summary = "Put (update) new socks data", description = "Update socks data if it exists")
    @PutMapping(value = "/api/socks/{id}")
    public ResponseEntity<?> update(
            @Parameter(description = "Integer value which is using to find by id Sock in database", example = "2")
            @PathVariable(name = "id") int id, @RequestBody Sock sock) {
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

    @Operation(summary = "Post import socks from .csv file", description = "Receiving file .csv, parsing it and importing Socks objects to Database, lines which not parseable are skipped")
    @PostMapping("/api/socks/batch")
    public ResponseEntity<?> batchRead(
            @Parameter(description = "Multipart value which consist from .csv file (color,cotton,amount)", example = "red,70,150")
            @RequestParam("file") MultipartFile file) {
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

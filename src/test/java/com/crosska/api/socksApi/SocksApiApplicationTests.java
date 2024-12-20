package com.crosska.api.socksApi;

import com.crosska.api.socksApi.controller.SockController;
import com.crosska.api.socksApi.model.Sock;
import com.crosska.api.socksApi.service.SockService;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SockController.class)
class SocksApiControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SockService sockService;
    @Autowired
    private SockController sockController;

    @Test
    @DisplayName("TEST: POST SOCK SUCCESSFULLY")
    public void testAddSock_Success() {
        // Создаем объект Sock с корректными параметрами
        Sock validSock = new Sock();
        validSock.setColor("red");
        validSock.setCotton(50);
        validSock.setAmount(100);

        // Настраиваем поведение sockService
        when(sockService.addSock(Mockito.any(Sock.class))).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        // Вызываем метод контроллера
        ResponseEntity<?> response = sockController.add(validSock);

        // Проверяем, что метод сервиса был вызван один раз с корректным объектом
        Mockito.verify(sockService, Mockito.times(1)).addSock(validSock);

        // Проверяем статус
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("TEST: POST SOCK WITH WRONG COTTON")
    void testAddSock_InvalidCotton() {
        // Создаем объект Sock с некорректным параметром cotton
        Sock invalidSock = new Sock();
        invalidSock.setColor("red");
        invalidSock.setCotton(150); // Некорректное значение (>100)
        invalidSock.setAmount(100);

        // Ожидаем IllegalArgumentException при вызове метода
        Assertions.assertThrows(IllegalArgumentException.class, () -> sockController.add(invalidSock));

        // Проверяем, что метод сервиса не был вызван
        Mockito.verify(sockService, Mockito.never()).addSock(Mockito.any(Sock.class));
    }

    @Test
    @DisplayName("TEST: POST SOCK WITH WRONG AMOUNT")
    void testAddSock_InvalidAmount() {
        // Создаем объект Sock с некорректным параметром amount
        Sock invalidSock = new Sock();
        invalidSock.setColor("blue");
        invalidSock.setCotton(50);
        invalidSock.setAmount(-10); // Некорректное значение (<0)

        // Ожидаем IllegalArgumentException при вызове метода
        Assertions.assertThrows(IllegalArgumentException.class, () -> sockController.add(invalidSock));

        // Проверяем, что метод сервиса не был вызван
        Mockito.verify(sockService, Mockito.never()).addSock(Mockito.any(Sock.class));
    }

    @Test
    @DisplayName("TEST: GET EXISTED ALL SOCKS")
    public void testGetAll_ExistingSocks() throws Exception {
        when(sockService.getAllSocks()).thenReturn(Arrays.asList(new Sock("red", 20, 55),
                new Sock("green", 60, 5),
                new Sock("blue", 100, 169)));

        mockMvc.perform(get("/api/socks/all").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("TEST: GET ALL EMPTY SOCKS")
    public void testGetAll_EmptySocks() throws Exception {
        mockMvc.perform(get("/api/socks/all").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(content().string("[]"));
    }

    @Test
    @DisplayName("TEST: GET WITH PARAMETERS EMPTY")
    public void testGetWithParametersEmptySocks() throws Exception {
        when(sockService.getSocksAmount(new String[]{"red", "moreThan", "80"})).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(get("/api/socks?color=red&comparison=moreThan&cotton=80").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(content().string(""));
    }

    @Test
    @DisplayName("TEST: MULTIPART SUCCESS")
    public void testPostFile() throws Exception {
        // Создаем мок для MultipartFile
        MultipartFile mockFile = Mockito.mock(MultipartFile.class);

        // Пример содержимого CSV
        String csvContent = "red,70,150\nblue,50,200";

        // Настраиваем поведение mockFile
        Mockito.when(mockFile.isEmpty()).thenReturn(false);
        Mockito.when(mockFile.getInputStream()).thenReturn(new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8)));

        // Вызываем метод контроллера
        ResponseEntity<?> response = sockController.batchRead(mockFile);

        // Проверяем, что метод SockService был вызван дважды
        Mockito.verify(sockService, Mockito.times(2)).addSock(Mockito.any(Sock.class));

        // Проверяем статус ответа
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("Sock data updated", response.getBody());
    }

    @Test
    @DisplayName("TEST: POST SOCK SUCCESSFULLY")
    void testBatchRead_WithInvalidLines() throws Exception {
        // Создаем мок для MultipartFile
        MultipartFile mockFile = Mockito.mock(MultipartFile.class);

        // Пример содержимого CSV с ошибочными строками
        String csvContent = "color,cotton,amount\nred,70,150\nblue,notANumber,200";

        // Настраиваем поведение mockFile
        Mockito.when(mockFile.isEmpty()).thenReturn(false);
        Mockito.when(mockFile.getInputStream()).thenReturn(new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8)));

        // Вызываем метод контроллера
        ResponseEntity<?> response = sockController.batchRead(mockFile);

        // Проверяем, что метод SockService был вызван один раз
        Mockito.verify(sockService, Mockito.times(1)).addSock(Mockito.any(Sock.class));

        // Проверяем статус ответа
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("Sock data updated, but invalid line were skipped", response.getBody());
    }

    @Test
    @DisplayName("TEST: MULTIPART EMPTY FILE")
    void testBatchRead_EmptyFile() {
        // Создаем мок для MultipartFile
        MultipartFile mockFile = Mockito.mock(MultipartFile.class);

        // Настраиваем поведение mockFile
        Mockito.when(mockFile.isEmpty()).thenReturn(true);

        // Ожидаем FileUploadException
        Assertions.assertThrows(FileUploadException.class, () -> sockController.batchRead(mockFile));
    }

    @Test
    @DisplayName("TEST: REMOVE SOCK SUCCESS")
    void testRemoveSock_Success() {
        // Создаем объект Sock с корректными параметрами
        Sock validSock = new Sock();
        validSock.setColor("red");
        validSock.setCotton(50);
        validSock.setAmount(100);

        // Настраиваем поведение sockService
        when(sockService.removeSock(Mockito.any(Sock.class))).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        // Вызываем метод контроллера
        ResponseEntity<?> response = sockController.remove(validSock);

        // Проверяем, что метод сервиса был вызван один раз с корректным объектом
        Mockito.verify(sockService, Mockito.times(1)).removeSock(validSock);

        // Проверяем статус
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("TEST: REMOVE NOT ENOUGH SOCKS")
    void testRemoveSock_NotEnoughAmount() {
        // Создаем объект Sock
        Sock validSock = new Sock();
        validSock.setColor("green");
        validSock.setCotton(70);
        validSock.setAmount(50);

        // Настраиваем поведение sockService, чтобы вернуть ошибку
        when(sockService.removeSock(Mockito.any(Sock.class))).thenReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));

        // Вызываем метод контроллера
        ResponseEntity<?> response = sockController.remove(validSock);

        // Проверяем, что метод сервиса был вызван один раз
        Mockito.verify(sockService, Mockito.times(1)).removeSock(validSock);

        // Проверяем статус и тело ответа
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

}

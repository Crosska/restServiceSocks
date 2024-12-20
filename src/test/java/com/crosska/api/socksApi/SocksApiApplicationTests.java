package com.crosska.api.socksApi;

import com.crosska.api.socksApi.controller.SockController;
import com.crosska.api.socksApi.model.Sock;
import com.crosska.api.socksApi.service.SockService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SockController.class)
class SocksApiControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SockService sockService;

    @Test
    @DisplayName("TEST: GET EXISTED ALL SOCKS")
    public void testGetAllExistingSocks() throws Exception {
        when(sockService.getAllSocks()).thenReturn(List.of(
                new Sock("red", 10, 20),
                new Sock("blue", 5, 25)
        ));

        mockMvc.perform(get("/api/socks/all").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].color").value("red"))
                .andExpect(jsonPath("$[0].cotton").value(10))
                .andExpect(jsonPath("$[0].amount").value(20))
                .andExpect(jsonPath("$[1].color").value("blue"))
                .andExpect(jsonPath("$[1].cotton").value(5))
                .andExpect(jsonPath("$[1].amount").value(25));
    }

    @Test
    @DisplayName("TEST: GET ALL EMPTY SOCKS")
    public void testGetAllEmptySocks() throws Exception {
        mockMvc.perform(get("/api/socks/all").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(content().string("[]"));
    }

    /*@Test
    @DisplayName("TEST: GET WITH PARAMETERS EXIST")
    public void testGetWithParametersExistingSocks() throws Exception {
        String[] parameters = {"red", "moreThan", "50"};
        Long result = 25L;
        when(sockService.getSocksAmount(parameters)).thenReturn(ResponseEntity.status(HttpStatus.OK).body(result));


        mockMvc.perform(get("/api/socks?color=red&comparison=moreThan&cotton=50").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("25"));
    }*/

    @Test
    @DisplayName("TEST: GET WITH PARAMETERS EMPTY")
    public void testGetWithParametersEmptySocks() throws Exception {
        when(sockService.getAllSocks()).thenReturn(List.of(
                new Sock("red", 10, 20),
                new Sock("red", 65, 25)
        ));

        mockMvc.perform(get("/api/socks?color=red&comparison=moreThan&cotton=80").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(content().string(""));
    }

    /*@Test
    @DisplayName("TEST: GET WITH MISSING PARAMETER COLOR")
    public void testGetWithMissingParameterColor() throws Exception {
        when(sockService.getAllSocks()).thenReturn(List.of(
                new Sock("red", 10, 20),
                new Sock("blue", 55, 25)
        ));

        mockMvc.perform(get("/api/socks?comparison=moreThan&cotton=5").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(content().string("45"));
    }*/

}

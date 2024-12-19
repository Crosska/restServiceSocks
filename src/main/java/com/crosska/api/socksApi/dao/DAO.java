package com.crosska.api.socksApi.dao;

import com.crosska.api.socksApi.model.Sock;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DAO {

    Sock findById(int id);

    void save(Sock sock);

    void update(Sock sock);

    void delete(Sock sock);

    List<Sock> findAll();

}

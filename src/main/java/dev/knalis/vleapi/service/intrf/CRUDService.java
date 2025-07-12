package dev.knalis.vleapi.service.intrf;


import java.util.List;

public interface CRUDService<E, K> {

    E create(E entity);

    E findById(K id);

    List<E> findAll();

    E update(E entity);

    void delete(K id);

    boolean existsById(K id);

    K getId(E created);
}


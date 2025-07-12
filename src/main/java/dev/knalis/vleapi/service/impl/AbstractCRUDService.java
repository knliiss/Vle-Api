package dev.knalis.vleapi.service.impl;

import dev.knalis.vleapi.service.intrf.CRUDService;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCRUDService<E, K> implements CRUDService<E, K> {

    abstract CrudRepository<E, K> getRepository();

    @Override
    public boolean existsById(K id) {
        return getRepository().findById(id).isPresent();
    }

    @Override
    public void delete(K id) {
        getRepository().deleteById(id);
    }

    @Override
    public E update(E object) {
        return getRepository().save(object);
    }

    @Override
    public List<E> findAll() {
        List<E> objects = new ArrayList<>();
        getRepository().findAll().forEach(objects::add);
        return objects;
    }

    @Override
    public E findById(K id) {
        return getRepository().findById(id).orElse(null);
    }

    @Override
    public E create(E object) {
        return getRepository().save(object);
    }

}

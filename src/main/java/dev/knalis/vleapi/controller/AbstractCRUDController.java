package dev.knalis.vleapi.controller;

import dev.knalis.vleapi.mapper.intrf.ObjectMapper;
import dev.knalis.vleapi.service.intrf.CRUDService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * Абстрактний контролер для CRUD операцій.
 *
 * @param <Entity>        Сутність (Entity)
 * @param <EntityDto>     DTO для виводу
 * @param <CreateRequest> DTO для створення
 * @param <UpdateRequest> DTO для оновлення
 * @param <ID>            Тип ідентифікатора (наприклад Long)
 */
public abstract class AbstractCRUDController<Entity, EntityDto, CreateRequest, UpdateRequest, ID> {

    protected abstract CRUDService<Entity, ID> getService();

    protected abstract ObjectMapper<Entity, EntityDto, CreateRequest, UpdateRequest> getMapper();

    protected abstract String getRestUrl();

    @PostMapping
    public ResponseEntity<EntityDto> create(@Valid @RequestBody CreateRequest request) {
        Entity created = getService().create(getMapper().fromCreateRequest(request));
        EntityDto createdDto = getMapper().toDto(created);
        if (createdDto == null) {
            return ResponseEntity.badRequest().build();
        }
        ID id = getService().getId(created);
        URI location = URI.create(String.format("/%s", id));

        return ResponseEntity.created(location).body(createdDto);
    }


    @PutMapping("/{id}")
    public ResponseEntity<EntityDto> update(@PathVariable ID id, @Valid @RequestBody UpdateRequest request) {
        Entity entity = getService().findById(id);
        if (entity == null) {
            return ResponseEntity.notFound().build();
        }

        getMapper().updateEntity(entity, request);
        Entity updatedEntity = getService().update(entity);

        EntityDto updatedDto = getMapper().toDto(updatedEntity);
        return updatedDto != null ? ResponseEntity.ok(updatedDto) : ResponseEntity.notFound().build();
    }


    @GetMapping("/{id}")
    public ResponseEntity<EntityDto> findById(@PathVariable ID id) {
        Entity entity = getService().findById(id);
        if (entity == null) {
            return ResponseEntity.notFound().build();
        }
        EntityDto dto = getMapper().toDto(entity);
        return ResponseEntity.ok(dto);
    }


    @GetMapping
    public ResponseEntity<List<EntityDto>> findAll() {
        List<Entity> entities = getService().findAll();
        List<EntityDto> dtos = entities.stream().map(getMapper()::toDto).toList();
        return ResponseEntity.ok(dtos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable ID id) {
        if (getService().existsById(id)) {
            getService().delete(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
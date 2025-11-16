package dev.knalis.vleapi.controller;

import dev.knalis.vleapi.mapper.intrf.ObjectMapper;
import dev.knalis.vleapi.service.intrf.CRUDService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;

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
    @Operation(summary = "Create resource", description = "Creates a new resource for this endpoint")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Resource created" , content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    public ResponseEntity<EntityDto> create(@Valid @RequestBody CreateRequest request) {
        Entity created = getService().create(getMapper().fromCreateRequest(request));
        EntityDto createdDto = getMapper().toDto(created);
        if (createdDto == null) {
            return ResponseEntity.badRequest().body(null);
        }
        ID id = getService().getId(created);
        URI location = URI.create(String.format("/%s", id));
        return ResponseEntity.created(location).body(createdDto);
    }


    @PutMapping("/{id}")
    @Operation(summary = "Update resource", description = "Updates an existing resource by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resource updated", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Resource not found")
    })
    public ResponseEntity<EntityDto> update(@PathVariable ID id, @Valid @RequestBody UpdateRequest request) {
        Entity entity = getService().findById(id);
        if (entity == null) {
            return ResponseEntity.status(404).body(null);
        }
        getMapper().updateEntity(entity, request);
        Entity updatedEntity = getService().update(entity);
        EntityDto updatedDto = getMapper().toDto(updatedEntity);
        return updatedDto != null ? ResponseEntity.ok(updatedDto) : ResponseEntity.badRequest().body(null);
    }


    @PatchMapping("/{id}")
    @Operation(summary = "Partially update resource", description = "Updates only provided fields (PATCH)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resource updated", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Resource not found")
    })
    public ResponseEntity<EntityDto> patch(@PathVariable ID id, @RequestBody UpdateRequest request) {
        Entity entity = getService().findById(id);
        if (entity == null) {
            return ResponseEntity.status(404).body(null);
        }
        getMapper().updateEntity(entity, request);
        Entity updatedEntity = getService().update(entity);
        EntityDto updatedDto = getMapper().toDto(updatedEntity);
        return updatedDto != null ? ResponseEntity.ok(updatedDto) : ResponseEntity.badRequest().body(null);
    }


    @GetMapping("/{id}")
    @Operation(summary = "Get resource by id", description = "Returns the resource for the given id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resource found", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Resource not found")
    })
    public ResponseEntity<EntityDto> findById(@PathVariable ID id) {
        Entity entity = getService().findById(id);
        if (entity == null) {
            return ResponseEntity.status(404).body(null);
        }
        EntityDto dto = getMapper().toDto(entity);
        return ResponseEntity.ok(dto);
    }


    @GetMapping
    @Operation(summary = "List resources", description = "Returns a list of resources for this endpoint")
    @ApiResponse(responseCode = "200", description = "List of resources returned", content = @Content(mediaType = "application/json"))
    public ResponseEntity<List<EntityDto>> findAll() {
        List<Entity> entities = getService().findAll();
        List<EntityDto> dtos = entities.stream().map(getMapper()::toDto).toList();
        return ResponseEntity.ok(dtos);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete resource", description = "Deletes resource by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Resource deleted"),
            @ApiResponse(responseCode = "404", description = "Resource not found")
    })
    public ResponseEntity<Void> delete(@PathVariable ID id) {
        if (getService().existsById(id)) {
            getService().delete(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(404).build();
        }
    }
}
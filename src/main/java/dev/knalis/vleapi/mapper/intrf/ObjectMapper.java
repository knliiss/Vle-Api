package dev.knalis.vleapi.mapper.intrf;


public interface ObjectMapper<Entity, Dto, CreateRequest, UpdateRequest> {

    Dto toDto(Entity entity);

    Entity fromCreateRequest(CreateRequest dto);
    void updateEntity(Entity entity, UpdateRequest updateRequest);

}

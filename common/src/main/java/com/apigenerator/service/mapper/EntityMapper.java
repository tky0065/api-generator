
package com.apigenerator.service.mapper;

import java.util.List;

/**
 * Interface générique pour les mappers d'entités.
 *
 * @param <D> - Type du DTO
 * @param <E> - Type de l'entité
 */
public interface EntityMapper<D, E> {

    E toEntity(D dto);

    D toDto(E entity);

    List<E> toEntity(List<D> dtoList);

    List<D> toDto(List<E> entityList);

    void update(E entity, E updateEntity);
}
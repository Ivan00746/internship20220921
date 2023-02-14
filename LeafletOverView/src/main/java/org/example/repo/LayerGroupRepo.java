package org.example.repo;

import org.example.entities.layers.LayerGroup;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LayerGroupRepo extends CrudRepository<LayerGroup, Long> {
    LayerGroup findByName(String name);
    Boolean existsByName (String name);
}

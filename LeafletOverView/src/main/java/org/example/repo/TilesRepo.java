package org.example.repo;

import org.example.entities.Tile;
import org.example.entities.TileCompKey;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TilesRepo extends CrudRepository<Tile, TileCompKey> {
}

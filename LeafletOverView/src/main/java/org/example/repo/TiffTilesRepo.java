package org.example.repo;

import org.example.entities.TiffTile;
import org.example.entities.TileCompKey;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TiffTilesRepo extends CrudRepository<TiffTile, TileCompKey> {
}

package org.example.services;

import org.example.entities.Tile;
import org.example.entities.TileCompKey;
import org.example.repo.TilesRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Calendar;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class TileService {
    private final RestTemplate restTemplate;
    private final TilesRepo tilesRepo;
    private final String tilesServerUrl;

    @Autowired
    public TileService(@Value("${application.OpenStreetMap.server.url}") String tilesServerUrl,
                       @Qualifier("rtOpenStreetMap") RestTemplate restTemplate,
                       TilesRepo tilesRepo) {
        this.restTemplate = restTemplate;
        this.tilesRepo = tilesRepo;
        this.tilesServerUrl = tilesServerUrl;
    }

    private static final Logger log = Logger.getLogger(TileService.class.getName());

    public byte[] getRemoteTile(int z, int x, int y) {
        byte[] tileByteArray = null;
        Optional<Tile> optionalTile = tilesRepo.findById(new TileCompKey(z, x, y));
        if (optionalTile.isEmpty()) {
            log.info("---Tile " + z + "/" + x + "/" + y + " isn't founded in local DB.");
            String url = tilesServerUrl + "/" + z + "/" + x + "/" + y + ".png";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.add("cache-control", "max-age");
            headers.add("User-Agent", "LeafletOverView/1.0");
            log.info("-->Get (getForObject) tile request to " + url);
            try {
                HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
                ResponseEntity<byte[]> response = restTemplate
                        .exchange(url, HttpMethod.GET, requestEntity, byte[].class);
                tileByteArray = response.getBody();
                if (tileByteArray != null) {
                    log.info("<--Response (" + response.getHeaders().getContentType() + ") from link: "
                            + url + ". Byte array length: " + tileByteArray.length);
                    Tile tileTemp = new Tile(z, x, y, tileByteArray,
                            tilesServerUrl, Calendar.getInstance().getTime().toString());
                    tilesRepo.save(tileTemp);
                    log.info("==>Tile " + z + "/" + x + "/" + y + " sent and saved in DB.");
                }
            } catch (RestClientException e) {
                log.warning(e.toString());
            }
        } else {
            log.info("==>Tile " + z + "/" + x + "/" + y + " is retrieved from local DB and sent.");
            tileByteArray = optionalTile.get().getTileByteArray();
        }
        return tileByteArray;
    }
}

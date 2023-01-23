package org.example.controllers;

import org.example.entities.TiffMapState;
import org.example.services.TiffTileService;
import org.example.services.TileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

@RestController
public class ControllerREST {
    private final Boolean geoTiffMapSupported;
    private final TileService tileService;
    private final TiffTileService tiffTileService;
    @Autowired
    public ControllerREST (@Value("${application.geofile.provided}") Boolean geoTiffMapSupported,
                           TileService tileService, TiffTileService tiffTileService) {
        this.geoTiffMapSupported = geoTiffMapSupported;
        this.tileService = tileService;
        this.tiffTileService = tiffTileService;
    }
    private static final Logger log = Logger.getLogger(TileService.class.getName());

    @GetMapping(path = "getTile/{z}/{x}/{y}.png", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] getTile(@PathVariable("z") String z,
                          @PathVariable("x") String x,
                          @PathVariable("y") String y) {
        log.info("<==Frontend request for the tile /" + z + "/" + x + "/" + y + ".png");
        int intZ = Integer.parseInt(z);
        int intX = Integer.parseInt(x);
        int intY = Integer.parseInt(y);
        return tileService.getRemoteTile(intZ, intX, intY);
    }

    @GetMapping(path = "getTiffTile/{z}/{x}/{y}.png", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] getTiffTile (@PathVariable("z") String z,
                              @PathVariable("x") String x,
                              @PathVariable("y") String y) {
        log.info("<==Frontend request for the tiffTile /" + z + "/" + x + "/" + y + ".png");
        if (!geoTiffMapSupported) {
            log.warning("Tiff tile service not supported according to the settings.");
            return null;
        }
        int intZ = Integer.parseInt(z);
        int intX = Integer.parseInt(x);
        int intY = Integer.parseInt(y);
        return tiffTileService.getTiffTile(intZ, intX, intY);
    }

    @GetMapping(path = "getResampledImg", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] getResampledImg() {
        log.info("<==Frontend request for saving source geo data with CRS-EPSG:3857 to the disk and brows a low resolution view.");
        return tiffTileService.writeSourceFile();
    }

    @GetMapping(path="getTiffMapState")
    public TiffMapState getTiffMapState() {
        log.info("<==>Frontend request for tiff map appearance state.");
        return new TiffMapState(geoTiffMapSupported);
    }
}
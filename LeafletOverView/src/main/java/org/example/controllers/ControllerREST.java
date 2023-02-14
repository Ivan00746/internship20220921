package org.example.controllers;

import org.example.entities.TiffMapState;
import org.example.entities.layers.LayerGroup;
import org.example.services.LayerGroupService;
import org.example.services.TiffTileService;
import org.example.services.TileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.logging.Logger;

@RestController
public class ControllerREST {
    private final Boolean geoTiffMapSupported;
    private final TileService tileService;
    private final TiffTileService tiffTileService;
    private final LayerGroupService layerGroupService;

    @Autowired
    public ControllerREST (@Value("${application.geofile.provided}") Boolean geoTiffMapSupported,
                           TileService tileService, TiffTileService tiffTileService, LayerGroupService layerGroupService) {
        this.geoTiffMapSupported = geoTiffMapSupported;
        this.tileService = tileService;
        this.tiffTileService = tiffTileService;
        this.layerGroupService = layerGroupService;
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

    @GetMapping(path = "getLayerGroups", produces = MediaType.APPLICATION_JSON_VALUE)
    public ArrayList<String> getLayerGroups() {
        return layerGroupService.getGroupsNames();
    }

    @PostMapping(path = "saveLayerGroup", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String saveLayerGroup(@RequestBody LayerGroup layerGroup) {
        log.info("<---Layer group SAVE request, 'layer group':" + layerGroup.toString());
        return layerGroupService.saveLayerGroup(layerGroup);
    }

    @PostMapping(path = "loadLayerGroup", consumes = MediaType.TEXT_HTML_VALUE)
    public LayerGroup loadLayerGroup(@RequestBody String groupName) {
        log.info("<---Layer group DOWNLOAD request, 'layer group name':" + groupName);
        return layerGroupService.findLayerGroup(groupName);
    }

    @PostMapping(path = "deleteLayerGroup", consumes = MediaType.TEXT_HTML_VALUE)
    public String deleteLayerGroup(@RequestBody String groupName) {
        log.info("<---Layer group DELETE request, 'layer group name':" + groupName);
        return layerGroupService.deleteLayerGroup(groupName);
    }
}
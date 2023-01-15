package org.example.controllers;

import org.example.services.TileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

@RestController
public class ControllerREST {

    @Autowired
    private TileService tileService;

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
}

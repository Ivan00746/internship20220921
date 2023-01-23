package org.example.components;

import org.example.services.TileService;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridCoverageFactory;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.coverage.grid.io.GridCoverage2DReader;
import org.geotools.coverage.grid.io.GridFormatFinder;
import org.geotools.coverage.processing.Operations;
import org.geotools.gce.geotiff.GeoTiffFormat;
import org.geotools.geometry.Envelope2D;
import org.geotools.referencing.CRS;
import org.geotools.util.factory.Hints;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.media.jai.Interpolation;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

@Component
public class CoverageSource {
    @Value("${application.geofile.source.path}")
    private String sourceFilePath;
    @Value("${application.geofile.data.x.correction}")
    private double xCorrection;
    @Value("${application.geofile.data.y.correction}")
    private double yCorrection;
    private static final Logger log = Logger.getLogger(TileService.class.getName());
    long timePoint1, timePoint2;

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
//    @SessionScope
    public GridCoverage2D coverageEPSG3857Create() {
        File sourceFile = new File(sourceFilePath);
        CoordinateReferenceSystem crsEPSG4326, crsEPSG3857, originalCRS;
        RenderedImage tiffImage;
        try {
            crsEPSG4326 = CRS.decode("EPSG:4326", true);
            crsEPSG3857 = CRS.decode("EPSG:3857", true);
        } catch (Exception e) {
            log.severe("CRS instances creation failed for internal reasons.");
            return null;
        }

        AbstractGridFormat format = GridFormatFinder.findFormat(sourceFile);
        if (format instanceof GeoTiffFormat) {
            log.info("---GeoTiff file" + sourceFile.getName() + " detected. Format name: "
                    + format.getName() + ", version: " + format.getVersion()
                    + ". Description: " + format.getDescription() + ".");
            timePoint1 = System.nanoTime();
            Hints hints = new Hints(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE);
            GridCoverage2DReader gridReader = format.getReader(sourceFile, hints);
            try {
                GridCoverage2D gcFullWorld = gridReader.read(null);
                tiffImage = gcFullWorld.getRenderedImage();
                originalCRS = gcFullWorld.getCoordinateReferenceSystem2D();
            } catch (IOException e) {
                log.severe("---Error occurs during reading geoTiff file " + sourceFile.getName());
                log.warning("---Class Path: " + new File(".").getAbsolutePath());
                log.warning("---File path: " + sourceFile.getAbsolutePath() + "File exists: " + sourceFile.exists());
                log.warning("---Format name: " + format.getName() + "Format version: " + format.getVersion());
                return null;
            }
            timePoint2 = System.nanoTime();
            log.info("---GeoTiff file" + sourceFile.getName() + " with internal metadata loaded in "
                    + (timePoint2 - timePoint1) / 1e6 + "ms.");
        } else {
            timePoint1 = System.nanoTime();
            try {
                tiffImage = ImageIO.read(sourceFile);
                originalCRS = crsEPSG4326;
            } catch (IOException e) {
                log.severe("---Error occurs during reading geo data file " + sourceFile.getName());
                log.warning("---Class Path: " + new File(".").getAbsolutePath());
                log.warning("---File path: " + sourceFile.getAbsolutePath());
                log.warning("---File exists: " + sourceFile.exists());
                return null;
            }
            timePoint2 = System.nanoTime();
            log.info("---Geo file" + sourceFile.getName() + " loaded. Downloading time: "
                    + (timePoint2 - timePoint1) / 1e6 + "ms.");
        }

        Envelope2D woldEnvelope = new Envelope2D(originalCRS, -180, -90, 360, 180);
        GridCoverage2D gcFullWorld = new GridCoverageFactory().create("world", tiffImage, woldEnvelope);

        if (xCorrection > 1.0 || xCorrection < -1.0) xCorrection = 0;
        if (yCorrection > 1.0 || yCorrection < -1.0) yCorrection = 0;
        timePoint1 = System.nanoTime();
        Envelope2D cropEnvelope = new Envelope2D(originalCRS,
                -180 - xCorrection, -85 - yCorrection, 360, 170 + yCorrection * 2);
        GridCoverage2D gcCropWorld = (GridCoverage2D) Operations.DEFAULT.crop(gcFullWorld, cropEnvelope);
        timePoint2 = System.nanoTime();
        log.info("---GeoTiff grid coverage data has been cropped to the acceptable size (-180-"
                + xCorrection + ", -85-" + yCorrection + ", 360, 170+2*" + yCorrection
                + "). Cropping time: " + (timePoint2 - timePoint1) / 1e6 + "ms.");

        timePoint1 = System.nanoTime();
        Hints.putSystemDefault(Hints.RESAMPLE_TOLERANCE, 0d);
        GridCoverage2D coverageEPSG3857 =
                (GridCoverage2D) Operations.DEFAULT.resample(
                        gcCropWorld,
                        crsEPSG3857,
                        null,
                        Interpolation.getInstance(Interpolation.INTERP_BILINEAR));
        timePoint2 = System.nanoTime();
        log.info("---GeoTiff grid coverage data with CRS-(" + originalCRS.getName()
                + ") has been rebuilt to coverage with CRS-(" + crsEPSG3857.getName()
                + "). Time of rebuilt: " + (timePoint2 - timePoint1) / 1e6 + "ms.");

//            BufferedImage coverageEPSG3857BI = ImageIO.read(coverageEPSG3857File);
//            Envelope2D woldEnvelope = new Envelope2D(crsEPSG3857);
//            coverageEPSG3857 = new GridCoverageFactory().create("world", coverageEPSG3857BI, woldEnvelope);

        return coverageEPSG3857;
    }
}

package org.example.services;

import org.example.entities.TiffTile;
import org.example.entities.Tile;
import org.example.entities.TileCompKey;
import org.example.repo.TiffTilesRepo;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.processing.CoverageProcessor;
import org.geotools.coverage.processing.Operations;
import org.geotools.geometry.Envelope2D;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.util.factory.Hints;
import org.imgscalr.Scalr;
import org.opengis.coverage.processing.Operation;
import org.opengis.geometry.Envelope;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import javax.imageio.ImageIO;
import javax.media.jai.Interpolation;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderContext;
import java.awt.image.renderable.RenderableImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class TiffTileService {
    private final String outPutFolderPath;
    private final String coverageEPSG3857FileName;
    private final GridCoverage2D coverageEPSG3857;
    private final TiffTilesRepo tiffTilesRepo;
    long timePoint1, timePoint2;

    @Autowired
    public TiffTileService(@Value("${application.output.folder.path}") String outPutFolderPath,
                           @Value("${application.output.geofileEPSG3857.name}") String coverageEPSG3857FileName,
                           @Qualifier("coverageEPSG3857Create") GridCoverage2D coverageEPSG3857,
                           TiffTilesRepo tiffTilesRepo) {
        this.outPutFolderPath = outPutFolderPath;
        this.coverageEPSG3857FileName = coverageEPSG3857FileName;
        this.coverageEPSG3857 = coverageEPSG3857;
        this.tiffTilesRepo = tiffTilesRepo;
    }

    private static final Logger log = Logger.getLogger(TileService.class.getName());

    public byte[] getTiffTile(int z, int x, int y) {
        byte[] tiffTileByteArray = null;
        Optional<TiffTile> optionalTile = tiffTilesRepo.findById(new TileCompKey(z, x, y));
        if (optionalTile.isEmpty()) {
            log.info("---Started creating of TiffTile " + z + "/" + x + "/" + y + ".");

            Envelope2D coverageEnvelope = coverageEPSG3857.getEnvelope2D();
            double coverageMinX = coverageEnvelope.getBounds().getMinX();
            double coverageMaxX = coverageEnvelope.getBounds().getMaxX();
            double coverageMinY = coverageEnvelope.getBounds().getMinY();
            double coverageMaxY = coverageEnvelope.getBounds().getMaxY();
            int htn = (int) Math.pow(2d, z);
            double geographicTileWidth = (coverageMaxX - coverageMinX) / htn;
            double geographicTileHeight = (coverageMaxY - coverageMinY) / htn;

            CoordinateReferenceSystem targetCRS = coverageEPSG3857.getCoordinateReferenceSystem2D();
            timePoint1 = System.nanoTime();
            Envelope envelope = getTileEnvelope(coverageMinX, coverageMinY,
                    geographicTileWidth, geographicTileHeight,
                    targetCRS, x, htn - 1 - y);
            GridCoverage2D finalCoverage = cropCoverage(coverageEPSG3857, envelope);
            timePoint2 = System.nanoTime();
            log.info("---TiffTile " + z + "/" + x + "/" + y + " has been cut in "
                    + (timePoint2 - timePoint1) / 1e6 + "ms.");

            timePoint1 = System.nanoTime();
            BufferedImage originalImage = PlanarImage
                    .wrapRenderedImage(finalCoverage.getRenderedImage()).getAsBufferedImage();
            BufferedImage tiffTile = Scalr.resize(originalImage, Scalr.Method.AUTOMATIC,
                    Scalr.Mode.FIT_EXACT, 256, 256, Scalr.OP_ANTIALIAS);
            timePoint2 = System.nanoTime();
            log.info("==>TiffTile " + z + "/" + x + "/" + y
                    + " has been rebuilt to size 256px x 256px and sent in " + (timePoint2 - timePoint1) / 1e6 + "ms.");

//        Operations ops = new Operations(null);
//        finalCoverage = (GridCoverage2D) ops.scale(finalCoverage, 1, 1, 0, 0);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try {
                ImageIO.write(tiffTile, "png", bos);
            } catch (IOException e) {
                log.severe("IO Error of transformation tile " + z + "/" + x + "/" + y + "to byte array.");
            }
            tiffTileByteArray = bos.toByteArray();
            TiffTile tileTemp = new TiffTile(z, x, y, tiffTileByteArray,
                    "localHost", Calendar.getInstance().getTime().toString());
            tiffTilesRepo.save(tileTemp);
        } else {
            log.info("==>TiffTile " + z + "/" + x + "/" + y + " is retrieved from local DB and sent.");
            tiffTileByteArray = optionalTile.get().getTileByteArray();
        }
        return tiffTileByteArray;
    }

    private Envelope getTileEnvelope(
            double coverageMinX,
            double coverageMinY,
            double geographicTileWidth,
            double geographicTileHeight,
            CoordinateReferenceSystem targetCRS,
            int horizontalIndex,
            int verticalIndex) {

        double envelopeStartX = (horizontalIndex * geographicTileWidth) + coverageMinX;
        double envelopeEndX = envelopeStartX + geographicTileWidth;
        double envelopeStartY = (verticalIndex * geographicTileHeight) + coverageMinY;
        double envelopeEndY = envelopeStartY + geographicTileHeight;

        return new ReferencedEnvelope(
                envelopeStartX, envelopeEndX, envelopeStartY, envelopeEndY, targetCRS);
    }

    private GridCoverage2D cropCoverage(GridCoverage2D gridCoverage, Envelope envelope) {
        CoverageProcessor processor = CoverageProcessor.getInstance();
        Collection<Operation> operations = processor.getOperations();
        ParameterValueGroup param = processor.getOperation("CoverageCrop").getParameters();
        param.parameter("Source").setValue(gridCoverage);
        param.parameter("Envelope").setValue(envelope);
        return (GridCoverage2D) processor.doOperation(param);
    }

    public byte[] writeSourceFile() {
        File coverageEPSG3857File = new File(outPutFolderPath + coverageEPSG3857FileName);
        log.info("---Start preparing geodata for writing to disk...");
        timePoint1 = System.nanoTime();
        BufferedImage sourceCoverageBI = PlanarImage.wrapRenderedImage(coverageEPSG3857.getRenderedImage()).getAsBufferedImage();
        timePoint2 = System.nanoTime();
        log.info("---Geo data have prepared to writing in " + (timePoint2 - timePoint1) / 1e6 + "ms.");

        timePoint1 = System.nanoTime();
        try {
            ImageIO.write(sourceCoverageBI, "png", coverageEPSG3857File);
        } catch (IOException e) {
            log.severe("---Error occurs during recording geo data file " + coverageEPSG3857File.getName());
            log.warning("---File path: " + coverageEPSG3857File.getAbsolutePath());
            log.warning("---File exists: " + coverageEPSG3857File.exists());
        }
        long timePoint2 = System.nanoTime();
        log.info("---Geo data with CRS-(" + coverageEPSG3857.getCoordinateReferenceSystem2D().getName()
                + ") has been written to the file " + coverageEPSG3857File.getName()
                + ". Recording time: " + (timePoint2 - timePoint1) / 1e6 + "ms.");

        timePoint1 = System.nanoTime();
        BufferedImage resizedImage = Scalr.resize(sourceCoverageBI, Scalr.Method.AUTOMATIC,
                Scalr.Mode.FIT_TO_WIDTH, 1200, 0, Scalr.OP_ANTIALIAS);
        timePoint2 = System.nanoTime();
        log.info("---Small view of the geo data with CRS-(" + coverageEPSG3857.getCoordinateReferenceSystem2D().getName()
                + ") created. Creation time: " + (timePoint2 - timePoint1) / 1e6 + "ms.");

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ImageIO.write(resizedImage, "png", bos);
        } catch (IOException e) {
            log.severe("---Geo data view transformation to byte array failed for internal reasons.");
        }
        return bos.toByteArray();
    }
}

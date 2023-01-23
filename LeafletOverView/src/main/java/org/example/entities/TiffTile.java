package org.example.entities;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tifftiles")
@IdClass(TileCompKey.class)
public class TiffTile {
    @Id
    @Column(name = "z")
    private int z;
    @Id
    @Column(name = "x")
    private int x;
    @Id
    @Column(name = "y")
    private int y;

    @Column(name = "tilebytearray")
    private byte[] tileByteArray;

    @Column(name = "source", length = 50)
    private String source;

    @Column(name = "addingtime", length = 50)
    private String addingTime;

    public TiffTile() {
    }

    public TiffTile(int z, int x, int y, byte[] tileByteArray,
                    String source, String addingTime) {
        this.z = z;
        this.x = x;
        this.y = y;
        this.tileByteArray = tileByteArray;
        this.source = source;
        this.addingTime = addingTime;
    }

    @Override
    public String toString() {
        return "TiffTile{" +
                "zoom=" + z +
                ", x=" + x +
                ", y=" + y +
                ", image_size=" + tileByteArray.length +
                "bytes, source='" + source + '\'' +
                ", addingTime='" + addingTime + '\'' +
                '}';
    }
}

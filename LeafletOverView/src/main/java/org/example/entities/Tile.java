package org.example.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.Arrays;

@Data
@Entity
@Table(name = "tiles")
@IdClass(TileCompKey.class)
public class Tile {
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

    public Tile() {
    }

    public Tile(int z, int x, int y, byte[] tileByteArray,
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
        return "Tile{" +
                "zoom=" + z +
                ", x=" + x +
                ", y=" + y +
                ", image_size=" + tileByteArray.length +
                "bytes, source='" + source + '\'' +
                ", addingTime='" + addingTime + '\'' +
                '}';
    }
}

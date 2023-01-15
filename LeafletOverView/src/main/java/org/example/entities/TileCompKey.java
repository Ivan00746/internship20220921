package org.example.entities;

import java.io.Serializable;

public class TileCompKey implements Serializable {
        private int z;
        private int x;
        private int y;

        public TileCompKey () {}

        public TileCompKey (int z, int x, int y) {
            this.z = z;
            this.x = x;
            this.y = y;
        }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TileCompKey)) return false;
        TileCompKey that = (TileCompKey) o;
        if (z != that.z) return false;
        if (x != that.x) return false;
        return y == that.y;
    }

    @Override
    public int hashCode() {
        int result = z;
        result = 31 * result + x;
        result = 31 * result + y;
        return result;
    }
}

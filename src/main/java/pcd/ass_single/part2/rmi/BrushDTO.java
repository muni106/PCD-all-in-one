package pcd.ass_single.part2.rmi;

import java.io.Serializable;

public class BrushDTO implements Serializable {
    private final Integer peerId;
    private final int x;
    private final int y;
    private final int color;


    public BrushDTO(Integer peerId, int x, int y, int color) {
        this.peerId = peerId;
        this.x = x;
        this.y = y;
        this.color = color;
    }

    public Integer getPeerId() {
        return peerId;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getColor() {
        return color;
    }
}

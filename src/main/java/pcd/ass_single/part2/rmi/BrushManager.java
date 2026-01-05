package pcd.ass_single.part2.rmi;


import java.awt.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BrushManager implements Serializable {
    private static final int BRUSH_SIZE = 10;
    private static final int STROKE_SIZE = 2;
    private Map<Integer, Brush> brushes = new HashMap<>();

    void draw(final Graphics2D g) {
        brushes.forEach((id, brush) -> {
            g.setColor(new Color(brush.color));
            var circle = new java.awt.geom.Ellipse2D.Double(brush.x - BRUSH_SIZE / 2.0, brush.y - BRUSH_SIZE / 2.0, BRUSH_SIZE, BRUSH_SIZE);
            // draw the polygon
            g.fill(circle);
            g.setStroke(new BasicStroke(STROKE_SIZE));
            g.setColor(Color.BLACK);
            g.draw(circle);
        });
    }

    public void addBrush(final Integer id, final Brush brush) {
        brushes.put(id, brush);
    }

    public Boolean containsBrush(final Integer id) {
        return brushes.containsKey(id);
    }

    public void updateBrushPosition(final Integer id, int x, int y) {
        brushes.get(id).updatePosition(x, y);
    }

    void removeBrush(final Integer id) {
        brushes.remove(id);
    }

    public static class Brush {
        private int x, y;
        private int color;

        public Brush(final int x, final int y, final int color) {
            this.x = x;
            this.y = y;
            this.color = color;
        }

        public void updatePosition(final int x, final int y) {
            this.x = x;
            this.y = y;
        }
        // write after this getter and setters
        public int getX(){
            return this.x;
        }
        public int getY(){
            return this.y;
        }
        public int getColor(){
            return this.color;
        }
        public void setColor(int color){
            this.color = color;
        }
    }
}

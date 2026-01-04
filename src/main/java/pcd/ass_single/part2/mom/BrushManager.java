package pcd.ass_single.part2.mom;


import java.awt.*;
import java.util.List;
import java.util.Map;

public class BrushManager {
    private static final int BRUSH_SIZE = 10;
    private static final int STROKE_SIZE = 2;
    private Map<String, Brush> brushes = new java.util.HashMap<>();

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

    void addBrush(final String id, final Brush brush) {
        System.out.println("add " + id);
        brushes.put(id, brush);
    }

    Boolean hasBrush(final String id) {
        return brushes.containsKey(id);
    }

    void updateBrushPosition(final String id, int x, int y) {
        brushes.get(id).updatePosition(x, y);
    }

    void printBrushes() {
        brushes.forEach((id, brush) -> {
            System.out.println(id);
        });
    }

    Brush getBrush(final String id) {
        return brushes.get(id);
    }


    void removeBrush(final String id) {
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

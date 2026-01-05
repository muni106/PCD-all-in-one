package pcd.ass_single.part2.rmi;

public interface BrushEventListener {
    void onBrushAdded(Integer id, int x, int y, int color);
    void onBrushMoved(Integer id, int x, int y, int color);
    void onBrushColorChanged(Integer id, int color);
    void onBrushRemoved(Integer id);
}

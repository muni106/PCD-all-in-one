package pcd.ass_single.part2.rmi;

public interface RemoteEventListener {
    void onBrushAdded(BrushDTO brushDTO);
    void onBrushMoved(BrushDTO brushDTO);
    void onBrushColorChanged(BrushDTO brushDTO);
    void onPixelDrawn(BrushDTO brushDTO);
    void onBrushRemoved(Integer id);
}

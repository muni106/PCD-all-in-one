package pcd.ass_single.part2.rmi;

import pcd.ass_single.part2.rmi.remote_components.RemoteServiceListener;

import java.util.Map;

public interface RemoteEventListener {
    void onBrushAdded(BrushDTO brushDTO);
    void onBrushMoved(BrushDTO brushDTO);
    void onBrushColorChanged(BrushDTO brushDTO);
    void onPixelDrawn(BrushDTO brushDTO);
    void onBrushRemoved(Integer id);
    void onNextLeaderElection(Integer leaderId, Map<Integer, RemoteServiceListener> listenersMap);
}

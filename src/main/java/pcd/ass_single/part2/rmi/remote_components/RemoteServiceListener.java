package pcd.ass_single.part2.rmi.remote_components;

import pcd.ass_single.part2.rmi.BrushDTO;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface RemoteServiceListener extends Remote {
    void notifyBrushAdded(BrushDTO brushDTO) throws RemoteException;
    void notifyBrushColorChanged(BrushDTO brushDTO) throws RemoteException;
    void notifyBrushMoved(BrushDTO brushDTO) throws RemoteException;
    void notifyPixelDrawn(BrushDTO brushDTO) throws RemoteException;
    void notifyBrushRemoved(Integer peerId) throws RemoteException;
    void notifyNextLeader(Integer leaderId, Map<Integer, RemoteServiceListener> listenersMap) throws RemoteException;
}

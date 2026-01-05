package pcd.ass_single.part2.rmi.remote_components;

import pcd.ass_single.part2.rmi.BrushEventListener;
import pcd.ass_single.part2.rmi.BrushManager;

import java.awt.*;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteServiceListener extends Remote {
    void notifyBrushAdded(Integer id, int x, int y, int color) throws RemoteException;
    void notifyBrushColorChanged(Integer id, int color) throws RemoteException;
    void notifyBrushMoved(Integer id, int x, int y, int color) throws RemoteException;
    void notifyPixelDrawn(int x, int y, int color) throws RemoteException;
    void notifyBrushRemoved(Integer id) throws RemoteException;
}

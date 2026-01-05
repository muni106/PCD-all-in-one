package pcd.ass_single.part2.rmi.remote_components;

import pcd.ass_single.part2.rmi.BrushManager;

import java.awt.*;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteServiceListener extends Remote {
    void addBrush(Integer id, int x, int y, int color) throws RemoteException;
    void changeBrushColor(Integer id, int color) throws RemoteException;
    void updateBrushPos(Integer id, int x, int y, int color) throws RemoteException;
    void draw(int x, int y, int color) throws RemoteException;
    void leave(Integer id) throws RemoteException;
}

package pcd.ass_single.part2.rmi.remote_components;

import pcd.ass_single.part2.rmi.PixelGrid;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteService extends Remote {
    Integer addPeer(RemoteServiceListener rsl, int x, int y, int color) throws RemoteException;
    PixelGrid getGrid() throws RemoteException;
    void updatePeers(Integer id, int x, int y, int color) throws RemoteException;
}


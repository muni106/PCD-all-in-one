package pcd.ass_single.part2.rmi.remote_components;

import pcd.ass_single.part2.rmi.PixelGrid;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteService extends Remote {
    void addListener(RemoteServiceListener rsl) throws RemoteException;
    PixelGrid getGrid() throws RemoteException;
}


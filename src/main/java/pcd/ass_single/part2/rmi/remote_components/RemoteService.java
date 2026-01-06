package pcd.ass_single.part2.rmi.remote_components;

import pcd.ass_single.part2.rmi.PixelGrid;
import pcd.ass_single.part2.rmi.RemoteEvent;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteService extends Remote {
    void handleEvent(RemoteEvent event) throws RemoteException;
    Integer join(RemoteServiceListener rsl) throws RemoteException;

    void setGrid(PixelGrid grid) throws RemoteException;

    PixelGrid getGrid() throws RemoteException;
}


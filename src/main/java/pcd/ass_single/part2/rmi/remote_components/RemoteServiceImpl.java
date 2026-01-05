package pcd.ass_single.part2.rmi.remote_components;

import pcd.ass_single.part2.rmi.PixelGrid;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class RemoteServiceImpl implements RemoteService{

    private List<RemoteServiceListener> listeners;
    private PixelGrid grid;

    public RemoteServiceImpl(PixelGrid newGrid) {
        grid = newGrid;
        listeners = new ArrayList<>();
    }

    @Override
    public void addListener(RemoteServiceListener rsl) throws RemoteException {
        listeners.add(rsl);
        log("Added Listener");

    }

    @Override
    public PixelGrid getGrid() throws RemoteException {
        return grid;
    }

    private static void log(String msg) {
        System.out.println(msg);
    }
}

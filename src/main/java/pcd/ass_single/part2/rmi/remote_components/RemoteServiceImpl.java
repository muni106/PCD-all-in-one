package pcd.ass_single.part2.rmi.remote_components;

import pcd.ass_single.part2.rmi.BrushManager;
import pcd.ass_single.part2.rmi.PixelGrid;

import java.rmi.RemoteException;
import java.util.*;

public class RemoteServiceImpl implements RemoteService{

    private Map<Integer, RemoteServiceListener> listeners;
    private PixelGrid grid;
    private Integer counter;

    public RemoteServiceImpl(PixelGrid newGrid) {
        grid = newGrid;
        listeners = new HashMap<>();
        counter = 0;
    }

    @Override
    public synchronized Integer addPeer(RemoteServiceListener rsl, int x, int y, int color) throws RemoteException {
        Integer currListener = counter;
        listeners.forEach((id, listener) ->{
            if (!id.equals(currListener)) {
                try {
                    listener.addBrush(currListener, x, y, color);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        counter += 1;
        listeners.put(currListener, rsl);
        log("Added Listener");
        return currListener;
    }

    @Override
    public PixelGrid getGrid() throws RemoteException {
        return grid;
    }

    @Override
    public synchronized void updatePeers(Integer id, int x, int y, int color) throws RemoteException {
        listeners.forEach((peerId, listener) -> {
            if (!id.equals(peerId)) {
                try {
                    listener.updateBrushPos(id, x, y, color);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private static void log(String msg) {
        System.out.println(msg);
    }
}

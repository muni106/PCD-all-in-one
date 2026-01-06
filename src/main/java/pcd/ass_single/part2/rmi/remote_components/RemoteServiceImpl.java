package pcd.ass_single.part2.rmi.remote_components;

import pcd.ass_single.part2.rmi.PixelGrid;
import pcd.ass_single.part2.rmi.RemoteEvent;

import java.rmi.RemoteException;
import java.util.*;

public class RemoteServiceImpl implements RemoteService{

    private final Map<Integer, RemoteServiceListener> listenersMap;
    private final Queue<RemoteEvent> remoteEventQueue = new ArrayDeque<>();
    private PixelGrid grid;
    private Integer idCounter;

    public RemoteServiceImpl(PixelGrid grid) {
        this.grid = grid;
        this.listenersMap = new HashMap<>();
        this.idCounter = 0;
    }

    @Override
    public synchronized void handleEvent(RemoteEvent event) throws RemoteException {
        remoteEventQueue.add(event);
        processQueue();
    }

    private synchronized void processQueue() throws RemoteException {
        while (!remoteEventQueue.isEmpty()) {
            RemoteEvent event = remoteEventQueue.poll();
            broadcastEvent(event);
        }
    }

    private synchronized void broadcastEvent(RemoteEvent event) {
        listenersMap.forEach((listenerId, listener) -> {
            try{
                switch (event.getEventType()) {
                    case ADD:
                        listener.notifyBrushAdded(event.getBrushDTO());
                        break;
                    case MOVE:
                        listener.notifyBrushMoved(event.getBrushDTO());
                        break;
                    case DRAW:
                        listener.notifyPixelDrawn(event.getBrushDTO());
                        break;
                    default:
                        log("Wrong event");
                        break;
                }

            } catch (RemoteException e) {
                log("PEER: " + event.getBrushDTO().getPeerId() + " EVENT: " + event.getEventType().toString() + " WENT WRONG!");
            }
        });

    }

    @Override
    public synchronized Integer join(RemoteServiceListener rsl) throws RemoteException {
        Integer newPeerId = idCounter;
        idCounter += 1;
        listenersMap.put(newPeerId, rsl);
        log("Added Listener");
        return newPeerId;
    }

    @Override
    public synchronized void setGrid(PixelGrid grid) throws RemoteException {
        this.grid = grid;
    }

    @Override
    public PixelGrid getGrid() throws RemoteException {
        return grid;
    }


    private static void log(String msg) {
        System.out.println(msg);
    }
}

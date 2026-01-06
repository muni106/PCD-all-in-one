package pcd.ass_single.part2.rmi.remote_components;

import pcd.ass_single.part2.rmi.EventType;
import pcd.ass_single.part2.rmi.PixelGrid;
import pcd.ass_single.part2.rmi.RemoteEvent;

import java.rmi.RemoteException;
import java.util.*;

public class RemoteServiceImpl implements RemoteService{

    private final Map<Integer, RemoteServiceListener> listenersMap;
    private final Queue<RemoteEvent> remoteEventQueue = new ArrayDeque<>();
    // private final List<RemoteEvent> loggedEvents = new LinkedList<>();
    private PixelGrid grid;
    private Integer idCounter;
    private Integer leaderId;

    public RemoteServiceImpl(Integer leader, PixelGrid grid) {
        this.grid = grid;
        this.listenersMap = new HashMap<>();
        this.leaderId = leader;
        this.idCounter = leader;
    }

    public RemoteServiceImpl(Integer nextLeaderId, PixelGrid grid, Map<Integer, RemoteServiceListener> listenersMap) {
        this.leaderId = nextLeaderId;
        this.grid = grid;
        this.listenersMap = listenersMap;
        this.idCounter = leaderId + 1;
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
                    case COLOR_CHANGE:
                        listener.notifyBrushColorChanged(event.getBrushDTO());
                        break;
                    case LEAVE:
                        listener.notifyBrushRemoved(event.getBrushDTO().getPeerId());
                        break;
                    default:
                        log("Wrong event");
                        break;
                }

            } catch (RemoteException e) {
                log("PEER: " + event.getBrushDTO().getPeerId() + " EVENT: " + event.getEventType().toString() + " WENT WRONG!");
            }

            if (event.getEventType().equals(EventType.LEAVE)) {
                listenersMap.remove(event.getBrushDTO().getPeerId());
                if (event.getBrushDTO().getPeerId().equals(leaderId)) {
                    try {
                        leaderLeft();
                    } catch (RemoteException e) {
                        log("Problems with leader election REMOTE SERVICE IMPL");
                    }
                }
            }
        });
    }

    private void leaderLeft() throws RemoteException {
        Integer nextLeader = idCounter - 1;
        while (!listenersMap.containsKey(nextLeader) && !nextLeader.equals(leaderId)) {
           nextLeader -= 1;
        }

        if (leaderId.equals(nextLeader)) {
            log("No eligible leader found");
        } else {
            log("next leader is " + nextLeader);
            Integer finalNextLeader = nextLeader;
            listenersMap.forEach((listenerId, listener) -> {
                try {
                    listener.notifyNextLeader(finalNextLeader, new HashMap<>(listenersMap));
                } catch (RemoteException e) {
                    log("something went wrong while notifying " + listenerId + "about new leader");
                }
            });
        }
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

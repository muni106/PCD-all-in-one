package pcd.ass_single.part2.rmi.remote_components;

import pcd.ass_single.part2.rmi.EventType;
import pcd.ass_single.part2.rmi.PixelGrid;
import pcd.ass_single.part2.rmi.RemoteEvent;
import pcd.ass_single.part2.rmi.RemoteEventHandler;

import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class RemoteServiceImpl implements RemoteService{

    private final Map<Integer, RemoteServiceListener> listenersMap;
    private final Queue<RemoteEvent> remoteEventQueue = new ArrayDeque<>();
    // private final List<RemoteEvent> loggedEvents = new LinkedList<>();
    private PixelGrid grid;
    private Integer idCounter;
    private final Integer leaderId;
    private final RemoteEventHandler leaderRemoteEventHandler;

    public RemoteServiceImpl(Integer leader, PixelGrid grid, RemoteEventHandler leaderRemoteEventHandler) {
        this.grid = grid;
        this.listenersMap = new ConcurrentHashMap<>();
        this.leaderId = leader;
        this.idCounter = leader + 1;
        this.leaderRemoteEventHandler = leaderRemoteEventHandler;
    }

    public RemoteServiceImpl(Integer nextLeaderId, PixelGrid grid, Map<Integer, RemoteServiceListener> listenersMap, RemoteEventHandler leaderRemoteEventHandler) {
        this.leaderId = nextLeaderId;
        this.grid = grid;
        this.listenersMap = listenersMap;
        this.leaderRemoteEventHandler = leaderRemoteEventHandler;
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

            if (event.getEventType().equals(EventType.LEAVE)) {
                listenersMap.remove(event.getBrushDTO().getPeerId());
                if (event.getBrushDTO().getPeerId().equals(leaderId)) {
                    try {
                        leaderLeft();
                    } catch (RemoteException e) {
                        log("Problems with leader election in REMOTE SERVICE ");
                    }
                }
            }
            informLeader(event);
            broadcastEvent(event);
        }
    }

    private void informLeader(RemoteEvent event) {
            switch (event.getEventType()) {
                case ADD:
                    leaderRemoteEventHandler.onBrushAdded(event.getBrushDTO());
                    break;
                case MOVE:
                    leaderRemoteEventHandler.onBrushMoved(event.getBrushDTO());
                    break;
                case DRAW:
                    leaderRemoteEventHandler.onPixelDrawn(event.getBrushDTO());
                    break;
                case COLOR_CHANGE:
                    leaderRemoteEventHandler.onBrushColorChanged(event.getBrushDTO());
                    break;
                case LEAVE:
                    leaderRemoteEventHandler.onBrushRemoved(event.getBrushDTO().getPeerId());
                    break;
                default:
                    log("Wrong event");
                    break;
            }
    }

    private void broadcastEvent(RemoteEvent event) {
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
                    log("error key : " + event.getBrushDTO().getPeerId());
                    listenersMap.forEach((k, v) -> {
                        log("listener id in map : " + k);
                    });
                    log("PEER: " + event.getBrushDTO().getPeerId() + " EVENT: " + event.getEventType().toString() + " WENT WRONG!");
                    log(e.getMessage());
                    // throw new RuntimeException(e);
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
        listenersMap.forEach((lId, l) -> {
            log(lId + " -> " + l);
        });
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

package pcd.ass_single.part2.rmi.remote_components;

import pcd.ass_single.part2.rmi.BrushDTO;
import pcd.ass_single.part2.rmi.RemoteEventListener;

import java.rmi.RemoteException;
import java.util.Map;

public class RemoteServiceListenerImpl implements RemoteServiceListener{
    private RemoteEventListener brushListener;

    public RemoteServiceListenerImpl(RemoteEventListener listener) {
       brushListener = listener;
    }

    @Override
    public synchronized void notifyBrushAdded(BrushDTO brushDTO) throws RemoteException {
        brushListener.onBrushAdded(brushDTO);
    }

    @Override
    public synchronized void notifyBrushColorChanged(BrushDTO brushDTO) throws RemoteException {
        brushListener.onBrushColorChanged(brushDTO);
    }

    @Override
    public synchronized void notifyBrushMoved(BrushDTO brushDTO) throws RemoteException {
        brushListener.onBrushMoved(brushDTO);
    }

    @Override
    public synchronized void notifyPixelDrawn(BrushDTO brushDTO) throws RemoteException {
        brushListener.onPixelDrawn(brushDTO);
    }

    @Override
    public synchronized void notifyBrushRemoved(Integer id) throws RemoteException {
        brushListener.onBrushRemoved(id);
    }

    @Override
    public void notifyNextLeader(Integer leaderId, Map<Integer, RemoteServiceListener> listenersMap) throws RemoteException {
        brushListener.onNextLeaderElection(leaderId, listenersMap);
    }
}

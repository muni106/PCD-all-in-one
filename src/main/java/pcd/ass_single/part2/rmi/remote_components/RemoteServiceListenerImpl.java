package pcd.ass_single.part2.rmi.remote_components;

import pcd.ass_single.part2.rmi.BrushEventListener;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RemoteServiceListenerImpl implements RemoteServiceListener{
    private BrushEventListener brushListener;

    public RemoteServiceListenerImpl(BrushEventListener listener) {
       brushListener = listener;
    }

    @Override
    public synchronized void notifyBrushAdded(Integer id, int x, int y, int color) throws RemoteException {
        brushListener.onBrushAdded(id, x, y, color);
    }

    @Override
    public synchronized void notifyBrushColorChanged(Integer id, int color) throws RemoteException {

    }

    @Override
    public synchronized void notifyBrushMoved(Integer id, int x, int y, int color) throws RemoteException {
        brushListener.onBrushMoved(id, x, y, color);

    }

    @Override
    public synchronized void notifyPixelDrawn(int x, int y, int color) throws RemoteException {

    }

    @Override
    public synchronized void notifyBrushRemoved(Integer id) throws RemoteException {

    }
}

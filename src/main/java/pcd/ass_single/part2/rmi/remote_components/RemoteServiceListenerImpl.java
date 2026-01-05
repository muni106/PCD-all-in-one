package pcd.ass_single.part2.rmi.remote_components;

import pcd.ass_single.part2.rmi.BrushManager;

import java.rmi.RemoteException;
import java.util.Map;

public class RemoteServiceListenerImpl implements java.io.Serializable, RemoteServiceListener{

    private BrushManager myBrushManager;

    public RemoteServiceListenerImpl(BrushManager brushManager) {
        myBrushManager = brushManager;
    }

    @Override
    public synchronized void addBrush(Integer id, int x, int y, int color)  throws RemoteException {
        myBrushManager.addBrush(id, new BrushManager.Brush(x, y, color));
    }

    @Override
    public void changeBrushColor(Integer id, int color) throws RemoteException {


    }

    @Override
    public synchronized void updateBrushPos(Integer id, int x, int y, int color) throws RemoteException {
        if (myBrushManager.containsBrush(id))
            myBrushManager.updateBrushPosition(id, x, y);
        else
            myBrushManager.addBrush(id, new BrushManager.Brush(x, y, color));
    }

    @Override
    public void draw(int x, int y, int color) throws RemoteException {

    }

    @Override
    public void leave(Integer id) throws RemoteException {

    }
}

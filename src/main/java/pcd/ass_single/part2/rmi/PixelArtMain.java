package pcd.ass_single.part2.rmi;

import pcd.ass_single.part2.rmi.remote_components.RemoteService;
import pcd.ass_single.part2.rmi.remote_components.RemoteServiceImpl;
import pcd.ass_single.part2.rmi.remote_components.RemoteServiceListener;
import pcd.ass_single.part2.rmi.remote_components.RemoteServiceListenerImpl;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;

public class PixelArtMain {

	public static int randomColor() {
		Random rand = new Random();
		return rand.nextInt(256 * 256 * 256);
	}

	public static void main(String[] args) throws RemoteException {
		// REMOTE CONFIG
		String host = (args.length < 1) ? null : args[0];
		Registry registry = LocateRegistry.getRegistry(host);

		RemoteService rs;
		try {
			rs = (RemoteService) registry.lookup("rsObj");
		} catch (NotBoundException e) {
            log("remote service not found, let's create a new one");
			PixelGrid newGrid = new PixelGrid(40,40);
			Random rand = new Random();
			for (int i = 0; i < 10; i++) {
				newGrid.set(rand.nextInt(40), rand.nextInt(40), randomColor());
			}
			rs = new RemoteServiceImpl(newGrid);
        }

		PixelGrid grid = rs.getGrid();

		RemoteService rsProxy = (RemoteService) UnicastRemoteObject.exportObject(rs, 0);
		registry.rebind("rsObj", rsProxy);

		// CONFIG LOCAL BRUSHES
		var brushManager = new BrushManager();
		var localBrush = new BrushManager.Brush(0, 0, randomColor());
		PixelGridView view = new PixelGridView(grid, brushManager, 800, 800);
		BrushEventListener bel = new BrushEventListener() {
			@Override
			public void onBrushAdded(Integer id, int x, int y, int color) {
				brushManager.addBrush(id, new BrushManager.Brush(x, y, color));
			}

			@Override
			public void onBrushMoved(Integer id, int x, int y, int color) {
				brushManager.updateBrushPosition(id, x, y);
			}

			@Override
			public void onBrushColorChanged(Integer id, int color) {
				brushManager.updateBrushColor(id, color);

			}

			@Override
			public void onBrushRemoved(Integer id) {
				brushManager.removeBrush(id);
			}
		};

		// CONFIG LISTENER (pattern observer)

        RemoteServiceListener rsl = new RemoteServiceListenerImpl(bel);
		RemoteServiceListener rslProxy = (RemoteServiceListener) UnicastRemoteObject.exportObject(rsl, 0);

		Integer peerId = rs.addPeer(rslProxy, localBrush.getX(), localBrush.getY(), localBrush.getColor());

		brushManager.addBrush(peerId, localBrush);

		final RemoteService finalRs = rs;

		view.addMouseMovedListener((x, y) -> {
			localBrush.updatePosition(x, y);
            try {
                finalRs.updatePeers(peerId, x, y, localBrush.getColor());
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
            view.refresh();
		});

		view.addPixelGridEventListener((x, y) -> {
			grid.set(x, y, localBrush.getColor());
			view.refresh();
		});

		view.addColorChangedListener(localBrush::setColor);

		view.display();
	}


	private static void log(String msg) {
		System.out.println(msg);
	}

}

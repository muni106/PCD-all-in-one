package pcd.ass_single.part2.rmi;

import pcd.ass_single.part2.rmi.remote_components.RemoteService;
import pcd.ass_single.part2.rmi.remote_components.RemoteServiceImpl;
import pcd.ass_single.part2.rmi.remote_components.RemoteServiceListener;
import pcd.ass_single.part2.rmi.remote_components.RemoteServiceListenerImpl;

import java.rmi.AlreadyBoundException;
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
		var brushManager = new BrushManager();
		var fooBrush = new BrushManager.Brush(0, 0, randomColor());
		var localBrush = new BrushManager.Brush(0, 0, randomColor());
		brushManager.addBrush(localBrush);

		//TODO fix that shit
		String host = (args.length < 1) ? null : args[0];
		Registry registry = LocateRegistry.getRegistry(host);

		// verify if we have the Remote Service is up, otherwise create a new one
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

        RemoteServiceListener rsl = new RemoteServiceListenerImpl();
		RemoteServiceListener rslProxy = (RemoteServiceListener) UnicastRemoteObject.exportObject(rsl, 0);

		PixelGridView view = new PixelGridView(grid, brushManager, 800, 800);

        brushManager.addBrush(fooBrush);
		view.addMouseMovedListener((x, y) -> {
			localBrush.updatePosition(x, y);
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

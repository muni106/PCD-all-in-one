package pcd.ass_single.part2.rmi;

import pcd.ass_single.part2.rmi.remote_components.RemoteService;

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
        // rmi setup

        String host = (args.length < 1) ? null : args[0];
        Registry registry = LocateRegistry.getRegistry(host);


		var brushManager = new BrushManager();
		var localBrush = new BrushManager.Brush(0, 0, randomColor());
		brushManager.addBrush(localBrush);
		PixelGrid grid = new PixelGrid(40,40);

		//TODO fix that shit
		// RemoteService remoteService = (RemoteService) UnicastRemoteObject.exportObject();

		Random rand = new Random();
		for (int i = 0; i < 10; i++) {
			grid.set(rand.nextInt(40), rand.nextInt(40), randomColor());
		}

		PixelGridView view = new PixelGridView(grid, brushManager, 800, 800);


        var fooBrush = new BrushManager.Brush(0, 0, randomColor());
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

}

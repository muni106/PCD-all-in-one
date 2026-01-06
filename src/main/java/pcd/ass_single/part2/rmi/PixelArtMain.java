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
			RemoteService rsProxy = (RemoteService) UnicastRemoteObject.exportObject(rs, 0);
			registry.rebind("rsObj", rsProxy);

			log("service bound to registry");
        }

		PixelGrid grid = rs.getGrid();

		// CONFIG LOCAL BRUSHES
		BrushManager brushManager = new BrushManager();
		BrushManager.Brush localBrush = new BrushManager.Brush(0, 0, randomColor());
		PixelGridView view = new PixelGridView(grid, brushManager, 800, 800);

		// CONFIG REMOTE EVENT LISTENER (pattern observer)
		RemoteEventListener bel = new RemoteEventListener() {
			@Override
			public void onBrushAdded(BrushDTO brushDTO) {
				brushManager.addBrush(brushDTO.getPeerId(), new BrushManager.Brush(brushDTO.getX(), brushDTO.getY(), brushDTO.getColor()));
			}

			@Override
			public void onBrushMoved(BrushDTO brushDTO) {
				if (brushManager.containsBrush(brushDTO.getPeerId())) {
					brushManager.updateBrushPosition(brushDTO.getPeerId(), brushDTO.getX(), brushDTO.getY());
				} else {
					brushManager.addBrush(brushDTO.getPeerId(), new BrushManager.Brush(brushDTO.getX(), brushDTO.getY(), brushDTO.getColor()));
				}
				view.refresh();
			}

			@Override
			public void onBrushColorChanged(BrushDTO brushDTO) {
				brushManager.updateBrushColor(brushDTO.getPeerId(), brushDTO.getColor());
			}

			@Override
			public void onPixelDrawn(BrushDTO brushDTO) {
				grid.set(brushDTO.getX(), brushDTO.getY(), brushDTO.getColor());
			}

			@Override
			public void onBrushRemoved(Integer id) {
				brushManager.removeBrush(id);
			}
		};


		// CONFIG LEADER LISTENER
        RemoteServiceListener rsl = new RemoteServiceListenerImpl(bel);
		RemoteServiceListener rslProxy = (RemoteServiceListener) UnicastRemoteObject.exportObject(rsl, 0);

		Integer peerId = rs.join(rslProxy);

		dispatchEvent(rs, EventType.ADD, peerId, localBrush);

		brushManager.addBrush(peerId, localBrush);

		final RemoteService finalRs = rs;

		view.addMouseMovedListener((x, y) -> {
			localBrush.updatePosition(x, y);
            try {
                dispatchEvent(finalRs, EventType.MOVE, peerId, localBrush);
            } catch (RemoteException e) {
                log("Something went wrong in addMouseMovedLister");
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

	private static void dispatchEvent(RemoteService rs, EventType eventType, Integer peerId, BrushManager.Brush brush) throws RemoteException {
		rs.handleEvent(new RemoteEvent(eventType, new BrushDTO(peerId, brush.getX(), brush.getY(), brush.getColor())));
	}

	private static void log(String msg) {
		System.out.println(msg);
	}

}

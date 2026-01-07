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
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import static java.rmi.server.LogStream.log;

public class PixelArtMain {
	private static Integer peerId;
	private static RemoteService rsRef;
	private static boolean leaderAvailable;

	public static int randomColor() {
		Random rand = new Random();
		return rand.nextInt(256 * 256 * 256);
	}

	public static void main(String[] args) throws RemoteException, InterruptedException {
		// REMOTE CONFIG
		String host = (args.length < 1) ? null : args[0];
		Registry registry = LocateRegistry.getRegistry(host);

		// LOCAL CONFIG
		boolean iAmLeader = false;
		BrushManager brushManager = new BrushManager();
		BrushManager.Brush localBrush = new BrushManager.Brush(0, 0, randomColor());
		PixelGrid grid = null;
		PixelGridView view = null;

		try {
			rsRef = (RemoteService) registry.lookup("rsObj");
		} catch (NotBoundException e) {
            log("remote service not found, let's create a new one");

			grid = new PixelGrid(40,40);
			view = new PixelGridView(grid, brushManager, 800, 800);
			Random rand = new Random();
			for (int i = 0; i < 10; i++) {
				grid.set(rand.nextInt(40), rand.nextInt(40), randomColor());
			}
			peerId = 0;
			brushManager.addBrush(peerId, localBrush);
			iAmLeader = true;
			RemoteEventHandler leaderRemoteEventHandler = new AbstractRemoteEventHandler(brushManager, view, grid) {
				@Override
				public void onNextLeaderElection(Integer leaderId, Map<Integer, RemoteServiceListener> listenersMap) {
					log("goodbye my fellas");
				}
			};
			rsRef = new RemoteServiceImpl(peerId, grid, leaderRemoteEventHandler);
			leaderAvailable = true;
			RemoteService rsProxy = (RemoteService) UnicastRemoteObject.exportObject(rsRef, 0);
			registry.rebind("rsObj", rsProxy);

			log("service bound to registry");
			log("I'm the leader");
        }

		if (grid == null) {
			grid = rsRef.getGrid();
		}
		if (view == null) {
			view = new PixelGridView(grid, brushManager, 800, 800);
		}

		if (!leaderAvailable) {
			// CONFIG REMOTE EVENT LISTENER (pattern observer)
			log("Im a peer");
			RemoteEventHandler peerRemoteEventHandler = new AbstractRemoteEventHandler(brushManager, view, grid) {
				@Override
				public void onNextLeaderElection(Integer leaderId, Map<Integer, RemoteServiceListener> listenersMap) {
					leaderAvailable = false;
					if (Objects.equals(peerId, leaderId)) {

						RemoteEventHandler leaderRemoteEventHandler = new AbstractRemoteEventHandler(brushManager, view, grid) {
							@Override
							public void onNextLeaderElection(Integer leaderId, Map<Integer, RemoteServiceListener> listenersMap) {
								log("goodbye my fellas");
							}
						};

						rsRef = new RemoteServiceImpl(leaderId, grid, listenersMap, leaderRemoteEventHandler);
						leaderAvailable = true;

						try {
							RemoteService newRsProxy = (RemoteService) UnicastRemoteObject.exportObject(rsRef, 0);
							registry.rebind("rsObj", newRsProxy);
						} catch (RemoteException e) {
							log("failed on leader Election remote service binding");
						}

					} else {
						new Thread(() -> {
							boolean connected = false;
							while (!connected) {
								try {
									rsRef = (RemoteService) registry.lookup("rsObj");
									connected = true;
									log("connected successfully to new leader");
									leaderAvailable = true;
								} catch (RemoteException | NotBoundException e) {
									log("not found yet");
									try {
										Thread.sleep(1000);
									} catch (InterruptedException ex) {
										throw new RuntimeException(ex);
									}
								}
							}

						}).start();
					}

				}
			};
			RemoteServiceListener rsl = new RemoteServiceListenerImpl(peerRemoteEventHandler );
			RemoteServiceListener rslProxy = (RemoteServiceListener) UnicastRemoteObject.exportObject(rsl, 0);

			peerId = rsRef.join(rslProxy);
			brushManager.addBrush(peerId, localBrush);
			leaderAvailable = true;
			dispatchEvent(rsRef, EventType.ADD, brushManager.getBrushDTO(peerId));
		}

		view.addMouseMovedListener((x, y) -> {
			dispatchEvent(rsRef, EventType.MOVE, brushManager.getBrushDTOWithUpdatedPos(peerId, x, y));
//            view.refresh();
		});

		view.addPixelGridEventListener((x, y) -> {
//			grid.set(x, y, localBrush.getColor());
			dispatchEvent(rsRef, EventType.DRAW, brushManager.getBrushDTO(peerId));
//            view.refresh();
		});

		view.addColorChangedListener((color) -> {
//			localBrush.setColor(color);
			dispatchEvent(rsRef, EventType.COLOR_CHANGE, brushManager.getBrushDTOWithUpdatedColor(peerId, color));
//			view.refresh();
        });



		view.display();
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			dispatchEvent(rsRef, EventType.LEAVE, brushManager.getBrushDTO(peerId));
		}));

	}

	private static void dispatchEvent(RemoteService rs, EventType eventType, BrushDTO brush) {
		if (leaderAvailable) {
			try {
				rs.handleEvent(new RemoteEvent(eventType, brush));
			} catch (RemoteException e) {
				log("Something went wrong while: " + eventType.toString() );
			}
		}
    }

	private static void log(String msg) {
		System.out.println(msg);
	}

}

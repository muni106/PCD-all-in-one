package pcd.ass_single.part2.mom;

import java.io.IOException;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rabbitmq.client.*;

public class PixelArtMain {

	public static int randomColor() {
		Random rand = new Random();
		return rand.nextInt(256 * 256 * 256);
	}

    private static final String EXCHANGE_NAME = "pixel_art";
    private static final String CLIENT_ID = UUID.randomUUID().toString();

	public static void main(String[] args) throws IOException, TimeoutException {
        // mom setup
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = null;
        connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // fanout => broadcast to everyone
        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");

        // main setup channel
        String queue = channel.queueDeclare().getQueue();
        channel.queueBind(queue , EXCHANGE_NAME, "");

        // app setup
		var brushManager = new BrushManager();
		var localBrush = new BrushManager.Brush(0, 0, randomColor());
		brushManager.addBrush(CLIENT_ID, localBrush); // myBrush


        PixelGrid grid = new PixelGrid(40,40); // grid view

        Random rand = new Random();
        for (int i = 0; i < 10; i++) {
            grid.set(rand.nextInt(40), rand.nextInt(40), randomColor());
        }

		PixelGridView view = new PixelGridView(grid, brushManager, 800, 800);

        // send join message
		view.addMouseMovedListener((x, y) -> {
			localBrush.updatePosition(x, y);
            sendMessage("move", x, y, localBrush.getColor(), channel);
			view.refresh();
		});

		view.addPixelGridEventListener((x, y) -> {
			grid.set(x, y, localBrush.getColor());
            sendMessage("draw", x, y, localBrush.getColor(), channel);
			view.refresh();
		});

        // I joined!
        sendMessage("join", localBrush.getX(), localBrush.getY(), localBrush.getColor(), channel);

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            handleIncomingMessage(message, brushManager, grid, view, channel);
        };

        channel.basicConsume(queue, true, deliverCallback, t -> {});

		view.addColorChangedListener((color -> {
            localBrush.setColor(color);
            sendMessage("colorChange", localBrush.getX(), localBrush.getY(), localBrush.getColor(), channel);
            view.refresh();
        }));

		view.display();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            sendMessage("leave", localBrush.getX(), localBrush.getY(), localBrush.getColor(), channel);
        }));
	}

    // helper to send messages
    private static void sendMessage(String type, int x, int y, int color, Channel channel)  {
        try {
            String message = String.format("%s|%s|%d|%d|%d", type, CLIENT_ID, x, y, color);
            channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes());
            log("[x] sent: " + message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void sendGridStatus(String serializedGrid, Channel channel) throws IOException {
        String message = String.format("%s|%s|%s", "grid_status", CLIENT_ID, serializedGrid);
        channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes());
        log("[x] GRID sent");
    }


    private static void handleIncomingMessage(String message, BrushManager brushManager, PixelGrid grid, PixelGridView view, Channel channel) throws IOException {
        String[] parts = message.split("\\|");

        String type = parts[0];
        String senderId = parts[1];
        if (!senderId.equals(CLIENT_ID)) {
            if (!type.equals("grid_status")) {
                int x = Integer.parseInt(parts[2]);
                int y = Integer.parseInt(parts[3]);
                int color = Integer.parseInt(parts[4]);
                switch (type) {
                    case "join":
                        brushManager.addBrush(senderId, new BrushManager.Brush(x, y, color));
                        sendGridStatus(grid.serializedGrid(), channel);
                        log(senderId + " joined");
                        break;
                    case "colorChange":
                        brushManager.getBrush(senderId).setColor(color);
                        log(senderId + " CHANGED COLOR");
                        break;
                    case "move":
                        // log(String.format("%s is moving", senderId));
                        // if client not yet in the map, I add it
                        brushManager.printBrushes();
                        // log("MOVE " + senderId);
                        if (brushManager.hasBrush(senderId)) {
                            // log("UPDATE");
                            brushManager.updateBrushPosition(senderId, x, y);
                        } else {
                            // log("CREATE");
                            brushManager.addBrush(senderId, new BrushManager.Brush(x, y, color));
                        }
                        view.refresh();
                        break;
                    case "draw":
                        grid.set(x, y, color);
                        view.refresh();
                        break;
                    case "leave":
                        brushManager.removeBrush(senderId);
                        break;
                }
            } else {
                log("GRID_UPDATE");
                String serializedGrid = parts[2];
                grid.setGrid(serializedGrid);
                view.refresh();
            }
        }
    }

    private static void log(String l) {
        System.out.println(l);
    }

}

package pcd.ass_single.part2.rmi;

import java.io.Serializable;

public class RemoteEvent implements Serializable {
    private final EventType eventType;
    private final BrushDTO brushDTO;


    public RemoteEvent(EventType eventType, BrushDTO brushDTO) {
        this.eventType = eventType;
        this.brushDTO = brushDTO;
    }

    public EventType getEventType() {
        return eventType;
    }

    public BrushDTO getBrushDTO() {
        return brushDTO;
    }
}

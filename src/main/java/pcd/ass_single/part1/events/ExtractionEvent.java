package pcd.ass_single.part1.events;

public record ExtractionEvent(ExtractionEventType eventType, String directoryPath, String searchWord) {
}

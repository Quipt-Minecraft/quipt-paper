package me.quickscythe.quipt.utils.events;

public class EventManager {
    private static Event event = null;

    public static void init() {
        event = new Event();
    }

    public static Event event() {
        return event;
    }
}


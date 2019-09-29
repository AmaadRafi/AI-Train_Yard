package com.switchproblem.ai;

public class Action {
    String direction;
    int fromTrack;
    int toTrack;

    public Action(String direction, int from, int to) {
        this.direction = direction;
        this.fromTrack = from;
        this.toTrack = to;
    }

    @Override
    public String toString() {
        return '[' + direction + ", " + fromTrack + ", " + toTrack + ']';
    }
}

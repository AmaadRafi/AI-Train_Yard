package com.switchproblem.ai;

import java.util.ArrayList;
import java.util.List;

public class Outcome {

    Action actionTaken;
    List<String> resultState;

    public Outcome(Action actionTaken, List<String> resultState) {
        this.actionTaken = actionTaken;
        this.resultState = resultState;
    }

    public Outcome() {
        this.actionTaken = null;
        this.resultState = null;
    }

    @Override
    public String toString() {
        return "" + resultState;
    }
}

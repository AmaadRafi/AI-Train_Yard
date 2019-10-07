package com.switchproblem.ai;

import java.util.ArrayList;
import java.util.List;

public class Node {

    //Node previousNode;
    Action actionTaken;
    List<String> initialState;
    List<String> resultState;
    int fCost;
    int gCost;
    int hCost;

    public Node(Action actionTaken, List<String> resultState) {
        this.actionTaken = actionTaken;
        this.resultState = resultState;
    }

    public Node(List<String> initialState, Action actionTaken) {
        this.initialState = initialState;
        this.actionTaken = actionTaken;
        if(actionTaken == null)
            this.resultState = initialState;
    }

    public Node(){}

    public int hCost(List<String> resultState, List<String> goalState) {
        String rFirstTrack = resultState.get(0);
        String gFirstTrack = goalState.get(0);
        String goalJoin = String.join("", goalState);
        String resultJoin = String.join("", resultState);
        List<String> goalCheckpoints = new ArrayList<>();

        int cost = gFirstTrack.length() - rFirstTrack.length();

        // create list of sorted substrings
        for (int i = goalJoin.length(); i > 0; i--) {
            goalCheckpoints.add(goalJoin.substring(0, i));
        }

        // remove substring contained by goalJoin
        for (int i = 0; i < goalCheckpoints.size(); i++) {
            if (resultJoin.contains(goalCheckpoints.get(i))){
                resultJoin = resultJoin.replace(goalCheckpoints.get(i), "");
                cost += resultJoin.length();
                break;
            }
        }

        return cost;
    }

    @Override
    public String toString() {
        return "" + resultState;
    }
}

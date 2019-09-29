package com.switchproblem.ai;

import java.util.List;

public class Result {

    Action actionTaken;
    List<String> prevState;
    List<String> resultState;
    int path_cost;

    public Result(List<String> prevState, Action actionTaken, List<String> resultState) {
        this.prevState = prevState;
        this.actionTaken = actionTaken;
        this.resultState = resultState;
    }

    public Result(Action actionTaken, List<String> resultState) {
        this.actionTaken = actionTaken;
        this.resultState = resultState;
    }

    public Result(List<String> prevState) {
        this.prevState = prevState;
        this.resultState = prevState;
    }

    public Result(){}


    public int calculateCost(List<String> currentState, List<String> goalState) {
        int cost = goalState.get(0).length();
        for(int i = 1; i < currentState.get(0).length() ; i++) {
            if(goalState.get(0).contains(currentState.get(0).substring(0, i))){
                cost -= 1;
            } else {
                return cost;
            }
        }

        return cost;
    }

    @Override
    public String toString() {
        return "" + resultState;
    }
}

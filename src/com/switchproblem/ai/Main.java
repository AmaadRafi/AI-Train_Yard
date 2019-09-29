package com.switchproblem.ai;


import java.util.*;

// go list of possible actions, do results next or store that list into something global?

public class Main {

    public static void main(String[] args) {
        int input = -1;
        Scanner ui = new Scanner(System.in);

        while(input != 6) {
            System.out.println("\n1. Test Yard 1");
            System.out.println("2. Test Yard 2");
            System.out.println("3. Test Yard 3");
            System.out.println("4. Test Yard 4");
            System.out.println("5. Test Yard 5");
            System.out.println("6. Exit");
            System.out.printf("Input : ");

            try{
                input = ui.nextInt();
            }catch (InputMismatchException e) {
                System.out.println("Invalid input");
                ui.next();
            }


            switch (input){
                case 1: testYard1(); break;
                case 2: testYard2(); break;
                case 3: testYard3(); break;
                case 4: testYard4(); break;
                case 5: testYard5(); break;
                case 6:
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid input");
                    break;
            }
        }
    }

    /**
     * Problem 1 - find list of all possible actions
     *
     * @param yard  - list of sublist's (connectivity list)
     * @param state - current position of trains
     */
    private static List<Action> possibleActions(List<List<Integer>> yard, List<String> state) {
        List<Action> actions = new ArrayList<Action>();
        int engineTrack = findEngineTrack(state);

        // find sublist that connects to that track
        for (List<Integer> sublist : yard) {
            if (sublist.contains(engineTrack)) {
                // Move left most element of right track to right most of left track
                actions.add(new Action("L", sublist.get(1), sublist.get(0)));
                // Move right most element of left track to left most element of right track
                actions.add(new Action("R", sublist.get(0), sublist.get(1)));
            }
        }
        // Collections.sort(actions, (action1, action2) -> (action1.direction).compareTo(action2.direction));
        return actions;
    }


//    private static List<String> result(Action action, List<String> state) {
//        List<String> result = new ArrayList<>(state);
//
//        int fromTrack = action.fromTrack - 1;
//        int toTrack = action.toTrack - 1;;
//        String fromString = state.get(fromTrack);
//        String toString = state.get(toTrack);
//
//        // System.out.println("CLONE = " + result);
//        if(action.direction.equals("LEFT")) {
//            // System.out.println("Action = LEFT From = " + action.fromTrack + " To = " + action.toTrack);
//            String leftTrack, rightTrack;
//            // move FROM right TO left
//            if(!fromString.isEmpty()) {
//                leftTrack = toString + fromString.charAt(0);
//                rightTrack = removeCharAt(fromString, 0);
//            }
//            else {
//                leftTrack = toString;
//                rightTrack = fromString;
//            }
//
////            // delete w.e we moved from right track
////            if(!toString.isEmpty())
////                rightTrack = removeCharAt(fromString, 0);
////            else
////                rightTrack = fromString;
//
//            result.set(toTrack, leftTrack);
//            result.set(fromTrack, rightTrack);
//            //System.out.println("final track L = " + result);
//        }

    /**
     * Problem 2 - consumes an Action and a State and produces the new State that
     *             will result after actually carrying out the input move in the input state
     *
     * @param action - object of Action that contains (string direction, int from, int to)
     * @param state  - current position of cars
     * @return result - a Result object that stores the next state and action taken to get to it
     */
    private static Result result(Action action, List<String> state) {
        Result result = new Result();
        List<String> carState = new ArrayList<>(state);

        int fromTrack = action.fromTrack - 1;
        int toTrack = action.toTrack - 1;

        String fromString = state.get(fromTrack);
        String toString = state.get(toTrack);

        // move car from left track to right track
        if (action.direction.equals("L")) {
            String leftTrack, rightTrack;

            if (!fromString.isEmpty()) {
                leftTrack = toString + fromString.charAt(0);
                rightTrack = removeCharAt(fromString, 0);
            } else {
                leftTrack = toString;
                rightTrack = fromString;
            }

            carState.set(toTrack, leftTrack);
            carState.set(fromTrack, rightTrack);
            result.prevState = state;
            result.actionTaken = action;
            result.resultState = carState;
        }

        // move car from right track to left track
        if (action.direction.equals("R")) {
            String rightTrack, leftTrack;

            if (!fromString.isEmpty()) {
                rightTrack = fromString.charAt(fromString.length() - 1) + toString;
                leftTrack = removeCharAt(fromString, fromString.length() - 1);
            } else {
                rightTrack = toString;
                leftTrack = fromString;
            }

            carState.set(toTrack, rightTrack);
            carState.set(fromTrack, leftTrack);
            result.actionTaken = action;
            result.resultState = carState;
        }

        return result;
    }

    /**
     * Problem 3 - consumes a State and a Yard, and produces a list of all states that
     *             can be reached in one Action from the given state
     *
     * @param yard  - list of sublist's (connectivity list)
     * @param state - current position of trains
     * @return possibleResults - list of all possible outcomes from give state and yard
     */
    private static List<Result> expand(List<List<Integer>> yard, List<String> state) {
        //List<List<String>> possibleStates = new ArrayList<>();
        List<Result> possibleResults = new ArrayList<>();

        List<Action> actions = possibleActions(yard, state);

        for (Action action : actions)
            if (!result(action, state).equals(state)) {
                Result possibleResult = new Result(result(action, state).actionTaken, result(action, state).resultState);
                possibleResults.add(possibleResult);
                //possibleStates.add(result(action, state).resultState);

            }

        return possibleResults;
    }

    /**
     * Problem 4 - consumes a connectivity list (Yard), an initial State, and a goal State as
     *             inputs, and produces a list of Actions that will take the cars in the initial state into the goal
     *             state.
     *
     * @param yard         - list of sublist's (connectivity list)
     * @param initialState - List of string we start with
     * @param goalState    - List of strings we want to end up with
     * @return
     */
    private static List<Action> bfs(List<List<Integer>> yard, List<String> initialState, List<String> goalState) {
        List<Action> actionsToGoal = new ArrayList<Action>();

        if (initialState.equals(goalState))
            return actionsToGoal;

        // queue of states + set of visited states
        Queue<Result> q = new LinkedList<>();
        Set<List<String>> visited = new HashSet<>();

        q.add(new Result(initialState));

        while (!q.isEmpty()) {
            int currentLevelSize = q.size();

            for (int i = 0; i < currentLevelSize; i++) {
                Result currentNode = q.poll();
                currentNode.path_cost = currentNode.calculateCost(currentNode.resultState, goalState);
                System.out.println("Step taken : " + currentNode.actionTaken + "\tCurrent state : " + currentNode.resultState + "\tCost : " + currentNode.path_cost);

                if (currentNode.actionTaken != null)
                    actionsToGoal.add(currentNode.actionTaken);

                if (currentNode.resultState.equals(goalState)) {
                    System.out.println("Steps needed to reach goal : " +actionsToGoal.size());
                    return actionsToGoal;
                }

                List<Result> possibleNodes = expand(yard, currentNode.resultState);

                for (Result node : possibleNodes) {
                    if (!visited.contains(node.resultState))
                        q.add(node);
                        visited.add(node.resultState);
                }
            }

        }

        System.out.println("Goal not reachable.");
        System.out.println("Visisted Nodes: " + visited.size());
        System.out.println("BFS counter = "  + actionsToGoal.size());
        System.out.println(visited);
        return actionsToGoal;
    }

    /**
     * HelperFxn - used to find the index of the string that contains the engine (*)
     * @param state - list of strings / layout of track
     * @return index of engine + 1 = track
     */
    private static int findEngineTrack(List<String> state){
        int index = -2;

        for (String s: state) {
            if(s.contains("*"))
                index = state.indexOf(s);
        }
        return index + 1;
    }

    /**
     * HelperFxn - checks to see if an ArrayList contains a certain sublist
     * @param list - big ArrayList
     * @param subX - x value of element in sublist
     * @param subY - y value of element in sublist
     * @return
     */
    private static boolean isValidSublist(ArrayList<?> list, int subX, int subY) {
        List<List<Integer>> sublist = new ArrayList<>();
        sublist.add(Arrays.asList(subX,subY));

        return Collections.indexOfSubList(list, sublist) != -1;
    }

    /**
     * HelperFxn - remove a char from a string
     * @param str
     * @param index
     * @return
     */
    private static String removeCharAt(String str, int index) {
        String finalString;
        if(str.isEmpty())
            finalString = str;
        else
            finalString = str.substring(0, index) + str.substring(index + 1);

        return finalString;
    }

    public static void testYard1() {
        // set yard
        List<List<Integer>> yard = new ArrayList<>();
        yard.add(Arrays.asList(1,2));
        yard.add(Arrays.asList(1,3));
        yard.add(Arrays.asList(3,5));
        yard.add(Arrays.asList(4,5));
        yard.add(Arrays.asList(2,6));
        yard.add(Arrays.asList(5,6));

        List<String> goalState = new ArrayList<>();
        for(int i = 0; i < 6; i++) {
            if(i == 0)
                goalState.add("*abcde");
            else
                goalState.add("");
        }

        List<String> initState = new ArrayList<>();
        initState.add("*");
        initState.add("e");
        initState.add("");
        initState.add("bca");
        initState.add("");
        initState.add("d");

        List<Action> actions = possibleActions(yard, initState);
        printYard(yard, initState, goalState, actions);
    }

    public static void testYard2() {
        // set yard
        List<List<Integer>> yard = new ArrayList<>();
        yard.add(Arrays.asList(1,2));
        yard.add(Arrays.asList(1,5));
        yard.add(Arrays.asList(2,3));
        yard.add(Arrays.asList(2,4));

        List<String> goalState = new ArrayList<>();
        for(int i = 0; i < 5; i++) {
            if(i == 0)
                goalState.add("*abcde");
            else
                goalState.add("");
        }

        List<String> initState = new ArrayList<>();
        initState.add("*");
        initState.add("d");
        initState.add("b");
        initState.add("ae");
        initState.add("c");

        List<Action> actions = possibleActions(yard, initState);
        printYard(yard, initState, goalState, actions);
    }

    public static void testYard3() {
        // set yard
        List<List<Integer>> yard = new ArrayList<>();
        yard.add(Arrays.asList(1,2));
        yard.add(Arrays.asList(1,3));

        List<String> goalState = new ArrayList<>();
        for(int i = 0; i < 3; i++) {
            if(i == 0)
                goalState.add("*ab");
            else
                goalState.add("");
        }

        List<String> initState = new ArrayList<>();
        initState.add("*");
        initState.add("a");
        initState.add("b");

        List<Action> actions = possibleActions(yard, initState);
        printYard(yard, initState, goalState, actions);
    }

    public static void testYard4() {
        // set yard
        List<List<Integer>> yard = new ArrayList<>();
        yard.add(Arrays.asList(1,2));
        yard.add(Arrays.asList(1,3));
        yard.add(Arrays.asList(1,4));

        List<String> goalState = new ArrayList<>();
        for(int i = 0; i < 4; i++) {
            if(i == 0)
                goalState.add("*abcd");
            else
                goalState.add("");
        }

        List<String> initState = new ArrayList<>();
        initState.add("*");
        initState.add("a");
        initState.add("bc");
        initState.add("d");

        List<Action> actions = possibleActions(yard, initState);
        printYard(yard, initState, goalState, actions);

    }

    public static void testYard5() {
        List<List<Integer>> yard = new ArrayList<>();
        yard.add(Arrays.asList(1,2));
        yard.add(Arrays.asList(1,3));
        yard.add(Arrays.asList(1,4));

        List<String> goalState = new ArrayList<>();
        for(int i = 0; i < 4; i++) {
            if(i == 0)
                goalState.add("*abcd");
            else
                goalState.add("");
        }

        List<String> initState = new ArrayList<>();
        initState.add("*");
        initState.add("a");
        initState.add("cb");
        initState.add("d");

        List<Action> actions = possibleActions(yard, initState);
        printYard(yard, initState, goalState, actions);
    }
    // helper fxn for tests
    public static void printYard(List<List<Integer>> yard, List<String> initState, List<String> goalState, List<Action> actions) {
        System.out.println("yard : " + yard);
        System.out.println("Initial State : " + initState);
        System.out.println("Goal State : " + goalState);
        System.out.println("Possible actions : " + actions.toString());
        System.out.println("Possible next states : " + expand(yard, initState));
        System.out.println("BFS : " + bfs(yard, initState, goalState));
        //bfs(yard, initState, goalState);

    }


}

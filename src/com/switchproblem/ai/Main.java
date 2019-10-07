/**
 * Amaad Rafi
 * Project 1
 * 10/1/2019
 *
 * Usage - if you would like to add a custom yard, scroll down to one of the yard functions and change/create your own by copy pasting.
 *         * Add a List<List<Integers>> of connected tracks and a List<String> of initial and goal states.
 *
 *         To test problem 4 or 6, goTo testYard() function at the bottom of this file and uncomment the the function you wish to test.
 */

package com.switchproblem.ai;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        int input = -1;
        long startTime, endTime;
        Scanner ui = new Scanner(System.in);

        while(input != 6) {
            System.out.println("\n1. Test Yard 1");
            System.out.println("2. Test Yard 2");
            System.out.println("3. Test Yard 3");
            System.out.println("4. Test Yard 4");
            System.out.println("5. Test Yard 5");
            System.out.println("6. Exit");
            System.out.printf("Input [1-6]: ");

            try{
                input = ui.nextInt();
            }catch (InputMismatchException e) {
                System.out.println("Invalid input");
                ui.next();
            }


            switch (input){
                case 1:
                    testYard1();
                    break;
                case 2:
                    testYard2();
                    break;
                case 3:
                    testYard3();
                    break;
                case 4:
                    testYard4();
                    break;
                case 5:
                    testYard5();
                    break;
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
     * @param yard - list of sublist's (connectivity list)
     * @param state - current position of trains
     * @return actions - list of all possible actions from given state and yard
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

    /**
     * Problem 2 - consumes an Action and a State and produces the new State that
     *             will result after actually carrying out the input move in the input state
     * @param action - object of Action that contains (string direction, int from, int to)
     * @param state  - current position of cars
     * @return node - a Node object that stores the next state and action taken to get to it
     */
    private static Node result(List<String> state, Action action) {
        Node node = new Node(state, action);
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
            node.actionTaken = action;
            node.resultState = carState;
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
            node.actionTaken = action;
            node.resultState = carState;
        }

        return node;
    }

    /**
     * Problem 3 - consumes a State and a Yard, and produces a list of all states that
     *             can be reached in one Action from the given state
     * @param yard  - list of sublist's (connectivity list)
     * @param state - current position of trains
     * @return possibleResults - list of all possible outcomes from give state and yard
     */
    private static List<Node> expand(List<List<Integer>> yard, List<String> state) {
        List<Node> possibleNodes = new ArrayList<>();
        List<Action> actions = possibleActions(yard, state);


        //Node parent = new Node();
        //parent.initialState = state;

        for (Action action : actions)
            if (!result(state, action).equals(state)) {
                Node possibleNode = new Node(result(state, action).actionTaken, result(state, action).resultState);
                possibleNode.initialState = state;
                //possibleNode.previousNode = parent;
                possibleNodes.add(possibleNode);

            }

        return possibleNodes;
    }

    /**
     * Problem 4 - consumes a connectivity list (Yard), an initial State, and a goal State as
     *             inputs, and produces a list of Actions that will take the cars in the initial state into the goal
     *             state.
     *             Algorithm used: Breadth First Search
     *
     * @param yard         - list of sublist's (connectivity list)
     * @param initialState - List of string we start with
     * @param goalState    - List of strings we want to end up with
     * @return actionsToGoal - list of actions needed to get to goal state
     */
    private static List<Action> breadthFirstSearch(List<List<Integer>> yard, List<String> initialState, List<String> goalState) {
        List<Action> actionsTakenToGoal = new ArrayList<Action>();

        if (initialState.equals(goalState))
            return actionsTakenToGoal;

        List<List<String>> correspondingStates = new ArrayList<>();

        HashMap<Node, Node> parentMap = new HashMap<>();

        Queue<Node> frontier = new LinkedList<>();
        Set<List<String>> explored = new HashSet<>();

        Node head = new Node(initialState, null);
        parentMap.put(head, null);
        frontier.add(head);

        while (!frontier.isEmpty()) {
            Node currentNode = frontier.poll();

            /**
             * UNCOMMENT LINE BELOW TO DISPLAY EXPANSION ORDER (note: this will increase compute time)
             */
            // System.out.println("Initial:\t" + currentNode.initialState + "\tResult:\t" + currentNode.resultState);

            if (currentNode.resultState.equals(goalState)) {
                Node backtrackNode = currentNode;

                while(parentMap.get(backtrackNode) != null){
                    actionsTakenToGoal.add(0, backtrackNode.actionTaken);
                    correspondingStates.add(0, backtrackNode.resultState);

                    backtrackNode = parentMap.get(backtrackNode);
                }

                System.out.println("---------------[Path]--------------");
                for(int i = 0; i < correspondingStates.size(); i++)
                    System.out.println(actionsTakenToGoal.get(i) + "\t->\t" + correspondingStates.get(i));
                System.out.println("--------------[/Path]--------------");

                System.out.println("Cost\t\t\t" + actionsTakenToGoal.size());
                return actionsTakenToGoal;
            }

            List<Node> possibleNodes = expand(yard, currentNode.resultState);

            for (Node node : possibleNodes) {

                /**
                 * UNCOMMENT LINE BELOW TO DISPLAY EXPANSION ORDER (note: this will increase compute time)
                 */
                //System.out.println("Initial:\t" + node.initialState + "\tResult:\t" + node.resultState);

                if (!explored.contains(node.resultState)) {
                    parentMap.put(node, currentNode);
                    frontier.add(node);
                    explored.add(node.resultState);
                }
            }
        }

        System.out.println("Goal not reachable.");
        return actionsTakenToGoal;
    }

    /**
     * Problem 6 - consumes a connectivity list (Yard), an initial State, and a goal State as
     *             inputs, and produces a list of Actions that will take the cars in the initial state into the goal
     *             state.
     *             Algorithm used: A*
     *
     *
     * @param yard         - list of sublist's (connectivity list)
     * @param initialState - List of string we start with
     * @param goalState    - List of strings we want to end up with
     * @return actionsToGoal - list of actions needed to get to goal state
     */
    private static List<Action> aStar(List<List<Integer>> yard, List<String> initialState, List<String> goalState) {
        List<Action> actionsTakenToGoal = new ArrayList<Action>();

        if (initialState.equals(goalState))
            return actionsTakenToGoal;

        List<List<String>> correspondingStates = new ArrayList<>();
        HashMap<Node, Node> parentMap = new HashMap<>();

        PriorityQueue<Node> frontier = new PriorityQueue<>((node1, node2) -> (node1.fCost)-(node2.fCost));
        Set<List<String>> explored = new HashSet<>();

        Node head = new Node(initialState, null);
        int g_cost = 1;
        head.gCost = 0;
        head.hCost = 0;
        head.fCost = head.hCost(head.resultState, goalState) + head.gCost;

        parentMap.put(head, null);
        frontier.add(head);

        while (!frontier.isEmpty()) {
            Node currentNode = frontier.poll();

            if (currentNode.resultState.equals(goalState)) {
                Node backtrackNode = currentNode;

                while(parentMap.get(backtrackNode) != null) {
                    actionsTakenToGoal.add(0, backtrackNode.actionTaken);
                    correspondingStates.add(0, backtrackNode.resultState);

                    backtrackNode = parentMap.get(backtrackNode);
                }

                System.out.println("---------------[Path]--------------");
                for(int i = 0; i < correspondingStates.size(); i++)
                    System.out.println(actionsTakenToGoal.get(i) + "\t->\t" + correspondingStates.get(i));
                System.out.println("--------------[/Path]--------------");

                System.out.println("Cost\t\t\t" + actionsTakenToGoal.size());
                return actionsTakenToGoal;
            }

            List<Node> possibleNodes = expand(yard, currentNode.resultState);

            int counter = 0;
            for (Node node : possibleNodes) {
                node.gCost = g_cost;
                node.hCost = node.hCost(node.resultState, goalState);
                node.fCost = node.hCost + node.gCost;

                /**
                 * UNCOMMENT LINE BELOW TO DISPLAY EXPANSION ORDER + COST DATA (note: this will increase compute time)
                 */
                //System.out.println("Initial:\t" + node.initialState + "\tResult:\t" + node.resultState + "\tgCost:\t" + node.gCost + "\thCost:\t" + node.hCost + "\tfCost:\t" + node.fCost);

                if (!explored.contains(node.resultState)) {
                    parentMap.put(node, currentNode);
                    frontier.add(node);
                    explored.add(node.resultState);
                }

                if(counter == possibleNodes.size() - 1)
                    g_cost++;

                counter++;
            }
        }

        System.out.println("Goal not reachable.");
        System.out.println("Nodes explored: " + actionsTakenToGoal.size());
        return actionsTakenToGoal;
    }

    /**
     * HelperFxn - used to find the index of the string that contains the engine (*)
     * @param state - list of strings / layout of track
     * @return index of engine + 1 = track
     */
    private static int findEngineTrack(List<String> state){
        int index = -2;

        for (String s: state)
            if(s.contains("*"))
                index = state.indexOf(s);

        return index + 1;
    }

    /**
     * HelperFxn - remove a char from a string
     * @param str
     * @param index
     * @return returns the string after given char is removed
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
        List<List<Integer>> yard1 = new ArrayList<>();
        yard1.add(Arrays.asList(1,2));
        yard1.add(Arrays.asList(1,3));
        yard1.add(Arrays.asList(3,5));
        yard1.add(Arrays.asList(4,5));
        yard1.add(Arrays.asList(2,6));
        yard1.add(Arrays.asList(5,6));

        // fill goal state
        List<String> goalState = new ArrayList<>();
        for(int i = 0; i < 6; i++) {
            if(i == 0)
                goalState.add("*abcde");
            else
                goalState.add("");
        }

        // fill initial state
        List<String> initState = new ArrayList<>();
        initState.add("*");
        initState.add("e");
        initState.add("");
        initState.add("bca");
        initState.add("");
        initState.add("d");

        // test yard / print data
        List<Action> actions = possibleActions(yard1, initState);
        testYard(yard1, initState, goalState, actions, 1);
    }

    public static void testYard2() {
        // set yard
        List<List<Integer>> yard2 = new ArrayList<>();
        yard2.add(Arrays.asList(1,2));
        yard2.add(Arrays.asList(1,5));
        yard2.add(Arrays.asList(2,3));
        yard2.add(Arrays.asList(2,4));

        // fill goal state
        List<String> goalState = new ArrayList<>();
        for(int i = 0; i < 5; i++) {
            if(i == 0)
                goalState.add("*abcde");
            else
                goalState.add("");
        }

        // fill initial state
        List<String> initState = new ArrayList<>();
        initState.add("*");
        initState.add("d");
        initState.add("b");
        initState.add("ae");
        initState.add("c");

        // test yard / print data
        List<Action> actions = possibleActions(yard2, initState);
        testYard(yard2, initState, goalState, actions, 2);
    }

    public static void testYard3() {
        // set yard
        List<List<Integer>> yard3 = new ArrayList<>();
        yard3.add(Arrays.asList(1,2));
        yard3.add(Arrays.asList(1,3));

        // fill goal state
        List<String> goalState = new ArrayList<>();
        for(int i = 0; i < 3; i++) {
            if(i == 0)
                goalState.add("*ab");
            else
                goalState.add("");
        }

        // fill initital state
        List<String> initState = new ArrayList<>();
        initState.add("*");
        initState.add("a");
        initState.add("b");

        // test yard / print data
        List<Action> actions = possibleActions(yard3, initState);
        testYard(yard3, initState, goalState, actions, 3);
    }

    public static void testYard4() {
        // set yard
        List<List<Integer>> yard4 = new ArrayList<>();
        yard4.add(Arrays.asList(1,2));
        yard4.add(Arrays.asList(1,3));
        yard4.add(Arrays.asList(1,4));

        // fill goal state
        List<String> goalState = new ArrayList<>();
        for(int i = 0; i < 4; i++) {
            if(i == 0)
                goalState.add("*abcd");
            else
                goalState.add("");
        }

        // fill initial state
        List<String> initState = new ArrayList<>();
        initState.add("*");
        initState.add("a");
        initState.add("bc");
        initState.add("d");

        // test yard / print data
        List<Action> actions = possibleActions(yard4, initState);
        testYard(yard4, initState, goalState, actions, 4);

    }

    public static void testYard5() {
        List<List<Integer>> yard5 = new ArrayList<>();
        yard5.add(Arrays.asList(1,2));
        yard5.add(Arrays.asList(1,3));
        yard5.add(Arrays.asList(1,4));

        // fill goal state
        List<String> goalState = new ArrayList<>();
        for(int i = 0; i < 4; i++) {
            if(i == 0)
                goalState.add("*abcd");
            else
                goalState.add("");
        }

        // fill initial state
        List<String> initState = new ArrayList<>();
        initState.add("*");
        initState.add("a");
        initState.add("cb");
        initState.add("d");

        // test yard / print data
        List<Action> actions = possibleActions(yard5, initState);
        testYard(yard5, initState, goalState, actions, 5);
    }

    // helper fxn for tests
    public static void testYard(List<List<Integer>> yard, List<String> initState, List<String> goalState, List<Action> actions, int yardNum) {
        // timer vars
        long startTime, endTime;

        System.out.println("\n-----------------------------------");
        System.out.println("Yard " + yardNum + "\t\t\t" + yard);
        System.out.println("Initial\t\t\t" + initState);
        System.out.println("Goal\t\t\t" + goalState);

        // start timer
        startTime = System.nanoTime();

        // ----------------------------------------------------------------------------------------

        /**
         * Problem 4 - Breadth First Search | UNCOMMENT LINE BELOW TO RUN A BFS ON THE YARD
         */

        System.out.println("Sequence(BFS)\t\t" + breadthFirstSearch(yard, initState, goalState));

        /**
         * Problem 6 - A* | UNCOMMENT LINE BELOW TO RUN A*
         */

        //System.out.println("Sequence(A*)\t" + aStar(yard, initState, goalState));

        // ----------------------------------------------------------------------------------------

        // stop timer
        endTime = System.nanoTime();

        System.out.println("Time\t\t\t" + (endTime - startTime) + " ns (" + (endTime - startTime)/1000000 + " ms)");
        System.out.println("-----------------------------------");
    }


}

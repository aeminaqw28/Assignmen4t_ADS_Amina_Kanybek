import java.util.*;

public class Main {

    static Scanner scanner = new Scanner(System.in);

    static Map<String, List<String>> graph = new LinkedHashMap<>();

    // Weighted graph for Dijkstra
    static Map<String, List<int[]>> weightedGraph = new LinkedHashMap<>();
    static Map<String, String> cityIndexToName = new LinkedHashMap<>();

    public static void main(String[] args) {
        buildGraph();
        buildWeightedGraph();

        System.out.println("Choose task:");
        System.out.println("3 - DFS and BFS");
        System.out.println("5 - Dijkstra Shortest Path");
        System.out.print("Your choice: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 3: runDfsAndBfs();   break;
            case 5: runDijkstra();    break;
            default: System.out.println("Invalid choice!");
        }

        scanner.close();
    }

    // Builds the graph from Task 1
    private static void buildGraph() {
        graph.put("A", Arrays.asList("C", "B", "D"));
        graph.put("B", Arrays.asList("A", "C", "E", "G"));
        graph.put("C", Arrays.asList("A", "B", "D"));
        graph.put("D", Arrays.asList("C", "A"));
        graph.put("E", Arrays.asList("G", "F", "B"));
        graph.put("F", Arrays.asList("G", "E"));
        graph.put("G", Arrays.asList("F", "B"));
    }

    // ПРАВИЛЬНЫЙ ГРАФ (твои данные)
    // 0=Edinburgh, 1=Glasgow, 2=Stirling, 3=Perth, 4=Dundee
    private static void buildWeightedGraph() {
        cityIndexToName.put("0", "Edinburgh");
        cityIndexToName.put("1", "Glasgow");
        cityIndexToName.put("2", "Stirling");
        cityIndexToName.put("3", "Perth");
        cityIndexToName.put("4", "Dundee");

        weightedGraph.put("0", new ArrayList<>());
        weightedGraph.put("1", new ArrayList<>());
        weightedGraph.put("2", new ArrayList<>());
        weightedGraph.put("3", new ArrayList<>());
        weightedGraph.put("4", new ArrayList<>());

        // ТВОИ ПРАВИЛЬНЫЕ РЁБРА И ВЕСА
        addRoad("0", "1", 70);   // Edinburgh - Glasgow
        addRoad("0", "2", 50);   // Edinburgh - Stirling
        addRoad("0", "3", 100);  // Edinburgh - Perth
        addRoad("1", "2", 50);   // Glasgow - Stirling
        addRoad("2", "3", 40);   // Stirling - Perth
        addRoad("3", "4", 60);   // Perth - Dundee
    }

    // Undirected weighted edge between two cities
    private static void addRoad(String cityA, String cityB, int distance) {
        weightedGraph.get(cityA).add(new int[]{Integer.parseInt(cityB), distance});
        weightedGraph.get(cityB).add(new int[]{Integer.parseInt(cityA), distance});
    }

    // TASK 3 - DFS and BFS
    private static void runDfsAndBfs() {
        System.out.print("Enter start node (default A): ");
        String startNode = scanner.nextLine().trim().toUpperCase();
        if (startNode.isEmpty()) startNode = "A";

        System.out.println("\nDFS result from " + startNode + ":");
        depthFirstSearch(startNode);

        System.out.println("\nBFS result from " + startNode + ":");
        breadthFirstSearch(startNode);
    }

    // DFS using Stack
    private static void depthFirstSearch(String startNode) {
        Set<String> visited = new LinkedHashSet<>();
        Stack<String> stack = new Stack<>();

        stack.push(startNode);

        while (!stack.isEmpty()) {
            String current = stack.pop();

            if (visited.contains(current)) continue;

            visited.add(current);

            List<String> neighbors = graph.get(current);
            if (neighbors != null) {
                for (int index = neighbors.size() - 1; index >= 0; index--) {
                    String neighbor = neighbors.get(index);
                    if (!visited.contains(neighbor)) {
                        stack.push(neighbor);
                    }
                }
            }
        }

        System.out.println("Order: " + String.join(" -> ", visited));
    }

    // BFS using Queue
    private static void breadthFirstSearch(String startNode) {
        Set<String> visited = new LinkedHashSet<>();
        Queue<String> queue = new LinkedList<>();

        queue.add(startNode);
        visited.add(startNode);

        while (!queue.isEmpty()) {
            String current = queue.poll();

            List<String> neighbors = graph.get(current);
            if (neighbors != null) {
                for (String neighbor : neighbors) {
                    if (!visited.contains(neighbor)) {
                        visited.add(neighbor);
                        queue.add(neighbor);
                    }
                }
            }
        }

        System.out.println("Order: " + String.join(" -> ", visited));
    }

    // TASK 5 - Dijkstra's algorithm
    private static void runDijkstra() {
        System.out.println("\nCities:");
        for (Map.Entry<String, String> entry : cityIndexToName.entrySet()) {
            System.out.println(entry.getKey() + " - " + entry.getValue());
        }

        System.out.print("Enter start city number (0-4): ");
        String source = scanner.nextLine().trim();
        System.out.print("Enter destination city number (0-4): ");
        String destination = scanner.nextLine().trim();

        findShortestPath(source, destination);
    }

    // Dijkstra using PriorityQueue
    private static void findShortestPath(String source, String destination) {
        int totalCities = weightedGraph.size();

        int[] distances = new int[totalCities];
        int[] previousCity = new int[totalCities];
        boolean[] visited = new boolean[totalCities];

        Arrays.fill(distances, Integer.MAX_VALUE);
        Arrays.fill(previousCity, -1);

        int sourceIndex = Integer.parseInt(source);
        distances[sourceIndex] = 0;

        PriorityQueue<int[]> priorityQueue = new PriorityQueue<>(
                Comparator.comparingInt(entry -> entry[1])
        );
        priorityQueue.add(new int[]{sourceIndex, 0});

        while (!priorityQueue.isEmpty()) {
            int[] current = priorityQueue.poll();
            int currentCity = current[0];
            int currentDistance = current[1];

            if (visited[currentCity]) continue;
            visited[currentCity] = true;

            // Stop early if we reached destination
            if (currentCity == Integer.parseInt(destination)) break;

            for (int[] road : weightedGraph.get(String.valueOf(currentCity))) {
                int neighborCity = road[0];
                int roadDistance = road[1];

                int newDistance = currentDistance + roadDistance;
                if (newDistance < distances[neighborCity]) {
                    distances[neighborCity] = newDistance;
                    previousCity[neighborCity] = currentCity;
                    priorityQueue.add(new int[]{neighborCity, newDistance});
                }
            }
        }

        printShortestPath(source, destination, distances, previousCity);
    }

    // Reconstructs and prints the path from source to destination
    private static void printShortestPath(String source, String destination,
                                          int[] distances, int[] previousCity) {
        int destIndex = Integer.parseInt(destination);
        String sourceName = cityIndexToName.get(source);
        String destName   = cityIndexToName.get(destination);

        if (distances[destIndex] == Integer.MAX_VALUE) {
            System.out.println("No path found from " + sourceName + " to " + destName);
            return;
        }

        // Reconstruct path by walking backwards through previousCity
        List<String> path = new ArrayList<>();
        int current = destIndex;
        while (current != -1) {
            path.add(0, cityIndexToName.get(String.valueOf(current)));
            current = previousCity[current];
        }

        System.out.println("\nShortest path from " + sourceName + " to " + destName + ":");
        System.out.println("Route: " + String.join(" -> ", path));
        System.out.println("Total distance: " + distances[destIndex] + " miles");
    }
}
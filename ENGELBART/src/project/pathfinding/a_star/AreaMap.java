
package project.pathfinding.a_star;

import java.awt.Point;
import java.util.ArrayList;

import project.datastructure.Cell;
import project.datastructure.Map;



/**
 * The AreaMap holds information about the With, Height, 
 * Start position, Goal position and Obstacles on the map.
 * A place on the map is referred to by it's (x,y) coordinates, 
 * where (0,0) is the upper left corner, and x is horizontal and y is vertical.
 */
public class AreaMap {

        private int mapWidth;
        private int mapHeight;
        private ArrayList<ArrayList<Node>> map;
        private int startLocationX = 0;
        private int startLocationY = 0;
        private int goalLocationX = 0;
        private int goalLocationY = 0;
        private Map obstacleMap ;

        
        
        /**
         * Class constructor specifying the With and Height of a otherwise empty map.
         * (no start and goal location or obstacles)
         * @param mapWith
         * @param mapHeight
         */
        public AreaMap(int mapWith, int mapHeight) {
                this.mapWidth = mapWith;
                this.mapHeight = mapHeight;
                
                createMap();
           
        }
        
        /**
         * Class constructor specifying the With, Height and Obstacles of the map.
         * (no start and goal location)
         * The Obstacle 2D array map can be any With and Height
         * @param mapWith               the with of the map as int
         * @param mapHeight             the Height of the map as int
         * @param obstacleMap   a 2D int array map of the obstacles on the map. '1' is obstacle, '0' is not.
         */
        public AreaMap(Map obstacleMap) {
                this.mapWidth = obstacleMap.getWidth();
                this.mapHeight = obstacleMap.getHeight();
                this.obstacleMap = obstacleMap;
                createMap();
      
        }
        
        /**
         * Sets up the Nodes of the map with the With and Height specified in the constructor
         * or set methods.
         */
        private void createMap() {
                Node node;
                map = new ArrayList<ArrayList<Node>>();
                for (int x=0; x<mapWidth; x++) {
                        map.add(new ArrayList<Node>());
                        for (int y=0; y<mapHeight; y++) {
                                node = new Node(x,y,this);
                                try {
                                        if (obstacleMap.getValue(y, x) ==Cell.OBSTACLE || obstacleMap.isCloseToWall(y, x, 0.4))
                                                node.setObstacle(true);
                                } catch (Exception e) {}
                                map.get(x).add(node);
                        }
                }
        }

        public ArrayList<ArrayList<Node>> getNodes() {
                return map;
        }
        public void setObstacle(int x, int y, boolean isObstical) {
                map.get(x).get(y).setObstacle(isObstical);
        }

        public Node getNode(int x, int y) {
                return map.get(x).get(y);
        }

        public void setStartLocation(int x, int y) {
                map.get(startLocationX).get(startLocationY).setStart(false);
                map.get(x).get(y).setStart(true);
                startLocationX = x;
                startLocationY = y;
        }

        public void setGoalLocation(int x, int y) {
                map.get(goalLocationX).get(goalLocationY).setGoal(false);
                map.get(x).get(y).setGoal(true);
                goalLocationX = x;
                goalLocationY = y;
        }

        public int getStartLocationX() {
                return startLocationX;
        }

        public int getStartLocationY() {
                return startLocationY;
        }
        
        public Node getStartNode() {
                return map.get(startLocationX).get(startLocationY);
        }

        public int getGoalLocationX() {
                return goalLocationX;
        }

        public int getGoalLocationY() {
                return goalLocationY;
        }
        
        public Point getGoalPoint() {
                return new Point(goalLocationX, goalLocationY);
        }
        
        /**
         * @return Node The Goal Node
         * @see Node
         */
        public Node getGoalNode() {
                return map.get(goalLocationX).get(goalLocationY);
        }
        
        /**
         * Determine the distance between two neighbor Nodes 
         * as used by the AStar algorithm.
         * 
         * @param node1 any Node
         * @param node2 any of Node1's neighbors
         * @return Float - the distance between the two neighbors
         */
        public float getDistanceBetween(Node node1, Node node2) {
                //if the nodes are on top or next to each other, return 1
                if (node1.getX() == node2.getX() || node1.getY() == node2.getY()){
                        return 1;//*(mapHeight+mapWith);
                } else { //if they are diagonal to each other return diagonal distance: sqrt(1^2+1^2)
                        return (float) Math.sqrt(2);//*(mapHeight+mapWith);
                }
        }
        
        public int getMapWith() {
                return mapWidth;
        }
        public int getMapHeight() {
                return mapHeight;
        }
        
        /**
         * Removes all the map information about start location, goal location and obstacles.
         * Then remakes the map with the original With and Height. 
         */
        public void clear() {
                startLocationX = 0;
                startLocationY = 0;
                goalLocationX = 0;
                goalLocationY = 0;
                createMap();
        }

}
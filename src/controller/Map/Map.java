package controller.Map;

import agents.Agent;
import agents.Guard;
import agents.Intruder;
import agents.TestAgent;
import controller.Area;
import controller.GraphicsConnector;
import controller.Map.tiles.*;
import controller.Scenario;
import controller.TelePortal;
import javafx.scene.paint.Color;
import org.w3c.dom.ls.LSOutput;
import utils.DirectionEnum;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;


public class Map {
    private Tile[][] tiles;
    private Agent agent;
    private GraphicsConnector graphicsConnector;
    private Tile tileVersion;
    private ArrayList<Guard> guards = new ArrayList<>();
    private ArrayList<Intruder> intruders = new ArrayList<>();
    private int horizontalSize;
    private int verticalSize;

    public Agent getAgent() {
        return agent;
    }

    public int getHorizontalSize() {
        return horizontalSize;
    }

    public int getVerticalSize() {
        return verticalSize;
    }

    public Map(int horizontalSize, int verticalSize, Agent agent){
        this.agent = agent;
        this.horizontalSize = horizontalSize;
        this.verticalSize = verticalSize;
        tiles = new Tile[horizontalSize][verticalSize];
        System.out.println(tiles);
    }



    public Map(int row, int col){
        tiles = new Tile[row][col];
    }

    public static Map createEmptyMap(Map map) {
        int horizontalSize = map.getTiles().length;
        int verticalSize = map.getTiles()[0].length;
        return new Map(horizontalSize, verticalSize);
    }

    public void addAgent(Agent agent, int x, int y) {
        getTile(x,y).addAgent(agent);

    }

    public void moveAgentFromTo(Agent agent, int xFrom, int yFrom, int xTo, int yTo){
        Tile tile = getTile(xTo, yTo);
        if(tile.isWalkable()) {
            getTile(xFrom, yFrom).removeAgent();
            getTile(xTo, yTo).addAgent(agent);
            agent.setAgentPosition(getTile(xTo, yTo));
        }
        else{
            throw new RuntimeException("Can not move to tile " + xTo + ", " + yTo);
        }
    }

    public Agent moveAgent(Agent agent, DirectionEnum direction){
        Tile fromTile = agent.getAgentPosition();
        Tile toTile = getTileFromDirection(agent.getAgentPosition(), direction);

        changeTiles(agent, fromTile, toTile);

//        if (checkTeleport(fromTile, toTile)) {
//            int a =0;
//        changeTiles(fromTile, toTile);
//        }

        return agent;
    }

    public void checkTeleport(Tile fromTile, Tile toTile){
        if(toTile.toString().equals("TelePortal")){
            TeleportalTile teleportalTile = (TeleportalTile) getTile(toTile.getX(), toTile.getY());
            int x = teleportalTile.getX();
            int y = teleportalTile.getY();
            fromTile = toTile;
            toTile = getTile(x, y);
        }
    }
    public void changeTiles(Agent agent, Tile fromTile, Tile toTile){
        if(fromTile.isWalkable()) {
            getTile(fromTile.getX(),fromTile.getY()).removeAgent();
            getTile(toTile.getX(),toTile.getY()).addAgent(agent);
            agent.setAgentPosition(toTile);
        }
        else{
            //throw new RuntimeException("Can not move to tile " + toTile.getX() + ", " + toTile.getY());
        }
    }

    private Tile getTileFromDirection(Tile agentPosition, DirectionEnum direction) {
        int x = agentPosition.getX();
        int y = agentPosition.getY();
        if(direction.equals(DirectionEnum.EAST)){
            x++;
        }
        else if(direction.equals(DirectionEnum.WEST)){
            x--;
        }
        else if(direction.equals(DirectionEnum.NORTH)){
            y--;
        }
        else if(direction.equals(DirectionEnum.SOUTH)){
            y++;
        }
        return getTile(x,y);
    }


    public boolean isExplored() {
        for(Tile[] tiles : tiles){
            for(Tile tile : tiles){
                if(!tile.isExploredByDefault()){
                    return false;
                }
            }
        }
        return true;
    }

    public double explored(){
        double notExplored = 0;
        double explored = 0;
        for(Tile[] tiles : tiles){
            for(Tile tile : tiles){
                if(!tile.isExploredByDefault()) {
                    if(tile.getExplored()){
                        explored++;
                    }
                    else{
                        notExplored++;
                    }
                }
                else{
                    explored++;
                }

            }
        }
        return explored/(notExplored+explored);
    }

    public Tile[][] getTiles() {
        return tiles;
    }

    /////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////
    ///////////////////---------INITIALIZATION----------/////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////



    public void loadMap(Scenario scenario){
        int left;
        int right;
        int up;
        int down;
        initializeEmptyMap();

        for (Area area : scenario.getAreas()) {
            left = area.getLeftBoundary();
            right = area.getRightBoundary();
            up = area.getTopBoundary();
            down = area.getBottomBoundary();

            switch (area.getType()) {
                case "targetArea":
                    for (int i = left; i <= right; i++) {
                        for (int j = down; j <= up; j++) {
                            setTile(new TargetArea(i,j));
                        }
                    }
                    break;
                case "spawnAreaIntruders":
                    for (int i = left; i <= right; i++) {
                        for (int j = down; j <= up; j++) {
                            setTile(new SpawnAreaIntruders(i,j));
                        }
                    }
                    break;
                case "spawnAreaGuards":
                    for (int i = left; i <= right; i++) {
                        for (int j = down; j <= up; j++) {
                            setTile(new SpawnAreaGuards(i,j));
                        }
                    }
                    break;
                case "wall":
                    for (int i = left; i <= right; i++) {
                        for (int j = down; j <= up; j++) {
                            setTile(new Wall(i,j));
                        }
                    }
                    break;
                case "shaded":
                    for (int i = left; i <= right; i++) {
                        for (int j = down; j <= up; j++) {
                            setTile(new Shaded(i,j));
                        }
                    }
                    break;
            }
        }

        for(TelePortal telePortal : scenario.getTeleportals()){ // teleportal here because it needs additional parameters
            left = telePortal.getLeftBoundary();
            right = telePortal.getRightBoundary();
            up = telePortal.getTopBoundary();
            down = telePortal.getBottomBoundary();
            for (int i = left; i <= right; i++) {
                for (int j = down; j <= up; j++) {
                    setTile(new TeleportalTile(i, j, telePortal.getxTarget(), telePortal.getyTarget(), telePortal.getOutOrientation()));
                }
            }
        }

        if (scenario.getGameMode() == 0) {
            for (int i = 0; i < scenario.getNumGuards(); i++) {
                spawnGuard(scenario.getSpawnAreaGuards());
            }
        } else if (scenario.getGameMode() == 1) {
            for (int i = 0; i < scenario.getNumGuards(); i++) {
                spawnGuard(scenario.getSpawnAreaGuards());
            }
            for (int i = 0; i < scenario.getNumIntruders(); i++) {
                spawnGuard(scenario.getSpawnAreaIntruders());
            }
        }
    }

    public void spawnGuard(Area givenArea){
        int rand1 = (int) (Math.random() * (givenArea.getRightBoundary() - givenArea.getLeftBoundary())) + givenArea.getLeftBoundary();
        int rand2 = (int) (Math.random() * (givenArea.getBottomBoundary() - givenArea.getTopBoundary())) + givenArea.getTopBoundary();
        Guard tempAgent = new Guard(rand1, rand2);
        tempAgent.setAgentPosition(getTile(rand1,rand2));
        guards.add(tempAgent);
        getTile(rand1, rand2).addAgent(tempAgent);
    }

    public void spawnIntruder(Area givenArea){
        int rand1 = (int) (Math.random() * (givenArea.getRightBoundary() - givenArea.getLeftBoundary())) + givenArea.getLeftBoundary();
        int rand2 = (int) (Math.random() * (givenArea.getBottomBoundary() - givenArea.getTopBoundary())) + givenArea.getTopBoundary();
        Intruder tempAgent = new Intruder(rand1, rand2);
        tempAgent.setAgentPosition(getTile(rand1,rand2));
        intruders.add(tempAgent);
        getTile(rand1, rand2).addAgent(tempAgent);
    }

    private void initializeEmptyMap() {
        for (int i = 0; i < tiles[0].length; i++) {
            for (int j = 0; j < tiles.length; j++) {
                setTile(new Floor(i,j));
            }
        }
    }


    public void printMap(){
        System.out.println(Arrays.deepToString(tiles).replace("], ", "]\n"));
    }

    public ArrayList<Tile> computeVisibleTiles(Agent agent) {
        int d = Scenario.config.getDistanceViewing();
        double angle = agent.getAngle();
        int agentX = agent.getAgentPosition().getX();
        int agentY = agent.getAgentPosition().getY();
        ArrayList<Tile> visibleTiles = new ArrayList<>();
        boolean middleLane = true;
        boolean leftLane = true;
        boolean rightLane = true;
        if(angle==0){
            int topLimit = Math.max(0, agentY-d+1);
            int leftLimit = 0;
            int rightLimit = tiles[0].length-1;

            for(int j = agentY; j>=topLimit && (middleLane || rightLane || leftLane); j--){
                if((agentX+1)<=rightLimit && rightLane){
                    visibleTiles.add(getTile(agentX+1, j));
                    if(!getTile(agentX+1, j).isSeeThrough()){
                        rightLane = false;
                    }
                }
                if(middleLane){
                    visibleTiles.add(getTile(agentX, j));
                    if(!getTile(agentX, j).isSeeThrough()){
                        middleLane = false;
                    }
                }
                if((agentX-1)>=leftLimit && leftLane){
                    visibleTiles.add(getTile(agentX-1, j));
                    if(!getTile(agentX-1, j).isSeeThrough()){
                        leftLane = false;
                    }
                }
            }
            return visibleTiles;

        }
        if(angle==45.0) {
            int topLimit = Math.max(0, agentY - d + 1);
            int leftLimit = Math.max(0, agentX - d + 1);
            int finalI = 0;
            int finalJ = 0;
            for (int i = agentX, j=agentY; i >= leftLimit && j >= topLimit && (middleLane || rightLane || leftLane); i--, j--) {

                if(i!=agentX && rightLane){
                        visibleTiles.add(getTile(i+1, j));
                        if(!getTile(i+1, j).isSeeThrough()){
                            rightLane = false;
                        }
                }
                if(i!=agentX && leftLane){
                    visibleTiles.add(getTile(i, j+1));
                    if(!getTile(i, j+1).isSeeThrough()){
                        leftLane = false;
                    }
                }

                if(middleLane){
                    visibleTiles.add(getTile(i, j));
                    if(!getTile(i,j).isSeeThrough()){
                        middleLane = false;
                    }
                }

                finalI = i;
                finalJ = j;
            }

            if(finalJ!=0 && rightLane){
                visibleTiles.add(getTile(finalI, finalJ-1));
            }
            if(finalI!=0 && leftLane){
                visibleTiles.add(getTile(finalI-1,finalJ));
            }
            return visibleTiles;
        }

        if(angle==90.0){
            int topLimit = 0;
            int bottomLimit = tiles.length-1;
            int leftLimit = Math.max(0, agentX - d + 1);
            for(int i = agentX; i>=leftLimit && (middleLane || rightLane || leftLane); i--){
                if((agentY +1)<=bottomLimit && leftLane){
                    visibleTiles.add(getTile(i, agentY +1));
                    if(!getTile(i, agentY +1).isSeeThrough()){
                        leftLane = false;
                    }
                }
                if(middleLane){
                    visibleTiles.add(getTile(i, agentY));
                    if(!getTile(i, agentY).isSeeThrough()){
                        middleLane = false;
                    }
                }
                if((agentY -1)>=topLimit && rightLane){
                    visibleTiles.add(getTile(i, agentY -1));
                    if(!getTile(i, agentY -1).isSeeThrough()){
                        rightLane = false;
                    }
                }
            }
            return visibleTiles;
        }

        if(angle==135){
            int bottomLimit = Math.max(0, agentY + d - 1);
            int leftLimit = Math.max(0, agentX - d + 1);
            int finalI = 0;
            int finalJ = 0;
            for (int i = agentX, j=agentY; i >= leftLimit && j <= bottomLimit && (middleLane || rightLane || leftLane); i--, j++) {


                if(i!=agentX && rightLane){
                    visibleTiles.add(getTile(i, j-1));
                    if(!getTile(i,j-1).isSeeThrough()){
                        rightLane = false;
                    }
                }
                if(i!=agentX && leftLane){
                    visibleTiles.add(getTile(i+1, j));
                    if(!getTile(i+1,j).isSeeThrough()){
                        leftLane = false;
                    }
                }

                if(middleLane) {
                    visibleTiles.add(getTile(i, j));
                    if(!getTile(i,j).isSeeThrough()){
                        middleLane = false;
                    }
                }
                finalI = i;
                finalJ = j;
            }
            if(finalI!=0 && rightLane){
                visibleTiles.add(getTile(finalI-1,finalJ));
            }
            if(finalJ!=tiles.length-1 && leftLane){
                visibleTiles.add(getTile(finalI, finalJ+1));
            }
            return visibleTiles;
        }

        if(angle==180){
            int bottomLimit = Math.min(tiles.length-1, agentY+d-1);
            int leftLimit = 0;
            int rightLimit = tiles[0].length-1;
            for(int j = agentY; j<=bottomLimit && (middleLane || rightLane || leftLane); j++){
                if((agentX+1)<=rightLimit && leftLane){
                    visibleTiles.add(getTile(agentX+1, j));
                    if(!getTile(agentX+1, j).isSeeThrough()){
                        leftLane = false;
                    }
                }
                if(middleLane){
                    visibleTiles.add(getTile(agentX, j));
                    if(!getTile(agentX, j).isSeeThrough()){
                        middleLane = false;
                    }
                }
                if((agentX-1)>=leftLimit && rightLane){
                    visibleTiles.add(getTile(agentX-1, j));
                    if(!getTile(agentX-1, j).isSeeThrough()){
                        rightLane = false;
                    }
                }
            }
            return visibleTiles;
        }

        if(angle==225){
            int bottomLimit = Math.min(tiles.length-1, agentY + d - 1);
            int rightLimit = Math.min(tiles[0].length, agentX + d - 1);
            int finalI = 0;
            int finalJ = 0;
            for (int i = agentX, j=agentY; i <= rightLimit && j <= bottomLimit && (middleLane || rightLane || leftLane); i++, j++) {


                if(i!=agentX && rightLane){
                    visibleTiles.add(getTile(i-1, j));
                    if(!getTile(i-1,j).isSeeThrough()){
                        rightLane = false;
                    }
                }
                if(i!=agentX && leftLane){
                    visibleTiles.add(getTile(i, j-1));
                    if(!getTile(i,j-1).isSeeThrough()){
                        leftLane = false;
                    }
                }

                if(middleLane) {
                    visibleTiles.add(getTile(i, j));
                    if(!getTile(i,j).isSeeThrough()){
                        middleLane = false;
                    }
                }
                finalI = i;
                finalJ = j;
            }
            if(finalJ!=tiles.length-1 && rightLane){
                visibleTiles.add(getTile(finalI, finalJ+1));
            }
            if(finalI!=tiles[0].length-1 && leftLane){
                visibleTiles.add(getTile(finalI+1,finalJ));
            }
            return visibleTiles;
        }

        if(angle==270.0){
            int topLimit = 0;
            int bottomLimit = tiles.length-1;
            int rightLimit = Math.min(tiles[0].length-1, agentX + d - 1);
            for(int i = agentX; i<=rightLimit && (middleLane || rightLane || leftLane); i++){
                if((agentY +1)<=bottomLimit && rightLane){
                    visibleTiles.add(getTile(i, agentY +1));
                    if(!getTile(i, agentY +1).isSeeThrough()){
                        rightLane = false;
                    }
                }
                if(middleLane){
                    visibleTiles.add(getTile(i, agentY));
                    if(!getTile(i, agentY).isSeeThrough()){
                        middleLane = false;
                    }
                }
                if((agentY -1)>=topLimit && leftLane){
                    visibleTiles.add(getTile(i, agentY -1));
                    if(!getTile(i, agentY -1).isSeeThrough()){
                        leftLane = false;
                    }
                }
            }
            return visibleTiles;
        }
        if(angle==315.0) {
            int topLimit = Math.max(0, agentY - d + 1);
            int rightLimit = Math.min(tiles[0].length, agentX + d - 1);
            int finalI = 0;
            int finalJ = 0;
            for (int i = agentX, j=agentY; i <= rightLimit && j >= topLimit && (middleLane || rightLane || leftLane); i++, j--) {

                if(i!=agentX && rightLane){
                    visibleTiles.add(getTile(i, j+1));
                    if(!getTile(i,j+1).isSeeThrough()){
                        rightLane = false;
                    }
                }
                if(i!=agentX && leftLane){
                    visibleTiles.add(getTile(i-1, j));
                    if(!getTile(i-1,j).isSeeThrough()){
                        leftLane = false;
                    }
                }

                if(middleLane) {
                    visibleTiles.add(getTile(i, j));
                    if(!getTile(i,j).isSeeThrough()){
                        middleLane = false;
                    }
                }
                finalI = i;
                finalJ = j;
            }
            if(finalI!=tiles[0].length-1 && rightLane){
                visibleTiles.add(getTile(finalI+1,finalJ));
            }
            if(finalJ!=0 && leftLane){
                visibleTiles.add(getTile(finalI, finalJ-1));
            }
            return visibleTiles;
        }

        throw new IllegalStateException("The angle of the agent is not a valid discrete value: " + angle);


    }

    public Tile getTile(int x, int y){
        return tiles[y][x];
    }

    public void setTile(int x, int y, Tile tile){
        tiles[y][x] = tile;
    }

    public void setTile(Tile tile){
        tiles[tile.getY()][tile.getX()] = tile;
    }

    public GraphicsConnector getGraphicsConnector() {
        return graphicsConnector;
    }

    /*public void printMap() {
        for(int i = 0; i < tiles.length; i++) {
            for(int j = 0; j < tiles[0].length; j++) {
                System.out.print("[] ");
            }
            System.out.println();
        }
    }*/

    public void setGraphicsConnector(GraphicsConnector graphicsConnector) {
        this.graphicsConnector = graphicsConnector;
    }

    public ArrayList<Guard> getGuards() {return guards;}

    public ArrayList<Intruder> getIntruders() {return intruders;}
}

package controller.Map;

import agents.Agent;
import agents.TestAgent;
import controller.Area;
import controller.Map.tiles.*;
import controller.Scenario;
import controller.TelePortal;
import javafx.scene.paint.Color;
import org.w3c.dom.ls.LSOutput;
import utils.DirectionEnum;


import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;

public class Map {
    private Tile[][] tiles;
    private Agent agent;

    private Tile tileVersion;
    private ArrayList<Agent> guards = new ArrayList<Agent>(); // TODO change to "Guard", "Intruder" later
    private ArrayList<Agent> intruders = new ArrayList<>();

    public Map(int horizontalSize, int verticalSize, Agent agent){
        this.agent = agent;
        tiles = new Tile[horizontalSize][verticalSize];
    }



    public Map(int row, int col){
        tiles = new Tile[row][col];
    }

    public static Map createEmptyMap(Map map) {
        int horizontalSize = map.getTiles().length;
        int verticalSize = map.getTiles().length;
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

    public void moveAgent(Agent agent, String direction){
        Tile fromTile = agent.getAgentPosition();
        Tile toTile = getTileFromDirection(agent.getAgentPosition(), direction);


        changeTiles(fromTile, toTile);

        checkTeleport(fromTile, toTile);


    }

    public void checkTeleport(Tile fromTile, Tile toTile){
        if(toTile.toString().equals("TelePortal")){
            TeleportalTile teleportalTile = (TeleportalTile) getTile(toTile.getX(), toTile.getY());
            int x = teleportalTile.getX();
            int y = teleportalTile.getY();
            fromTile = toTile;
            toTile = getTile(x, y);
            changeTiles(fromTile, toTile);
        }
    }
    public void changeTiles(Tile fromTile, Tile toTile){
        if(fromTile.isWalkable()) {
            getTile(fromTile.getX(),fromTile.getY()).removeAgent();
            getTile(toTile.getX(),toTile.getY()).addAgent(agent);
            agent.setAgentPosition(toTile);
        }
        else{
            throw new RuntimeException("Can not move to tile " + toTile.getX() + ", " + toTile.getY());
        }
    }

    private Tile getTileFromDirection(Tile agentPosition, String direction) {
        int x = agentPosition.getX();
        int y = agentPosition.getY();
        if(direction.equals(DirectionEnum.RIGHT.getDirection())){
            x++;
        }
        else if(direction.equals(DirectionEnum.LEFT.getDirection())){
            x--;
        }
        else if(direction.equals(DirectionEnum.UP.getDirection())){
            y--;
        }
        else if(direction.equals(DirectionEnum.DOWN.getDirection())){
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
                    if (!tile.isExploredByDefault()) {
                        notExplored++;
                    }
                    else{
                        explored++;
                    }
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
        Agent tempAgent = new TestAgent(rand1, rand2);
        guards.add(tempAgent);
        getTile(rand1, rand2).addAgent(tempAgent); // TODO replace with Guard agent later
    }

    public void spawnIntruder(Area givenArea){
        int rand1 = (int) (Math.random() * (givenArea.getRightBoundary() - givenArea.getLeftBoundary())) + givenArea.getLeftBoundary();
        int rand2 = (int) (Math.random() * (givenArea.getBottomBoundary() - givenArea.getTopBoundary())) + givenArea.getTopBoundary();
        // tiles[rand1][rand2].addAgent(new Intruder(rand1, rand2));
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
        double angle = 270.0;//agent.getAngle();
        int agentX = agent.getAgentPosition().getX();
        int agentY = agent.getAgentPosition().getY();
        ArrayList<Tile> visibleTiles = new ArrayList<>();

        if(angle==0){
            int topLimit = Math.max(0, agentY-d+1);
            int leftLimit = Math.max(0,agentX-1);
            int rightLimit = Math.min(tiles[0].length-1, agentX+1);

            for(int i=leftLimit; i<=rightLimit; i++){
                for(int j=agentY; j>=topLimit; j--){
                    if(getTile(i,j).isSeeThrough()){
                        visibleTiles.add(getTile(i,j));
                    }
                    else{
                        break;
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
            for (int i = agentX, j=agentY; i >= leftLimit && j >= topLimit; i--, j--) {

                if(i!=agentX){
                    visibleTiles.add(getTile(i+1, j));
                    visibleTiles.add(getTile(i, j+1));
                }
                visibleTiles.add(getTile(i, j));
                finalI = i;
                finalJ = j;
            }
            if(finalI!=0){
                visibleTiles.add(getTile(finalI-1,finalJ));
            }
            if(finalJ!=0){
                visibleTiles.add(getTile(finalI, finalJ-1));
            }
            return visibleTiles;
        }

        if(angle==90.0){
            int topLimit = Math.max(0, agentY-1);
            int bottomLimit = Math.min(tiles.length-1, agentY+1);
            int leftLimit = Math.max(0, agentX - d + 1);
            for(int j=bottomLimit; j>=topLimit; j--){
                for(int i=agentX; i>=leftLimit; i--){
                    if(getTile(i,j).isSeeThrough()){
                        visibleTiles.add(getTile(i,j));
                    }
                    else{
                        break;
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
            for (int i = agentX, j=agentY; i >= leftLimit && j <= bottomLimit; i--, j++) {


                if(i!=agentX){
                    visibleTiles.add(getTile(i+1, j));
                    visibleTiles.add(getTile(i, j-1));
                }
                visibleTiles.add(getTile(i, j));
                finalI = i;
                finalJ = j;
            }
            if(finalI!=0){
                visibleTiles.add(getTile(finalI-1,finalJ));
            }
            if(finalJ!=tiles.length-1){
                visibleTiles.add(getTile(finalI, finalJ+1));
            }
            return visibleTiles;
        }

        if(angle==180){
            int  bottomLimit = Math.min(tiles.length-1, agentY+d-1);
            int leftLimit = Math.max(0,agentX-1);
            int rightLimit = Math.min(tiles[0].length-1, agentX+1);
            for(int i=leftLimit; i<=rightLimit; i++){
                for(int j=agentY; j<=bottomLimit; j++){
                    if(getTile(i,j).isSeeThrough()){
                        visibleTiles.add(getTile(i,j));
                    }
                    else{
                        break;
                    };

                }
            }
            return visibleTiles;
        }

        if(angle==225){
            int bottomLimit = Math.min(tiles.length-1, agentY + d - 1);
            int rightLimit = Math.min(tiles[0].length, agentX + d - 1);
            int finalI = 0;
            int finalJ = 0;
            for (int i = agentX, j=agentY; i <= rightLimit && j <= bottomLimit; i++, j++) {


                if(i!=agentX){
                    visibleTiles.add(getTile(i-1, j));
                    visibleTiles.add(getTile(i, j-1));
                }
                visibleTiles.add(getTile(i, j));
                finalI = i;
                finalJ = j;
            }
            if(finalI!=tiles[0].length-1){
                visibleTiles.add(getTile(finalI+1,finalJ));
            }
            if(finalJ!=tiles.length-1){
                visibleTiles.add(getTile(finalI, finalJ+1));
            }
            return visibleTiles;
        }

        if(angle==270.0){
            int topLimit = Math.max(0, agentY-1);
            int bottomLimit = Math.min(tiles.length-1, agentY+1);
            int rightLimit = Math.min(tiles[0].length-1, agentX + d - 1);
            for(int j=bottomLimit; j>=topLimit; j--){
                for(int i=agentX; i<=rightLimit; i++){
                    if(getTile(i,j).isSeeThrough()){
                        visibleTiles.add(getTile(i,j));
                    }
                    else{
                        break;
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
            for (int i = agentX, j=agentY; i <= rightLimit && j >= topLimit; i++, j--) {

                if(i!=agentX){
                    visibleTiles.add(getTile(i-1, j));
                    visibleTiles.add(getTile(i, j+1));
                }
                visibleTiles.add(getTile(i, j));
                finalI = i;
                finalJ = j;
            }
            if(finalI!=tiles[0].length-1){
                visibleTiles.add(getTile(finalI+1,finalJ));
            }
            if(finalJ!=0){
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
}

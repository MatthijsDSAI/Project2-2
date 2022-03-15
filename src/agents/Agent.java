package agents;

import controller.Area;
import controller.Map.Map;
import controller.Map.tiles.Tile;
import controller.Scenario;
import javafx.scene.paint.Color;
import utils.Utils;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public abstract class Agent{
    double audiostdeviation;
    int x_position,y_position, angle;
    public String a_name;
    double baseSpeed, range, visangle, visibility,restTime,sprintTime, turn_speed, noiseProd;
    public Map ownMap;
    //not to be used in agent class
    private Tile agentPosition;

    public Agent(Tile agentpos){
        this.agentPosition = agentpos;
        this.a_name = "Agent";
        this.baseSpeed = Scenario.config.getBASESPEEDGUARD();
        this.audiostdeviation=10;
        angle=180;
    }

    public Agent(int x_position, int y_position)
    {
        this.x_position = x_position;
        this.y_position = y_position;
        this.a_name = "Agent";
        this.baseSpeed = Scenario.config.getBASESPEEDGUARD();
        this.audiostdeviation=10;
        angle=180;
    }

    public Agent(double baseSpeed, int x_position, int y_position, int angle)
    {
        this.a_name="Agent";
        this.baseSpeed = baseSpeed;
        this.x_position = x_position;
        this.y_position = y_position;
        this.audiostdeviation=10;
        this.angle=angle;
    }

    public Agent(String name, double baseSpeed, int x_position, int y_position, int angle)
    {
        this.a_name=name;
        this.baseSpeed = baseSpeed;
        this.audiostdeviation=10;
        this.x_position = x_position;
        this.y_position = y_position;
        this.angle=angle;
    }

    public void setVelocities(double speed, double rest, double sprint_time, double turn_speed, double noise_level)
    {
        this.baseSpeed=speed;
        this.restTime = rest;
        this.sprintTime=sprint_time;
        this.turn_speed= turn_speed;
        this.noiseProd = noise_level;
    }

    public void setVisualcap(double range, double angle, double visibility){
        this.range = range;
        this.visangle = angle;
        this.visibility = visibility;
    }

    //To be done
    public void setAudiocap(){
        this.audiostdeviation=10;
    }

    public void setCommunication(Area[] markers, int[] type){
        //To be done
    }

    public boolean checkarea(int posx, int posy)
    {
        Tile check = agentPosition;
        check.setX(posx);
        check.setY(posy);
        if(check.isWalkable()==true)
            return true;
        else
            return false;
    }

    public void turnNorth()
    {
        rotate(-angle);
    }

    public void turnEast()
    {
        rotate(-angle+90);
    }

    public void turnSouth()
    {
        rotate(-angle+180);
    }

    public void turnWest()
    {
        rotate(-angle-90);
    }


    //I changed this so that it only actually moves forward by one step. So the angle has to be changed beforehand.
    //Also not baseSpeed but 1
    public void move()
    {
        //0  -> north
        if(angle == 0)
        {
            y_position++;
//            checkarea(x_position,y_position);
        }
        //90 -> east
        if(angle == 90)
        {
            x_position++;
            //checkarea(x_position,y_position);
        }
        //180 -> South
        if(angle == 180)
        {
            y_position--;
            //checkarea(x_position,y_position);
        }
        // 270 -> west
        if(angle == 270)
        {
            x_position--;
            //checkarea(x_position,y_position);
        }
        //check relationship between speed and position when related to time
    }

    public void rotate(int angle){
        angle = this.angle + angle;
        angle = Utils.TransFormIntoValidAngle(angle);
    }


    public String runIntoAgent(String agent1, String agent2)
    {
        if(agent1.equals(agent2)) {
            System.out.println("Agent can't run into itself");
            return null;
        }
        double rand = Math.random();
        if(rand%2==0)
            return agent1;
        return agent2;
    }

    public double getAngle(){
        return angle;
    }

    public void initializeEmptyMap(Map map){
        this.ownMap = Map.createEmptyMap(map);
    }

    public void computeVisibleTiles(Map map){
        ArrayList<Tile> visibleTiles = map.computeVisibleTiles(this);

        //Todo: temp just for visualisation
        for(Tile tile : visibleTiles){
            tile.setExplored(true);
            ownMap.setTile(tile.clone());
        }
    }


    public void setAgentPosition(Tile tile){
        agentPosition = tile;
    }

    public Tile getAgentPosition(){
        return agentPosition;
    }

    public int getX_position() {
        return x_position;
    }

    public int getY_position() {
        return y_position;
    }
}

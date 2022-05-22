package utils;

import javafx.scene.paint.Color;


public class Config {
    public boolean TRAINING = false;
    private int VISION = 5;
    private int HEARINGWALKING = 5;
    private int HEARINGSPRINT = 6;
    private int SMELL = 5;
    private double BASESPEEDINTRUDER;
    private double BASESPEEDGUARD;
    private double SPRINTSTEEDINTRUDER;
    private Color agentColor = Color.CYAN;
    public boolean GUI = true;
    public boolean DEBUG = false;
    public long sleep = 200;
    private int[] centerOfTargetArea;
    private int timeStepSize;
    private boolean MOVE = true;
    private int gameMode = 1;

    //store an instance of this in our main gamerunner class eventually
    //Then we can access that instance as a field from other classes and have all these constants in one place
    public Config(){
    }

    public int getDistanceViewing() {
        return VISION;
    }

    public void setDistanceViewing(int VISION) {
        this.VISION = VISION;
    }

    public int getDistanceHearingWalk() {return HEARINGWALKING;}

    public void setDistanceHearingWalk(int HEARINGWALKING) { this.HEARINGWALKING = HEARINGWALKING; }

    public int getDistanceHearingSprint() {return HEARINGSPRINT;}

    public void setDistanceHearingSprint(int HEARINGSPRINT) { this.HEARINGSPRINT = HEARINGSPRINT; }

    public int getDistanceSmell(){return SMELL;}

    public void setDistanceSmell(int smell){this.SMELL=smell;}

    public double getBASESPEEDINTRUDER() {
        return BASESPEEDINTRUDER;
    }

    public void setBASESPEEDINTRUDER(double BASESPEEDINTRUDER) {
        this.BASESPEEDINTRUDER = BASESPEEDINTRUDER;
    }

    public double getBASESPEEDGUARD() {
        return BASESPEEDGUARD;
    }

    public void setBASESPEEDGUARD(double BASESPEEDGUARD) {
        this.BASESPEEDGUARD = BASESPEEDGUARD;
    }

    public double getSPRINTSTEEDINTRUDER() {
        return SPRINTSTEEDINTRUDER;
    }

    public void setSPRINTSTEEDINTRUDER(double SPRINTSTEEDINTRUDER) {
        this.SPRINTSTEEDINTRUDER = SPRINTSTEEDINTRUDER;
    }

    public Color getAgentColor(){
        return agentColor;
    }

    public long getSleep() {
        return sleep;
    }

    public int[] getCenterOfTargetArea(){
        return centerOfTargetArea;
    }

    public void setCenterOfTargetArea(int[] centerOfTargetArea) {
        this.centerOfTargetArea = centerOfTargetArea;
    }

    public int getTimeStepSize(){
        return this.timeStepSize;
    }

    public void computeStepSize(){
        this.timeStepSize = Utils.LcmArray(new int[]{(int) getBASESPEEDGUARD(), (int) getBASESPEEDINTRUDER(), (int) getSPRINTSTEEDINTRUDER()}, 0);
    }
    public boolean isMOVE() {
        return MOVE;
    }

    public void setGameMode(int gameMode) {
        this.gameMode = gameMode;
    }

    public int getGameMode(){
        return gameMode;
    }
    public boolean getTraining(){
        return TRAINING;
    }

}

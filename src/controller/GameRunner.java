package controller;

import GUI.MapGui;
import agents.Agent;
import agents.TestAgent;
import controller.Map.Map;
import utils.Config;
import utils.DirectionEnum;

//idea is to make this the class where we run store everything and have our main loop
public class GameRunner {
    private Map map;
    private final Config config = Scenario.config;
    private Agent agent;
    private MapGui gui;
    private Scenario scenario;
    private int t;

    public GameRunner(Scenario scenario) {
        this.scenario = scenario;
        init();
        GraphicsConnector graphicsConnector = new GraphicsConnector(this);
        gui = new MapGui();
        map.setGraphicsConnector(graphicsConnector);



        if(Scenario.config.GUI){
            try{
                gui.launchGUI(graphicsConnector);
            }
            catch(IllegalStateException e ){
                System.out.println("There has been an issue with the initialization of the GUI");
            }
        }

    }


    public Map getMap() {
        return map;
    }

    public void init(){
        agent = new TestAgent(0,0);
        map = new Map(scenario.getMapHeight()+1, scenario.getMapWidth()+1, agent);

        map.loadMap(scenario);
        int x = 0, y = 0;
        map.addAgent(agent,x,y);
        agent.setAgentPosition(map.getTile(x,y));
        agent.initializeEmptyMap(map);
        agent.computeVisibleTiles(map);
        t = 0;

    }


    //does nothing yet
    public void step(){
        t++;
        //map.moveAgent(agent, DirectionEnum.RIGHT.getDirection());
        //System.out.println(map.getAgentPosition(agent));
        //for(int i =0; i<Scenario.config.getBASESPEEDINTRUDER(); i++){
        map.moveAgent(agent, DirectionEnum.EAST);
       // }
        map.getGraphicsConnector().updateGraphics();
    }


    public void run(){
        boolean explored = false;
        while(!explored){
            try {
                Thread.sleep(100000);
            }
            catch(InterruptedException e){
                System.out.println("Threading issue");
            }
            step();
            explored = map.isExplored();
            System.out.println(map.explored() + " of map has been explored");
            //map.printMap();
        }
    }



    public Agent getAgent() {
        return agent;
    }

    public static void main(String[] args){
        GameRunner g = new GameRunner(new Scenario("textmap.txt"));
    }
}

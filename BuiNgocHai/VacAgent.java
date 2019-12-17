/* Name: Bui Ngoc Hai
 * Class: K17 CS
 * Assignment 1: Vacuum World
 */

package BuiNgocHai;
import agent.*;
import vacworld.*;
import java.util.ArrayList;

public class VacAgent extends Agent{
	
	// status from percept
	private String status;  
	// current state of agent
	private AgentState state = new AgentState();
	// list for history storage
	private ArrayList<AgentState> stateList = new ArrayList<AgentState>();
	private ArrayList<Action> actionList = new ArrayList<Action>();
	// num of dirt sucked
	private int numDirt = 0;
	
	
	//-----------------------------------------------------------------------
	/** Provide a Percept to the agent. This function is called by the
	environment at the beginning of each agent's turn. */ 
	public void see(Percept p){
		 status = p.toString();
	}
	//-----------------------------------------------------------------------
	//Have the agent select its next action to perform
	public Action selectAction(){
	   
	   Action act = null;
	   int max_state = 60;
	   
       if(status.equals("DIRT "))
       	    act = new SuckDirt();
	   else if(status.equals("DIRT OBSTACLE "))
	   	    act = new SuckDirt();
	   else if(stateList.size()>max_state ) 
	   	    act = new ShutOff();
	   else if(status.equals("OBSTACLE ")) // if meet an obstacle (decide turn left or turn right)
	   {  
	       state.obstacle = true;
	       if(stateList.size()>0)
	            stateList.get(stateList.size()-1).obstacle = true;
	       
	       if(stateList.size()<10)
	       {
				act = new TurnRight();
	       }
	       if(stateList.size()>=4 && stateList.get(stateList.size()-2).obstacle) // the previous step also met an obstacle
	       {
	       	    // if agent keep position for 4 steps, it must go back
	       	    if(state.position.equals(stateList.get(stateList.size()-4).position))
	       	    	act = actionList.get(actionList.size()-1);
	       	    else 
	       	    if(actionList.get(actionList.size()-1) instanceof TurnRight){
	       	    	if(state.equals(stateList.get(stateList.size()-3)) )
	       	    	   act = new TurnRight();
	       	    	else act = new TurnLeft();
	       	    }
	       	    else // previous action is turn left  
	       	    if(state.equals(stateList.get(stateList.size()-3)) )
	       	    	   act = new TurnLeft();
	       	    else 
	       	    	act = new TurnRight();
	       }
	       else  // if two previous action in turn are go forward and turn right  
	       	if(actionList.size()>=2&& actionList.get(actionList.size()-1) instanceof TurnRight 
	       	&& actionList.get(actionList.size()-2) instanceof GoForward)
	       {
	       		act = new TurnLeft();
	       }
	      else // if two previous action in turn are go forward and turn left
	      	if(actionList.size()>=2 && actionList.get(actionList.size()-1) instanceof TurnLeft 
	       	&& actionList.get(actionList.size()-2) instanceof GoForward)
	       {
	       		act = new TurnRight();
	       }
	       else // other situations when meet an obstacle, check this position whether passed or not
	       {
	       	   int j=-1;
               for(int i=stateList.size()-2; i>=0 ;i--)
               	   if(state.equals(stateList.get(i))) { //  if passed this position in the past
               	   	  j=i;
               	   	  break;
               }
               if(j>=0 ){
                  if((actionList.get(j) instanceof TurnRight)&& stateList.get(j+1).obstacle)
               	  	 act = new TurnLeft();
               	  else 
               	  if((actionList.get(j) instanceof TurnLeft)&& stateList.get(j+1).obstacle)
               	  	 act = new TurnRight();
               	  else 
               	  if( stateList.size()>=3 &&
               	  	(actionList.get(j) instanceof TurnRight)
               	  	&& !(state.equals(stateList.get(stateList.size()-3))))
               	  	act = new TurnLeft();
               	  else act = new TurnRight();
               }
               else  // default
               	   act = new TurnRight();
	       }
	   }
	   else {  // if no obstacle, no dirt (decide turn left or turn right or go foward)
	   	    
	   	    state.obstacle = false;
	   	    if(stateList.size()>0)
	   	       stateList.get(stateList.size()-1).obstacle = false;  

            //-------------------------------
			for(int i=stateList.size()-2; i>1 ;i--)
               	   if(state.equals(stateList.get(i))) // if passed this position in the past
               	   { 
               	   	   if( stateList.size()>=5 && 
               	   	   	state.position.equals(stateList.get(stateList.size()-5).position))
               	   	   {
               	   	   	         act = new GoForward();
               	   	   	         break;
               	   	   }
               	   	   
               	   	   // try to turn right, check the position if turn right and go foward
               	   	   int newDir = state.dir+1;
               	   	   if(newDir>3)newDir=0;
					   
               	   	   if(visitted(new Coordinate(state.position.x+Direction.DELTA_X[newDir],
               	   	   state.position.y+Direction.DELTA_Y[newDir]))==0
               	   	   	&& !(state.equals(stateList.get(stateList.size()-3))))
               	   	  {
               	   	   	   act = new TurnRight();
               	   	   	   break;
               	   	   }
               	   	   // try to turn left, check the position if turn left and go foward
               	   	   newDir = state.dir-1;
               	   	   if(newDir<0)newDir=3;
               	   	   
               	   	   if(visitted(new Coordinate(state.position.x+Direction.DELTA_X[newDir],
               	   	   state.position.y+Direction.DELTA_Y[newDir]))==0
               	   	   	&&!(state.equals(stateList.get(stateList.size()-3))))
               	   	   {
               	   	   	   act = new TurnLeft();
               	   	   	   break;
               	   	   }
               	   	   break;  
               	   }
            //-------------------------------------------------------------   	     
	   	    if(actionList.size()>=2)
            {    // if two previous action in turn are turn left and go forward 
                 if((actionList.get(actionList.size()-1) instanceof GoForward)
            	     &&(actionList.get(actionList.size()-2) instanceof TurnLeft)
            	     && (visitted(new Coordinate(state.position.x+Direction.DELTA_X[state.dir],
               	   	   state.position.y+Direction.DELTA_Y[state.dir]))>0))
 	    	        act = new TurnRight();
 	    	     
 	    	     // if two previous action in turn are turn right and go forward    
                 if((actionList.get(actionList.size()-1) instanceof GoForward)
            	     &&(actionList.get(actionList.size()-2) instanceof TurnRight)
            	     && (visitted(new Coordinate(state.position.x+Direction.DELTA_X[state.dir],
               	   	   state.position.y+Direction.DELTA_Y[state.dir]))>0))
 	    	        act = new TurnLeft(); 
            }
	   	    if(act == null)act = new GoForward();
	   }
	   //-----------------------------------------------------------------------
	   
	   if(stateList.size()==0)
	   	    stateList.add(new AgentState(state));
       // update agent state and add to the list ---------------------------
	   if(act instanceof TurnRight){
	   	  	state.dir++;
	   	  	if(state.dir>3)state.dir=0;
	   	  	stateList.add(new AgentState(state));
	   	  	actionList.add(act);
	   }
	   else if(act instanceof TurnLeft){
	   	  	state.dir--;
	   	  	if(state.dir<0)state.dir=3;
	   	  	stateList.add(new AgentState(state));
	   	  	actionList.add(act);
	   }
	   else if(act instanceof GoForward){
	   	    state.position.x += Direction.DELTA_X[state.dir];
			state.position.y += Direction.DELTA_Y[state.dir];
			stateList.add(new AgentState(state));
			actionList.add(act);
	   }
	   else if(act instanceof SuckDirt){
            numDirt++;
	   }

	   //System.out.println(state.position.x+" "+state.position.y+" "+state.dir+" "+ stateList.size());
	   
	   return act;
	}
   //--------------------------------------------------------------------
   // return the num of times the Agent visitted given position	 
   public int visitted(Coordinate c){
   	   int count =0;
   	   for(int i=0; i<stateList.size();i++)
   	   	  if(c.equals(stateList.get(i).position)) count++;
   	   return count;
   }
   //--------------------------------------------------------------------
   public String getId(){
   	  return "";
   }
	
}
//**************************************************************************
// internal state of agent 
class AgentState{
	
	public Coordinate position;
	public int dir;
	public boolean obstacle; 
	
	public AgentState(){
		position = new Coordinate();
		dir = Direction.NORTH;
		obstacle = false;
	}
	public AgentState(AgentState s){
		this.position = new Coordinate(s.position);
		this.dir = s.dir;
		this.obstacle = s.obstacle;
	}
	public boolean equals(Object o) {
		if (!(o instanceof AgentState))
			return false;
		AgentState s = (AgentState)o;
		if (s.dir == dir && s.position.equals(position))
			return true;
		else
			return false;
	}
}

//*************************************************************************
// coordinate of agent
class Coordinate{
	
	public int x;
	public int y;
	
	public Coordinate(){
		x=0;
		y=0;
	}
	public Coordinate(int x, int y){
		this.x=x;
		this.y=y;
	}
	public Coordinate(Coordinate c){
		this.x=c.x;
		this.y=c.y;
	}
	
	public boolean equals(Object o) {
		if (!(o instanceof Coordinate))
			return false;
		Coordinate c = (Coordinate)o;
		if (c.x == x && c.y == y)
			return true;
		else
			return false;
	}
}
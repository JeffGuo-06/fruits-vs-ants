import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.ArrayList;

/**
 * Write a description of class Queen here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Queen extends SuperSmoothMover
{
    /**
     * Act - do whatever the Queen wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    private int actCount;
    private boolean turned;
    private int speed;
    private int x;
    private int y;
    private int xMin;
    private int yMin;
    private int xMax;
    private int yMax;
    private int screenWidth;
    private Ant targetAnt;
    private ArrayList<Ant> ants;
    private int targetAntX;
    private int targetAntY;
    private static String state;
    private static double hpMax=100;
    private static double hp=hpMax;
    private SuperStatBar hpStat;
    private GreenfootImage originalImage = new GreenfootImage("images/queenOriginal.png");
    private GreenfootImage damagedImage = new GreenfootImage("images/queenDamaged.png");
    private int damageCounter = 0;

    public Queen(){
        this.hp=hpMax;
        turned = false;
        state="NESTING";
        hpStat = new SuperStatBar ((int)hpMax, 0, this, 100, 12, 80);
        setImage(originalImage);
    }

    protected void addedToWorld(World world) {
        speed=4;
        VehicleWorld vehicleWorld = (VehicleWorld) world;
        world.addObject(hpStat, 0, 0);
        screenWidth = vehicleWorld.screenWidth;
        xMin = (int) ((double) vehicleWorld.screenWidth * 0.6);
        xMax = (int) ((double) vehicleWorld.screenWidth * 0.9);
        yMin = vehicleWorld.TOP_SPAWN + vehicleWorld.getLaneHeight() + vehicleWorld.getSpaceBetweenLanes();
        yMax = vehicleWorld.TOP_SPAWN + (vehicleWorld.getLaneHeight() + vehicleWorld.getSpaceBetweenLanes()) * vehicleWorld.getLaneCount();
        x = xMin + Greenfoot.getRandomNumber(xMax - xMin + 1);
        y = yMin + Greenfoot.getRandomNumber(yMax - yMin + 1);
    }

    public void act()
    {
        actCount++;
        VehicleWorld world = (VehicleWorld) getWorld();
        checkDamage();

        if(state=="REVIVING"&&getX()<screenWidth/2){
            state="NESTING";
            turned=false;
        }

        //Queen creates AntHills.
        if(state=="NESTING"){
            createAntHills();
        }

        //Queen goes around reviving ants
        if(state=="REVIVING"){

            revive();
        }
        hpStat.update ((int)(hp));
    }

    /**
     * Retrieves the current state of the Queen.
     * @return The current state of the Queen as a String.
     */
    public static String getState(){
        return state;
    }

    /**
     * Checks and applies the damage effect to the Queen, reducing her health and triggering damage visuals
     */
    public void checkDamage(){
        if (damageCounter > 0) {
            damageCounter--;
            vibrate((damageCounter/2),2);
            if (damageCounter == 0) {
                setImage(originalImage);
            }
        }
    }

    
    /**
     * In the NESTING state, the Queen moves around the world and creates new ant hills at random locations, 
     * also reviving and giving immunity to nearby downed ants
     */
    public void createAntHills(){
        move(speed); 
        ArrayList<Ant> healableAnts = getDownedAntsInRange(100);  //While creating ant hills, also heal nearby ants
        for(Ant a : healableAnts){
            a.healMe();
            a.giveImmunity(60);
        }
        
        if (turned == false){ //Turn to an initial location
            turnTowards(x,y);
            turned = true;
        }
        //If ant reaches its destination, pick a new random destination
        //System.out.println(x+","+y);
        if(distanceFrom(getX(),getY(),x,y)<=speed+10){
            boolean locationFound = false;
            while(!locationFound){ 
                xMin = (int) ((double) screenWidth*0.6);

                x = xMin + Greenfoot.getRandomNumber(xMax - xMin + 1);
                y = yMin + Greenfoot.getRandomNumber(yMax - yMin + 1); //Pick a random location within the designated area

                if(getWorld().getObjectsAt(x,y,AntHill.class).size()==0){ //If there is no ant hill at the specified location, a location has been found
                    turnTowards(x,y);
                    locationFound=true;
                    getWorld().addObject(new AntHill(),getX(),getY());
                }

            }
        }
    }
    
    /**
     * In the REVIVING state, the Queen targets and revives downed ants, moving towards them and giving them immunity upon revival.
     */
    public void revive(){
        move(speed);

        if (turned == false){
            targetClosestDownedAnt(); //Turn towards the nearest downed ant
            turned = true;
        }
        if(anyAntsDowned()){
            //Check if any ants are healable AKA touching queen, and downed
            ArrayList<Ant> healableAnts = getDownedAntsInRange(100); 
            if(healableAnts.size() == 0){
                //No ants in range;
                targetClosestDownedAnt(); //Turn towards the nearest downed ant
            }
            else{
                //Heal the ants
                for(Ant a : healableAnts){
                    a.healMe();
                    a.giveImmunity(120);
                }
            }

        }
    }


    /**
     * Calculates the distance between two points, defined by the parameters, x1, y1, x2, y2
     */
    private int distanceFrom(int x1, int y1, int x2, int y2){
        int xLength = x2 - x1;
        int yLength = y2 - y1;
        int distance = (int)Math.sqrt(Math.pow(xLength, 2) + Math.pow(yLength, 2));
        return distance;
    }

    /**
     * Checks if there are any downed ants within the world. A downed ant is one that is not currently awake
     * 
     * @return true if there is at least one downed ant in the world, false otherwise.
     */
    private boolean anyAntsDowned(){
        ArrayList<Ant> ants = (ArrayList<Ant>) getWorld().getObjects(Ant.class);
        for(Ant ant : ants){
            if(!ant.isAwake()){
                return true;
            }
        }
        return false;
    }

    /**
     * Modified version of Mr Cohen's flower targetting function from the bug simulator
     */
    private void targetClosestDownedAnt ()
    {
        double closestTargetDistance = 0;
        double distanceToActor;

        ArrayList<Ant> ants = (ArrayList<Ant>)getObjectsInRange(40, Ant.class);
        ArrayList<Ant> downedAnts = new ArrayList<Ant>();

        downedAnts=getDownedAntsInRange(40); //Ants are really close

        if (downedAnts.size() == 0){ 
            downedAnts=getDownedAntsInRange(140);//Ants are close
        } 
        if (downedAnts.size() == 0){
            downedAnts=getDownedAntsInRange(400);//Ants are further
        }
        if (downedAnts.size() == 0){
            downedAnts=getDownedAntsInRange(1000);//Ants are FAR
        }

        if (downedAnts.size() > 0)
        {
            // set the first one as my target
            targetAnt = downedAnts.get(0);
            // Use method to get distance to target. This will be used
            // to check if any other targets are closer
            closestTargetDistance = distanceFrom (this.getX(), this.getY(), targetAnt.getX(), targetAnt.getY());

            // Loop through the objects in the ArrayList to find the closest target
            for (Ant o : downedAnts)
            {
                // Cast for use in generic method
                //Actor a = (Actor) o;
                // Measure distance from me
                distanceToActor = distanceFrom (this.getX(), this.getY(), targetAnt.getX(), targetAnt.getY());
                // If I find an Ant closer than my current target, I will change targets
                if (distanceToActor < closestTargetDistance)
                {
                    targetAnt = o;
                    closestTargetDistance = distanceToActor;
                }
            }
            targetAntX=targetAnt.getX();
            targetAntY=targetAnt.getY();
            turnTowards(targetAnt.getX(), targetAnt.getY());
        }
    }
    
    /**
     * Sets the state
     * @param theState is the new state
     */
    public static void setState(String theState){
        state=theState;
    }
    
    /**
     * Returns an arrayList of all the downed ants in the range.
     * @param range is the range of the search
     * @return the arrayList of all downed ants
     */
    private ArrayList<Ant> getDownedAntsInRange(int range){
        ArrayList<Ant> ants = (ArrayList<Ant>)getObjectsInRange(range, Ant.class);
        ArrayList<Ant> downedAnts = new ArrayList<Ant>();
        for(Ant ant : ants) {
            if(!ant.isAwake()) {
                downedAnts.add(ant);
            }
        }
        return downedAnts;
    }

    /**
     * Calculates the damage to the Queen from an attack, updates her health and image to reflect the damage, and removes her from the world if her health reaches zero.
     * @param dmg The amount of damage inflicted on the Queen.
     */
    public void damage(double dmg){
        setImage(damagedImage);
        hp -= dmg;
        damageCounter = 8; // Adjust this number based on your desired delay length

        if (hp <= 0) {
            // Perform any actions needed when the actor is defeated (e.g., removal from the world)
            getWorld().removeObject(this);
        }

    }
    
    /**
     * Gets the current hp of the Queen
     * @return the current hp of the Queen
     */
    public static double gethp(){
        return hp;
    }

}

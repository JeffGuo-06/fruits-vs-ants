import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.ArrayList;

/**
 * Write a description of class Ant here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Ant extends Pedestrian
{
    /**
     * Act - do whatever the Ant wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */

    private int actCount;
    private GreenfootImage originalImage;
    private int xMin;
    private int xMax;
    private int yMin;
    private int yMax;
    private int x;
    private int y;
    private boolean turned;
    private static double antTerritory;
    private int screenWidth;
    private static int speed;
    private static String state;
    private int lives;
    //Sounds
    private static final int MAX_VOLUME = 85;
    private static final int MIN_VOLUME = 40;
    private Fruit targetFruit;
    private int targetFruitX;
    private int targetFruitY;
    public Ant(int direction){
        super(direction);
        originalImage=getImage();
        originalImage.scale(50,37);
        setImage(originalImage);

        turned = false;
        state="WANDERING";
        lives=10;
        antTerritory=0.2;
    }

    protected void addedToWorld(World world) {
        //Pick a random location in the world
        VehicleWorld vehicleWorld = (VehicleWorld) world;
        xMin = (int) ((double) vehicleWorld.screenWidth * antTerritory);
        xMax = (int) ((double) vehicleWorld.screenWidth * 0.9);
        screenWidth = vehicleWorld.screenWidth;
        yMin = vehicleWorld.TOP_SPAWN + vehicleWorld.getLaneHeight() + vehicleWorld.getSpaceBetweenLanes();
        yMax = vehicleWorld.TOP_SPAWN + (vehicleWorld.getLaneHeight() + vehicleWorld.getSpaceBetweenLanes()) * vehicleWorld.getLaneCount();
        x = xMin + Greenfoot.getRandomNumber(xMax - xMin + 1);
        y = yMin + Greenfoot.getRandomNumber(yMax - yMin + 1);
        turnTowards(x,y);
    }

    public void act()
    {

        if(isAwake()){
            if(immuneActs>0){
                immuneActs-=1;

            }
            else{

                immune=false;
            }
            if(immune==true){

            }
            else{
                deactivateShield();
            }

            if(state=="WANDERING"){
                //Turn the towards a random location once, as it spawns or wakes up
                if (turned == false){
                    turnTowards(x,y);
                    turned = true;
                }
                //If ant reaches its destination, pick a new random destination
                wander();
            }
            else if(state=="ATTACKING"){
                attack();
            }
            move(speed);
            //System.out.println(speed+","+getX());
        }
        else{
            turned=false;
        }
        if(getX()>1024){
            getWorld().removeObject(this);
        }
    }

    /**
     * Moves the ant randomly within its territory when in the wandering state.
     */
    public void wander(){
        if(distanceFrom(getX(),getY(),x,y)<=speed+10){//If destination reached
            //Pick a random location within territory and turn to it
            xMin = (int) ((double) screenWidth * antTerritory);
            x = xMin + Greenfoot.getRandomNumber(xMax - xMin + 1);
            y = yMin + Greenfoot.getRandomNumber(yMax - yMin + 1);
            turnTowards(x,y);
        }
    }

    /**
     * Defines the ant's attacking behavior, particularly targeting and moving towards fruits.
     */
    public void attack(){
        speed=3;
        if(getWorld().getObjects(Fruit.class).size()>0){//If there are fruits in the world
            if(isTouching(Fruit.class)){ //If touching fruit attack it by moving into it
                move(1);
            }
            else{
                targetClosestFruit(); //Otherwise, look for the nearest fruit
            }
        }
    }

    /**
     * Returns the current state of the ant.
     * @return The current state as a string.
     */
    public static String getState(){
        return state;
    }

    /**
     * Handles the ant's response when knocked down, including decrementing lives and checking for game over.
     */
    public void knockDown () {

        if(!immune){//Only trigger if not immune
            speed = 0;
            GreenfootImage currentImage = getImage();
            currentImage.setTransparency(90); //Reduce transparency of ant, it is now downed
            setImage(currentImage);
            //setRotation (direction * 270);
            awake = false;
            lives--;
            if(lives==0){ //If no more lives, the ant officially dies
                getWorld().removeObject(this);
            }
        }

    }

    /**
     * Calculates the distance between two points, defined by the parameters, x1, y1, x2, y2
     */
    public int distanceFrom(int x1, int y1, int x2, int y2){
        int xLength = x2 - x1;
        int yLength = y2 - y1;
        int distance = (int)Math.sqrt(Math.pow(xLength, 2) + Math.pow(yLength, 2));
        return distance;
    }

    /**
     * Sets the territory percentage for the ant within the world.
     * @param territory The percentage of the screen the ant can occupy.
     */
    public static void setAntTerritory(double territory){
        antTerritory = territory;
    }

    /**
     * Sets the speed at which the ant moves.
     * @param theSpeed The new speed of the ant.
     */
    public static void setSpeed(int theSpeed){
        speed = theSpeed;
    }

    /**
     * Sets the current state of the ant.
     * @param theState The new state of the ant.
     */
    public static void setState(String theState){
        state = theState;
    }

    /**
     * Gets the number of lives remaining for the ant.
     * @return The number of lives as an integer.
     */
    public int getLives(){
        return lives;
    }

    /**
     * Modified version of Mr Cohen's flower targetting function from the bug simulator
     */
    private void targetClosestFruit ()
    {
        double closestTargetDistance = 0;
        double distanceToActor;


        ArrayList<Fruit> fruits = (ArrayList<Fruit>)getObjectsInRange(40, Fruit.class);

        fruits=getFruitsInRange(40); //Fruits real close

        if (fruits.size() == 0){
            fruits=getFruitsInRange(140); //Fruits are close
        } 
        if (fruits.size() == 0){
            fruits=getFruitsInRange(400); //Fruits are further
        }
        if (fruits.size() == 0){
            fruits=getFruitsInRange(1000); //Fruits are FAR
        }

        if (fruits.size() > 0)
        {
            // set the first one as my target
            targetFruit = fruits.get(0);
            // Use method to get distance to target. This will be used
            // to check if any other targets are closer
            closestTargetDistance = distanceFrom (this.getX(), this.getY(), targetFruit.getX(), targetFruit.getY());

            // Loop through the objects in the ArrayList to find the closest target
            for (Fruit o : fruits)
            {
                // Cast for use in generic method
                //Actor a = (Actor) o;
                // Measure distance from me
                distanceToActor = distanceFrom (this.getX(), this.getY(), targetFruit.getX(), targetFruit.getY());
                // If I find a Fruit closer than my current target, I will change
                // targets
                if (distanceToActor < closestTargetDistance)
                {
                    targetFruit = o;
                    closestTargetDistance = distanceToActor;
                }
            }
            targetFruitX=targetFruit.getX();
            targetFruitY=targetFruit.getY();
            turnTowards(targetFruit.getX(), targetFruit.getY());
        }
    }

    /**
     * Retrieves a list of fruits within a specified range from the ant.
     * @param range The range within which to find fruits.
     * @return A list of fruits within the specified range.
     */
    private ArrayList<Fruit> getFruitsInRange(int range){
        ArrayList<Fruit> fruits = (ArrayList<Fruit>)getObjectsInRange(range, Fruit.class);
        return fruits;
    }
}

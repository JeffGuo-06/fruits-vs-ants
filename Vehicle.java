import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.ArrayList;

/**
 * This is the superclass for Vehicles.
 * 
 */
public abstract class Vehicle extends SuperSmoothMover
{
    protected double maxSpeed;
    protected double speed;
    protected int direction; // 1 = right, -1 = left
    protected boolean moving;
    protected boolean isNew;
    protected boolean isNatural;
    protected int yOffset;
    protected VehicleSpawner origin;
    protected int followingDistance;
    protected int myLaneNumber;
    protected boolean removed;
    protected boolean slowed;
    protected abstract boolean checkHitPedestrian ();

    public Vehicle(){
        moving = true;
        speed = maxSpeed;
        isNew = true;
    }
    public Vehicle (VehicleSpawner origin, boolean natural) {
        // remember the VehicleSpawner I came from. This includes information
        // about which lane I'm in and which direction I should face
        this.origin = origin;
        moving = true;
        // ask the Spawner that spawned me what my lane number is
        myLaneNumber = origin.getLaneNumber();
        // Determine if this lane is facing towards the right and
        // set the direction accordingly
        if (origin.facesRightward()){ // Right facing vehicles
            direction = 1;        
        } else { // left facing Vehicles
            direction = -1;
            // Reverse the image so it appears correct when moving the opposite direction
            getImage().mirrorHorizontally();
        }
        // If speed modifiers were set for lanes, this will change the max speed
        // accordingly. If speed modifiers are not set, this multiplies by 1.0 (as in,
        // does nothing).
        maxSpeed *= origin.getSpeedModifier();
        speed = maxSpeed;
        isNew = false;
        isNatural = natural;
    }
    public Vehicle (VehicleSpawner origin) {
        // remember the VehicleSpawner I came from. This includes information
        // about which lane I'm in and which direction I should face
        this.origin = origin;
        moving = true;
        // ask the Spawner that spawned me what my lane number is
        myLaneNumber = origin.getLaneNumber();
        // Determine if this lane is facing towards the right and
        // set the direction accordingly
        if (origin.facesRightward()){ // Right facing vehicles
            direction = 1;        
        } else { // left facing Vehicles
            direction = -1;
            // Reverse the image so it appears correct when moving the opposite direction
            getImage().mirrorHorizontally();
        }
        // If speed modifiers were set for lanes, this will change the max speed
        // accordingly. If speed modifiers are not set, this multiplies by 1.0 (as in,
        // does nothing).
        maxSpeed *= origin.getSpeedModifier();
        speed = maxSpeed;
        isNew = true;
    }
    
    

    /**
     * This method is called automatically when the Vehicle is added to the World, and places
     * the Vehicle just off screen (centered 100 pixels beyond the center of the lane spawner)
     * so it will appear to roll onto the screen smoothly.
     */
    public void addedToWorld (World w){
        if(this.origin != null){ 
            if(isNatural){
                setLocation (getX(), origin.getY() - yOffset);
                isNew = false;            }
            else if(isNew){
                setLocation (origin.getX() + (-100), origin.getY() - yOffset);
                isNew = false;

            }
        }
    }

    /**
     * The superclass Vehicle's act() method. This can be called by a Vehicle subclass object 
     * (for example, by a Car) in two ways:
     * - If the Vehicle simply does NOT have a method called public void act(), this will be called
     *   instead. 
     * - subclass' act() method can invoke super.act() to call this, as is demonstrated here.
     */
    public void act () {
        drive(); 
        if (!checkHitPedestrian()){
        }

        if (checkEdge()){
            removed=true;
        }
        if(removed){
            getWorld().removeObject(this);
            return;
        }
    }
    
    /**
     * A method used by all Vehicles to check if they are at the edge.
     * 
     * Note that this World is set to unbounded (The World's super class is (int, int, int, FALSE) which means
     * that objects should not be stopped from leaving the World. However, this introduces a challenge as there
     * is the potential for objects to disappear off-screen but still be fully acting and thus wasting resources
     * and affecting the simulation even though they are not visible.
     */
    protected boolean checkEdge() {
        if (direction == 1)
        { // if moving right, check 200 pixels to the right (above max X)
            if (getX() > getWorld().getWidth() + 200){
                return true;
            }
        } 
        else 
        { // if moving left, check 200 pixels to the left (negative values)
            if (getX() <= -200){
                return true;
            }
        }
        return false;
    }

    // The Repel Pedestrian Experiment - Currently a work in Progress (Feb 2023)
    public void repelPedestrians() {
        ArrayList<Pedestrian> pedsTouching = (ArrayList<Pedestrian>)getIntersectingObjects(Pedestrian.class);
        pushAwayFromObjects(pedsTouching, this.getImage().getHeight()/2);
    }

    // The Repel Pedestrian Experiment (primary method) - Currently a work in Progress (Feb 2023)
    public void pushAwayFromObjects(ArrayList<Pedestrian> nearbyObjects, double minDistance) {

        // Get the current position of this actor
        int currentX = getX();
        int currentY = getY();

        // Iterate through the nearby objects
        for (Pedestrian object : nearbyObjects) {
            if (!object.isAwake()){ continue;}
            // Get the position of the nearby object
            int objectX = object.getX();
            int objectY = object.getY();

            // Calculate the distance between this actor and the nearby object
            double distance = Math.sqrt(Math.pow(currentX - objectX, 2) + Math.pow(currentY - objectY, 2));

            // Check if the distance is less than the minimum required distance
            if (distance < minDistance) {
                // Calculate the direction vector from this actor to the nearby object
                int deltaX = objectX - currentX;
                int deltaY = objectY - currentY;

                // Calculate the unit vector in the direction of the nearby object
                double length = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
                double unitX = deltaX / length;
                double unitY = deltaY / length;

                // Calculate the amount by which to push the nearby object
                double pushAmount = minDistance - distance;

                // Update the position of the nearby object to push it away
                object.setLocation(objectX, objectY + (int)(pushAmount * unitY));
                // removed from line above, objectX ==> + (int)(pushAmount * unitX)
            }
        }
    }

    /**
     * Method that deals with movement. Speed can be set by individual subclasses in their constructors
     */
    public void drive() 
    {
        // Ahead is a generic vehicle - we don't know what type BUT
        // since every Vehicle "promises" to have a getSpeed() method,
        // we can call that on any vehicle to find out it's speed
        Vehicle ahead = (Vehicle) getOneObjectAtOffset (direction * (int)(speed + getImage().getWidth()/2 + 6), 0, Vehicle.class);
        double otherVehicleSpeed = -1;
        if (ahead != null && myLaneNumber == ahead.getLaneNumber()) {
            
            otherVehicleSpeed = ahead.getSpeed();
        }

        // Various things that may slow down driving speed 
        // You can ADD ELSE IF options to allow other 
        // factors to reduce driving speed.

        if (otherVehicleSpeed >= 0 && otherVehicleSpeed < maxSpeed){ // Vehicle ahead is slower?
            speed = otherVehicleSpeed;
            slowed=true;
        } else {
            speed = maxSpeed; // nothing impeding speed, so go max speed
            slowed=false;
        }
        setLocation (getPreciseX() + speed, getPreciseY());
        //System.out.println("v:" + speed);
    }   
    
    /**
     * Objects are pushed based on their inertia
     */
    public void push(int speed){
        
        double inertia = 0.3;
        double movement = speed * inertia;
        setLocation(getX()-movement,getY());
        //System.out.println("pushed");
    }
    /**
     * Gets the lane number
     */
    public int getLaneNumber(){
        return myLaneNumber;
    }
    /**
     * Sets the lane number
     */
    public void setLaneNumber(int laneNumber){
         myLaneNumber=laneNumber;
    }

    /**
     * An accessor that can be used to get this Vehicle's speed. Used, for example, when a vehicle wants to see
     * if a faster vehicle is ahead in the lane.
     */
    public double getSpeed(){
        return speed;
    }
    
}

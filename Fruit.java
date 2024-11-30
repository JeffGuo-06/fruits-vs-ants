import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.ArrayList;

public class Fruit extends Vehicle
{
    
    int actCount;

    //Movement Variables
    private double orientation;
    private double angularVelocity;
    private double exactX;
    private double speedModifier;
    private int height;
    private int width;
    private int radius;
    private static int radiusCap = 50 ;
    private static int minimumRad = 15;

    //Lifespan and Tier related Variables
    private int tier=0;
    private double hp;
    private static int maxTier=7;
    private int explosionTime;
    private boolean exploded;
    private boolean pushed;

    //Sound Variables
    private static GreenfootSound[] explodeSounds;
    private static int explodeSoundsIndex = 0; 
    private static final int MAX_VOLUME = 85;
    private static final int MIN_VOLUME = 40;
    public Fruit(){
        super();
    }
    
    public Fruit(VehicleSpawner origin){
        this(origin,(int)minimumRad, 2);
    }
    
    /**
     * This constructor is designed for apples that come out of watermelons
     */
    public Fruit(VehicleSpawner origin,boolean natural){
        super(origin,natural);
        disableStaticRotation();
        orientation = 0;
        speedModifier=1;
        boolean imageExists = false;
        GreenfootImage currentImage = null;
        int imageIndex = 0;
        setImage(currentImage);
        GreenfootImage image = getImage();

        radius=minimumRad+(int)((double)Greenfoot.getRandomNumber(50)/10);
        removed=false;
        updateRadius();
        exactX=getPreciseX();
        explosionTime=-1;
    }
    
    /**
     * This constructor takes in a minimum radius and a range of tiers to randomly choose from
     */
    public Fruit(VehicleSpawner origin, int theRadius, int tierRange ){
        super(origin);

        disableStaticRotation();
        orientation = 0;
        speedModifier=1;

        boolean imageExists = false;
        GreenfootImage currentImage = null;
        int imageIndex = 0;

        setImage(currentImage);
        GreenfootImage image = getImage();

        radius=theRadius;
        tier=Greenfoot.getRandomNumber(tierRange);
        removed=false;
        updateRadius();
        exactX=getPreciseX();
    }

    public void act()
    {
        // Add your action code here.
        actCount++;
        setRotation(0); //Reset the rotation before movement
        checkFruitAhead();
        checkHitBuilding();
        checkHitQueen();
        if(explosionTime>=0){
            explosionTime--;
            explode(explosionTime);
            angularVelocity=0;
            //System.out.println("explosionTime: "+explosionTime);
        }
        super.act(); //Movement unaffected since rotation has beeen reset
        setRotation(orientation); //Set rotation to the perceived rotation
        orientation+=angularVelocity; //Make fruit appear to be rolling based on angular velocity
    }
    
    /**
     * Drive Modified from Mr Cohen's Code
     */
    public void drive(){

        Vehicle ahead = (Vehicle) getOneObjectAtOffset (direction * (int)(speed + getImage().getWidth()/2 + 6), 0, Vehicle.class);
        double otherVehicleSpeed = -1;
        if (ahead != null && myLaneNumber == ahead.getLaneNumber()) {
            otherVehicleSpeed = ahead.getSpeed();
        }

        if (otherVehicleSpeed >= 0 && otherVehicleSpeed < maxSpeed){ // Vehicle ahead is slower?
            speed = otherVehicleSpeed;

        } else {
            speed = maxSpeed*speedModifier; // nothing impeding speed, so go max speed
            updateAngularVelocity();//Fruits roll, and if speed changes, angular velocity must change too
        }
        if(pushed==false && !(explosionTime>1)){ //If fruit is being pushed or is exploding, do not move

            move(speed);
            if(speed<0.5&&actCount%2==0){
                move(0.5);
            }

            //System.out.println("tier:"+tier+"speed:" +speed);
        }

        pushed=false;
    }
    
    /**
     * Increases the fruits tier. If too big, triggers explosion
     * @param theTier small fruits make other fruits grow a little, bigger fruits make other fruits grow alot
     */
    public void grow(int theTier){
        if(tier<maxTier){ //If the fruit has room to grow then grow
            tier+=theTier;
            updateRadius();
        }
        else{
            if(explosionTime<1){ //If it does not have room to grow, and is not already exploding, begin explosion sequence
                explosionTime=120;
            }
        }
    }

    /**
     * Updates the radius, tier, hp, speed, scale, and image accordingly
     */
    public void updateRadius(){
        if(tier<0){ //If the fruit tier falls below zero, it means it got destroyed
            removed=true;
            return;
        }
        radius = (int)((((double) tier * (radiusCap - minimumRad)) / 7) + (double) minimumRad); //Change fruit size based on tier
        if(radius>=radiusCap-1){
            maxSpeed=0.2; //If at max radius set speed, to a low value
        }
        else{
            maxSpeed = ((double)Math.pow(1.07, -(radius-33)))+0.15; //Calculate speed given radius, larger => heavier => slower
        }
        speed = maxSpeed*speedModifier; //Apply speed modifiers
        yOffset=radius-10; //Make fruit appear on top of the ground, rather than below it
        tier = (int) Math.round(((double) (radius - minimumRad) * 7) / (radiusCap - minimumRad));
        if(tier>maxTier){ //Maximum tier
            tier=maxTier;
        }
        hp=(double)tier; //HP is converted into tier
        GreenfootImage image = new GreenfootImage("images/fruit/" + tier + ".png"); //Assign image based on tier
        image.scale(2*radius,2*radius); //Draw image
        setImage(image);

    }

    /**
     * Scales the speed modifier
     * @param factor is the factor by which the speedModifier is scaled
     */
    public void scaleSpeedModifier(double factor){
        speedModifier*=factor;
    }

    /**
     * Sets speed modifier
     * @param newSpeed is the new speed
     */
    public void setSpeedModifier(double newSpeed){
        speedModifier=newSpeed;
    }

    /**
     * Angular velocity is calculated based on the formula for a wheel's angular velocity with respect to speed and radius,
     * used an arbitrary constant that looks good
     */
    public void updateAngularVelocity(){
        if(explosionTime<0){ //If object is exploding, do not rotate
            angularVelocity=speed*105/radius*direction;
        }
    }

    /**
     * This function is responsible for detecting fruit merges
     */
    public void checkFruitAhead() 
    {
        // If touching fruit ahead
        Fruit ahead = (Fruit) getOneObjectAtOffset (direction * (int)(speed + getImage().getWidth()/2 + 6), 0, Fruit.class);
        double otherVehicleSpeed = -1;
        if (ahead != null && myLaneNumber == ahead.getLaneNumber()) {

            otherVehicleSpeed = ahead.getSpeed();
        }

        if (otherVehicleSpeed >= 0 && otherVehicleSpeed < speed){ // Vehicle ahead is slower?
            ahead.grow(this.tier+1); //Merge the fruits
            ahead.setSpeedModifier(1); //Refresh Speed modifier, that way fruits don't get stuck
            removed=true;
        } 
    } 

    /**
     * Fruit interactions with pedestrians, knock down vulnerable ants, take damage from aggressive ants, and grow from picking up honey
     */
    public boolean checkHitPedestrian(){
        ArrayList<Pedestrian> pedestrians = (ArrayList<Pedestrian>)getIntersectingObjects(Pedestrian.class);
        for (Pedestrian p: pedestrians)
        {
            if(p instanceof Ant && p.isAwake() && !p.isImmune()){
                if(!p.isImmune()){
                    p.knockDown();//If ant not immune knock down ant
                }

                if(Ant.getState()=="ATTACKING"){
                    this.push(500);//Get knocked back slightly
                    this.damage(1);//Take damage from ant
                }
                return true;

            }
            if(p instanceof Honey){ //If touching honey, grow by one size
                getWorld().removeObject(p);
                grow(1); 
                return true;

            }

        }
        return false;
    }

    /**
     * Fruits can damage the queen, if it hits the queen, do damage to her
     */
    public boolean checkHitQueen(){
        Queen queen = (Queen)getOneIntersectingObject(Queen.class);
        if(queen!=null){
            this.push(500);
            this.damage(1);//damage queen if shes touching the fruit
            return true;
        }
        return false;
    }

    /**
     * Breaks buildings, when intersecting, buildings include antHills
     */
    public boolean checkHitBuilding(){
        ArrayList<Building> buildings = (ArrayList<Building>)getIntersectingObjects(Building.class);
        for (Building b: buildings){
            b.destroy(actCount);
            this.damage(2);
            return true;

        }
        return false;
    }

    /**
     * Push is called when the fruit is being pushed
     * Larger fruits have more inertia, they resist the pushing strength
     * @param strength is the strength at which the fruit is pushed
     */
    public void push(int strength){
        pushed=true;
        double inertia = 0.02*((double)(maxTier)/(double)(tier+1)); 
        double movement = strength * inertia;
        //Inertia isn't quite the right word, but basically the larger the fruit the less the movement
        setLocation(getX()-movement,getY());
    }

    /**
     * Explosion sequence
     * @param time is explosionTime and it dictates what stage the explosion is in
     * 
     */
    public void explode(int time){
        angularVelocity=0; //Stop rolling when exploding
        vibrate(time,5); //Vibrate for clarity and drama
        if(time==0){
            playExplodeSound(); //The sound used to be a big dramatic sound but got super annoying, so now is a small pop
            getWorld().addObject(new WatermelonExplosion(),getX(),getY()+radius+10);
            removed=true;
        }

    }

    /**
     * damage the fruit, bigger fruits take less damage since they are tougher
     */
    public void damage(){
        if(tier>maxTier/2){
            hp-=0.025;
        }
        else{
            hp-=0.05;
        }
        tier=(int)Math.floor(hp);
        updateRadius();
    }

    /**
     * damage the fruit
     * @param dmg is the flat amount of damage taken
     * 
     */
    public void damage(double dmg){
        hp-=dmg;
        tier=(int)Math.floor(hp);
        updateRadius();
    }

    /**
     * returns the tier
     */
    public int getTier(){
        return tier;
    }

    /**
     * SOUNDS Code taken from Mr. Cohen
     */
    public static void init(){
        explodeSoundsIndex = 0;
        explodeSounds = new GreenfootSound[10]; // lots of simultaneous explodeing!
        for (int i = 0; i < explodeSounds.length; i++){
            explodeSounds[i] = new GreenfootSound("sounds/watermelonExplosion.wav");
            explodeSounds[i].setVolume(0);
            explodeSounds[i].play();
            Greenfoot.delay(1);
            explodeSounds[i].stop();
            explodeSounds[i].setVolume(50);
        }   
    }

    public void playExplodeSound(){
        explodeSounds[explodeSoundsIndex].setVolume(80);
        explodeSounds[explodeSoundsIndex].play();
        explodeSoundsIndex++;
        if (explodeSoundsIndex >= explodeSounds.length){
            explodeSoundsIndex = 0;
        }
    }
}

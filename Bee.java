import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.ArrayList;


public class Bee extends Vehicle
{
    
    protected int height;
    protected int width;
    private GreenfootSound beeSound = new GreenfootSound("sounds/bee.mp3");
    private GreenfootSound stingSound = new GreenfootSound("sounds/sting.mp3");
    
    protected GreenfootImage originalImage;
    protected int damage;
    protected int actCount;
    
    public Bee(VehicleSpawner origin){
        //Set up default values
        super(origin);
        beeSound.setVolume(70);
        beeSound.play();
        maxSpeed = 1.5 + ((Math.random() * 5)/5);
        speed = maxSpeed;
        yOffset = 4;
        followingDistance = 6;
        originalImage = getImage();
        originalImage.scale(65,65);
        height = originalImage.getHeight();
        width = originalImage.getWidth();
        setImage(originalImage);
        
    }

    public void act()
    {
        changeLanes();
        checkHitQueen();
        super.act();
        
    }
    
    /**
     * Pushes ants that are awake out of the way
     */
    public boolean checkHitPedestrian(){
        Pedestrian p = (Pedestrian)getOneObjectAtOffset((int)speed + getImage().getWidth()/2, 0, Pedestrian.class);
        if (p != null)
        {
            ArrayList<Ant> ants = (ArrayList<Ant>)getObjectsInRange(100, Ant.class);
            ArrayList<Ant> awakeAnts = new ArrayList<Ant>();
            for(Ant ant : ants) {
                if(ant.isAwake()) {
                    awakeAnts.add(ant);
                }
            }

            for(Ant ant : awakeAnts) {
                ant.push(50,getY());
            }

            return true;
        }
        return false;
    }
    
    /**
     * LANE CHANGE ALGORITHM
     */
    
    public void changeLanes(){
        if(slowed){
            if(this.origin.getLaneNumber()>0 && this.origin.getLaneNumber()<7){
                if(!tryChangeLaneLeft()){
                    tryChangeLaneRight();
                }
            }
            else if(this.origin.getLaneNumber()>0){
                tryChangeLaneLeft();
            }
            else{
                tryChangeLaneRight();
            }
        }
    }

    /**
     * Checks if its is safe to change left, if so, changes left
     */
    public boolean tryChangeLaneLeft(){
        VehicleWorld world = (VehicleWorld)getWorld();
        Vehicle v1 = (Vehicle)getOneObjectAtOffset(-2*width,(int)(-height*0.9),Vehicle.class);
        Vehicle v2 = (Vehicle)getOneObjectAtOffset(+2*width,(int)(-height*0.9),Vehicle.class);
        Vehicle v3 = (Vehicle)getOneObjectAtOffset(-width,(int)(-height*0.9),Vehicle.class);
        Vehicle v4 = (Vehicle)getOneObjectAtOffset(width,(int)(-height*0.9),Vehicle.class);
        Vehicle v5 = (Vehicle)getOneObjectAtOffset(0,(int)(-height*0.9),Vehicle.class);
        if(v1 == null && v2 == null && v3==null && v4 == null && v5==null){
            this.origin=world.getVehicleSpawner(this.origin.getLaneNumber()-1);
            setLocation(getX(),getY()-world.getLaneHeight()-world.getSpaceBetweenLanes());
            this.setLaneNumber(this.origin.getLaneNumber());

            return true;

        }
        return false;
    }

    /**
     * Checks if its is safe to change right, if so, changes right
     */
    public boolean tryChangeLaneRight(){
        VehicleWorld world = (VehicleWorld)getWorld();
        Vehicle v1 = (Vehicle)getOneObjectAtOffset(-2*width,height,Vehicle.class);
        Vehicle v2 = (Vehicle)getOneObjectAtOffset(+2*width,height,Vehicle.class);
        Vehicle v3 = (Vehicle)getOneObjectAtOffset(-width,height,Vehicle.class);
        Vehicle v4 = (Vehicle)getOneObjectAtOffset(width,height,Vehicle.class);
        Vehicle v5 = (Vehicle)getOneObjectAtOffset(0,height,Vehicle.class);
        if(v1 == null && v2 == null && v3==null && v4 == null && v5==null){
            this.origin=world.getVehicleSpawner(this.origin.getLaneNumber()+1);
            setLocation(getX(),getY()+world.getLaneHeight()+world.getSpaceBetweenLanes());
            this.setLaneNumber(this.origin.getLaneNumber());

            return true;

        }
        return false;
    }

    /**
     * Checks if bees hit queen, if so sting for a specific amount of damage
     */
    public boolean checkHitQueen () {
        Queen q = (Queen)getOneObjectAtOffset((int)speed + getImage().getWidth()/2, 0, Queen.class);
        Queen q1 = (Queen)getOneObjectAtOffset(0, 0, Queen.class);
        if (q != null)
        {
            q.damage(damage);
            removed=true;
            stingSound.play();
            return true;
        }
        if (q1 != null)
        {
            q1.damage(damage);
            removed=true;
            stingSound.play();
            return true;
        }
        return false;
    }
}

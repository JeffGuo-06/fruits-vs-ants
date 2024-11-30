import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * A Pedestrian that tries to walk across the street
 */
public abstract class Pedestrian extends SuperSmoothMover
{
    protected double speed;
    protected double maxSpeed;
    protected int direction; // direction is always -1 or 1, for moving down or up, respectively
    protected boolean awake, gettingUp;
    protected boolean immune;
    protected int immuneActs;
    private GreenfootImage originalImage;   
    public Pedestrian(int direction) {
        // choose a random speed
        maxSpeed = Math.random() * 2 + 1;
        speed = maxSpeed;
        // start as awake 
        awake = true;
        gettingUp = false;
        this.direction = direction;
        immune = false;
        //activateShield();
        originalImage = getImage(); 
    }

    public Pedestrian(){

    }
    
    public void act()
    {

    }
    /**
     * Method to return whether the pedestrian is immune
     */
    public boolean isImmune(){
        return immune;
    }

    /**
     * Method to cause this Pedestrian to become knocked down - stop moving, turn onto side
     */
    public void knockDown () {
        //System.out.println("immune: "+immune);
        if(!immune){
            speed = 0;
            GreenfootImage currentImage = getImage();
            currentImage.setTransparency(90);
            setImage(currentImage);
            //setRotation (direction * 270);
            awake = false;
        }
        else{
            //System.out.println("Ant is immune");
        }
    }

    /**
     * Method to allow a downed Pedestrian to be healed
     */
    public void healMe () {
        speed = maxSpeed;
        setRotation (0);
        GreenfootImage currentImage = getImage();
        currentImage.setTransparency(255);
        setImage(currentImage);
        awake = true;
    }

    /**
     * Method to return whether the pedestrian is awake
     */
    public boolean isAwake () {
        return awake;
    }
    
    /**
     * Give immunity for a certain amount of time
     */
    public void giveImmunity(int duration){
        activateShield();
        immune=true;
        //System.out.println("immunity given");
        immuneActs = duration;
    }
    
    /**
     * Shield is active, create a translucent oval around the pedestrian
     * With the help of ChatGPT
     */
    public void activateShield() {
        // Create a new image larger than the original to accommodate the border
        GreenfootImage shieldImage = new GreenfootImage(originalImage.getWidth() + 10, originalImage.getHeight() + 10);

        // Set the color for your shield border
        shieldImage.setColor(new Color(255, 0, 0, 128)); //Red with semi-transparency
        shieldImage.fillOval(0, 0, shieldImage.getWidth()-1, shieldImage.getHeight()-1); 

        // Draw the original character image onto the new image, centered
        shieldImage.drawImage(originalImage, 5, 5);

        // Set the new image with the shield effect as the character's image
        setImage(shieldImage);
        //System.out.println("Shield Active");
    }
    /**
     * Pushes pedestrians. yMovement is calculated relative to a y-level
     */
    public void push(int xMovement,int yPos){
        int yMovement = (int)(((double)(yPos-getY())*xMovement)*(0.03));
        if(yMovement==0){
            yMovement=-5;
        }
        setLocation(getX()+xMovement,getY()+yMovement);
    }
    
    /**
     * Deactivates shield
     */
    public void deactivateShield() {
        setImage(originalImage); // Revert to the original image when the shield is deactivated
    }
}

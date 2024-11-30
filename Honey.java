import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Honey here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Honey extends Pedestrian
{
    /**
     * Act - do whatever the Honey wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    private int actCount;
    private GreenfootImage originalImage;
    private int height;
    private int width;
    private int vibrationStrength;
    public Honey (int direction){
        super(direction);
        originalImage = getImage();
        originalImage.scale(50,50);
        height = originalImage.getHeight();
        width = originalImage.getWidth();
        setImage(originalImage);
        actCount=0;
    }

    public void act()
    {
        actCount++;
        super.act();
        if(getX()>450){
            explode();
        }
        else{
            if(actCount>=300){
                getWorld().removeObject(this);
            }
        }
    }
    
    /**
     * Explosion sequence and the preparation for it
     */
    public void explode(){
        //Vibrate before exploding
        if(actCount<60){
            vibrationStrength=(int)Math.round(actCount/10);
            vibrate(actCount,vibrationStrength);
        }
        //Explode for real now
        if(actCount==60){
            getWorld().addObject(new HoneyExplosion(),getX(),getY());
            getWorld().removeObject(this);
        }
    }
}

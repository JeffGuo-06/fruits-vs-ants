import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class AntHill here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class AntHill extends Building
{
    /**
     * Ant
     */
    //private GreenfootSound destroySound = new GreenfootSound("sounds/antHillDestroy.mp3");

    private GreenfootSound riser = new GreenfootSound("sounds/riser.mp3");
    private int destroyAct;
    private static GreenfootSound[] destroySounds;
    private static int destroySoundsIndex = 0; 
    private static final int MAX_VOLUME = 85;
    private static final int MIN_VOLUME = 40;
    private static boolean riserPlayed=false;
    public AntHill(){
        GreenfootImage image = getImage();
        image.scale(100,100);
        destroyAct=-1;
    }

    public void act()
    {
        // If the AntHill is being destroyed, (only called on the last AntHill), it vibrates for a couple of seconds, before its removed
        if(destroyAct>0){
            destroyAct--;
            if(destroyAct>105){
                vibrate(destroyAct,10);
            }
        }
        if(destroyAct==0){
            getWorld().removeObject(this);
        }
    }

    public static void init(){
        destroySoundsIndex = 0;
        destroySounds = new GreenfootSound[5]; 
        for (int i = 0; i < destroySounds.length; i++){
            destroySounds[i] = new GreenfootSound("sounds/destroyAntHill.wav");
            destroySounds[i].setVolume(0);
            destroySounds[i].play();
            Greenfoot.delay(1);
            destroySounds[i].stop();
            
        }   
    }

    //Plays the destroy sound
    public void playDestroySound(){
        destroySounds[destroySoundsIndex].setVolume(100);
        destroySounds[destroySoundsIndex].play();
        destroySoundsIndex++;
        if (destroySoundsIndex >= destroySounds.length){
            destroySoundsIndex = 0;
        }
        //System.out.println(getX()+","+getY());
    }
    
    //Returns if it is touchingAntHill
    public boolean isTouchingAntHill(){
        return this.isTouching(AntHill.class);
    }
    
    public void destroy(int actCount){

        if(destroyAct==-1){
            playDestroySound();
            
            //If last AntHill
            if(getWorld().getObjects(Queen.class).size()==0&&getWorld().getObjects(AntHill.class).size()==1){
                
                //Only play the riser once per run
                if(!riserPlayed){
                    riser.play();
                    riserPlayed=true;
                }
                
                //Begin destruction sequence
                destroyAct=225;

            }
            else{
                getWorld().removeObject(this);
            }
        }
    }
    
    //Sets riser played to false, this is called when the world is created
    public static void setRiserPlayed(){
        riserPlayed=false;
    }
}

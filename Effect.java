import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * This is the effects class, mostly just for explosions, or things that need to fade out
 *
 */
public class Effect extends SuperSmoothMover
{
    public void act()
    {
        
    }
    
    //Fade out provided by Mr Cohen
    public void fadeOut(int timeLeft, int totalTime){
        GreenfootImage image = getImage();
        double percent = ((double)timeLeft/(double)totalTime);
        int transparency = (int)(percent*255.0);
        if(percent>1.0)return;
        if(percent<0)return;
        image.setTransparency(transparency);
    }
}

import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Building here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public abstract class Building extends SuperSmoothMover
{
    /**
     *
     */
    public void act()
    {
        // Add your action code here.
    }
    public void destroy(int actCount){
        
        getWorld().removeObject(this);
    }
}

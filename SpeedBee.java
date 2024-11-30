import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.ArrayList;

/**
 * Write a description of class FighterBee here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class SpeedBee extends Bee
{
    /**
     * Act - do whatever the FighterBee wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    
    public SpeedBee(VehicleSpawner origin){
        super(origin);
        maxSpeed = 5 + ((Math.random() * 10)/3); //Speed bees are faster than normal bees
        damage= 8;
        speed = maxSpeed;
    }

    public void act()
    {
        super.act();
    }
    

    
    
   
}

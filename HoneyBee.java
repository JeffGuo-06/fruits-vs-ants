import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.ArrayList;



public class HoneyBee extends Bee
{
    
    private int delay;
    
    public HoneyBee(VehicleSpawner origin) {
        super(origin); // call the superclass' constructor
        delay=Greenfoot.getRandomNumber(10)+55;
        damage=5;
    }

    public void act()
    {
        actCount++;
        if(actCount%delay==0){
            getWorld().addObject(new Honey(1),getX()-20,getY()); //Drops honey periodically
        }
        super.act();
    }


}

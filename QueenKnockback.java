import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.ArrayList;

/**
 * Write a description of class QueenKnockback here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class QueenKnockback extends Explosion
{
    /**
     * Act - do whatever the QueenKnockback wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public QueenKnockback(){
        refreshRate=8;
    }

    public void act()
    {
        super.act();
    }

    public void hitEffect(){
        ArrayList<Vehicle> vehicles = (ArrayList<Vehicle>)getWorld().getObjects(Vehicle.class);
        for (Vehicle v : vehicles){
            v.push(knockbackStrength);
        }
        knockbackStrength--;
    }
}

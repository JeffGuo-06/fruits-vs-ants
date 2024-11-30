import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.ArrayList;

/**
 * Write a description of class PheromoneWave here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class PheromoneWave extends Explosion
{
    /**
     * Act - do whatever the PheromoneWave wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public PheromoneWave(){
        color=new Color(0,255,0,100);
        refreshRate=4;
    }

    public void act()
    {   
        super.act();
    }

    public void hitEffect(){
        ArrayList<Ant> ants = (ArrayList<Ant>)getObjectsInRange(radius,Ant.class);
        for (Ant a : ants){
            //System.out.println("fruits");
            if(!a.isAwake()){
                a.healMe();
                a.giveImmunity(120);
            }
        }
    }
}

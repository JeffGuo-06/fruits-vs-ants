import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.ArrayList;

/**
 * Typical explosion, just 
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class HoneyExplosion extends Explosion
{
    
    public HoneyExplosion(){
        maxRadius=100;
        timeLeft=120;
        totalTime=120;
    }

    public void act()
    {
        super.act();
    }
    
    public void hitEffect(){
        killAnts();
    }
    
    /**
     * Kills the ants but also ant hills
     */
    public void killAnts(){
        ArrayList<Ant> ants = (ArrayList<Ant>)getObjectsInRange(100, Ant.class);
        ArrayList<Ant> awakeAnts = new ArrayList<Ant>();
        for(Ant ant : ants) {
            if(ant.isAwake()) {
                awakeAnts.add(ant);
            }
        }

        for(Ant ant : awakeAnts) {
            ant.knockDown();
        }

        ArrayList<AntHill> antHills = (ArrayList<AntHill>)getObjectsInRange(100, AntHill.class);
        for(AntHill h : antHills) {
            getWorld().removeObject(h);
        }
    }

    public void updateImage() {
        GreenfootImage image = new GreenfootImage(radius * 2 + thickness * 2, radius * 2 + thickness * 2);
        image.setColor(Color.ORANGE);
        image.setTransparency(100);

        // Fill the largest oval first to create a solid base
        image.fillOval(thickness, thickness, radius * 2, radius * 2);

        setImage(image);
    }

}

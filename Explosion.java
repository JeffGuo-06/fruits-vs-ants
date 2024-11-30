import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.ArrayList;

/**
 * Write a description of class Explosion here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
import greenfoot.*;

public class Explosion extends Effect {
    protected int radius = 1;
    protected int maxRadius = 1000;
    protected double growthRate = 5;
    protected double growthRateFactor = 0.1;
    protected int thickness = 5; 
    protected int knockbackStrength = 100;
    protected int timeLeft;
    protected int totalTime;
    protected Color color = Color.WHITE;
    protected int actCount;
    protected int lingerTime;
    protected boolean isExploding=true;
    protected int refreshRate=2;
    public Explosion() {
        timeLeft = 120;
        totalTime = 120;
        lingerTime=0;
        updateImage();
    }

    public void act() {
        actCount++;
        explode();
    }

    /**
     * Method that explodes, a circle that gets bigger and bigger, lingers if needed
    */
    public void explode(){
        if (isExploding) {
            if (radius < maxRadius) {
                radius += (int)growthRate; //Increase the size of the circle
                growthRate+=growthRateFactor; //Change the growth rate overtime

                //Reduce frequency of updates, to reduce lag
                if(actCount%refreshRate==0){
                    updateImage();
                }
                hitEffect();
                fadeOut(timeLeft,totalTime);
            } else {
                endEffect();
                isExploding = false;
            }
        }
        else {
            lingerTime--; // Decrease lingering time
            if (lingerTime <= 0) {
                getWorld().removeObject(this); // Remove the object after the lingering period is over
            }
        }
        timeLeft--;
    }

    public void hitEffect(){
        //If an explosion has an effect when it hits something, this function will be needed
    }

    public void endEffect(){
        //If an explosion has an effect when it ends, this function will be needed
    }

    /**
     * Redraws the image of the explosion effect
     * Done with the help of ChatGPT
    */
    public void updateImage() {
        GreenfootImage image = new GreenfootImage(radius * 2 + thickness * 2, radius * 2 + thickness * 2);
        image.setColor(color);
        image.setTransparency(100);
        image.fillOval(thickness, thickness, radius * 2, radius * 2);
        setImage(image);
    }
}


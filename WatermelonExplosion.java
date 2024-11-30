import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

public class WatermelonExplosion extends Explosion {
    
    public WatermelonExplosion() {
        maxRadius = 150;
        growthRate = 2;
        timeLeft = 120;
        totalTime = 120;
        lingerTime = 80; 
        growthRateFactor = 5;
        updateImage();
    }

    //The Watermelon explosion must linger, done with the help of ChatGPT
    public void act() {
        super.act();
    }
    
    public void endEffect(){
        spawnApples(15,300,getX(),getY());
    }
    public void updateImage() {
        GreenfootImage image = new GreenfootImage(radius * 2 + thickness * 2 + 1, radius * 2 + thickness * 2 + 1);
        image.setColor(new Color(230,69,57,255));
        image.fillOval(thickness, thickness,  radius * 2,(int)((double)radius * 1.6));
        setImage(image);
    }

    
    
    public void spawnApples(int count, int range, int xPos, int yPos){
        for(int i = 0; i < count;){
            int radius = Greenfoot.getRandomNumber(range + 1);
            double angle = Math.random() * 2 * Math.PI;
            int x = xPos -50+ (int)(radius * Math.cos(angle));
            int y = 40+yPos + (int)(radius * Math.sin(angle));
            int laneNumber = Math.round((y-262)/54);
            //int h = getWorld().getLaneHeight();
            //System.out.println(x+","+y);
            if(laneNumber>0&&laneNumber<8){
                getWorld().addObject(new Fruit(((VehicleWorld)getWorld()).getVehicleSpawner(laneNumber),true),x,y);
                i++;
            }
        }
    }
}

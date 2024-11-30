import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 * CLASSES
 *      Fruit: 
 *      -The fruits are vehicles that roll
 *      -Fruits are classified into 8 tiers from 0 to 7, each tier corresponding to a different sized fruit
 *      -Different images are assigned to each tier
 *      -Their rolling speed is determined by their radius and/or tier
 *      -Higher tier fruits such as the Watermelon roll slower
 *      -(Their angular velocity is proportional to radius and speed)
 *      -Upon contact, fruits merge, increasing the tier of the fruit
 *      -Watermelons explode when it tries to merge, dropping several low tier fruits (apples)
 *      -Fruits can knock down ants, and destory antHills
 *      -Fruits can absorb honey to grow stronger
 *      -The larger the fruit, the harder it is for ants to destroy it.
 *      
 *      Bee:
 *      -Bees come in after war is declared
 *      -Bees CHANGE LANES when there is the space to do so, and slow traffic
 *      -Bees sting the Queen Ant when she is close enough
 *      -Bees force ants out of their way, to make way for fruits
 *      -Bees die upon using their stinger
 *      
 *      Honey Bee:
 *      -These bees drop honey, feeding fruits, and exploding ants and ant hills, 
 *      -They keep the ant population in check
 *      
 *      Speed Bee:
 *      -These bees come in after the normal bees have cleared the path to the Queen
 *      -These bees are faster, and deal the most damage to the Queen.
 *      -They do not drop honey
 *      
 *      Ant Hill:
 *      -Spawns ants periodically
 *      -When the last ant hill is broken, the Queen comes out dramatically, the war has begun
 *      
 *      Ant:
 *      -They wander around their predetermined territory
 *      -When it is time to attack AKA their queen has arrived, they target and attack fruits, reducing their tier.
 *      -They can be downed and revived again, until they run out of lives
 *      -Once revived they recieve brief immunity, illustrated by their red aura
 *      
 *      Ant Queen:
 *      -The Ant Queen spawns after the last ant hill is destroyed
 *      -She knocks back all vehicles upon her entrance.
 *      -She builds ant hills on her territory to generate new ants
 *      -When there are many downed ants on the field, she emits a wave of pheromones reviving all the ants on screen
 *      -She can also manually revive ants by going near them.
 *      -She is the only actor in the simulation with an HP bar
 *      -When the Queen dies, the game ends.
 * 
 * 
 *EFFECTS
 *      Ant Panic: 
 *      -When the first ant dies, all the other ants begin to panic, their territory is reduced, and they increase their speed collectively
 *      
 *      Pheromone Wave:
 *      -When many ants are downed on the field, the queen emitts a wave of pheromones
 *      -Pheromones revive and give immunity to all ants on screen
 *      
 *KNOWN BUGS
 *      Vehicles sometimes get stuck together when there are too many
 *      The queen, very rarely wanders off the screen.
 *      
 *Cited Ressources
 *IMAGES:
 *  Fruits: SciGho, itch.io
 *  Bees: SanctumPixel, itch.io
 *  Ant and Queen: Robert Brooks, itch.io
 *  Ant Hill: https://antwario.fandom.com/wiki/Anthill
 *SOUNDS:
 *  Bee spawn, and sting: https://minecraft.wiki/w/Category:Bee_sounds
 *  AntHill break: minecraft dirt block break sound
 *  Watermelon pop: pixabay.com
 *  Queen boom and queen riser: SUBMORITY, https://pixabay.com/users/submority-30821389/
 *  Nature Ambience: Nature Meditations, https://www.youtube.com/watch?v=DeHUFsrCYr0&ab_channel=NatureMeditations
 *  Battle Ambience: Michael Ghelfi Studios, https://www.youtube.com/watch?v=-pSCxok55zw&ab_channel=MichaelGhelfiStudios
 *  
 */
public class VehicleWorld extends World
{
    private GreenfootImage background;

    // World Constans
    public static int screenWidth;
    public static int screenHeight;
    // Color Constants
    public static Color GREY_BORDER = new Color (126, 200, 80,0);
    public static Color GREY_STREET = new Color (86, 125, 70,0);
    public static Color YELLOW_LINE = new Color (255, 216, 0, 0);

    public static boolean SHOW_SPAWNERS = true;

    // Set Y Positions for Pedestrians to spawn
    public static final int TOP_SPAWN = 190; // Pedestrians who spawn on top
    public static final int BOTTOM_SPAWN = 705; // Pedestrians who spawn on the bottom

    // Instance variables / Objects
    private boolean twoWayTraffic, splitAtCenter;
    private int laneHeight, laneCount, spaceBetweenLanes;
    private int[] lanePositionsY;
    private VehicleSpawner[] laneSpawners;

    // Fruit Variables
    private int fruitSpawnChance;
    private int fruitTierRange;

    // Ant Variables
    private int antHillsCount;
    private ArrayList<AntHill> antHills = new ArrayList<AntHill>();
    private int lastAntHillX;
    private int lastAntHillY;
    private int wallsCount;
    private int antSpeed;

    // Sound Variables
    private GreenfootSound queenBoom = new GreenfootSound("sounds/queenBoom.mp3");
    private GreenfootSound battleAmbience = new GreenfootSound("sounds/battleAmbience.mp3");
    private GreenfootSound natureAmbience = new GreenfootSound("sounds/natureAmbience.mp3");
    
    // Act Counters
    private int actCount = 0;
    private int queenActs = 0;

    public VehicleWorld()
    {    
        // Create a new world with 1024x800 cells with a cell size of 1x1 pixels.
        super(1024, 800, 1, false); 
        screenWidth=1024;
        screenHeight=800;
        // This command (from Greenfoot World API) sets the order in which 
        // objects will be displayed. In this example, Pedestrians will
        // always be on top of everything else, then Vehicles (of all
        // sub class types) and after that, all other classes not listed
        // will be displayed in random order. 
        //setPaintOrder (Pedestrian.class, Vehicle.class); // Commented out to use Z-sort instead

        // set up background -- If you change this, make 100% sure
        // that your chosen image is the same size as the World
        background = new GreenfootImage ("background03.png");
        setBackground (background);

        // Set critical variables - will affect lane drawing
        laneCount = 8;
        laneHeight = 48;
        spaceBetweenLanes = 6;
        splitAtCenter = false;
        twoWayTraffic = false;

        // Init lane spawner objects 
        laneSpawners = new VehicleSpawner[laneCount];

        // Prepare lanes method - draws the lanes
        lanePositionsY = prepareLanes (this, background, laneSpawners, 232, laneHeight, laneCount, spaceBetweenLanes, twoWayTraffic, splitAtCenter);

        setBackground (background);

        wallsCount=20;

        // Spawn Ant City
        antHillsCount=10;
        spawnAntHills(antHillsCount,screenWidth/2,(int)((double)screenWidth*0.95),TOP_SPAWN+laneHeight+spaceBetweenLanes,TOP_SPAWN+(laneHeight+spaceBetweenLanes)*laneCount);
        //spawnWall(wallsCount,screenWidth/2-80,screenWidth/2-40,TOP_SPAWN+laneHeight+spaceBetweenLanes,TOP_SPAWN+(laneHeight+spaceBetweenLanes)*laneCount);

        setPaintOrder(SuperStatBar.class,Queen.class,Vehicle.class, Ant.class, Honey.class, Effect.class,AntHill.class);
        spawnAnts(1);
        
        AntHill.init();
        AntHill.setRiserPlayed();
        Fruit.init();
        fruitSpawnChance=100;
        fruitTierRange=2;
        Ant.setSpeed(1);
        new Queen();
    }

    //Starts ambience where needed and resets speed to normal speed
    public void started(){
        Greenfoot.setSpeed(50);
        System.out.println("start");
        if(queenActs>0){
            battleAmbience.play();
        }
        else{
            natureAmbience.setVolume(40);
            natureAmbience.play();
        }
        if(Queen.gethp()>0){
            natureAmbience.play();
        }
    }

    //Stops ambience when paused
    public void stopped(){
        battleAmbience.stop();
        natureAmbience.stop();
    }

    /**
     * The following statements control the flow of the simulation, to make sure it roughly follows the "plot" and timings.
     */
    public void act () {
        if(Queen.gethp()>0){
            if(actCount%60==0){
                System.out.println("T:"+actCount/60+"s  FS:"+getFruitScore()+"  AS:"+getAntScore ());
            }
            Ant.setSpeed(antSpeed);
            actCount++;

            if(actCount==1){
                antSpeed=1;
                spawnAnts(12);

            }
            if(getDownedAnts()==1){
                antSpeed=2;

            }
            if(actCount==60*2){
                spawnFruits(1);

            }
            if(actCount>60*3){

                spawnFruits(fruitSpawnChance,fruitTierRange);
            }
            if(actCount==60*4){
                fruitSpawnChance = 40;
                fruitTierRange=3;

            }
            if(actCount==60*5){
                fruitSpawnChance = 5;
                fruitTierRange=4;
            }
            if(getObjects(Queen.class).size()>0){
                Ant.setState("ATTACKING");
                queenActs++;

            }
            if(queenActs==1){
                battleAmbience.setVolume(30);
                natureAmbience.setVolume(20);
                battleAmbience.play();
                fruitSpawnChance = 5;
                System.out.println("QUEEN SPAWNED AT "+actCount/60+"s");
            }
            if(queenActs==500){
                fruitSpawnChance = 10;
                fruitTierRange=7;
                battleAmbience.setVolume(25);
                //Queen.setState("IDLING");
            }
            if(queenActs>60&&queenActs<60*5&&getObjects(NormalBee.class).size()<5){
                spawnNormalBee(5);

                //spawnSpeedBee(20);
                //Queen.setState("IDLING");
            }
            if(queenActs>60*5&&getObjects(HoneyBee.class).size()<4){
                spawnHoneyBee(20);
                if(getDownedAnts()>20){
                    spawnHoneyBee(1);
                }
                //spawnSpeedBee(20);
                //Queen.setState("IDLING");
            }
            if(queenActs>60*20&&getObjects(SpeedBee.class).size()<3){
                spawnSpeedBee(5);

                //spawnSpeedBee(20);
                //Queen.setState("IDLING");
            }
            
            if(getDownedAnts()>20 && getObjects(PheromoneWave.class).size()==0){
                if(getObjects(Queen.class).size()!=0){
                    Queen q=getObjects(Queen.class).get(0);
                    addObject(new PheromoneWave(),q.getX(),q.getY());
                    System.out.println("ADDED");

                }
            }
            spawnAnts();
        }
        else{
            //GAME OVER
            battleAmbience.setVolume(10);
            natureAmbience.setVolume(40);
        }

        zSort ();
    }

    /**
     * Spawns a number of AntHills at the beginning in an area defined by the parameters
     * @param count The number of AntHills to spawn
     * @param xMin The minimum x-coordinate of the spawning area
     * @param xMax The maximum x-coordinate of the spawning area
     * @param yMin The minimum y-coordinate of the spawning area
     * @param yMax The maximum y-coordinate of the spawning area
     */
    public void spawnAntHills(int count, int xMin, int xMax, int yMin, int yMax) {
        //spawn AntHills
        for (int i = 0; i < count; i++) {
            boolean placed = false;

            while (!placed) {
                int x = xMin + Greenfoot.getRandomNumber(xMax - xMin + 1);
                int y = yMin + Greenfoot.getRandomNumber(yMax - yMin + 1);
                AntHill tempAntHill = new AntHill();

                addObject(tempAntHill, x, y);

                // check if the new AntHill is touching any existing AntHill
                if (!tempAntHill.isTouchingAntHill()) {
                    placed = true; // no overlap, AntHill placed successfully
                    antHills.add(tempAntHill);
                } else {
                    removeObject(tempAntHill); // overlap found, remove and try again
                }

            }
        }
    }

    /**
     * Spawns fruits with a given chance, the fruit's tier is set to a default value
     *
     * @param spawnChance The chance of spawning a fruit. A random number is generated, and if it's 0, a fruit is spawned.
     */    
    private void spawnFruits (int spawnChance) {

        spawnFruits(spawnChance,2);

    }

    /**
     * Spawns fruits with a given chance and within a specified tier range
     *
     * @param spawnChance The chance of spawning a fruit. A random number is generated, and if it's 0, a fruit is spawned.
     * @param tierRange The range of fruit tiers that can be spawned
     */
    private void spawnFruits (int spawnChance, int tierRange) {
        // Chance to spawn a fruit

        if (Greenfoot.getRandomNumber (spawnChance) == 0){
            int lane = Greenfoot.getRandomNumber(laneCount);
            if (!laneSpawners[lane].isTouchingVehicle()){
                addObject(new Fruit(laneSpawners[lane],15,tierRange), 0, 0);
            }
        }

    }

    /**
     * Spawns a specified number of ants at a random AntHill.
     *
     * @param num The number of ants to spawn.
     */
    public void spawnAnts(int num){
        for(int i = 0 ; i < num ; i++){
            int antHillIndex = Greenfoot.getRandomNumber (antHills.size());
            AntHill a = antHills.get(antHillIndex);
            addObject(new Ant(-1),a.getX(),a.getY());
        }
    }

    /**
     * The default ant spawning algorithm, which alters whether the queen is alive or not, also spawns queen when last ant hill is destroyed
     * 
     */
    public void spawnAnts(){
        if(getObjects(Queen.class).size()==0){
            antHills = (ArrayList<AntHill>) getObjects(AntHill.class);
            //Increased ants will spawn when less Hills present in the beginning
            if (Greenfoot.getRandomNumber (10*(antHills.size()+1)) == 0 && antHills.size()>0){
                int antHillIndex = Greenfoot.getRandomNumber (antHills.size());
                AntHill a = antHills.get(antHillIndex);
                addObject(new Ant(-1),a.getX(),a.getY());
            }

            //Figures out the position of the last ant hill
            if(antHills.size()==1){
                AntHill a = antHills.get(0);
                lastAntHillX = a.getX();
                lastAntHillY = a.getY();

            }

            //Spawns the queen when the ant hill is removed, plays a dramatic sound
            if(antHills.size()==0 && getObjects(Queen.class).size()==0){

                addObject(new Queen(),lastAntHillX,lastAntHillY);
                addObject(new QueenKnockback(),lastAntHillX,lastAntHillY);

                queenBoom.play();
            }
        }
        else{
            antHills = (ArrayList<AntHill>) getObjects(AntHill.class);

            //Each ant hill is equally likely and capable of spawning ants
            if (Greenfoot.getRandomNumber (80) < antHills.size() && antHills.size()>0){
                int antHillIndex = Greenfoot.getRandomNumber (antHills.size());
                AntHill a = antHills.get(antHillIndex);
                Ant ant =new Ant(-1);

                addObject(ant,a.getX(),a.getY());
                ant.giveImmunity(120);
            }
        }
    }
    /**
     * Spawns a normal bee in a random lane
     */
    public void spawnNormalBee(int spawnChance){
        if (Greenfoot.getRandomNumber (spawnChance) == 0){
            int lane = Greenfoot.getRandomNumber(laneCount);
            if (!laneSpawners[lane].isTouchingVehicle()){
                addObject(new NormalBee(laneSpawners[lane]), 0, 0);
            }
        }
    }
    /**
     * Spawns a honey bee in a random lane
     */
    public void spawnHoneyBee(int spawnChance){
        if (Greenfoot.getRandomNumber (spawnChance) == 0){
            int lane = Greenfoot.getRandomNumber(laneCount);
            if (!laneSpawners[lane].isTouchingVehicle()){
                addObject(new HoneyBee(laneSpawners[lane]), 0, 0);
            }
        }
    }

    /**
     * Spawns a speed bee in a random lane
     */
    public void spawnSpeedBee(int spawnChance){
        if (Greenfoot.getRandomNumber (spawnChance) == 0){
            int lane = Greenfoot.getRandomNumber(laneCount);
            if (!laneSpawners[lane].isTouchingVehicle()){
                addObject(new SpeedBee(laneSpawners[lane]), 0, 0);
            }
        }
    }

    /**
     * This method evaluates the overall strength of the fruit population, factoring in bees, and fruit tier. It was useful for balancing out the two sides of the war
     */
    public int getFruitScore(){
        int score=0;
        ArrayList<Fruit> fruits = (ArrayList<Fruit>)getObjects(Fruit.class);
        for(Fruit f : fruits){
            score+=f.getTier()+1;
        }
        ArrayList<Bee> bees = (ArrayList<Bee>)getObjects(Bee.class);
        for(Bee b : bees){
            score+=10;
        }
        return score;
    }

    /**
     * This method evaluates the overall strength of the ant population, factoring in the amount of lives left. It was useful for balancing out the two sides of the war
     */
    public int getAntScore(){
        int score=0;
        ArrayList<Ant> ants = (ArrayList<Ant>)getObjects(Ant.class);
        for(Ant a : ants){
            if(a.isAwake()){
                score+=a.getLives();
            }

        }
        return score;
    }

    /**
     * Returns the amount of ants which are downed
     */
    public int getDownedAnts(){
        int num=0;
        ArrayList<Ant> ants = (ArrayList<Ant>)getObjects(Ant.class);
        for(Ant a : ants){
            if(!a.isAwake()){
                num+=1;
            }

        }
        return num;
    }

    /**
     *  Given a lane number (zero-indexed), return the y position
     *  in the centre of the lane. (doesn't factor offset, so 
     *  watch your offset, i.e. with Bus).
     *  
     *  @param lane the lane number (zero-indexed)
     *  @return int the y position of the lane's center, or -1 if invalid
     */
    public int getLaneY (int lane){
        if (lane < lanePositionsY.length){
            return lanePositionsY[lane];
        } 
        return -1;
    }

    /**
     * Given a y-position, return the lane number (zero-indexed).
     * Note that the y-position must be valid, and you should 
     * include the offset in your calculations before calling this method.
     * For example, if a Bus is in a lane at y=100, but is offset by -20,
     * it is actually in the lane located at y=80, so you should send
     * 80 to this method, not 100.
     * 
     * @param y - the y position of the lane the Vehicle is in
     * @return int the lane number, zero-indexed
     * 
     */
    public int getLane (int y){
        for (int i = 0; i < lanePositionsY.length; i++){
            if (y == lanePositionsY[i]){
                return i;
            }
        }
        return -1;
    }

    public static int[] prepareLanes (World world, GreenfootImage target, VehicleSpawner[] spawners, int startY, int heightPerLane, int lanes, int spacing, boolean twoWay, boolean centreSplit, int centreSpacing)
    {
        // Declare an array to store the y values as I calculate them
        int[] lanePositions = new int[lanes];
        // Pre-calculate half of the lane height, as this will frequently be used for drawing.
        // To help make it clear, the heightOffset is the distance from the centre of the lane (it's y position)
        // to the outer edge of the lane.
        int heightOffset = heightPerLane / 2;

        // draw top border
        target.setColor (GREY_BORDER);
        target.fillRect (0, startY, target.getWidth(), spacing);

        // Main Loop to Calculate Positions and draw lanes
        for (int i = 0; i < lanes; i++){
            // calculate the position for the lane
            lanePositions[i] = startY + spacing + (i * (heightPerLane+spacing)) + heightOffset ;

            // draw lane
            target.setColor(GREY_STREET); 
            // the lane body
            target.fillRect (0, lanePositions[i] - heightOffset, target.getWidth(), heightPerLane);
            // the lane spacing - where the white or yellow lines will get drawn
            target.fillRect(0, lanePositions[i] + heightOffset, target.getWidth(), spacing);

            // Place spawners and draw lines depending on whether its 2 way and centre split
            if (twoWay && centreSplit){
                // first half of the lanes go rightward (no option for left-hand drive, sorry UK students .. ?)
                if ( i < lanes / 2){
                    spawners[i] = new VehicleSpawner(false, heightPerLane, i);
                    world.addObject(spawners[i], target.getWidth(), lanePositions[i]);
                } else { // second half of the lanes go leftward
                    spawners[i] = new VehicleSpawner(true, heightPerLane, i);
                    world.addObject(spawners[i], 0, lanePositions[i]);
                }

                // draw yellow lines if middle 
                if (i == lanes / 2){
                    target.setColor(YELLOW_LINE);
                    target.fillRect(0, lanePositions[i] - heightOffset - spacing, target.getWidth(), spacing);

                } else if (i > 0){ // draw white lines if not first lane
                    for (int j = 0; j < target.getWidth(); j += 120){
                        target.setColor (new Color(0,0,0,0));
                        target.fillRect (j, lanePositions[i] - heightOffset - spacing, 60, spacing);
                    }
                } 

            } else if (twoWay){ // not center split
                if ( i % 2 == 0){
                    spawners[i] = new VehicleSpawner(false, heightPerLane, i);
                    world.addObject(spawners[i], target.getWidth(), lanePositions[i]);
                } else {
                    spawners[i] = new VehicleSpawner(true, heightPerLane, i);
                    world.addObject(spawners[i], 0, lanePositions[i]);
                }

                // draw Grey Border if between two "Streets"
                if (i > 0){ // but not in first position
                    if (i % 2 == 0){
                        target.setColor(GREY_BORDER);
                        target.fillRect(0, lanePositions[i] - heightOffset - spacing, target.getWidth(), spacing);

                    } else { // draw dotted lines
                        for (int j = 0; j < target.getWidth(); j += 120){
                            target.setColor (YELLOW_LINE);
                            target.fillRect (j, lanePositions[i] - heightOffset - spacing, 60, spacing);
                        }
                    } 
                }
            } else { // One way traffic
                spawners[i] = new VehicleSpawner(true, heightPerLane, i);
                world.addObject(spawners[i], 0, lanePositions[i]);
                if (i > 0){
                    for (int j = 0; j < target.getWidth(); j += 120){
                        target.setColor (new Color(0,0,0,0));
                        target.fillRect (j, lanePositions[i] - heightOffset - spacing, 60, spacing);
                    }
                }
            }

        }
        // draws bottom border
        target.setColor (GREY_BORDER);
        target.fillRect (0, lanePositions[lanes-1] + heightOffset, target.getWidth(), spacing);

        return lanePositions;
    }

    /**
     * A z-sort method which will sort Actors so that Actors that are
     * displayed "higher" on the screen (lower y values) will show up underneath
     * Actors that are drawn "lower" on the screen (higher y values), creating a
     * better perspective. 
     */
    public void zSort() {
        List<Vehicle> rawVehicles = getObjects(Vehicle.class); // This will return a List<Vehicle>
        ArrayList<ActorContent> acList = new ArrayList<ActorContent>();

        for (Vehicle fruit : rawVehicles) { // No need for the instanceof check now
            acList.add(new ActorContent(fruit, fruit.getX(), fruit.getY()));
        }

        Collections.sort(acList);

        for (ActorContent ac : acList) {
            Vehicle fruit = (Vehicle) ac.getActor();
            removeObject(fruit); // remove and re-add to sort the paint order
            addObject(fruit, ac.getX(), ac.getY());
        }
    }

    /**
     * <p>The prepareLanes method is a static (standalone) method that takes a list of parameters about the desired roadway and then builds it.</p>
     * 
     * <p><b>Note:</b> So far, Centre-split is the only option, regardless of what values you send for that parameters.</p>
     *
     * <p>This method does three things:</p>
     * <ul>
     *  <li> Determines the Y coordinate for each lane (each lane is centered vertically around the position)</li>
     *  <li> Draws lanes onto the GreenfootImage target that is passed in at the specified / calculated positions. 
     *       (Nothing is returned, it just manipulates the object which affects the original).</li>
     *  <li> Places the VehicleSpawners (passed in via the array parameter spawners) into the World (also passed in via parameters).</li>
     * </ul>
     * 
     * <p> After this method is run, there is a visual road as well as the objects needed to spawn Vehicles. Examine the table below for an
     * in-depth description of what the roadway will look like and what each parameter/component represents.</p>
     * 
     * <pre>
     *                  <=== Start Y
     *  ||||||||||||||  <=== Top Border
     *  /------------\
     *  |            |  
     *  |      Y[0]  |  <=== Lane Position (Y) is the middle of the lane
     *  |            |
     *  \------------/
     *  [##] [##] [##| <== spacing ( where the lane lines or borders are )
     *  /------------\
     *  |            |  
     *  |      Y[1]  |
     *  |            |
     *  \------------/
     *  ||||||||||||||  <== Bottom Border
     * </pre>
     * 
     * @param world     The World that the VehicleSpawners will be added to
     * @param target    The GreenfootImage that the lanes will be drawn on, usually but not necessarily the background of the World.
     * @param spawners  An array of VehicleSpawner to be added to the World
     * @param startY    The top Y position where lanes (drawing) should start
     * @param heightPerLane The height of the desired lanes
     * @param lanes     The total number of lanes desired
     * @param spacing   The distance, in pixels, between each lane
     * @param twoWay    Should traffic flow both ways? Leave false for a one-way street (Not Yet Implemented)
     * @param centreSplit   Should the whole road be split in the middle? Or lots of parallel two-way streets? Must also be two-way street (twoWay == true) or else NO EFFECT
     * 
     */
    public static int[] prepareLanes (World world, GreenfootImage target, VehicleSpawner[] spawners, int startY, int heightPerLane, int lanes, int spacing, boolean twoWay, boolean centreSplit){
        return prepareLanes (world, target, spawners, startY, heightPerLane, lanes, spacing, twoWay, centreSplit, spacing);
    }

    public int getLaneHeight(){
        return laneHeight;
    }

    public int getLaneCount(){
        return laneCount;
    }

    public int getSpaceBetweenLanes(){
        return spaceBetweenLanes;
    }

    public VehicleSpawner getVehicleSpawner(int lane){
        return laneSpawners[lane];
    }
}

/**
 * Container to hold and Actor and an LOCAL position (so the data isn't lost when the Actor is temporarily
 * removed from the World).
 */
class ActorContent implements Comparable <ActorContent> {
    private Actor actor;
    private int xx, yy;
    public ActorContent(Actor actor, int xx, int yy){
        this.actor = actor;
        this.xx = xx;
        this.yy = yy;
    }

    public void setLocation (int x, int y){
        xx = x;
        yy = y;
    }

    public int getX() {
        return xx;
    }

    public int getY() {
        return yy;
    }

    public Actor getActor(){
        return actor;
    }

    public String toString () {
        return "Actor: " + actor + " at " + xx + ", " + yy;
    }

    public int compareTo (ActorContent a){
        return this.getY() - a.getY();
    }

}

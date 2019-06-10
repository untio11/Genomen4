package GameState.Entities;

import GameState.Position;
import GameState.TileType;
import GameState.World;
import org.joml.Vector3f;
import util.Observable;
import util.Observer;

import java.util.ArrayList;
import java.util.List;

import java.util.Arrays;

/**
 * For keeping track of the players
 */
public class Actor extends Entity implements Observable {
    private List<Observer<Actor>> observers;
    private float speed;
    private float size;
    private boolean kidnapper;
    private World world;
    private double previousAngle;
    private double targetAngle;

    // Turnspeed has to be a divisor of 90
    private float turnSpeed = 10f;

    /**
     * Initialize a actor with the appropriate properties
     *
     * @param position  The position of the actor
     * @param rotation  The rotation of the model
     * @param kidnapper The role of the actor
     */
    public Actor(World world, float size, Vector3f position, Vector3f rotation, boolean kidnapper) {
        super(position);
        this.rotation = rotation;
        this.size = size;
        this.kidnapper = kidnapper;
        this.speed = kidnapper ? 3 : 4; //different speed for the two players
        this.world = world;
        this.observers = new ArrayList<>();
    }

    public float resetDegrees(float angle) {
        // Reset the degrees after rotating a full circle
        if(angle >= 360f) {
            return angle - 360;
        } else if(angle < 0f) {
            return angle + 360;
        }
        return angle;
    }

    /**
     * Move the actor up, unless obstacle
     * @param dt time elapsed
     */
    public void moveUp(double dt) { //
        int tileY = (int) (position.y - size / 2);                  //the tile where the upper side of the actor is
        float offY = (position.y - size / 2) - tileY;               //the y offset in that tile
        int tileXLeft = (int) (position.x - size / 2);              //the tile where the left side of the actor is in
        int tileXRight = (int) (position.x + size / 2 - 0.0001);    //the tile where the right side of the actor is in
        double distance = dt * speed;
        if (world.getCollision((int) position.x, tileY - 1)     //if upper tile is an obstacle
                || world.getCollision(tileXRight, tileY - 1)
                || world.getCollision(tileXLeft, tileY - 1)) {
            if (distance > offY) {  //in case that the travelled distance would lead into an obstacle
                position.y -= offY;
                return;
            }
        }
        position.y -= distance;
    }

    /**
     * Move the actor down, unless obstacle
     * @param dt time elapsed
     */
    public void moveDown(double dt) {
        int tileY = (int) (position.y + size / 2 - 0.00001);
        float offY = tileY + 1 - (position.y + size / 2);
        int tileXLeft = (int) (position.x - size / 2);
        int tileXRight = (int) (position.x + size / 2 - 0.00001);
        double distance = dt * speed;
        if (world.getCollision((int) position.x, tileY + 1)
                || world.getCollision(tileXRight, tileY + 1)
                || world.getCollision(tileXLeft, tileY + 1)) {
            if (distance > offY) {
                position.y += offY;
                return;
            }
        }
        position.y += distance;
    }

    /**
     * Move the actor left, unless obstacle
     * @param dt time elapsed
     */
    public void moveLeft(double dt) {
        int tileX = (int) (position.x - size / 2);
        float offX = (position.x - size / 2) - tileX;
        int tileYUp = (int) (position.y - size / 2);
        int tileYDown = (int) (position.y + size / 2 - 0.00001);
        double distance = dt * speed;
        if (world.getCollision(tileX - 1, (int) position.y)
                || world.getCollision(tileX - 1, tileYUp)
                || world.getCollision(tileX - 1, tileYDown)) {
            if (distance > offX) {
                position.x -= offX;
                return;
            }
        }
        position.x -= distance;
    }

    /**
     * Move the actor right, unless obstacle
     * @param dt time elapsed
     */
    public void moveRight(double dt) {
        int tileX = (int) (position.x + size / 2 - 0.00001);
        float offX = tileX + 1 - (position.x + size / 2);
        int tileYUp = (int) (position.y - size / 2);
        int tileYDown = (int) (position.y + size / 2 - 0.0001);
        double distance = dt * speed;
        if (world.getCollision(tileX + 1, (int) position.y)
                || world.getCollision(tileX + 1, tileYUp)
                || world.getCollision(tileX + 1, tileYDown)) {
            if (distance > offX) {
                position.x += offX;
                return;
            }
        }
        position.x += distance;
    }

    public void move(double horizontal, double vertical) {
        if (horizontal != 0 && vertical != 0) {
            if (vertical > 0) {
                moveDown(Math.sqrt(2)/2 * vertical);
                targetAngle = 0;
            } else {
                moveUp(Math.sqrt(2)/2 * -vertical);
                targetAngle = 180;
            }
            if (horizontal > 0) {
                moveRight(Math.sqrt(2)/2 * horizontal);
                targetAngle += vertical > 0 ? 45 : -45;
            } else {
                moveLeft(Math.sqrt(2)/2 * -horizontal);
                targetAngle += vertical > 0 ? 315 : 45;
            }
        } else if (horizontal > 0) {
            moveRight(horizontal);
            targetAngle = 90;
        } else if (horizontal < 0) {
            moveLeft(-horizontal);
            targetAngle = 270;
        } else if (vertical < 0) {
            moveUp(-vertical);
            targetAngle = 180;
        }  else if (vertical > 0) {
            moveDown(vertical);
            targetAngle = 0;
        }

        double rotleft = resetDegrees((float) (targetAngle - rotation.y));
        double rotright = resetDegrees((float) (rotation.y - targetAngle));
        if (rotation.y != targetAngle) {
            if (rotleft <= rotright) {
                rotation.y = resetDegrees(rotation.y + turnSpeed);
            } else {
                rotation.y = resetDegrees(rotation.y - turnSpeed);
            }
            if (Math.abs(rotation.y - targetAngle) < turnSpeed) {
                rotation.y = (float) targetAngle;
            }
        }
        broadcast();
    }

    /**
     * Adds the observer and sends an immediate update.
     * @param obs Object that implements the observer interface.
     */
    @Override
    public void add(Observer obs) {
        this.observers.add(obs);
        broadcast();
    }

    @Override
    public void remove(Observer obs) {
        this.observers.remove(obs);
    }

    @Override
    public void broadcast() {
        for (Observer<Actor> obs : this.observers) {
            obs.update(this);
        }
    }

    public float getSize() { return this.size; }

    public boolean isKidnapper() { return kidnapper; }

    public float getRotX() {
        return this.rotation.x;
    }

    public void setRotX(float rotX) {
        this.rotation.x = rotX;
    }

    public float getRotY() {
        return this.rotation.y;
    }

    public void setRotY(float rotY) {
        this.rotation.y = rotY;
    }

    public float getRotZ() {
        return this.rotation.z;
    }

    public void setRotZ(float rotZ) {
        this.rotation.z = rotZ;
    }

    public Vector3f get3DPosition() {
        return new Vector3f(position.x, position.z, position.y);
    }

    /**
     *
     * @param num the total number of rays to cast. It works like a radiant circle. So first ray is to the right, and
     *            it moves anti-clockwise
     * @return a 2D array giving the result of the rays, in the same order as they are cast. So starting straight to
     * the right and going anti-clockwise. The format of the inner array is [Accessible ? 1 : 0, Max(distance, 3), angle (degrees)]
     * Also, if the opponent player is in sight, an extra ray will be appended with a first value of 2, together with the distance
     * and the angle in degrees.
     */
    public double[][] castRays(int num, int maxRayLength) {

        boolean playerInSight = false;
        double distanceToOpponent;
        Vector3f opponentPos;
        if (kidnapper) {
            opponentPos = world.getFather().getPosition();
        } else {
            opponentPos = world.getKidnapper().getPosition();
        }
        double xToOpponent = Math.abs(position.x - opponentPos.x);
        double yToOpponent = Math.abs(position.y - opponentPos.y);
        distanceToOpponent = Math.sqrt(Math.pow(xToOpponent, 2) + Math.pow(yToOpponent, 2));
        int angleDegrees = getScreamAngle();

        double[] rayToOpponent = castRay(angleDegrees, maxRayLength, true);

        playerInSight = distanceToOpponent <= rayToOpponent[1] && distanceToOpponent <= maxRayLength;

        double[][] results = new double[num + 1][3];

        for (int a = 0; a < num; ++a) {
            int angle = getRayAngle(num, a);
            results[a] = castRay(angle, maxRayLength, false);
        }

        if (playerInSight) {
            rayToOpponent[0] = 2;
            rayToOpponent[1] = distanceToOpponent;
            previousAngle = rayToOpponent[2];
            results[results.length - 1] = rayToOpponent;
        } else {
            rayToOpponent[0] = 0;
            rayToOpponent[1] = -1;
            rayToOpponent[2] = previousAngle;
        }

        results[results.length - 1] = rayToOpponent;

        return results;
    }

    public  void receiveScream() {
        previousAngle = getScreamAngle();
    }

    public int getScreamAngle() {
        Vector3f opponentPos;
        if (kidnapper) {
            opponentPos = world.getFather().getPosition();
        } else {
            opponentPos = world.getKidnapper().getPosition();
        }
        double angleRads = Math.atan2(position.y - opponentPos.y, opponentPos.x - position.x);
        int angleDegrees = (int) Math.toDegrees(angleRads);

        if (angleDegrees < 0) {
            angleDegrees += 360;
        }

        return angleDegrees;
    }

    private double[] castRay(int angle, int maxRayLength, boolean ignoreWater) {
        float rayDirX = (float) Math.cos(Math.toRadians(angle));
        float rayDirY = (float) Math.sin(Math.toRadians(angle));
        float sideDistX;
        float sideDistY;
        float deltaDistX = Math.abs(1 / rayDirX);
        float deltaDistY = Math.abs(1 / rayDirY);
        int stepX;
        int stepY;
        int mapX = (int) position.x;
        int mapY = (int) position.y;
        float posX = position.x;
        float posY = position.y;
        int hit = 0;

        //calculate step and initial sideDist
        if (rayDirX < 0) {
            stepX = -1;
            sideDistX = (posX - mapX) * deltaDistX;
        } else {
            stepX = 1;
            sideDistX = (mapX + 1 - posX) * deltaDistX;
        } if (rayDirY > 0) {
            stepY = -1;
            sideDistY = (posY - mapY) * deltaDistY;
        } else {
            stepY = 1;
            sideDistY = (mapY + 1 - posY) * deltaDistY;
        }

        while (hit == 0) {
            //jump to next map square, OR in x-direction, OR in y-direction
            if (sideDistX < sideDistY) {
                sideDistX += deltaDistX;
                mapX += stepX;
            } else {
                sideDistY += deltaDistY;
                mapY += stepY;
            }
            //Check if ray has hit a wall
            if ((!ignoreWater && !world.getTile(mapX, mapY).isAccessible()) || (world.getTile(mapX, mapY).getType() == TileType.TREE) || isWorldEdge(mapX, mapY)) {
                hit = 1;
                //double distance = Math.sqrt((Float.isInfinite(sideDistX) || sideDistX > 20 ? 0 : Math.pow(sideDistX - deltaDistX, 2))
                //        + (Float.isInfinite(sideDistY) || sideDistY > 20 ? 0 : Math.pow(sideDistY - deltaDistY, 2)));
                double distance = Math.sqrt(Math.pow(Math.abs(position.x - (mapX + 0.5)), 2) + Math.pow(Math.abs(position.y - (mapY + 0.5)), 2));
                if (distance <= maxRayLength) {
                    return new double[] {0, distance, (double) angle};
                } else {
                    return new double[] {1, maxRayLength, (double) angle};
                }

            }
        }
        return null; //Will never reach this, since eventually it will always hit an inaccessible tile (island)
    }

    private boolean isWorldEdge(int x, int y) {
        return x == 0 || y == 0 || x == world.getWidth() - 1 || y == world.getHeight() - 1;
    }

    private int getRayAngle(int total, int x) {
        return (x * (360 / total));
    }

    private int getRayQuadrant(int angle) {
        angle %= 360;
        if (angle < 0) {
            angle += 360;
        }
        return (angle / 90) % 4 + 1;
    }

    public double getPreviousAngle() {
        return previousAngle;
    }
}

package oopants.ants;

import java.awt.Color;
import java.awt.Point;
import java.util.concurrent.Semaphore;

import oopants.brain.Matrix;
import oopants.brain.Network;

public class Ant extends Thread {
    /**
     * this is abstract class for ant
     */
    public Semaphore allowedNextMove = new Semaphore(0);
    public Semaphore doneMakingMove = new Semaphore(0);
    public AntFitness fitness;

    protected boolean carriesFood = false;
    protected boolean allowedToDie = false;
    private int timeToDeath;
    private Network brain;
    private Point position;
    private Color color;
    private Direction lastMove;
    private Matrix input;

    public Ant(int[] brainSizes, Color color, Point position, int timeToDeath) {
        this.color = color;
        this.position = position;
        this.timeToDeath = timeToDeath;
        brain = new Network(brainSizes, 0.1, 1);
        fitness = new AntFitness();
    }

    public Ant(Network brain, Color color, Point position, int timeToDeath) {
        this.color = color;
        this.position = position;
        this.timeToDeath = timeToDeath;
        this.brain = brain;
        fitness = new AntFitness();
    }

    @Override
    public void run() {
        while (true) {
            try {
                allowedNextMove.acquire();
                if (allowedToDie) {
                    return;
                }
                lastMove = whereDoIWantToGo(input);
                timeToDeath -= 1;
                doneMakingMove.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public Direction whereDoIWantToGo(Matrix x) {
        // x is column vector of inputs to the neural network
        Matrix result = brain.feedforward(x);
        int max_index = 0;
        double max_value = result.get(0, 0);
        for (int y = 1; y < result.sizeY; y++) {
            if (result.get(0, y) > max_value) {
                max_index = y;
                max_value = result.get(0, y);
            }
        }
        switch (max_index) {
            case 0:
                return Direction.Up;
            case 1:
                return Direction.Down;
            case 2:
                return Direction.Left;
            case 3:
                return Direction.Right;
            default:
                return Direction.Stay;
        }
    }

    public void makeMeLiveLonger(int deltaLife) {
        timeToDeath += deltaLife;
    }

    public void setTimeToDeathToZero() {
        timeToDeath = 0;
    }

    public Color getColor() {
        return color;
    }

    public void setPosition(Point p) {
        position = p;
    }

    public void setPosition(int x, int y) {
        position = new Point(x, y);
    }

    public void addToPosition(int x, int y) {
        position.x += x;
        position.y += y;
    }

    public Point getPosition() {
        return position;
    }

    public Direction getLastMove() {
        return lastMove;
    }

    public void setLastMove(Direction lastMove) {
        this.lastMove = lastMove;
    }

    public void setInput(Matrix input) {
        this.input = input;
    }

    public void allowNextMove() {
        this.allowedNextMove.release();
    }

    public boolean isDead() {
        if (timeToDeath > 0) {
            return false;
        }
        return true;
    }

    public void setBrain(Network brain) {
        this.brain = brain;
    }

    public Network getBrain() {
        return brain;
    }

    public boolean hasFood() {
        return carriesFood;
    }

    public void setFood(boolean value) {
        carriesFood = value;
    }

    public int computeFitness() {
        return fitness.getFitness();
    }

    public String identify() {
        return "base";
    }
}

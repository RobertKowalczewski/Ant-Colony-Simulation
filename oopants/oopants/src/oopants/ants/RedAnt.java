package oopants.ants;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

import oopants.brain.Network;

public class RedAnt extends Ant {
    /**
     * this class defines red ants behaviour
     */
    public static HashMap<Point, ArrayList<RedAnt>> redAntPositions = new HashMap<Point, ArrayList<RedAnt>>();
    public static Point defaultPosition;

    public RedAnt(int[] brainSizes, int timeToDeath) {
        super(brainSizes, Color.red, (Point) defaultPosition.clone(), timeToDeath);
        redAntPositions.get(defaultPosition).add(this);

    }

    public RedAnt(Network brain, int timeToDeath) {
        super(brain, Color.red, (Point) defaultPosition.clone(), timeToDeath);
        redAntPositions.get(defaultPosition).add(this);
    }

    public boolean isInNest() {
        return RedAnt.defaultPosition.x == getPosition().x && RedAnt.defaultPosition.y == getPosition().y;
    }

    public void makeMove(int gridSize) {
        Point oldPos = (Point) getPosition().clone();
        switch (getLastMove()) {
            case Down:
                if (getPosition().y < gridSize - 1) {
                    redAntPositions.get(getPosition()).remove(this);
                    addToPosition(0, 1);
                    redAntPositions.get(getPosition()).add(this);
                }
                break;
            case Left:
                if (getPosition().x > 0) {
                    redAntPositions.get(getPosition()).remove(this);
                    addToPosition(-1, 0);
                    redAntPositions.get(getPosition()).add(this);
                }
                break;
            case Right:
                if (getPosition().x < gridSize - 1) {
                    redAntPositions.get(getPosition()).remove(this);
                    addToPosition(1, 0);
                    redAntPositions.get(getPosition()).add(this);
                }
                break;
            case Up:
                if (getPosition().y > 0) {
                    redAntPositions.get(getPosition()).remove(this);
                    addToPosition(0, -1);
                    redAntPositions.get(getPosition()).add(this);
                }
                break;
            case Stay:
                break;
        }
        fitness.onMove(oldPos, getPosition(), getLastMove(), gridSize);
    }

    public String identify() {
        return "Red";
    }

    public void putOutOfMisery() {
        allowedToDie = true;
        redAntPositions.get(getPosition()).remove(this);
    }
}

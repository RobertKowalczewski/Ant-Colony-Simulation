package oopants.ants;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

import oopants.brain.Network;

public class BlueAnt extends Ant {
    /**
     * this class defines blue ants behaviour
     */
    public static HashMap<Point, ArrayList<BlueAnt>> blueAntPositions = new HashMap<Point, ArrayList<BlueAnt>>();
    public static Point defaultPosition;

    public BlueAnt(int[] brainSizes, int timeToDeath) {
        super(brainSizes, Color.blue, (Point) defaultPosition.clone(), timeToDeath);
        blueAntPositions.get(defaultPosition).add(this);
    }

    public BlueAnt(Network brain, int timeToDeath) {
        super(brain, Color.blue, (Point) defaultPosition.clone(), timeToDeath);
        blueAntPositions.get(defaultPosition).add(this);
    }

    public void makeMove(int gridSize) {
        Point oldPos = (Point) getPosition().clone();
        switch (getLastMove()) {
            case Down:
                if (getPosition().y < gridSize - 1) {
                    blueAntPositions.get(getPosition()).remove(this);
                    addToPosition(0, 1);
                    blueAntPositions.get(getPosition()).add(this);
                }
                break;
            case Left:
                if (getPosition().x > 0) {
                    blueAntPositions.get(getPosition()).remove(this);
                    addToPosition(-1, 0);
                    blueAntPositions.get(getPosition()).add(this);
                }
                break;
            case Right:
                if (getPosition().x < gridSize - 1) {
                    blueAntPositions.get(getPosition()).remove(this);
                    addToPosition(1, 0);
                    blueAntPositions.get(getPosition()).add(this);
                }
                break;
            case Up:
                if (getPosition().y > 0) {
                    blueAntPositions.get(getPosition()).remove(this);
                    addToPosition(0, -1);
                    blueAntPositions.get(getPosition()).add(this);
                }
                break;
            case Stay:
                break;
        }
        fitness.onMove(oldPos, getPosition(), getLastMove(), gridSize);
    }

    public String identify() {
        return "Blue";
    }

    public void putOutOfMisery() {
        allowedToDie = true;
        blueAntPositions.get(getPosition()).remove(this);
    }
}

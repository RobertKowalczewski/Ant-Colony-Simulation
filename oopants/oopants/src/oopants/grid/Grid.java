package oopants.grid;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.List;
import java.util.Collections;

import oopants.ants.Ant;
import oopants.ants.BlueAnt;
import oopants.ants.RedAnt;
import oopants.brain.Matrix;
import oopants.brain.Network;

public class Grid {
    /**
     * this is the map of the entire simulation
     */
    private ArrayList<BlueAnt> blueAnts;
    private ArrayList<RedAnt> redAnts;
    private HashSet<Point> food;
    private int n;
    private int amountOfAnts;
    private int foodSigma;
    private int amountOfFood;
    private double lifetimeMean;
    private double lifetimeSigma;
    private int maxSightDistance = 10;
    private double mutationProbability = 0.4;

    public Grid(int n, int amountOfAnts, int amountOfFood, double lifetimeMean, double lifetimeSigma, int foodSigma) {
        this.n = n;
        this.amountOfAnts = amountOfAnts;
        this.amountOfFood = amountOfFood;
        this.lifetimeMean = lifetimeMean;
        this.lifetimeSigma = lifetimeSigma;
        this.foodSigma = foodSigma;
        this.blueAnts = new ArrayList<BlueAnt>();
        this.redAnts = new ArrayList<RedAnt>();

        this.food = new HashSet<Point>();
        Random random = new java.util.Random();

        RedAnt.defaultPosition = new Point(n / 2 - n / 3, n / 2);
        for (int i = 0; i < amountOfAnts; i++) {
            redAnts.add(new RedAnt(new int[] { (maxSightDistance * 2 + 1) *
            (maxSightDistance * 2 + 1) * 2 + 4,
            100, 4 }, randomLifetime(random)));
            //redAnts.add(new RedAnt(new int[] { 12, 4 }, randomLifetime(random)));
        }

        BlueAnt.defaultPosition = new Point(n / 2 + n / 3, n / 2);
        for (int i = 0; i < amountOfAnts; i++) {
            blueAnts.add(new BlueAnt(new int[] { (maxSightDistance * 2 + 1) *
            (maxSightDistance * 2 + 1) * 2 + 4,
            100, 4 }, randomLifetime(random)));
            //blueAnts.add(new BlueAnt(new int[] { 12, 4 }, randomLifetime(random)));
        }

        while (food.size() < amountOfFood) {
            int x = Math.min(Math.max((int) random.nextGaussian(n / 2, foodSigma), 0), n - 1);
            int y = Math.min(Math.max((int) random.nextGaussian(n / 2, foodSigma), 0), n - 1);

            Point point = new Point(x, y);
            food.add(point);
        }
    }

    public void refillFood() {
        Random random = new java.util.Random();
        while (food.size() < amountOfFood) {
            int x = Math.min(Math.max((int) random.nextGaussian(n / 2, foodSigma), 0), n - 1);
            int y = Math.min(Math.max((int) random.nextGaussian(n / 2, foodSigma), 0), n - 1);

            Point point = new Point(x, y);
            food.add(point);
        }
    }

    public Matrix getAntInputs(Ant ant) {
        Matrix inputs = new Matrix((maxSightDistance * 2 + 1) * (maxSightDistance * 2 + 1) * 2 + 4, 1);
        Point pos = ant.getPosition();
        int offset = (maxSightDistance * 2 + 1) * (maxSightDistance * 2 + 1);

        for (int i = -maxSightDistance; i < maxSightDistance; ++i) {
            for (int j = -maxSightDistance; j < maxSightDistance; ++j) {
                if (i == 0 && j == 0 || pos.x + i < 0 || pos.x + i >= n || pos.y + j < 0 || pos.y + j >= n) {
                    continue;
                }
                if (getFood().contains(new Point(pos.x + i, pos.y + j))) {
                    inputs.set(0, (i + maxSightDistance) + (j + maxSightDistance), 1);
                }
                if (ant.identify().equals("Red")) {
                    if (!BlueAnt.blueAntPositions.get(new Point(pos.x + i, pos.y + j)).isEmpty()) {
                        inputs.set(0, (i + maxSightDistance) + (j + maxSightDistance) + offset, 1);
                    }
                } else {
                    if (!RedAnt.redAntPositions.get(new Point(pos.x + i, pos.y + j)).isEmpty()) {
                        inputs.set(0, (i + maxSightDistance) + (j + maxSightDistance) + offset, 1);
                    }
                }
            }
        }
        double borderSightDistance = 4;
        offset = (maxSightDistance * 2 + 1) * (maxSightDistance * 2 + 1) * 2;
        // up
        if (ant.getPosition().y <= borderSightDistance) {
            inputs.set(0, offset, (borderSightDistance - ant.getPosition().y) / borderSightDistance);
        }
        // down
        if (ant.getPosition().y >= n - borderSightDistance) {
            inputs.set(0, offset + 1, (ant.getPosition().y - (n - borderSightDistance)) / borderSightDistance);
        }
        // left
        if (ant.getPosition().x <= borderSightDistance) {
            inputs.set(0, offset + 2, (borderSightDistance - ant.getPosition().x) / borderSightDistance);
        }
        // right
        if (ant.getPosition().x >= n - borderSightDistance) {
            inputs.set(0, offset + 3, (ant.getPosition().x - (n - borderSightDistance)) / borderSightDistance);
        }
        return inputs;
    }

    public Matrix getRedAntInputs(RedAnt ant) {
        Matrix inputs = new Matrix(12, 1);
        Point pos = ant.getPosition();

        // food
        // up
        for (int i = 1; i < maxSightDistance; i++) {
            if (pos.y - i < 0) {
                break;
            }
            if (getFood().contains(new Point(pos.x, pos.y - i))) {
                inputs.set(0, 0, 1 - i / maxSightDistance);
                break;
            }
        }
        // down
        for (int i = 1; i < maxSightDistance; i++) {
            if (pos.y + i >= n) {
                break;
            }
            if (getFood().contains(new Point(pos.x, pos.y + i))) {
                inputs.set(0, 1, 1 - i / maxSightDistance);
                break;
            }
        }
        // left
        for (int i = 1; i < maxSightDistance; i++) {
            if (pos.x - i < 0) {
                break;
            }
            if (getFood().contains(new Point(pos.x - i, pos.y))) {
                inputs.set(0, 2, 1 - i / maxSightDistance);
                break;
            }
        }
        // right
        for (int i = 1; i < maxSightDistance; i++) {
            if (pos.x + i >= n) {
                break;
            }
            if (getFood().contains(new Point(pos.x + i, pos.y))) {
                inputs.set(0, 3, 1 - i / maxSightDistance);
                break;
            }
        }
        // enemies
        // up
        for (int i = 1; i < maxSightDistance; i++) {
            if (pos.y - i < 0) {
                break;
            }
            if (!BlueAnt.blueAntPositions.get(new Point(pos.x, pos.y - i)).isEmpty()) {
                inputs.set(0, 4, (1 - i / maxSightDistance));
                break;
            }
        }
        // down
        for (int i = 1; i < maxSightDistance; i++) {
            if (pos.y + i >= n) {
                break;
            }
            if (!BlueAnt.blueAntPositions.get(new Point(pos.x, pos.y + i)).isEmpty()) {
                inputs.set(0, 5, (1 - i / maxSightDistance));
                break;
            }
        }
        // left
        for (int i = 1; i < maxSightDistance; i++) {
            if (pos.x - i < 0) {
                break;
            }
            if (!BlueAnt.blueAntPositions.get(new Point(pos.x - i, pos.y)).isEmpty()) {
                inputs.set(0, 6, (1 - i / maxSightDistance));
                break;
            }
        }
        // right
        for (int i = 1; i < maxSightDistance; i++) {
            if (pos.x + i >= n) {
                break;
            }
            if (!BlueAnt.blueAntPositions.get(new Point(pos.x + i, pos.y)).isEmpty()) {
                inputs.set(0, 7, (1 - i / maxSightDistance));
                break;
            }
        }
        // border
        double borderSightDistance = n;
        int offset = 8;
        // up
        if (ant.getPosition().y <= borderSightDistance) {
            inputs.set(0, offset, (borderSightDistance - ant.getPosition().y) / borderSightDistance);
        }
        // down
        if (ant.getPosition().y >= n - borderSightDistance) {
            inputs.set(0, offset + 1, (ant.getPosition().y - (n - borderSightDistance)) / borderSightDistance);
        }
        // left
        if (ant.getPosition().x <= borderSightDistance) {
            inputs.set(0, offset + 2, (borderSightDistance - ant.getPosition().x) / borderSightDistance);
        }
        // right
        if (ant.getPosition().x >= n - borderSightDistance) {
            inputs.set(0, offset + 3, (ant.getPosition().x - (n - borderSightDistance)) / borderSightDistance);
        }
        return inputs;
    }

    public Matrix getBlueAntInputs(BlueAnt ant) {
        Matrix inputs = new Matrix(12, 1);
        Point pos = ant.getPosition();

        // food
        // up
        for (int i = 1; i < maxSightDistance; i++) {
            if (pos.y - i < 0) {
                break;
            }
            if (getFood().contains(new Point(pos.x, pos.y - i))) {
                inputs.set(0, 0, 1 - i / maxSightDistance);
                break;
            }
        }
        // down
        for (int i = 1; i < maxSightDistance; i++) {
            if (pos.y + i >= n) {
                break;
            }
            if (getFood().contains(new Point(pos.x, pos.y + i))) {
                inputs.set(0, 1, 1 - i / maxSightDistance);
                break;
            }
        }
        // left
        for (int i = 1; i < maxSightDistance; i++) {
            if (pos.x - i < 0) {
                break;
            }
            if (getFood().contains(new Point(pos.x - i, pos.y))) {
                inputs.set(0, 2, 1 - i / maxSightDistance);
                break;
            }
        }
        // right
        for (int i = 1; i < maxSightDistance; i++) {
            if (pos.x + i >= n) {
                break;
            }
            if (getFood().contains(new Point(pos.x + i, pos.y))) {
                inputs.set(0, 3, 1 - i / maxSightDistance);
                break;
            }
        }
        // enemies
        // up
        for (int i = 1; i < maxSightDistance; i++) {
            if (pos.y - i < 0) {
                break;
            }
            if (!RedAnt.redAntPositions.get(new Point(pos.x, pos.y - i)).isEmpty()) {
                inputs.set(0, 4, (1 - i / maxSightDistance));
                break;
            }
        }
        // down
        for (int i = 1; i < maxSightDistance; i++) {
            if (pos.y + i >= n) {
                break;
            }
            if (!RedAnt.redAntPositions.get(new Point(pos.x, pos.y + i)).isEmpty()) {
                inputs.set(0, 5, (1 - i / maxSightDistance));
                break;
            }
        }
        // left
        for (int i = 1; i < maxSightDistance; i++) {
            if (pos.x - i < 0) {
                break;
            }
            if (!RedAnt.redAntPositions.get(new Point(pos.x - i, pos.y)).isEmpty()) {
                inputs.set(0, 6, (1 - i / maxSightDistance));
                break;
            }
        }
        // right
        for (int i = 1; i < maxSightDistance; i++) {
            if (pos.x + i >= n) {
                break;
            }
            if (!RedAnt.redAntPositions.get(new Point(pos.x + i, pos.y)).isEmpty()) {
                inputs.set(0, 7, (1 - i / maxSightDistance));
                break;
            }
        }
        // border
        double borderSightDistance = n;
        int offset = 8;
        // up
        if (ant.getPosition().y <= borderSightDistance) {
            inputs.set(0, offset, (borderSightDistance - ant.getPosition().y) / borderSightDistance);
        }
        // down
        if (ant.getPosition().y >= n - borderSightDistance) {
            inputs.set(0, offset + 1, (ant.getPosition().y - (n - borderSightDistance)) / borderSightDistance);
        }
        // left
        if (ant.getPosition().x <= borderSightDistance) {
            inputs.set(0, offset + 2, (borderSightDistance - ant.getPosition().x) / borderSightDistance);
        }
        // right
        if (ant.getPosition().x >= n - borderSightDistance) {
            inputs.set(0, offset + 3, (ant.getPosition().x - (n - borderSightDistance)) / borderSightDistance);
        }
        return inputs;
    }

    public int randomLifetime(Random random) {
        return Math.max((int) random.nextGaussian(lifetimeMean, lifetimeSigma), 0);
    }

    public RedAnt[] CreateNewRedAnts() {
        Random rng = new Random();
        int tSize = amountOfAnts / 10;
        RedAnt parent1 = tournamentRed(rng, tSize);
        RedAnt parent2 = tournamentRed(rng, tSize);
        Network[] childBrains;
        try {
            childBrains = Network.crossover(parent1.getBrain(), parent2.getBrain());
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;

        }
        if (Math.random() < mutationProbability) {
            childBrains[0].mutate();
        }
        if (Math.random() < mutationProbability) {
            childBrains[1].mutate();
        }
        RedAnt child1 = new RedAnt(childBrains[0], randomLifetime(rng));
        RedAnt child2 = new RedAnt(childBrains[1], randomLifetime(rng));
        return new RedAnt[] { child1, child2 };
    }

    public BlueAnt[] CreateNewBlueAnts() {
        Random rng = new Random();
        int tSize = amountOfAnts / 10;
        BlueAnt parent1 = tournamentBlue(rng, tSize);
        BlueAnt parent2 = tournamentBlue(rng, tSize);
        Network[] childBrains;
        try {
            childBrains = Network.crossover(parent1.getBrain(), parent2.getBrain());
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
        if (Math.random() < mutationProbability) {
            childBrains[0].mutate();
        }
        if (Math.random() < mutationProbability) {
            childBrains[1].mutate();
        }
        BlueAnt child1 = new BlueAnt(childBrains[0], randomLifetime(rng));
        BlueAnt child2 = new BlueAnt(childBrains[1], randomLifetime(rng));
        return new BlueAnt[] { child1, child2 };

    }

    public RedAnt getRandomRed(Random rng) {
        int[] antFitness = new int[getAmountOfAnts()];
        int fitnessSum = 0;
        for (int i = 0; i < redAnts.size(); i++) {
            antFitness[i] = redAnts.get(i).computeFitness();
            fitnessSum += antFitness[i];
        }
        double r = rng.nextDouble() * fitnessSum;

        int sum = 0;
        for (int i = 0; i < redAnts.size(); i++) {
            sum += antFitness[i];
            if (sum >= r) {
                return redAnts.get(i);
            }
        }
        return null; // should only happen when there are no entries
    }

    public BlueAnt getRandomBlue(Random rng) {
        int[] antFitness = new int[getAmountOfAnts()];
        int fitnessSum = 0;
        for (int i = 0; i < blueAnts.size(); i++) {
            antFitness[i] = blueAnts.get(i).computeFitness();
            fitnessSum += antFitness[i];
        }
        double r = rng.nextDouble() * fitnessSum;

        int sum = 0;
        for (int i = 0; i < blueAnts.size(); i++) {
            sum += antFitness[i];
            if (sum >= r) {
                return blueAnts.get(i);
            }
        }
        return null; // should only happen when there are no entries
    }

    public RedAnt tournamentRed(Random rng, int tSize) {
        List<RedAnt> chosen = new ArrayList<RedAnt>();
        for (int i = 0; i < tSize; ++i) {
            int r = rng.nextInt(redAnts.size());
            chosen.add(redAnts.get(r));
        }
        Collections.sort(chosen, (a, b) -> b.computeFitness() - a.computeFitness());
        return chosen.get(0);
    }

    public BlueAnt tournamentBlue(Random rng, int tSize) {
        List<BlueAnt> chosen = new ArrayList<BlueAnt>();
        for (int i = 0; i < tSize; ++i) {
            int r = rng.nextInt(blueAnts.size());
            chosen.add(blueAnts.get(r));
        }
        Collections.sort(chosen, (a, b) -> b.computeFitness() - a.computeFitness());
        return chosen.get(0);
    }

    public void checkForCombatRed(RedAnt ant) {
        Point pos = ant.getPosition();
        if (!BlueAnt.blueAntPositions.get(pos).isEmpty()) {
            for (BlueAnt blueAnt : BlueAnt.blueAntPositions.get(pos)) {
                blueAnt.setTimeToDeathToZero();
                ant.fitness.onCollectedNewFood(blueAnt.fitness.getCollectedFood());
            }
        }
    }

    public void checkForCombatBlue(BlueAnt ant) {
        Point pos = ant.getPosition();
        if (!RedAnt.redAntPositions.get(pos).isEmpty()) {
            for (RedAnt redAnt : RedAnt.redAntPositions.get(pos)) {
                redAnt.setTimeToDeathToZero();
                ant.fitness.onCollectedNewFood(redAnt.fitness.getCollectedFood());
            }
        }
    }

    public ArrayList<BlueAnt> getBlueAnts() {
        return blueAnts;
    }

    public ArrayList<RedAnt> getRedAnts() {
        return redAnts;
    }

    public void addRedAnt(RedAnt ant) {
        redAnts.add(ant);
    }

    public void addBlueAnt(BlueAnt ant) {
        blueAnts.add(ant);
    }

    public void setBlueAnts(ArrayList<BlueAnt> ants) {
        this.blueAnts = ants;
    }

    public void setRedAnts(ArrayList<RedAnt> ants) {
        this.redAnts = ants;
    }

    public HashSet<Point> getFood() {
        return food;
    }

    public int getSize() {
        return n;
    }

    public int getAmountOfAnts() {
        return amountOfAnts;
    }
}

package oopants.ants;

import java.awt.Point;

public class AntFitness {
    /**
     * Class that keeps track of statistics that help with evaluation of ants.
     * Each statistic has an assigned weight that can be set before the simulation
     */

    // collector traits
    private int collectedFood = 0;
    private int distanceTraveledInDirectionOfClosestFood = 0;
    private int timeSpentOnTheBorderOfTheMap = 0;
    private int movingAwayFromEnemies = 0;

    public int kills;
    public int distanceTraveledInDirectionOfClosestWorkerAntWithFood;
    public int distanceTraveledInDirectionOfClosestWorkerAnt;
    public int movingAwayFromWorkerAntsWithFood;
    // for now hardcoded
    private int[] weights = new int[] { 20, 0, -1, 0, 0, 0, 0, 0 };
    private int weightsSum;

    private int lastComputedFitness = 100;
    private boolean needToRecompute = true;

    AntFitness() {
        for (int w : weights) {
            weightsSum += w;
        }
    }

    private void evaluate() {
        int sum = 100;
        sum += collectedFood * weights[0];
        sum += distanceTraveledInDirectionOfClosestFood * weights[1];
        sum += timeSpentOnTheBorderOfTheMap * weights[2];
        sum += movingAwayFromEnemies * weights[3];
        // lastComputedFitness = Math.max(sum, 0);

        lastComputedFitness = sum;
        needToRecompute = false;
    }

    public int getFitness() {
        if (needToRecompute) {
            evaluate();
        }
        // System.out.println(lastComputedFitness);
        return lastComputedFitness;
    }

    public void onCollectedNewFood(int amount) {
        collectedFood += amount;
        needToRecompute = true;
    }

    public int getCollectedFood() {
        return collectedFood;
    }

    public void onMove(Point oldPos, Point newPos, Direction direction, int mapSize) {
        // if (newPos.x == 0 || newPos.x == mapSize - 1 || newPos.y == 0 || newPos.y ==
        // mapSize - 1) {
        // timeSpentOnTheBorderOfTheMap += 1;
        // needToRecompute = true;
        // }
        double threshold = mapSize / 4.0;
        double penalityY = 0;
        double penalityX = 0;
        if (newPos.y < threshold) {
            penalityY = 1 - newPos.y / threshold;
        } else if (newPos.y >= mapSize - threshold) {
            penalityY = (newPos.y - mapSize + threshold) / threshold;
        }
        if (newPos.x < threshold) {
            penalityX = 1 - newPos.x / threshold;
        } else if (newPos.x >= mapSize - threshold) {
            penalityX = (newPos.x - mapSize + threshold) / threshold;
        }
        double sum = penalityX + penalityY;
        timeSpentOnTheBorderOfTheMap += sum / 10.0;
        needToRecompute = true;
    }

    public void onDeath() {

    }

    public void onKill() {

    }

    public int getWeightsSum() {
        return weightsSum;
    }
}

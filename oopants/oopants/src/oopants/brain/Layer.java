package oopants.brain;

import java.util.Random;

public class Layer {
    /*
     * this is how layer for neural network is defined
     */
    public int size;
    public Matrix weights;
    public Matrix biases;

    public Layer(int size) {
        this.size = size;
    }

    public Layer() {
    }

    public void initializeLayer(int previousLayerSize, double wieightsScalingFactor) {
        weights = new Matrix(size, previousLayerSize);
        biases = new Matrix(size, 1);

        Random rng = new Random();
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < previousLayerSize; x++) {
                weights.set(x, y, rng.nextGaussian() / Math.sqrt(wieightsScalingFactor));
            }
            biases.set(0, y, rng.nextGaussian());
        }
    }

    public Matrix compute(Matrix x) {
        Matrix resultZ = weights.dot(x).add(biases);
        // apply sigmoid activation function
        for (int y = 0; y < size; y++) {
            resultZ.set(0, y, 1.0 / (1.0 + Math.exp(-resultZ.get(0, y))));
        }
        return resultZ;
    }

    void mutateWeight(int x, int y, double amount) {
        double newValueChance = 0.05;
        double changeByPercentageChance = 0.6;
        double addOrSubtractNumberChance = 0.3;
        //double changeSignChance = 0.05;
        double r = Math.random();

        if (r < newValueChance) {
            weights.set(x, y, amount);
        } else if (r < newValueChance + changeByPercentageChance) {
            weights.set(x, y, weights.get(x, y) * (Math.random() / 2 + 0.25));
        } else if (r < newValueChance + changeByPercentageChance + addOrSubtractNumberChance) {
            weights.addElementwise(x, y, (Math.random() * 2 - 1) / 2);
        } else {
            weights.set(x, y, -weights.get(x, y));
        }
    }

    void mutateBias(int y, double amount) {
        biases.addElementwise(0, y, amount);
    }

    void exchangeNeuronWeightsAndBias(int neuron, Matrix newWeights, double bias) {
        for (int i = 0; i < weights.sizeX; i++) {
            weights.set(i, neuron, newWeights.get(i, neuron));
        }
        biases.set(0, neuron, bias);
    }

}

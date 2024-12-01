package oopants.brain;

import java.util.Random;

public class Network implements Cloneable {
    public Layer[] layers;
    public int size;
    double mutationSigma;
    int[] layerSizes;
    double wieightsScalingFactor;

    public Network(int[] layerSizes, double wieightsScalingFactor, double mutationSigma) {
        // where first layer is an input layer - has no weights and biases
        /*
         * this is neural network for ants
         */
        this.wieightsScalingFactor = wieightsScalingFactor;
        this.layerSizes = layerSizes;
        size = layerSizes.length;
        layers = new Layer[size];
        this.mutationSigma = mutationSigma;

        for (int i = 0; i < size; i++) {
            layers[i] = new Layer(layerSizes[i]);
        }
        for (int i = 1; i < size; i++) {
            this.layers[i].initializeLayer(this.layers[i - 1].size, wieightsScalingFactor);
        }
    }

    public Network(int[] layerSizes, double wieightsScalingFactor, double mutationSigma,
            Layer[] oldLayers) {

        this.wieightsScalingFactor = wieightsScalingFactor;
        this.layerSizes = layerSizes;
        size = layerSizes.length;
        this.mutationSigma = mutationSigma;
        layers = new Layer[size];
        for (int i = 0; i < size; i++) {
            layers[i] = new Layer(layerSizes[i]);
            if (i >= 1) {
                layers[i].weights = oldLayers[i].weights.copy();
                layers[i].biases = oldLayers[i].biases.copy();
            }
        }
    }

    public Matrix feedforward(Matrix x) {
        // x is a column vector of size (layers[0].size, 1)
        Matrix output = x;
        for (int i = 1; i < size; i++) {
            output = layers[i].compute(output);
        }
        return output;
    }

    public void mutate() {
        Random rng = new Random();
        double mutationRatio = 0.05;
        double biasMutationRatio = 0.1;

        for (int i = 1; i < size; i++) {
            int expected = (int) Math.floor(layerSizes[i - 1] * layerSizes[i] * mutationRatio);
            for (int j = 0; j < expected; j++) {
                layers[i].mutateWeight(rng.nextInt(layers[i - 1].size), rng.nextInt(layers[i].size),
                        rng.nextGaussian(0, mutationSigma));
            }
        }

        for (int i = 1; i < size; i++) {
            int expectedBiases = (int) Math.floor(layers[i].size * biasMutationRatio);
            for (int j = 0; j < expectedBiases; j++) {
                layers[i].mutateBias(rng.nextInt(layers[i].size), rng.nextGaussian(0, mutationSigma) / 3);
            }
        }
    }

    public static Network[] crossover(Network net1, Network net2) throws CloneNotSupportedException {
        double neuronExchangeProbability = 0.05;
        Network child1 = new Network(net1.layerSizes, net1.wieightsScalingFactor, net1.mutationSigma, net1.layers);
        Network child2 = new Network(net2.layerSizes, net2.wieightsScalingFactor, net2.mutationSigma, net2.layers);

        for (int layer = 1; layer < net1.size; layer++) {
            for (int neuron = 0; neuron < child1.layerSizes[layer]; neuron++) {
                if (Math.random() < neuronExchangeProbability) {
                    child1.layers[layer].exchangeNeuronWeightsAndBias(neuron,
                            net2.layers[layer].weights, net2.layers[layer].biases.get(0, neuron));
                }
                if (Math.random() < neuronExchangeProbability) {
                    child2.layers[layer].exchangeNeuronWeightsAndBias(neuron,
                            net1.layers[layer].weights, net1.layers[layer].biases.get(0, neuron));
                }
            }
        }

        return new Network[] { child1, child2 };
    }
}

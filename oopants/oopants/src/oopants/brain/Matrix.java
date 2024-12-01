package oopants.brain;

public class Matrix {
    /*
     * this is the class that implements mathematical matrics that is used by neural network
     */
    double[][] m;
    public int sizeY;
    public int sizeX;

    public Matrix(int sizeY, int sizeX) {
        m = new double[sizeY][sizeX];
        this.sizeX = sizeX;
        this.sizeY = sizeY;
    }

    public Matrix(double[] values) {
        m = new double[values.length][1];
        this.sizeX = 1;
        this.sizeY = values.length;
        for (int y = 0; y < this.sizeY; y++) {
            set(0, y, values[y]);
        }
    }

    public double get(int x, int y) {
        return m[y][x];
    }

    public void set(int x, int y, double value) {
        m[y][x] = value;
    }

    public void addElementwise(int x, int y, double value) {
        m[y][x] += value;
    }

    public Matrix add(Matrix other) {
        // adding two matricies elementwise, assumes the same shape of matricies
        Matrix result = new Matrix(sizeY, sizeX);
        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                result.set(x, y, this.get(x, y) + other.get(x, y));
            }
        }
        return result;
    }

    public Matrix addVector(Matrix other) {
        // adding matrix and column vector together
        Matrix result = new Matrix(sizeY, sizeX);
        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                result.set(x, y, this.get(x, y) + other.get(0, y));
            }
        }
        return result;
    }

    public Matrix dot(Matrix other) {
        Matrix result = new Matrix(sizeY, other.sizeX);
        for (int i = 0; i < this.sizeY; i++) {
            for (int j = 0; j < other.sizeX; j++) {
                for (int k = 0; k < other.sizeY; k++)
                    result.addElementwise(j, i, this.get(k, i) * other.get(j, k));
            }
        }
        return result;
    }

    public void print() {
        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                System.out.print(String.valueOf(get(x, y)) + " ");
            }
            System.out.println();
        }
    }

    public Matrix copy() {
        Matrix newCopy = new Matrix(sizeY, sizeX);
        for (int y = 0; y < sizeY; y++) {
            newCopy.m[y] = m[y].clone();
        }
        return newCopy;
    }
}

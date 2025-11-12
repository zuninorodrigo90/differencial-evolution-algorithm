import java.util.Arrays;

public class Individual {
    public final double[] position;   // current candidate vector
    public double fitness;            // f(position)

    public Individual(int dimension) {
        this.position = new double[dimension];
        this.fitness = Double.POSITIVE_INFINITY;
    }

    public void copyFrom(Individual other) {
        System.arraycopy(other.position, 0, this.position, 0, this.position.length);
        this.fitness = other.fitness;
    }

    public Individual cloneDeep() {
        Individual c = new Individual(position.length);
        System.arraycopy(this.position, 0, c.position, 0, position.length);
        c.fitness = this.fitness;
        return c;
    }

    @Override
    public String toString() {
        return "Individual{fitness=" + fitness + ", position=" + Arrays.toString(position) + "}";
    }
}

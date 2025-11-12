@FunctionalInterface
public interface ObjectiveFunction {
    // Returns the objective value to MINIMIZE for a candidate vector x.
    double evaluate(double[] x);
}

import java.util.Random;

public class ObjectiveFunctions {

    static final Random rnd = new Random();

    public static double evaluate(int f, double[] x) {
        return switch (f) {
            case 1 -> f1(x);
            case 2 -> f2(x);
            case 3 -> f3(x);
            case 4 -> f4(x);
            case 5 -> f5(x);
            case 6 -> f6(x);
            case 7 -> f7(x);
            case 8 -> f8(x);
            case 9 -> f9(x);
            case 10 -> f10(x);
            default -> Double.NaN;
        };
    }

    // f1 - Sphere
    static double f1(double[] x) {
        double s = 0;
        for (double xi : x) s += xi * xi;
        return s;
    }

    // f2 - Schwefel 2.22
    static double f2(double[] x) {
        double sum = 0;
        double mul = 1;
        for (double xi : x) {
            sum += Math.abs(xi);
            mul *= Math.abs(xi);
        }
        return sum + mul;
    }

    // f3 - Schwefel 1.2
    static double f3(double[] x) {
        double total = 0;
        for (int i = 0; i < x.length; i++) {
            double ps = 0;
            for (int j = 0; j <= i; j++) ps += x[j];
            total += ps * ps;
        }
        return total;
    }

    // f4 - Schwefel 2.21
    static double f4(double[] x) {
        double max = Math.abs(x[0]);
        for (int i = 1; i < x.length; i++) {
            max = Math.max(max, Math.abs(x[i]));
        }
        return max;
    }

    // f5 - Rosenbrock
    static double f5(double[] x) {
        double sum = 0;
        for (int i = 0; i < x.length - 1; i++) {
            double a = x[i + 1] - x[i] * x[i];
            double b = 1 - x[i];
            sum += 100 * a * a + b * b;
        }
        return sum;
    }

    // f6 - Step Function
    static double f6(double[] x) {
        double sum = 0;
        for (double xi : x) sum += Math.floor(xi + 0.5) * Math.floor(xi + 0.5);
        return sum;
    }

    // f7 - Quartic + random noise
    static double f7(double[] x) {
        double s = 0;
        for (int i = 0; i < x.length; i++) s += (i + 1) * x[i] * x[i] * x[i] * x[i];
        return s + rnd.nextDouble();
    }

    // f8 - Schwefel
    static double f8(double[] x) {
        double s = 0;
        for (double xi : x) s += -xi * Math.sin(Math.sqrt(Math.abs(xi)));
        return s;
    }

    // f9 - Rastrigin
    static double f9(double[] x) {
        double sum = 10 * x.length;
        for (double xi : x) sum += xi * xi - 10 * Math.cos(2 * Math.PI * xi);
        return sum;
    }

    // f10 - Ackley
    static double f10(double[] x) {
        double a = 0;
        double b = 0;
        for (double xi : x) {
            a += xi * xi;
            b += Math.cos(2 * Math.PI * xi);
        }
        double n = x.length;
        return -20 * Math.exp(-0.2 * Math.sqrt(a / n))
                - Math.exp(b / n)
                + 20 + Math.E;
    }
}

import java.util.Random;
import java.util.Scanner;

public class Main {

    // ===== Fixed hyperparameters (change here if you want) =====
    static final double F = 0.8;     // differential weight (typical 0.5–0.9)
    static final double CR = 0.9;    // crossover rate (typical 0.5–0.9)
    static final int GMAX = 2000;    // number of generations (simple stopping rule)

    // Bounds (applied with simple clip)
    static final double LOWER = -30.0;
    static final double UPPER =  30.0;

    // RNG (fixed seed for reproducibility)
    static final Random RNG = new Random(42);

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter population size NP: ");
        int NP = sc.nextInt();

        System.out.print("Enter problem dimension D: ");
        int D = sc.nextInt();

        // Choose objective function
        System.out.println("Choose objective: 1=Sphere, 2=SumSquares, 3=Rosenbrock, 4=Schwefel-like");
        int choice = sc.nextInt();
        ObjectiveFunction f = chooseObjective(choice);

        // ---- 1) Initialize population uniformly in [LOWER, UPPER] ----
        Individual[] pop = new Individual[NP];
        for (int i = 0; i < NP; i++) {
            pop[i] = new Individual(D);
            for (int j = 0; j < D; j++) {
                pop[i].position[j] = uniform(LOWER, UPPER);
            }
            pop[i].fitness = f.evaluate(pop[i].position);
        }

        // Track global best
        Individual gBest = bestOf(pop).cloneDeep();

        // ---- 2) Evolution loop (DE/rand/1/bin) ----
        for (int gen = 0; gen < GMAX; gen++) {
            for (int i = 0; i < NP; i++) {
                // --- Mutation: v = x[r1] + F * (x[r2] - x[r3]) ---
                int r1, r2, r3;
                do { r1 = RNG.nextInt(NP); } while (r1 == i);
                do { r2 = RNG.nextInt(NP); } while (r2 == i || r2 == r1);
                do { r3 = RNG.nextInt(NP); } while (r3 == i || r3 == r1 || r3 == r2);

                double[] v = new double[D];
                for (int j = 0; j < D; j++) {
                    v[j] = pop[r1].position[j] + F * (pop[r2].position[j] - pop[r3].position[j]);
                    // simple bound handling: clip to [LOWER, UPPER]
                    if (v[j] < LOWER) v[j] = LOWER;
                    if (v[j] > UPPER) v[j] = UPPER;
                }

                // --- Binomial crossover: produce trial vector u ---
                double[] u = new double[D];
                int jRand = RNG.nextInt(D); // ensure at least one gene from v
                for (int j = 0; j < D; j++) {
                    if (RNG.nextDouble() < CR || j == jRand) {
                        u[j] = v[j];
                    } else {
                        u[j] = pop[i].position[j];
                    }
                }

                // --- Selection: replace if trial is not worse (minimization) ---
                double fu = f.evaluate(u);
                if (fu <= pop[i].fitness) {
                    System.arraycopy(u, 0, pop[i].position, 0, D);
                    pop[i].fitness = fu;

                    if (fu < gBest.fitness) {
                        gBest = pop[i].cloneDeep();
                    }
                }
            }

            // Print progress occasionally
            if (gen % 100 == 0 || gen == GMAX - 1) {
                System.out.println("Gen " + gen + " -> gBest = " + gBest.fitness);
            }
        }

        // ---- 3) Final result ----
        System.out.println("====================================");
        System.out.println("Best solution value found: " + gBest.fitness);
        System.out.print("Best position: [");
        for (int d = 0; d < gBest.position.length; d++) {
            System.out.print(gBest.position[d]);
            if (d < gBest.position.length - 1) System.out.print(", ");
        }
        System.out.println("]");

        sc.close();
    }

    // ===== Helpers =====

    // Select objective based on user choice; all are minimization
    static ObjectiveFunction chooseObjective(int c) {
        return switch (c) {
            case 1 -> Main::sphere;
            case 2 -> Main::sumSquares;
            case 4 -> Main::schwefelLike;
            case 3 -> Main::rosenbrock; // default to Rosenbrock if 3 or anything else
            default -> Main::rosenbrock;
        };
    }

    // Sphere: sum(x_i^2)
    static double sphere(double[] x) {
        double s = 0.0;
        for (double xi : x) s += xi * xi;
        return s;
    }

    // SumSquares: sum_{i=1..n} (sum_{j=1..i} x_j)^2
    static double sumSquares(double[] x) {
        double total = 0.0;
        double partial = 0.0;
        for (double xi : x) {
            partial += xi;
            total += partial * partial;
        }
        return total;
    }

    // Rosenbrock (classic): sum_{i=1..n-1} [100*(x_{i+1} - x_i^2)^2 + (1 - x_i)^2]
    static double rosenbrock(double[] x) {
        double sum = 0.0;
        for (int i = 0; i < x.length - 1; i++) {
            double xi = x[i];
            double xi1 = x[i + 1];
            double a = (xi1 - xi * xi);
            sum += 100.0 * a * a + (1.0 - xi) * (1.0 - xi);
        }
        return sum;
    }

    // Schwefel-like (negative xi * sin(sqrt(|xi|)), but we minimize the negative sum -> same as your objective4)
    static double schwefelLike(double[] x) {
        double s = 0.0;
        for (double xi : x) {
            s += -xi * Math.sin(Math.sqrt(Math.abs(xi)));
        }
        return s;
    }

    static Individual bestOf(Individual[] pop) {
        Individual best = pop[0];
        for (int i = 1; i < pop.length; i++) {
            if (pop[i].fitness < best.fitness) best = pop[i];
        }
        return best;
    }

    static double uniform(double a, double b) {
        return a + RNG.nextDouble() * (b - a);
    }
}


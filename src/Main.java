import java.util.*;

public class Main {

    static final double LOWER = -10.0; // lower bound of search space
    static final double UPPER = 10.0; // upper bound of search space
    static final int GMAX = 2000; // number of generations (stopping criterion)
    static final int POP_SIZE = 40; // size of the population (number of individuals)
    static final int dimension = 30; // dimensionality of the problem (size of the vector)
    static final int runs = 10; // number of independent runs for statistical evaluation

    // DE hyperparams (will be overridden by configs)
    static double F; // differential weight (mutation strength)
    static double CR; // crossover rate (probability of mixing vectors)

    static final Random RNG = new Random(System.nanoTime());

    static class DEConfig {
        final double F;
        final double CR;
        final String name;

        public DEConfig(double F, double CR, String name) {
            this.F = F;
            this.CR = CR;
            this.name = name;
        }
    }

    static List<DEConfig> configs = List.of(
            new DEConfig(0.8, 0.9, "baseline"),
            new DEConfig(0.9, 0.5, "exploration"),
            new DEConfig(0.5, 0.9, "exploitation"),
            new DEConfig(0.6, 0.6, "balanced"),
            new DEConfig(1.0, 0.3, "aggressive mutation")
    );


    public static void main(String[] args) {

        long startTime = System.currentTimeMillis();

        for (int functionId = 1; functionId <= 10; functionId++) {

            System.out.println("\n====================================================");
            System.out.println("                FUNCTION f" + functionId);
            System.out.println("====================================================");

            int index = 1;
            for (DEConfig cfg : configs) {

                F = cfg.F;
                CR = cfg.CR;

                System.out.printf(" %s | F=%.2f CR=%.2f | RUN VALUES:\n", cfg.name, cfg.F, cfg.CR);

                double total = 0;

                for (int r = 1; r <= runs; r++) {
                    double result = runDE(functionId);
                    total += result;
                    System.out.printf("   run %2d: %.15f\n", r, result);
                }

                double avg = total / runs;
                System.out.printf("   AVERAGE = %.15f\n\n", avg);

                index++;
            }
        }

        long endTime = System.currentTimeMillis();
        System.out.println("\n====================================================");
        System.out.printf("TOTAL EXECUTION TIME: %.2f seconds%n", (endTime - startTime) / 1000.0);
        System.out.println("====================================================");
    }


    static double runDE(int functionId) {

        Individual[] pop = new Individual[POP_SIZE];
        for (int i = 0; i < POP_SIZE; i++) {
            pop[i] = new Individual(dimension);
            for (int j = 0; j < dimension; j++) {
                pop[i].position[j] = LOWER + RNG.nextDouble() * (UPPER - LOWER);
            }
            pop[i].fitness = ObjectiveFunctions.evaluate(functionId, pop[i].position);
        }

        Individual gBest = bestOf(pop).cloneDeep();

        for (int gen = 0; gen < GMAX; gen++) {

            for (int i = 0; i < POP_SIZE; i++) {

                int r1, r2, r3;
                do {
                    r1 = RNG.nextInt(POP_SIZE);
                } while (r1 == i);
                do {
                    r2 = RNG.nextInt(POP_SIZE);
                } while (r2 == i || r2 == r1);
                do {
                    r3 = RNG.nextInt(POP_SIZE);
                } while (r3 == i || r3 == r1 || r3 == r2);

                double[] v = new double[dimension];
                for (int j = 0; j < dimension; j++) {
                    v[j] = pop[r1].position[j] + F * (pop[r2].position[j] - pop[r3].position[j]);
                    if (v[j] > UPPER) v[j] = UPPER;
                    if (v[j] < LOWER) v[j] = LOWER;
                }

                double[] u = new double[dimension];
                int jRand = RNG.nextInt(dimension);
                for (int j = 0; j < dimension; j++) {
                    if (RNG.nextDouble() < CR || j == jRand) u[j] = v[j];
                    else u[j] = pop[i].position[j];
                }

                double fu = ObjectiveFunctions.evaluate(functionId, u);
                if (fu <= pop[i].fitness) {
                    System.arraycopy(u, 0, pop[i].position, 0, dimension);
                    pop[i].fitness = fu;

                    if (fu < gBest.fitness) {
                        gBest = pop[i].cloneDeep();
                    }
                }
            }
        }
        return gBest.fitness;
    }


    static Individual bestOf(Individual[] pop) {
        Individual best = pop[0];
        for (int i = 1; i < pop.length; i++) {
            if (pop[i].fitness < best.fitness)
                best = pop[i];
        }
        return best;
    }
}

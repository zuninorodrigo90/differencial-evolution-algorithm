# Differential Evolution (DE)

This repository contains a minimal implementation of the **Differential Evolution (DE)** algorithm using the classic **DE/rand/1/bin** strategy in plain Java (tested with Java 25).

---

## Overview

**Differential Evolution (DE)** is a population-based, derivative-free optimization algorithm for real-valued functions.
It is robust for **non-linear**, **multimodal**, and **noisy** optimization problems.
DE evolves a population of candidate vectors through three main steps in each generation:

1. **Mutation** — combine random individuals to create a *mutant vector* using scaled differences.
2. **Crossover** — mix the mutant vector with the parent to generate a *trial vector*.
3. **Selection** — keep the better individual between the parent and trial (for minimization).

---
## Configuration used

| Config name         | F   | CR  | Characteristics    |
| ------------------- | --- | --- | ------------------ |
| baseline            | 0.8 | 0.9 | common standard    |
| exploration         | 0.9 | 0.5 | stronger mutation  |
| exploitation        | 0.5 | 0.9 | stronger crossover |
| balanced            | 0.6 | 0.6 | trade-off          |
| aggressive mutation | 1.0 | 0.3 | extreme mutation   |


Each configuration is applied to each function over N runs.

---

## Algorithm Parameters

| Symbol | Meaning | Typical Range / Notes |
|:-------|:---------|:----------------------|
| `NP` | Population size | ≈ `5 × D` – `10 × D` |
| `D` | Problem dimension (number of variables) | ≥ 1 |
| `F` | Differential weight (mutation factor) | 0.5 – 0.9  (typically 0.8) |
| `CR` | Crossover rate | 0.5 – 0.9  (typically 0.9) |
| `Gmax` | Number of generations | user-defined |
| `L, U` | Lower / upper bounds | values are clipped to this range |

---

## Configuration used
For each f1..f10:
```
====================================================
FUNCTION f7
====================================================
exploitation | F=0.50 CR=0.90 | RUN VALUES:
run  1: 0.001731923445812
run  2: 0.003510879334112
...
AVERAGE = 0.002947191443272
```
This produces:

- 10 objective function evaluations ×

- 5 configurations ×

- 10 repeated runs each

- Total: 500 optimization runs per execution.


---

## Core DE Strategy (DE/rand/1/bin)
For each candidate:

```
mutant = pop[r1] + F * (pop[r2] - pop[r3])
trial[i] = (rand < CR) ? mutant[i] : parent[i]
if trial is better → accept

```
Boundary handling: clipping
````
if v[j] > UPPER → v[j] = UPPER
if v[j] < LOWER → v[j] = LOWER

````

---

## Pseudocode — DE/rand/1/bin
```

Input:
f(x)       // objective function to minimize
D          // number of variables (dimension)
L, U       // lower and upper bounds
NP         // population size
F          // differential weight (0.5–0.9)
CR         // crossover rate (0.5–0.9)
Gmax       // number of generations

Output:
x_best, f_best

1) Initialization
   for i = 1..NP:
   for j = 1..D:
   x[i][j] = Uniform(L[j], U[j])
   f[i] = f(x[i])
   (x_best, f_best) = argmin_i f[i]

2) Evolution Loop
   for g = 1..Gmax:
   for i = 1..NP:

           // --- Mutation ---
           choose r1, r2, r3 distinct and not i
           v = x[r1] + F * (x[r2] - x[r3])
           v = clip(v, L, U)

           // --- Crossover (binomial) ---
           u = empty vector
           j_rand = random index in {1..D}
           for j = 1..D:
               if rand(0,1) < CR or j == j_rand:
                   u[j] = v[j]
               else:
                   u[j] = x[i][j]

           // --- Selection ---
           fu = f(u)
           if fu ≤ f[i]:
               x[i] = u
               f[i] = fu
               if fu < f_best:
                   x_best = u
                   f_best = fu

3) Return (x_best, f_best)
```

---

## Java Structure

| File | Description |
|------|--------------|
| **`ObjectiveFunction.java`** | Functional interface for the objective function (`double evaluate(double[] x)`). |
| **`Individual.java`** | Represents one candidate (position vector + fitness value). |
| **`MainDE.java`** | Contains the Differential Evolution algorithm and several example objective functions. |

## Compile and Run
```
javac *.java
java MainDE
```

---

## Example Run

When executed, the program will prompt for:

```
Enter population size NP:
Enter problem dimension D:
Choose objective: 1=Sphere, 2=SumSquares, 3=Rosenbrock, 4=Schwefel-like
```

### Example Output

```
Enter population size NP: 30
Enter problem dimension D: 5
Choose objective: 3
Gen 0 -> gBest = 320.158
Gen 100 -> gBest = 8.935
Gen 200 -> gBest = 0.003
====================================
Best solution value found: 0.0019
Best position: [0.997, 0.996, 0.997, 0.996, 0.997]
```

---

## Objective Functions

Available benchmark functions (to **minimize**):

| ID | Function        | Landscape                  |
| -: | --------------- | -------------------------- |
|  1 | Sphere          | Smooth, convex             |
|  2 | Schwefel 2.22   | Absolute + product         |
|  3 | Schwefel 1.2    | Cumulative sums            |
|  4 | Schwefel 2.21   | Uses max(abs(xi))          |
|  5 | Rosenbrock      | Narrow curved valley       |
|  6 | Step            | Discontinuous, plateaus    |
|  7 | Quartic + noise | Increasing polynomial      |
|  8 | Schwefel        | Highly multimodal          |
|  9 | Rastrigin       | Highly multimodal periodic |
| 10 | Ackley          | Common GA/DE benchmark     |

All functions return a real number to be minimized.

---


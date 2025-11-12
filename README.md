# Differential Evolution (DE) — Simple Java Implementation

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

## Pseudocode — DE/rand/1/bin

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

| Option | Name | Definition | Description |
|:------:|------|-------------|--------------|
| **1** | Sphere | f(x) = Σ x_i² | Simple convex quadratic bowl. |
| **2** | SumSquares | f(x) = Σ (Σ_{j=1}^i x_j)² | Uses cumulative sums; harder than Sphere. |
| **3** | Rosenbrock | f(x) = Σ [100(x_{i+1}-x_i²)² + (1-x_i)²] | Classic non-convex "banana" valley. |
| **4** | Schwefel-like | f(x) = -Σ x_i sin(√|x_i|) | Multimodal function with many local minima. |

You can modify or extend the objectives inside `MainDE.java` by implementing the `ObjectiveFunction` interface.

---

## Notes

- **Strategy used:** `DE/rand/1/bin`
- **Boundary handling:** simple clipping to `[LOWER, UPPER]`
- **Stopping criterion:** fixed number of generations (`GMAX`)
- **Optimization goal:** minimization
- **Reproducibility:** fixed random seed (`42`)
- **Java version:** 25 or newer

Default parameters (set in `MainDE.java`):

F = 0.8
CR = 0.9
GMAX = 2000
LOWER = -30.0
UPPER = 30.0

You can safely adjust these to test different configurations.


## Author

**Rodrigo Zunino**  
*Design of bio-inspired algorithm (DABI)* —
*Master’s Student — Formal Methods in Software Engineering*  

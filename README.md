# SIR-Virus-Model
Java virus simulation

Simulate virus spread over time in a population of autonomous cells. Every cell has up to 8 static neighbors and follows an SIR (Susceptible, Infectious, Recovered) model to simulate virus spread.

## Results
This model takes in parameters to simulate, visualizes the simulation, and outputs a CSV file with the simulation results.
Below are some graphs generated with various parameters, using the default infection and recovery rates.

![Baseline Simulation with no masked cells](https://user-images.githubusercontent.com/77563588/143784274-638aaa92-13d7-4947-99a1-9c72a840a09a.png)
![70% Masked Cell simulation](https://user-images.githubusercontent.com/77563588/143784292-3859e1e5-65a0-4598-9667-657978f5eed5.png)

Wearing masks has a noticeable impact on virus spread.

![30% Masked, 20% Quarantine, 5% Contact Tracing](https://user-images.githubusercontent.com/77563588/143784320-9abaf1c4-e6c1-4086-a880-132265645bf7.png)

With more protective behaviors, similar results are achieved with fewer members taking action. 55% action with advanced behavior is comparable to 70% action with only masks.

![30% Masked, 40% Introvert](https://user-images.githubusercontent.com/77563588/143784374-d6586555-d60d-42ee-a10c-1bb8cbd742e9.png)

Not interacting with as many people is less effective in this model than wearing a mask.


## How to run
```
$ javac *.java
$ java MixedSIR <percentMasked> <infectionRate> <recoveryRate> <maxDays> <gridSize> <frameDuration> 
    <outFileName> <percentQuarantine> <percentContactTracing> <percentIntrovert>
```
#### Parameters
All parameters are optional and default to standard values
 * precentMasekd: (double) the percentage of the population that is wearing a mask and thus spreading the virus at lower rates
   * default 0.5
 * infectionRate: (double) representing the probability a cell will get infected by each infectious neighbor each day
   * default 0.166
 * recoveryRate:  (double) representing the probability an infected cell will recover each day
   * default 0.037
 * maxDays:       (int) the maximum number of days the simulation will run
   * default 90
 * gridSize:      (int) the length of one row of the grid of cells (population count is this value, squared)
   * default 500
 * frameDuration: (long) milliseconds between each frame
   * default 50
 * outFileName:   (String) the name of the output csv file to create
   * default "", which outputs a file that uses the other values in the name
 * percentQuarantine:  (double) the percent of the population that will quarantine when infected
   * default 0.0
 * percentContactTracing:  (double) the percent of the population that will quarantine if it's neighbors are infected
   * default 0.0
 * percentIntrovert:   (double) the percent of the population that will not be in contact with all of its neighbors
   * default 0.0

Default infection rate and mask impact reduction are taken from the CDC publications on COVID-19

## Types of cells
### Unmasked
These cells have standard rates to receive the infection and then infect neighbors
### Masked
Masked cells spread the virus at a significantly lower rate than unmasked cells, and receive the virus at a moderately lower rate
### Quarantine
Quarantine cells go into quarantine after becoming infected, preventing them from spreading the virus to neighbors
### Contact Tracing
Contact Tracing cells go into quarantine if any of their neighbors are infected, preventing them from receiving the virus
### Introvert
Introvert cells do not interact with all of their neighbors. Roughly half of their neighbors will not spread or receive the virus because of this cell

## Project context
This project was made as a lab assignment for an Object Oriented Programming class.

## Future Work
- Simulate more dynamic populations with cells that can move to interact with different neighbors
- Factor in vaccinations and how they reduce rates

package project2;

import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class EightQueensSolver {
	private static final int BOARD_SIZE = 8;
	private static final int POPULATION_SIZE = 50; // Adjust population size as needed
	private static final int MAX_GENERATIONS = 100; // Maximum number of generations

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		int choice;

		do {
			System.out.println("Please select an option:");
			System.out.println("[1] Steepest Ascent Hill Climbing");
			System.out.println("[2] Genetic Algorithm");
			System.out.println("[3] Exit");
			System.out.print("Enter your choice: ");
			choice = scanner.nextInt();

			int totalSearchCostSteepest = 0;
			double totalTimeTakenSteepest = 0;
			int instancesSolvedSteepest = 0;

			int totalSearchCostGA = 0;
			double totalTimeTakenGA = 0;
			int instancesSolvedGA = 0;

			switch (choice) {
			case 1:
				System.out.println("\nSteepest Ascent Hill Climbing:");
				int stepestInstances = getNumberOfInstances(scanner);

				for (int i = 1; i <= stepestInstances; i++) {
					System.out.println("\nInstance " + i + ":");
					boolean success = solveEightQueensUsingSteepestAscentHillClimbing();
					if (success) {
						totalSearchCostSteepest += getSearchCost();
						totalTimeTakenSteepest += getTimeTaken();
						instancesSolvedSteepest++;
					}
				}

				int solvedSteepest = instancesSolvedSteepest * 100 / stepestInstances;
				System.out.println("\nPercentage of solved problems: " + solvedSteepest + "%");

				// Calculate average search cost for Steepest Ascent Hill Climbing
				if (instancesSolvedSteepest > 0) {
					double avgSearchCostSteepest = (double) totalSearchCostSteepest / instancesSolvedSteepest;
					System.out.printf("Average Search Cost (Steepest Ascent Hill Climbing algorithm): %.2f\n",
							avgSearchCostSteepest);
				}
				break;
			case 2:
				System.out.println("\nGenetic Algorithm:");
				int gaInstances = getNumberOfInstances(scanner);

				for (int i = 1; i <= gaInstances; i++) {
					System.out.println("\nInstance " + i + ":");
					boolean success = solveEightQueensUsingGeneticAlgorithm();
					if (success) {
						totalSearchCostGA += getSearchCost();
						totalTimeTakenGA += getTimeTaken();
						instancesSolvedGA++;
					}
				}

				// Calculate percentage of solved problems for Genetic Algorithm
				int solvedGA = instancesSolvedGA * 100 / gaInstances;
				System.out.println("\nPercentage of solved problems (Genetic algorithm): " + solvedGA + "%");

				// Calculate average search cost for Genetic Algorithm
				if (instancesSolvedGA > 0) {
					double avgSearchCostGA = (double) totalSearchCostGA / instancesSolvedGA;
					System.out.printf("Average Search Cost (Genetic algorithm): %.2f%n", avgSearchCostGA);
				}
				break;
			case 3:
				System.out.println("Exiting the program.");
				break;
			default:
				System.out.println("Invalid choice. Please try again.");
			}

			if (instancesSolvedSteepest > 0) {
				double avgTimeTakenSteepest = (double) totalTimeTakenSteepest / instancesSolvedSteepest;
				System.out.printf("Average Time Taken (Steepest Ascent Hill Climbing algorithm): %.2f milliseconds\n",
						avgTimeTakenSteepest);
			}

			if (instancesSolvedGA > 0) {
				double avgTimeTakenMinConflicts = (double) totalTimeTakenGA / instancesSolvedGA;
				System.out.printf("Average Time Taken (Genetic algorithm): %.2f milliseconds\n",
						avgTimeTakenMinConflicts);
			}

			System.out.println();
		} while (choice != 3);

		scanner.close();
	}

	private static int getNumberOfInstances(Scanner scanner) {
		System.out.print("Enter the number of 8-Queen instances to generate: ");
		return scanner.nextInt();
	}

	private static boolean solveEightQueensUsingSteepestAscentHillClimbing() {
		int[] board = generateRandomBoard();
		int conflicts = countConflicts(board);

		int step = 1;
		long startTime = System.currentTimeMillis();
		boolean success = false; // Add a flag to track if puzzle is solved

		System.out.println("Initial Puzzle:");
		printBoard(board);

		while (conflicts > 0) {
			int[] nextBoard = Arrays.copyOf(board, BOARD_SIZE);
			int minConflicts = conflicts;
			int minConflictsQueen = -1;
			int minConflictsRow = -1;

			for (int i = 0; i < BOARD_SIZE; i++) {
				int currentRow = board[i];
				for (int j = 0; j < BOARD_SIZE; j++) {
					if (board[i] != j) {
						nextBoard[i] = j;
						int newConflicts = countConflicts(nextBoard);
						if (newConflicts < minConflicts) {
							minConflicts = newConflicts;
							minConflictsQueen = i;
							minConflictsRow = j;
						}
					}
				}
				nextBoard[i] = currentRow;
			}

			if (minConflicts >= conflicts) {
				// Local minimum found, no need to restart, puzzle not solved
				System.out.println("Failed to find a solution using Steepest-Ascent Hill Climbing:");
				return false;
			} else {
				board[minConflictsQueen] = minConflictsRow;
				conflicts = minConflicts;
			}

			System.out.println("Step " + step + ":");
			printBoard(board);
			step++;
		}

		// Puzzle solved successfully, calculate search cost and time taken
		long elapsedTime = System.currentTimeMillis() - startTime;
		success = true; // Mark the puzzle as solved

		System.out.println("Solution using Steepest-Ascent Hill Climbing:");
		printBoard(board);
		System.out.println("Search Cost: " + (step - 1));
		System.out.println("Time Taken: " + elapsedTime + " milliseconds");

		// After solving the puzzle:
		setSearchCost(step - 1);
		setTimeTaken(elapsedTime);

		return success;
	}

	private static boolean solveEightQueensUsingGeneticAlgorithm() {
		int[][] population = new int[POPULATION_SIZE][BOARD_SIZE];
		Random random = new Random();

		// Initialize the initial population randomly
		for (int i = 0; i < POPULATION_SIZE; i++) {
			population[i] = generateRandomBoard();
		}

		long startTime = System.currentTimeMillis();
		int generation = 0;
		int ρ = POPULATION_SIZE / 10; // 10% of the population size

		System.out.println("Initial Puzzle:");
		printBoard(population[0]);

		while (generation < MAX_GENERATIONS) {
			int[][] newPopulation = new int[POPULATION_SIZE][BOARD_SIZE];
			int eliteSize = POPULATION_SIZE / 10;

			Arrays.sort(population, (a, b) -> Integer.compare(countConflicts(a), countConflicts(b)));
			for (int i = 0; i < eliteSize; i++) {
				newPopulation[i] = population[i];
			}

			// Generate the rest of the new population using recombination and mutation
			for (int i = eliteSize; i < POPULATION_SIZE; i++) {
				// Select ρ individuals randomly from the population
				int[][] selection = select(ρ, population);
				int[] parent1 = selection[random.nextInt(selection.length)];
				int[] parent2 = selection[random.nextInt(selection.length)];

				int[] child = recombine(parent1, parent2);
				child = mutate(child);
				newPopulation[i] = child;
			}

			population = newPopulation;

			// Check if a solution is found in the current population
			for (int[] individual : population) {
				if (countConflicts(individual) == 0) {
					long endTime = System.currentTimeMillis();
					long elapsedTime = endTime - startTime;

					System.out.println("Solution using Genetic Algorithm:");
					printBoard(individual);
					System.out.println("Generation: " + generation);
					System.out.println("Search Cost: " + generation * POPULATION_SIZE + POPULATION_SIZE);
					System.out.println("Time Taken: " + elapsedTime + " milliseconds");

					// After solving the puzzle:
					setSearchCost(generation * POPULATION_SIZE + POPULATION_SIZE);
					setTimeTaken(elapsedTime);

					return true;
				}
			}

			// Print step-by-step progress for each generation
			System.out.println("Generation: " + generation);
			System.out.println("Best Individual:");
			printBoard(population[0]);
			System.out.println("Conflicts: " + countConflicts(population[0]));
			System.out.println();

			generation++;
		}

		System.out.println("Genetic Algorithm reached the maximum number of generations without finding a solution.");
		return false;
	}

	private static int[][] select(int ρ, int[][] population) {
		int[][] selection = new int[ρ][BOARD_SIZE];
		Random random = new Random();

		// Select ρ individuals randomly from the population
		for (int i = 0; i < ρ; i++) {
			selection[i] = population[random.nextInt(population.length)];
		}

		return selection;
	}

	private static int[] recombine(int[] parent1, int[] parent2) {
		int[] child = new int[BOARD_SIZE];
		Random random = new Random();
		int crossover = random.nextInt(BOARD_SIZE);

		for (int i = 0; i < BOARD_SIZE; i++) {
			if (i < crossover) {
				child[i] = parent1[i];
			} else {
				child[i] = parent2[i];
			}
		}

		return child;
	}

	private static int[] mutate(int[] individual) {
		int[] mutatedIndividual = Arrays.copyOf(individual, BOARD_SIZE);
		Random random = new Random();
		if (random.nextDouble() < 0.1) { // Mutation rate set to 10%
			int pos = random.nextInt(BOARD_SIZE);
			mutatedIndividual[pos] = random.nextInt(BOARD_SIZE);
		}
		return mutatedIndividual;
	}

	private static int[] generateRandomBoard() {
		int[] board = new int[BOARD_SIZE];
		Random random = new Random();

		for (int i = 0; i < BOARD_SIZE; i++) {
			board[i] = random.nextInt(BOARD_SIZE);
		}

		return board;
	}

	private static int countConflicts(int[] board) {
		int conflicts = 0;

		for (int i = 0; i < BOARD_SIZE; i++) {
			for (int j = i + 1; j < BOARD_SIZE; j++) {
				if (board[i] == board[j] || Math.abs(board[i] - board[j]) == j - i) {
					conflicts++;
				}
			}
		}

		return conflicts;
	}

	private static void printBoard(int[] board) {
		for (int i = 0; i < BOARD_SIZE; i++) {
			for (int j = 0; j < BOARD_SIZE; j++) {
				if (board[i] == j) {
					System.out.print("Q ");
				} else {
					System.out.print(". ");
				}
			}
			System.out.println();
		}
	}

	private static int searchCost;
	private static long timeTaken;

	private static int getSearchCost() {
		return searchCost;
	}

	private static long getTimeTaken() {
		return timeTaken;
	}

	private static void setSearchCost(int cost) {
		searchCost = cost;
	}

	private static void setTimeTaken(long time) {
		timeTaken = time;
	}
}
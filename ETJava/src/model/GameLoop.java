package model;

import controller.facade.GameFacade;
import controller.Controls;

import javax.swing.*;
import java.awt.*;

public class GameLoop extends JPanel implements Runnable {
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;
    final int FPS = 60;
    private Thread gameThread;
    private boolean running = false;
    private boolean paused = false;

    // Two facades, one for each player
    private GameFacade player1Facade;
    private GameFacade player2Facade;

    // Toggle for two-player mode
    private boolean isTwoPlayerMode;

    public GameLoop(boolean isTwoPlayerMode) {
        this.isTwoPlayerMode = isTwoPlayerMode; // Set whether the game is single-player or two-player
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(Color.WHITE);
        this.setLayout(null);

        // Initialize game facade(s) depending on the mode
        player1Facade = new GameFacade(200, 400, 1); // Player 1 is always present

        if (isTwoPlayerMode) {
            // Player 2's game area (right side) only initialized in two-player mode
            player2Facade = new GameFacade(200, 400, 2);
        }
    }

    public void startGame() {
        if (gameThread == null || !running) {
            gameThread = new Thread(this);
            gameThread.start();
            running = true;
            paused = false;
        }
    }

    public void pauseGame() {
        if (running && !paused) {
            paused = true;
        }
    }

    public void resumeGame() {
        if (running && paused) {
            paused = false;
        }
    }

    public void stopGame() {
        if (running) {
            running = false;
            paused = false;

            // Interrupt the game thread to exit the wait state if it's paused
            if (gameThread != null) {
                gameThread.interrupt();
            }

            try {
                // Ensure the game thread finishes execution
                gameThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Preserve the interrupt status
                System.out.println("Stopping game");
            } finally {
                gameThread = null; // Clean up the reference to the game thread
            }
        }
    }

    public void resetGame() {
        stopGame();
        player1Facade.startNewGame(); // Reset Player 1's game
        if (isTwoPlayerMode) {
            player2Facade.startNewGame(); // Reset Player 2's game in two-player mode
        }
    }

    private void update() {
        if (!paused && !Controls.pause) {
            player1Facade.updateGame();  // Update Player 1's game
            if (isTwoPlayerMode) {
                player2Facade.updateGame();  // Update Player 2's game in two-player mode
            }
        }
    }

    @Override
    public void run() {
        double drawInterval = 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while (running) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        if (isTwoPlayerMode) {
            // Player 1's game on the left side
            g2d.translate(-150, 0); // Shift Player 1's game area to the left
            player1Facade.renderGame(g2d);

            // Player 2's game on the right side
            g2d.translate(450, 0); // Shift Player 2's game area to the right
            player2Facade.renderGame(g2d);
        } else {
            // Single-player mode, no translation
            player1Facade.renderGame(g2d);
        }
    }

    public void adjustGameDimensions(int width, int height) {
        player1Facade.setGameDimensions(width, height);
        if (isTwoPlayerMode) {
            player2Facade.setGameDimensions(width, height);
        }
    }

    public boolean isGameOver() {
        if (isTwoPlayerMode) {
            // Game over is true if either player's game is over in two-player mode
            return player1Facade.isGameOver() || player2Facade.isGameOver();
        }
        // Single-player mode, only check Player 1
        return player1Facade.isGameOver();
    }
}

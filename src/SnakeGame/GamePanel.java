package SnakeGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener, KeyListener {
    // === Constants for board size ===
    private final int WIDTH = 600;
    private final int HEIGHT = 600;
    private final int UNIT_SIZE = 20;

    // === Game objects ===
    private Timer timer;
    private LinkedList<Point> snake; // stores snake body
    private Point food;              // food position
    private Direction direction = Direction.RIGHT; // start moving right
    private boolean running = false;
    private Random random;
    private int score = 0;

    // === Constructor ===
    public GamePanel() {
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(this);
        startGame();
    }

    // === Start a new game ===
    public void startGame() {
        snake = new LinkedList<>();

        // Start snake with 3 segments
        snake.add(new Point(5, 5));
        snake.add(new Point(4, 5));
        snake.add(new Point(3, 5));

        random = new Random();
        spawnFood();
        running = true;

        // timer controls game speed (ms per step)
        timer = new Timer(150, this);
        timer.start();
    }

    // === Spawn food at random location ===
    private void spawnFood() {
        int x = random.nextInt(WIDTH / UNIT_SIZE);
        int y = random.nextInt(HEIGHT / UNIT_SIZE);
        food = new Point(x, y);
    }

    // === Painting logic ===
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    private void draw(Graphics g) {
        if (running) {
            // draw food (red square)
            g.setColor(Color.RED);
            g.fillRect(food.x * UNIT_SIZE, food.y * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);

            // draw snake
            for (Point p : snake) {
                g.setColor(Color.GREEN);
                g.fillRect(p.x * UNIT_SIZE, p.y * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
            }

            // draw score
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 16));
            g.drawString("Score: " + score, 10, 20);
        } else {
            gameOver(g);
        }
    }

    // === Game over screen ===
    private void gameOver(Graphics g) {
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("Game Over! Score: " + score, WIDTH / 2 - 100, HEIGHT / 2);
    }

    // === Main game loop (called every timer tick) ===
    public void actionPerformed(ActionEvent e) {
        if (running) {
            moveSnake();
            checkFood();
            checkCollision();
        }
        repaint();
    }

    // === Snake movement ===
    private void moveSnake() {
        Point head = snake.getFirst();
        Point newHead = new Point(head);

        // move head depending on direction
        switch (direction) {
            case UP -> newHead.y--;
            case DOWN -> newHead.y++;
            case LEFT -> newHead.x--;
            case RIGHT -> newHead.x++;
        }

        // add new head
        snake.addFirst(newHead);

        // if no food eaten, remove tail (keeps length constant)
        if (!newHead.equals(food)) {
            snake.removeLast();
        }
    }

    // === Check if snake eats food ===
    private void checkFood() {
        Point head = snake.getFirst();
        if (head.equals(food)) {
            score++;
            spawnFood();
            // snake grows automatically because we didn’t remove tail
        }
    }

    // === Check collisions with walls or itself ===
    private void checkCollision() {
        Point head = snake.getFirst();

        // wall collision
        if (head.x < 0 || head.x >= WIDTH / UNIT_SIZE || head.y < 0 || head.y >= HEIGHT / UNIT_SIZE) {
            running = false;
            timer.stop();
        }

        // self collision
        for (int i = 1; i < snake.size(); i++) {
            if (head.equals(snake.get(i))) {
                running = false;
                timer.stop();
                break;
            }
        }
    }

    // === Key events (WASD + arrow keys) ===
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W, KeyEvent.VK_UP -> { if (direction != Direction.DOWN) direction = Direction.UP; }
            case KeyEvent.VK_S, KeyEvent.VK_DOWN -> { if (direction != Direction.UP) direction = Direction.DOWN; }
            case KeyEvent.VK_A, KeyEvent.VK_LEFT -> { if (direction != Direction.RIGHT) direction = Direction.LEFT; }
            case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> { if (direction != Direction.LEFT) direction = Direction.RIGHT; }
        }
    }

    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}
}

package SnakeGame;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener, KeyListener {

    private final int WIDTH = 600;
    private final int HEIGHT = 600;
    private final int UNIT_SIZE = 20;

    private Timer timer;
    private LinkedList<Point> snake;
    private Point food;
    private Direction direction = Direction.RIGHT;
    private boolean running = false;
    private Random random;
    private int score = 0;

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
        snake.add(new Point(5, 5));
        snake.add(new Point(4, 5));
        snake.add(new Point(3, 5));

        random = new Random();
        spawnFood();
        running = true;

        timer = new Timer(150, this);
        timer.start();
    }

    // === Spawn food ===
    private void spawnFood() {
        int x = random.nextInt(WIDTH / UNIT_SIZE);
        int y = random.nextInt(HEIGHT / UNIT_SIZE);
        food = new Point(x, y);
    }

    // === Sound effect method ===
    public void playSound(String fileName) {
        try {
            AudioInputStream audio = AudioSystem.getAudioInputStream(
                    getClass().getResource("/SnakeGame/" + fileName)
            );
            Clip clip = AudioSystem.getClip();
            clip.open(audio);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    // === Painting logic ===
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    private void draw(Graphics g) {
        if (running) {
            // Grid
            g.setColor(new Color(50, 50, 50));
            for (int i = 0; i <= WIDTH / UNIT_SIZE; i++) {
                g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, HEIGHT);
            }
            for (int j = 0; j <= HEIGHT / UNIT_SIZE; j++) {
                g.drawLine(0, j * UNIT_SIZE, WIDTH, j * UNIT_SIZE);
            }

            // Draw food
            g.setColor(Color.RED);
            g.fillOval(food.x * UNIT_SIZE, food.y * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
            g.setColor(Color.WHITE);
            g.fillOval(food.x * UNIT_SIZE + 5, food.y * UNIT_SIZE + 5, UNIT_SIZE / 3, UNIT_SIZE / 3);

            // Draw snake
            for (int i = 0; i < snake.size(); i++) {
                Point p = snake.get(i);
                if (i == 0) {
                    g.setColor(Color.YELLOW);
                    g.fillOval(p.x * UNIT_SIZE, p.y * UNIT_SIZE, UNIT_SIZE + 2, UNIT_SIZE + 2);
                } else {
                    float ratio = (float) i / snake.size();
                    g.setColor(new Color(0, (int) (255 * (1 - ratio)), 0));
                    g.fillOval(p.x * UNIT_SIZE, p.y * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
                }
            }

            // Display score
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Score: " + score, 10, 25);
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

    // === Main game loop ===
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

        switch (direction) {
            case UP -> newHead.y--;
            case DOWN -> newHead.y++;
            case LEFT -> newHead.x--;
            case RIGHT -> newHead.x++;
        }

        snake.addFirst(newHead);

        if (!newHead.equals(food)) {
            snake.removeLast();
        }
    }

    // === Check food collision ===
    private void checkFood() {
        Point head = snake.getFirst();
        if (head.equals(food)) {
            score++;
            spawnFood();
            playSound("eat.wav"); // play eating sound
        }
    }

    // === Check wall/self collision ===
    private void checkCollision() {
        Point head = snake.getFirst();

        if (head.x < 0 || head.x >= WIDTH / UNIT_SIZE || head.y < 0 || head.y >= HEIGHT / UNIT_SIZE) {
            running = false;
            timer.stop();
            playSound("gameover.wav");
        }

        for (int i = 1; i < snake.size(); i++) {
            if (head.equals(snake.get(i))) {
                running = false;
                timer.stop();
                playSound("gameover.wav");
                break;
            }
        }
    }

    // === Key controls ===
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


import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import java.util.ArrayList;
import java.util.Random;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {
    int boardHeight = 640;
    int boardWidth = 360;

    // images
    Image bg;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;

    // bird position
    int birdX = boardWidth / 8;
    int birdY = boardHeight / 2;
    int birdWidth = 30;
    int birdHeight = 24;

    class Bird {
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image img;

        Bird(Image img) {
            this.img = img;
        }
    }

    // pipes
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;

    class Pipe {
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean passed = false;

        Pipe(Image img) {
            this.img = img;
        }
    }

    // game logic
    Bird bird;
    int velocityY = 0;
    int velocityX = -4;
    int gravity = 1;

    ArrayList<Pipe> pipes;
    Random random = new Random();

    // timer
    Timer gameLoop;
    Timer pipeLoop;
    boolean gameOver = false;
    double score = 0;

    FlappyBird() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true);
        addKeyListener(this);

        // load images
        bg = new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("./flappybird.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();

        // bird
        bird = new Bird(birdImg);
        pipes = new ArrayList<Pipe>();

        // pipes timer
        pipeLoop = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });
        pipeLoop.start();

        // game timer
        gameLoop = new Timer(1000 / 60, this);
        gameLoop.start();

    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        // bg
        g.drawImage(bg, 0, 0, boardWidth, boardHeight, null);

        // bird
        g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);

        // pipes
        for (Pipe pipe : pipes) {
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 32));
        if (gameOver) {
            g.drawString("Game Over", boardWidth / 2 - 100, boardHeight / 2);
            g.drawString("Score: " + (int) score, boardWidth / 2 - 100, boardHeight / 2 + 50);
        } else {
            g.drawString("Score: " + (int) score, boardWidth / 2 - 100, 50);
        }
    }

    public void placePipes() {
        int randomPipeY = (int) (pipeY - pipeHeight / 4 - Math.random() * (pipeHeight / 2));
        int openingSpace = boardHeight / 4;

        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = randomPipeY + pipeHeight + openingSpace;
        pipes.add(bottomPipe);

    }

    public boolean isColliding(Pipe p, Bird b) {
        return p.x < b.x + b.width && p.x + p.width > b.x && p.y < b.y + b.height && p.y + p.height > b.y;

    }

    public void move() {
        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(0, bird.y);

        for (Pipe pipe : pipes) {
            pipe.x += velocityX;
            if (pipe.x + pipe.width < 0) {
                pipes.remove(pipe);
            }

            if (!pipe.passed && pipe.x + pipe.width < bird.x) {
                pipe.passed = true;
                score += 0.5;
            }

            if (isColliding(pipe, bird)) {
                gameOver = true;
            }
        }

        if (bird.y + bird.height > boardHeight) {
            gameOver = true;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            gameLoop.stop();
            pipeLoop.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            velocityY = -10;
        }
        if (gameOver) {
            bird.y = birdY;
            pipes.clear();
            velocityY = 0;
            score = 0;
            gameOver = false;
            gameLoop.start();
            pipeLoop.start();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
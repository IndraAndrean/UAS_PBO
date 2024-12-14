import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

public class ChromeDinosaur extends JPanel implements ActionListener, KeyListener {
    private int boardWidth = 750;
    private int boardHeight = 250;

    // Images
    private Image dinosaurImg;
    private Image dinosaurDeadImg;
    private Image dinosaurJumpImg;
    private Image cactus1Img;
    private Image cactus2Img;
    private Image cactus3Img;

    abstract class Drawable {
        protected int x, y, width, height;

        public Drawable(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public abstract void draw(Graphics g);
    }
    

    class Block extends Drawable {
        private Image img;

        public Block(int x, int y, int width, int height, Image img) {
            super(x, y, width, height);
            this.img = img;
        }

        public Image getImg() {
            return img;
        }

        public void setImg(Image img) {
            this.img = img;
        }

        @Override
        public void draw(Graphics g) {
            g.drawImage(img, x, y, width, height, null);
        }
    }

    // Static variable for high score
    private static int highScore = 0;

    private int dinosaurWidth = 88;
    private int dinosaurHeight = 94;
    private int dinosaurX = 50;
    private int dinosaurY = boardHeight - dinosaurHeight;

    private Block dinosaur;

    private int cactus1Width = 34;
    private int cactus2Width = 69;
    private int cactus3Width = 102;
    private int cactusHeight = 70;
    private int cactusX = 700;
    private int cactusY = boardHeight - cactusHeight;
    private ArrayList<Block> cactusArray;

    private int velocityX = -12; // Cactus moving left speed
    private int velocityY = 0; // Dinosaur jump speed
    private int gravity = 1;

    private boolean gameOver = false;
    private int score = 0;

    private Timer gameLoop;
    private Timer placeCactusTimer;

    public ChromeDinosaur() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.lightGray);
        setFocusable(true);
        addKeyListener(this);

        try {
            dinosaurImg = new ImageIcon(getClass().getResource("./img/dino-run.gif")).getImage();
            dinosaurDeadImg = new ImageIcon(getClass().getResource("./img/dino-dead.png")).getImage();
            dinosaurJumpImg = new ImageIcon(getClass().getResource("./img/dino-jump.png")).getImage();
            cactus1Img = new ImageIcon(getClass().getResource("./img/cactus1.png")).getImage();
            cactus2Img = new ImageIcon(getClass().getResource("./img/cactus2.png")).getImage();
            cactus3Img = new ImageIcon(getClass().getResource("./img/cactus3.png")).getImage();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load images: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        dinosaur = new Block(dinosaurX, dinosaurY, dinosaurWidth, dinosaurHeight, dinosaurImg);
        cactusArray = new ArrayList<>();

        gameLoop = new Timer(1000 / 60, this);
        gameLoop.start();

        placeCactusTimer = new Timer(1500, e -> placeCactus());
        placeCactusTimer.start();
    }

    private void placeCactus() {
        if (gameOver) {
            return;
        }

        double placeCactusChance = Math.random();
        Block cactus;
        if (placeCactusChance > .90) {
            cactus = new Block(cactusX, cactusY, cactus3Width, cactusHeight, cactus3Img);
        } else if (placeCactusChance > .70) {
            cactus = new Block(cactusX, cactusY, cactus2Width, cactusHeight, cactus2Img);
        } else {
            cactus = new Block(cactusX, cactusY, cactus1Width, cactusHeight, cactus1Img);
        }
        cactusArray.add(cactus);

        if (cactusArray.size() > 10) {
            cactusArray.remove(0);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    private void draw(Graphics g) {
        dinosaur.draw(g);
        for (Block cactus : cactusArray) {
            cactus.draw(g);
        }

        g.setColor(Color.black);
        g.setFont(new Font("Courier", Font.PLAIN, 32));
        if (gameOver) {
            g.drawString("Game Over: " + score, 10, 35);
            g.drawString("High Score: " + highScore, 10, 70);
        } else {
            g.drawString(String.valueOf(score), 10, 35);
        }
    }

    private void move() {
        velocityY += gravity;
        dinosaur.y += velocityY;

        if (dinosaur.y > dinosaurY) {
            dinosaur.y = dinosaurY;
            velocityY = 0;
            dinosaur.setImg(dinosaurImg);
        }

        for (Block cactus : cactusArray) {
            cactus.x += velocityX;
            if (collision(dinosaur, cactus)) {
                gameOver = true;
                dinosaur.setImg(dinosaurDeadImg);
                highScore = Math.max(highScore, score);
            }
        }
        score++;
    }

    private boolean collision(Block a, Block b) {
        return a.x < b.x + b.width && a.x + a.width > 
        b.x && a.y < b.y + b.height && a.y + a.height > b.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            placeCactusTimer.stop();
            gameLoop.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (dinosaur.y == dinosaurY) {
                velocityY = -17;
                dinosaur.setImg(dinosaurJumpImg);
            }

            if (gameOver) {
                dinosaur.y = dinosaurY;
                dinosaur.setImg(dinosaurImg);
                velocityY = 0;
                cactusArray.clear();
                score = 0;
                gameOver = false;
                gameLoop.start();
                placeCactusTimer.start();
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}



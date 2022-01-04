import javax.swing.JPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.Color;
import javax.swing.Timer;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.Font;


public class PongPanel extends JPanel implements ActionListener, KeyListener {
	
	private final static Color BACKGROUND_COLOUR = Color.BLACK;
	private final static int TIMER_DELAY = 5;
	private final static int BALL_MOVEMENT_SPEED = 2;
	private final static int POINTS_TO_WIN = 3;
	private final static int SCORE_TEXT_X = 100;
	private final static int SCORE_TEXT_Y = 100;
	private final static int SCORE_FONT_SIZE = 50;
	private final static String FONT_FAMILY = "SansSerif";
	private final static int WIN_MESSAGE_TEXT_X = 50;
	private final static int WIN_MESSAGE_TEXT_Y = 200;
	private final static int WIN_MESSAGE_FONT_SIZE = 40;
	int player1Score = 0, player2Score = 0;
	Player gameWinner;
	
	GameState gameState = GameState.INITIALISING;
	
	Ball ball;
	Paddle paddle1, paddle2;
	
	
	public PongPanel() {
		setBackground(BACKGROUND_COLOUR);
		Timer timer = new Timer(TIMER_DELAY, this);
			timer.start();
		addKeyListener(this);
		setFocusable(true);
	}
	

	@Override
	public void keyTyped(KeyEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.VK_W) {
        	paddle1.setYVelocity(-1);
        } else if (event.getKeyCode() == KeyEvent.VK_S) {
        	paddle1.setYVelocity(1);
        } else if(event.getKeyCode() == KeyEvent.VK_UP) {
            paddle2.setYVelocity(-1);
		} else if(event.getKeyCode() == KeyEvent.VK_DOWN) {
            paddle2.setYVelocity(1);
        } 
	}

	@Override
	public void keyReleased(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.VK_W || event.getKeyCode() == KeyEvent.VK_S) {
        	paddle1.setYVelocity(0);
        } else if(event.getKeyCode() == KeyEvent.VK_UP || event.getKeyCode() == KeyEvent.VK_DOWN) {
            paddle2.setYVelocity(0);
        } 
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		update();
		repaint();
	}

	private void update() {
		switch(gameState) {
        case INITIALISING: {
            createObjects();
            gameState = GameState.PLAYING;
            ball.setXVelocity(BALL_MOVEMENT_SPEED);
			ball.setYVelocity(BALL_MOVEMENT_SPEED);
            break;
        }
        case PLAYING: {
        	moveObject(paddle1);
        	moveObject(paddle2);
        	moveObject(ball);
        	checkWallBounce();
        	checkPaddleBounce();
        	checkWin();
            break;
       }
       case GAMEOVER: {
           break;
       }
	}
	}
	
	public void createObjects() {
		ball = new Ball(getWidth(), getHeight());
		paddle1 = new Paddle(Player.One, getWidth(), getHeight());
		paddle2 = new Paddle(Player.Two, getWidth(), getHeight());
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		paintDottedLine(g);
		if (gameState != GameState.INITIALISING) {
		paintSprite(g, ball);
		paintSprite(g, paddle1);
		paintSprite(g, paddle2);
		paintScores(g);
		paintWinMessage(g);
		}
	}
	
	private void paintDottedLine(Graphics g) {
		Graphics2D g2d = (Graphics2D) g.create();
		Stroke dashed = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float [] {9}, 0);
			g2d.setStroke(dashed);
			g2d.setPaint(Color.WHITE);
			g2d.drawLine(getWidth() /2, 0, getWidth() / 2, getHeight());
			g2d.dispose();
	}

	private void paintSprite(Graphics g, Sprite sprite) {
		g.setColor(sprite.getColour());
		g.fillRect(sprite.getXPosition(), sprite.getYPosition(), sprite.getWidth(), sprite.getHeight());
	}
	
	
	private void paintScores(Graphics g) {
        Font scoreFont = new Font(FONT_FAMILY, Font.BOLD, SCORE_FONT_SIZE);
        String leftScore = Integer.toString(player1Score);
        String rightScore = Integer.toString(player2Score);
        g.setFont(scoreFont);
        g.drawString(leftScore, SCORE_TEXT_X, SCORE_TEXT_Y);
        g.drawString(rightScore, getWidth()-SCORE_TEXT_X, SCORE_TEXT_Y);
   }
	
	private void paintWinMessage (Graphics g) {
		Font winMessageFont = new Font(FONT_FAMILY, Font.BOLD, WIN_MESSAGE_FONT_SIZE);
		String player1Win = "Player One Wins!";
		String player2Win = "Player Two Wins!";
		g.setFont(winMessageFont);
		if (gameWinner == Player.One) {
			g.drawString(player1Win, WIN_MESSAGE_TEXT_X, WIN_MESSAGE_TEXT_Y);
		} else if (gameWinner == Player.Two) {
			g.drawString(player2Win, getWidth()/2 + WIN_MESSAGE_TEXT_X, WIN_MESSAGE_TEXT_Y);
		}
	}
	
	private void moveObject(Sprite sprite) {
		sprite.setXPosition(sprite.getXPosition() + sprite.getXVelocity(), getWidth());
		sprite.setYPosition(sprite.getYPosition() + sprite.getYVelocity(), getHeight());
	}
	
	// Checking if out of bounds of screen is redundant because we have already controlled for this in the setXPosition and setYPostion methods.
	private void checkWallBounce() {
		if (ball.getXPosition() <= 0) {
			ball.setXVelocity(-ball.getXVelocity());
			addScore(Player.Two);
			resetBall();
		} else if (ball.getXPosition() >= getWidth() - ball.getWidth()) {
			ball.setXVelocity(-ball.getXVelocity());
			addScore(Player.One);
			resetBall();
		} 
		if (ball.getYPosition() <= 0 || ball.getYPosition() >= getHeight() - ball.getHeight()) {
			ball.setYVelocity(-ball.getYVelocity());
		}
	}
	
	private void resetBall() {
		ball.resetToInitialPosition();
	}
	
	private void checkPaddleBounce() {
	      if(ball.getXVelocity() < 0 && ball.getRectangle().intersects(paddle1.getRectangle())) {
	          ball.setXVelocity(BALL_MOVEMENT_SPEED);
	      }
	      if(ball.getXVelocity() > 0 && ball.getRectangle().intersects(paddle2.getRectangle())) {
	          ball.setXVelocity(-BALL_MOVEMENT_SPEED);
	      }
	}
	
	private void addScore(Player player) {
		if (player == Player.One) {
			player1Score++;
		} else if (player == Player.Two) {
			player2Score++;
		}
	}
	
	private void checkWin() {
		if (player1Score >= POINTS_TO_WIN) {
			gameWinner = Player.One;
			gameState = GameState.GAMEOVER;
		} else if (player2Score >= POINTS_TO_WIN) {
			gameWinner = Player.Two;
			gameState = GameState.GAMEOVER;
		}
	}
}

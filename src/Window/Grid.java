package Window;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class Grid extends JPanel implements ActionListener, MouseListener, MouseMotionListener, KeyListener {
	private static final long serialVersionUID = 1L;
	private APathFinder pathFinder;
	private char key = (char) 0;
	private Node start, end;
	private int size = 20;
	private Window window;
	private boolean steps = true;
	
	private Timer timer = new Timer(100, this);
	
	public Grid(Window window) {
		this.window = window;
		this.setFocusable(true);
		this.addKeyListener(this);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.setSize(new Dimension(window.getSize().width, window.getSize().height));
		
		pathFinder = new APathFinder(this, size);
		pathFinder.setDiagonal(true);
	}
	
	public void start() {
		if(start != null && end != null) {
			if(!steps) {
				pathFinder.start(start, end);
			} else {
				pathFinder.setup(start, end);
				timer.start();
			}
		} else {
			System.out.println("no start or end point");
		}
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		// Grid background
		g.setColor(Color.LIGHT_GRAY);
		for(int i=0; i<window.getWidth(); i+=size) {
			for(int j=0; j<window.getHeight(); j+=size) {
				g.drawRect(i, j, size, size);
			}
		}
		
		// Borders
		g.setColor(Color.BLACK);
		for(int i=0; i<pathFinder.getBorderList().size(); i++) {
			g.fillRect(pathFinder.getBorderList().get(i).getX()+1, pathFinder.getBorderList().get(i).getY()-1, size+1, size+1);
		}
		
		// Starting node
		if(start != null) {
			g.setColor(Color.CYAN);
			g.fillRect(start.getX()+1, start.getY()+1, size-1, size-1);
		}
		
		// End node
		if(end!= null) {
			g.setColor(Color.RED);
			g.fillRect(end.getX()+1, end.getY()+1, size-1, size-1);
		}
		
		// Open nodes
		for(int i=0; i<pathFinder.getOpenList().size(); i++) {
			Node current = pathFinder.getOpenList().get(i);
			
			g.setColor(Color.MAGENTA);
			g.fillRect(current.getX()+1, current.getY()+1, size-1, size-1);
		}
		
		// Closed nodes
		for(int i=0; i<pathFinder.getClosedList().size(); i++) {
			Node current = pathFinder.getClosedList().get(i);
					
			g.setColor(Color.PINK);
			g.fillRect(current.getX()+1, current.getY()+1, size-1, size-1);
		}
		
		// Path nodes
		for(int i=0; i<pathFinder.getPathList().size(); i++) {
			Node current = pathFinder.getPathList().get(i);
					
			g.setColor(Color.ORANGE);
			g.fillRect(current.getX()+1, current.getY()+1, size-1, size-1);
		}
				
		repaint();
		revalidate();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(pathFinder.isRunning() && steps) {
			pathFinder.findPath(pathFinder.getPar());
		}
		
		repaint();
	}
	
	public void mouseActions(MouseEvent e) {
		if(SwingUtilities.isLeftMouseButton(e)) {
			if(key == 's') {
				int xOver = e.getX() % size;
				int yOver = e.getY() % size;
				
				if(start == null) start = new Node(e.getX() - xOver, e.getY() - yOver);
				else start.setXY(e.getX() - xOver, e.getY() - yOver);
				this.repaint();
			}
			if(key == 'e') {
				int xOver = e.getX() % size;
				int yOver = e.getY() % size;
				
				if(end == null) end = new Node(e.getX() - xOver, e.getY() - yOver);
				else end.setXY(e.getX() - xOver, e.getY() - yOver);
				this.repaint();
			}
			if(key == 'b') {
				int borderX = e.getX() - (e.getX() % this.size);
				int borderY = e.getY() - (e.getY() % this.size);
				
				Node border = new Node(borderX, borderY);
				this.pathFinder.addBorder(border);
				this.repaint();
			}
		} else if(SwingUtilities.isRightMouseButton(e)) {
			if(key == 's') {
				int x = e.getX() - (e.getX() % size);
				int y = e.getY() - (e.getY() % size);
				
				if(start.getX() == x && start.getY() == y) start = null;
			}
			if(key == 'e' ) {
				int x = e.getX() - (e.getX() % size);
				int y = e.getY() - (e.getY() % size);
				
				if(end.getX() == x && end.getY() == y) end = null;
			}
			if(key == 'b') {
				int x = e.getX() - (e.getX() % size);
				int y = e.getY() - (e.getY() % size);
				
				for (int i=0; i<pathFinder.getBorderList().size(); i++) {
					Node border = pathFinder.getBorderList().get(i);
					if(border.getX() == x && border.getY() == y) {
						pathFinder.removeBorder(i);
					}
				}
			}
		}
	}

	@Override
	public void keyPressed(KeyEvent e) { 
		key = e.getKeyChar(); 
		if(key == ' ') {
			start();
		} else if(key == 'r') {
			pathFinder.reset();
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) { mouseActions(e); }
	@Override
	public void mouseDragged(MouseEvent e) { mouseActions(e); }
	
	@Override
	public void mousePressed(MouseEvent e) { }
	@Override
	public void mouseReleased(MouseEvent e) { }
	@Override
	public void mouseEntered(MouseEvent e) { }
	@Override
	public void mouseExited(MouseEvent e) {	}
	@Override
	public void keyTyped(KeyEvent e) { }
	@Override
	public void keyReleased(KeyEvent e) { }
	@Override
	public void mouseMoved(MouseEvent e) { }

}

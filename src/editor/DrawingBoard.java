package editor;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import abstracts.Tile;

public class DrawingBoard extends Canvas implements Runnable {
	private static final long serialVersionUID = 1L;
	private LevelEditor editor;
	
	private BufferedImage image;
	private int[] tiles;
	private int sizeW, sizeH;
	private int dx, dy;
	
	public DrawingBoard(final LevelEditor editor) {
		super();
		this.editor = editor;
		dx = dy = 0;
	}
	
	@Override
	public void run() {
		while (editor.running) {
			render();
			try {
				Thread.sleep(1);
			}
			catch (InterruptedException e) {
			}
		}
	}
	
	@Override
	public void setSize(int width, int height) {
		int w = width * Tile.WIDTH;
		int h = height * Tile.HEIGHT;
		Dimension size = new Dimension(w, h);
		//this.setSize(size);
		this.setPreferredSize(size);
		this.setMaximumSize(size);
		this.setMinimumSize(size);
		this.validate();
		this.editor.validate();
	}
	
	public void setImageSize(int w, int h) {
		if (w == 0 || h == 0)
			return;
		if (image != null) {
			image.flush();
			image = null;
		}
		tiles = new int[w * h];
		image = new BufferedImage(w * Tile.WIDTH, h * Tile.HEIGHT, BufferedImage.TYPE_INT_ARGB);
		int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
		for (int i = 0; i < pixels.length; i++)
			pixels[i] = -1;
		dx = dy = 0;
		editor.input.dx = editor.input.dy = 0;
		sizeW = w;
		sizeH = h;
	}
	
	public void newImage() {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				JTextField widthField = new JTextField("10");
				JTextField heightField = new JTextField("10");
				JPanel panel = new JPanel(new GridLayout(1, 1));
				panel.add(new JLabel("Width: "));
				panel.add(widthField);
				panel.add(new JLabel("Height: "));
				panel.add(heightField);
				int result = JOptionPane.showConfirmDialog(null, panel, "Create New Area", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
				if (result == JOptionPane.OK_OPTION) {
					setImageSize(Integer.valueOf(widthField.getText()), Integer.valueOf(heightField.getText()));
				}
			}
		});
	}
	
	public void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null) {
			this.createBufferStrategy(2);
			bs = this.getBufferStrategy();
		}
		
		if (editor.input.isDragging()) {
			dx = editor.input.dx;
			dy = editor.input.dy;
		}
		//		else if (editor.input.isDrawing()) {
		//			int px = editor.input.mouseX % (dx - image.getWidth());
		//			int py = editor.input.mouseY / (dx - image.getHeight());
		//			Iterator<Data> it = editor.getResourceFilePaths().iterator();
		//			for (; it.hasNext();) {
		//				Data d = it.next();
		//				if (d.editorID == editor.controlPanel.getSelectedData().editorID) {
		//					tiles[py * sizeW + px] = 0xFF00000;//d.editorID;
		//				}
		//			}
		//		}
		
		Graphics g = bs.getDrawGraphics();
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		g.translate(-dx, -dy);
		
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		if (image != null) {
			//g.drawImage(image, (this.getWidth() - image.getWidth()) / 2, (this.getHeight() - image.getHeight()) / 2, null);
			g.drawImage(image, 0, 0, null);
		}
		
		g.dispose();
		bs.show();
	}
	
	public void start() {
		new Thread(this).start();
	}
}

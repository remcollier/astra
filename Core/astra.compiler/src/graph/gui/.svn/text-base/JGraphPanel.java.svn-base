package graph.gui;

import graph.core.Edge;
import graph.core.Graph;
import graph.core.Vertex;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.Map;

import javax.swing.JPanel;


public class JGraphPanel<V, E> extends JPanel {
	private class DefaultOverlay implements GraphOverlay {
		@Override
		public Color edgeColor(Edge edge) {
			return Color.BLACK;
		}

		@Override
		public Color vertexColor(Vertex vertex) {
			return Color.BLACK;
		}
	}
	
	private Graph<V,E> G;
	private Map<Vertex<V>, Coordinate> points;
	private FontMetrics fm;
	private int ascent, fh, space;
	private int width;
	private int height;
	private GraphOverlay overlay;
	
	public JGraphPanel(Graph<V,E> G, Map<Vertex<V>, Coordinate> points) {
		this(G, points, 50, 30);
	}
	
	public JGraphPanel(Graph<V,E> G, Map<Vertex<V>, Coordinate> points, int w, int h) {
		this.G = G;
		this.points = points;
		width = w;
		height = h;
		overlay = new DefaultOverlay();
	}

	public void setOverlay(GraphOverlay overlay) {
		this.overlay = overlay;
	}
	
	public void clearOverlay() {
		overlay = new DefaultOverlay();
	}
	
	public void setVertexExtents(int w, int h) {
		width = w;
		height = h;
	}

	public void paintComponent(Graphics g) {
		g.setColor(Color.WHITE);
		super.paintComponent(g);
		
		paintEdges(g);
		paintVertices(g);
	}
	
	public void paintEdges(Graphics g) {
		Coordinate[] coords = new Coordinate[2];
		for (Edge<E> edge : G.edges()) {
			Vertex<V>[] endpoints = G.endVertices(edge);
			coords[0] = points.get(endpoints[0]);
			coords[1] = points.get(endpoints[1]);
			g.setColor(overlay.edgeColor(edge));
			g.drawLine(coords[0].x, coords[0].y, coords[1].x, coords[1].y);
			g.drawString(edge.toString(), 5+(coords[0].x + coords[1].x)/2, (coords[0].y + coords[1].y)/2);//center
		}
	}
	
	public void paintVertices(Graphics g) {
		Coordinate coord = null;
		for (Vertex<V> vertex : G.vertices()) {
			coord = points.get(vertex);
			g.setColor(overlay.vertexColor(vertex));
			g.fillOval(coord.x-width/2, coord.y-height/2, width, height);
			g.setColor(Color.WHITE);
			
		    if (fm == null) {
		        fm = g.getFontMetrics();
		        ascent = fm.getAscent();
		        fh = ascent + fm.getDescent();
		        space = fm.stringWidth(" ");
		    }
		    
		    String line = vertex.toString();
		    drawString(g, line, fm.stringWidth(line), coord.x, coord.y + ascent/2);
		    
		}
	}
	
	public void drawString(Graphics g, String line, int lineW, int x, int y) {
		g.drawString(line, x - lineW/2, y);//center
	}	
}

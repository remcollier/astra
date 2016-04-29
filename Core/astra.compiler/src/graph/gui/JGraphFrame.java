package graph.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import graph.core.Edge;
import graph.core.Graph;
import graph.core.GraphAlgorithm;
import graph.core.Parameter;
import graph.core.Vertex;
import graph.util.List;

public class JGraphFrame extends JFrame {
	private static final long serialVersionUID = 1L;

	private Graph<String, String> G;
	private Map<Vertex<String>, Coordinate> points;
	private Map<String, GraphAlgorithm<String, String>> searchMap;
	private Map<String, Vertex<String>> vertexMap;

	private JGraphPanel<String,String> graphPanel;
	private JComboBox searchBox;
	private DefaultComboBoxModel searchBoxModel;
	
	public JGraphFrame(Graph<String, String> G) {
		this.G = G;
		points = new HashMap<Vertex<String>, Coordinate>();
		searchMap = new HashMap<String, GraphAlgorithm<String, String>>();
		vertexMap = new HashMap<String, Vertex<String>>();
		init();
	}
	
	public void registerGraphAlgorithm(String name, GraphAlgorithm<String, String> algorithm) {
		searchMap.put(name, algorithm);
		((DefaultComboBoxModel) searchBox.getModel()).addElement(name);
		searchBox.repaint();
	}
	
	public JGraphFrame(Graph<String, String> G, String filename) {
		this(G);
		loadGraph(filename);
		initGraph();
	}
	
	private void init() {
		JMenuBar mb = new JMenuBar();
		JMenu menu = new JMenu("File");
		JMenuItem item = new JMenuItem("Load");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser("Select a File to Load");
				int returnVal = chooser.showOpenDialog(JGraphFrame.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = chooser.getSelectedFile();
					clearGraph();
					loadGraph(file.getAbsolutePath());
					initGraph();
				}
			}
		});
		menu.add(item);
		mb.add(menu);
		this.setJMenuBar(mb);
		
		graphPanel = new JGraphPanel<String, String>(G, points);
		setLayout(new BorderLayout());
		add(graphPanel, BorderLayout.CENTER);
		
		JPanel searchPanel = new JPanel();
		searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		searchBox = new JComboBox();
		searchBoxModel = new DefaultComboBoxModel();
		searchBoxModel.addElement("Remove Overlay");
		searchBox.setModel(searchBoxModel);
		searchPanel.add(searchBox);
		this.add(searchPanel, BorderLayout.SOUTH);
		JButton button = new JButton("Apply");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (searchBox.getSelectedItem().toString().equals("Remove Overlay")) {
					graphPanel.clearOverlay();
				} else {
					GraphAlgorithm<String, String> algorithm = searchMap.get(searchBox.getSelectedItem().toString());
					algorithm.setGraph(G);
					List<Parameter> parameters = algorithm.parameterList();
					Map<String, Vertex<String>> values = new HashMap<String, Vertex<String>>();
					
					for (Parameter parameter : parameters) {
						String value = JOptionPane.showInputDialog(parameter.getDescription());
						values.put(parameter.getName(), vertexMap.get(value));
					}
					
					algorithm.search(values);
					graphPanel.setOverlay(algorithm.getOverlay());
				}
				graphPanel.repaint();
			}
		});
		searchPanel.add(button);
		pack();
		setSize(640,480);
	}
	
	private void initGraph() {
		graphPanel.repaint();
	}

	private void clearGraph() {
		Iterator<Vertex<String>> it = G.vertices().iterator();
		while (it.hasNext()) {
			Vertex<String> v = it.next();
			Iterator<Edge<String>> it2 = G.incidentEdges(v).iterator();
			while (it2.hasNext()) it2.remove();
			it.remove();
		}
	}
	
	@SuppressWarnings("unchecked")
	public void loadGraph(String filename) {
		clearGraph();
		points.clear();
		vertexMap.clear();
		setTitle("Graph Viewer on: " + filename);
		try {
			Vertex<String>[] endpoints = (Vertex<String>[]) new Vertex[2];
			String line = null;
			BufferedReader in = new BufferedReader(new FileReader(filename));
			while ((line = in.readLine()) != null) {
				if (line.trim().isEmpty()) continue;
				
				String[] values = line.split(" ");
				if (values[0].equals("vertex")) {
					// line format: vertex <name> <x> <y>
					Vertex<String> vertex = G.insertVertex(values[1]);
					vertexMap.put(values[1], vertex);
					points.put(vertex, new Coordinate(Integer.parseInt(values[2]), Integer.parseInt(values[3])));
				} else if (values[0].equals("edge")) {
					// line format: edge <edge> <v1> <v2>
					endpoints[0] = vertexMap.get(values[2]);
					endpoints[1] = vertexMap.get(values[3]);
					G.insertEdge(endpoints[0], endpoints[1], values[1]);
				}
			}
			in.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

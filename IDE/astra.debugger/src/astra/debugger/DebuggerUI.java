package astra.debugger;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import astra.core.Agent;
import astra.gui.AstraEventListener;
import astra.gui.AstraGui;

public class DebuggerUI extends JFrame implements AstraGui {
	private AstraEventListener listener;

	// UI Components
	private JDesktopPane desktopPane;
	private AgentModel agentModel;
	private Map<String, AgentView> agentViews = new HashMap<String, AgentView>();
	
	public DebuggerUI() {
		setTitle("ASTRA Debugger V0.1");
		JList<String> list = new JList<String>(agentModel=new AgentModel());
		JScrollPane listScrollPane = new JScrollPane(list);
		
		list.addMouseListener(new MouseAdapter() {
		    public void mouseClicked(MouseEvent evt) {
		        JList<String> list = (JList<String>)evt.getSource();
		        if (evt.getClickCount() == 2) {
		            int index = list.locationToIndex(evt.getPoint());
		            
		            if (index > -1) {
		            	String name = list.getSelectedValue();
		            	AgentView view = agentViews.get(name);
		            	if (view == null) {
		            		view = new AgentView(listener,name);
		            		agentViews.put(name, view);
		            		desktopPane.add(view);
		            	}
	            		view.setVisible(!view.isVisible());
		            }
		        }
		    }
		});
		
		desktopPane = new JDesktopPane();
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                listScrollPane, desktopPane);
		
		setLayout(new BorderLayout());
		add(BorderLayout.CENTER, splitPane);
	}

	@Override
	public boolean receive(String type, List<?> args) {
		return false;
	}

	@Override
	public void launch(AstraEventListener listener) {
		this.listener = listener;
		this.setSize(800, 600);
		this.setVisible(true);
	}}

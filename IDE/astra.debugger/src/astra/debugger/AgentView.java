package astra.debugger;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;

import astra.core.Agent;
import astra.core.Intention;
import astra.core.Module.EVENT;
import astra.eis.EISAgent;
import astra.eis.EISAgent.EISBeliefBase;
import astra.eis.EISService;
import astra.event.Event;
import astra.formula.Formula;
import astra.gui.AstraEventListener;
import astra.trace.TraceEvent;
import astra.trace.TraceEventListener;
import astra.trace.TraceManager;

public class AgentView extends JInternalFrame implements TraceEventListener {
	private String name;
	private JTextArea display;
	private JComboBox<String> options;
	private AstraEventListener listener;
	
	public AgentView(AstraEventListener listener, String name) {
		super("Agent: " + name, true, true, true, true);
		
		this.listener = listener;
		this.name = name;
		
		DefaultComboBoxModel<String> comboModel = new DefaultComboBoxModel<String>();
		comboModel.addElement("BELIEFS");
		comboModel.addElement("EIS STATES");
		comboModel.addElement("INTENTIONS");
		comboModel.addElement("EVENTS");
		options = new JComboBox<String>(comboModel);
		options.addActionListener (new ActionListener () {
		    public void actionPerformed(ActionEvent e) {
				refreshView(Agent.getAgent(name));
		    }
		});
		JButton button;
		JToolBar toolBar = new JToolBar();
        toolBar.add(button=new JButton("Stop"));
        button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				listener.addEvent("suspend", new Object[] {name});
			}
        });
        toolBar.add(button=new JButton("Start"));
        button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				listener.addEvent("resume", new Object[] {name});
			}
        });
        toolBar.add(button=new JButton("Step"));
        button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				listener.addEvent("step", new Object[] {name});
			}
        });
        
		JPanel body = new JPanel();
		body.setLayout(new BorderLayout());
		body.add(BorderLayout.NORTH, options);
		body.add(BorderLayout.CENTER, new JScrollPane(display=new JTextArea()));
		display.setEditable(false);
		
		setLayout(new BorderLayout());
		add(BorderLayout.NORTH, toolBar);
		add(BorderLayout.CENTER, body);
		setSize(200, 100);
		
		refreshView(Agent.getAgent(name));
		TraceManager.getInstance().addListener(this);
	}
	
	@Override
	public void update(TraceEvent event) {
		if (event.type().equals(TraceEvent.END_OF_CYCLE) && event.source().name().equals(name)) {
			refreshView(event.source());
		}
	}

	private void refreshView(Agent source) {
		StringBuffer buf = new StringBuffer();
		if (options.getSelectedItem().equals("BELIEFS")) {
			for(Formula formula : source.beliefs().beliefs()) {
				buf.append(formula).append("\n");
			}
		} else if (options.getSelectedItem().equals("EIS STATES")) {
			for(EISService service: EISService.getServices()) {
				EISAgent agt = service.get(name);
				if (agt != null) {
					buf.append("Environment: " + service.id()+"\n\n");
					for(HashMap.Entry<String, EISBeliefBase> entry : agt.beliefs().entrySet()) {
						buf.append("Entity: " + entry.getKey()+"\n");
						for (String formula : entry.getValue().beliefStrings()) {
							buf.append(formula).append("\n");
						}
					}
					buf.append("\n");
				}
			}
		} else if (options.getSelectedItem().equals("EVENTS")) {
			for(Event event : source.events()) {
				buf.append(event.toString()).append("\n");
			}
		} else if (options.getSelectedItem().equals("INTENTIONS")) {
			for(Intention intention : source.intentions()) {
				buf.append(intention.toString()).append("\n");
			}
		}
		
		display.setText(buf.toString());
	}

}

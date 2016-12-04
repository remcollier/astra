import java.io.IOException;
import java.util.Map;

import com.sun.jdi.Bootstrap;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.StackFrame;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.VirtualMachineManager;
import com.sun.jdi.connect.AttachingConnector;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;

public class VMTest {
	public static void main(String[] args) throws IOException {
		VMTest test = new VMTest();
		VirtualMachine vm = test.connect(8000);
		
		try {
			for (ThreadReference thread : vm.allThreads()) {
//				System.out.println(thread.name());
//				System.out.println("status: "+thread.status());
				if (thread.name().equals("pool-1-thread-1")) {
					thread.suspend();
					
					for (StackFrame frame : thread.frames()) {
						System.out.println(frame.thisObject());
					}
					StackFrame currentFrame = thread.frame(0);
					ObjectReference object = currentFrame.thisObject();
					System.out.println("object: " + object.toString());
				}
			}
		} catch (IncompatibleThreadStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	/**
	 * Call this with the localhost port to connect to.
	 */
	public VirtualMachine connect(int port) throws IOException {
		String strPort = Integer.toString(port);
		AttachingConnector connector = getConnector();
		try {
			return connect(connector, strPort);
		} catch (IllegalConnectorArgumentsException e) {
			throw new IllegalStateException(e);
		}
	}

	private AttachingConnector getConnector() {
		VirtualMachineManager vmManager = Bootstrap.virtualMachineManager();
		for (Connector connector : vmManager.attachingConnectors()) {
			if ("com.sun.jdi.SocketAttach".equals(connector.name())) {
				return (AttachingConnector) connector;
			}
		}
		throw new IllegalStateException();
	}

	private VirtualMachine connect(AttachingConnector connector, String port)
			throws IllegalConnectorArgumentsException, IOException {
		Map<String, Connector.Argument> args = connector.defaultArguments();
		Connector.Argument pidArgument = args.get("port");
		if (pidArgument == null) {
			throw new IllegalStateException();
		}
		pidArgument.setValue(port);

		return connector.attach(args);
	}

}

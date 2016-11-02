import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.sun.jdi.Bootstrap;
import com.sun.jdi.ClassType;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.VirtualMachineManager;
import com.sun.jdi.connect.AttachingConnector;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;

public class VMTest {
	public static void main(String[] args) throws IOException {
		VMTest test = new VMTest();
		VirtualMachine vm = test.connect(8000);
		List<ReferenceType> list = vm.classesByName("astra.core.Agent");
//		((ClassType) list.get(0)).
	}
	/**
	 * Call this with the localhost port to connect to.
	 */
	public VirtualMachine connect(int port) throws IOException {
		String strPort = Integer.toString(port);
		AttachingConnector connector = getConnector();
		try {
			VirtualMachine vm = connect(connector, strPort);
			return vm;
		} catch (IllegalConnectorArgumentsException e) {
			throw new IllegalStateException(e);
		}
	}

	private AttachingConnector getConnector() {
		VirtualMachineManager vmManager = Bootstrap.virtualMachineManager();
		for (Connector connector : vmManager.attachingConnectors()) {
			System.out.println(connector.name());
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

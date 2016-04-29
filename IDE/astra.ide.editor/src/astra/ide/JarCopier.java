package astra.ide;

import java.io.FileOutputStream;
import java.io.InputStream;

import org.eclipse.core.runtime.IPath;

public class JarCopier {
	public static void copy(IPath location, String library) throws Exception {
		IPath jarPath = location.append(library);
		FileOutputStream fout = new FileOutputStream(jarPath.toString());
		try {
			InputStream jarIS = JarCopier.class.getResourceAsStream("/" + library);
			byte[] data = new byte[1024];
			int len = 0;
			do {
				len = jarIS.read(data, 0, data.length);
				if (len > -1) fout.write(data, 0, len);
			} while (len >= 0);
			
			jarIS.close();
		} catch (Exception io) {
			io.printStackTrace();
		}
		fout.close();
	}

}

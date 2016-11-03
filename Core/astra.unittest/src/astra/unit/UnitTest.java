package astra.unit;
import astra.core.Module;

public class UnitTest extends Module {
	public static final String SUCCESS_MESSAGE = "Success";
	@ACTION
	public boolean assertEquals(TestSuite suite, int first, int second) {
		if (first == second) {
			suite.testResult(first + " == " + second, true);
		} else {
			suite.testResult(first + " != " + second, false);
		}
		return true;
	}

	@ACTION
	public boolean assertEquals(TestSuite suite, long first, long second) {
		if (first == second) {
			suite.testResult(first + " == " + second, true);
		} else {
			suite.testResult(first + " != " + second, false);
		}
		return true;
	}

	@ACTION
	public boolean assertEquals(TestSuite suite, String first, String second) {
		if (first.equals(second)) {
			suite.testResult(first + " == " + second, true);
		} else {
			suite.testResult(first + " != " + second, false);
		}
		return true;
	}

	@ACTION
	public boolean success(TestSuite suite) {
		suite.testResult(SUCCESS_MESSAGE, true);
		return true;
	}
}
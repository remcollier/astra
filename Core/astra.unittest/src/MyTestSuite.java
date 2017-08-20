import astra.unit.TestSuite;

public class MyTestSuite extends TestSuite {
	public MyTestSuite() {
		testClasses.add("Core");
	}

	public static void main(String[] args) {
		MyTestSuite suite = new MyTestSuite();
		suite.execute();
		TestSuite.displayResults(suite);
	}
}

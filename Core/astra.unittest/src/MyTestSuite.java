import astra.unit.TestSuite;

public class MyTestSuite {
	public static void main(String[] args) {
		TestSuite suite = new TestSuite(new String[] { "Core" });
		suite.execute();
		TestSuite.displayResults(suite);
	}
}

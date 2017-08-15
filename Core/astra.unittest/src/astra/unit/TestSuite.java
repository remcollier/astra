package astra.unit;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import astra.core.ASTRAClass;
import astra.core.ASTRAClassNotFoundException;
import astra.core.Agent;
import astra.core.AgentCreationException;
import astra.core.Intention;
import astra.core.Rule;
import astra.core.Scheduler;
import astra.event.GoalEvent;
import astra.execution.DummySchedulerStrategy;
import astra.formula.Goal;
import astra.formula.Predicate;
import astra.term.Primitive;
import astra.term.Term;


public class TestSuite {
	/**
	 * Class to represent a test record
	 *  
	 * @author Rem
	 */
	private static class Test {
		public Test(String agent, Goal goal) {
			this.agent = agent;
			this.goal = goal;
		}
		String agent;
		Goal goal;
		String message;
		int steps;
		boolean result;
		boolean completed;
		
		public String toString() {
			return "["+(result ? "PASSED":"FAILED") + "] " + agent+"."+goal.formula().predicate()+" (steps: " + steps + ") -> " + message;
		}
	}
	
	/**
	 * The test classes
	 */
	protected List<String> testClasses = new LinkedList<String>();
	
	/**
	 * The test records
	 */
	protected List<Test> records = new LinkedList<Test>();
	
	/**
	 * The test record currently being evaluated.
	 */
	private Test current;
	
	
	public TestSuite(String[] tests) {
		for(String test : tests) {
			testClasses.add(test);
		}
	}
	
	/**
	 * Execution of the test suite: 
	 * - for each test class specified in the test set, an agent is created
	 *   - the test records for that test class are identified 
	 *   - the !setup(TestSuite) goal is executed
	 *   - each test record is executed
	 *   - the !teardown(TestSuite) goal is executed.
	 *   - the agent is terminated  
	 */
	public void execute() {
		Scheduler.setStrategy(new DummySchedulerStrategy());
		
		try {
			for (String clazz : testClasses) {
				// The next line of code create an agent whose name is the key part of the entry and
				// whose type (class) is the value part of the entry.
				ASTRAClass cls = (ASTRAClass) Class.forName(clazz).newInstance();

				buildTestList(cls);
				
				// Create the test agent
				Agent agent = cls.newInstance(clazz);
				
				// Now execute the three goals that correspond to a test: 
				executeIntention(agent, new Goal(new Predicate("setup", new Term[] {Primitive.newPrimitive(this)})));
				for (int i=0; i<records.size(); i++) {
					current = records.get(i);
					executeTestIntention(agent, current.goal);
				}
				executeIntention(agent, new Goal(new Predicate("teardown", new Term[] {Primitive.newPrimitive(this)})));
				agent.terminate();
			}
		} catch (AgentCreationException | ASTRAClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Contructs the test list by identifying all goals with prefix "test_". A test record is
	 * created for each such goal.
	 * 
	 * @param cls
	 * @return
	 * @throws ASTRAClassNotFoundException
	 */
	private List<Goal> buildTestList(ASTRAClass cls) throws ASTRAClassNotFoundException {
		for (ASTRAClass clazz : cls.getLinearization()) {
			for(Entry<String, List<Rule>> entry : clazz.rules().entrySet()) {
				for(Rule rule : entry.getValue()) {
					if (rule.event instanceof GoalEvent) {
						GoalEvent event = (GoalEvent) rule.event;
						if (event.goal.formula().predicate().startsWith("test_") && (event.goal.formula().size() == 1)){
							records.add(new Test(cls.getCanonicalName(), new Goal(new Predicate(event.goal.formula().predicate(), new Term[] {Primitive.newPrimitive(this)}))));
						}
					}
				}
			}
		}
		return null;
	}

	/**
	 * Executes an intention to completion (identified by the agent
	 * having no more intentions).
	 * 
	 * @param agent
	 * @param goal
	 */
	private void executeIntention(Agent agent, Goal goal) {
		agent.initialize(goal);
		do {
			agent.execute();
		} while (!agent.intentions().isEmpty());
	}

	/**
	 * Special version of the execute intention method that automatically
	 * detects the succes / failure of the test goal and updates the
	 * test record accordingly.
	 * 
	 * @param agent
	 * @param goal
	 */
	private void executeTestIntention(Agent agent, Goal goal) {
		current.steps = 0;
		agent.initialize(goal);
		Intention intention = null;
		do {
			agent.execute();
			current.steps++;
			if (intention == null) intention = agent.intention();
		} while (!agent.intentions().isEmpty());
		
		if (intention != null) { 
			if (intention.isFailed()) {
				current.message = intention.failureReason();
				current.result = false;
			} else {
				current.result = true;
			}
			current.completed=true;
		}
	}

	/**
	 * Completes the test record for the current test.
	 * 
	 * @param message
	 * @param result
	 */
	public void testResult(String message, boolean result) {
		current.message = message;
		current.result = result;
		current.completed = true;
	}
	
	/**
	 * Returns the total number of tests carried out
	 * @return
	 */
	public int totalTests() {
		return records.size();
	}
	
	/**
	 * Returns the number of tests that were passed
	 * @return
	 */
	public int passedTests() {
		int count = 0;
		for (Test test : records) {
			if (test.result) count++;
		}
		return count;
	}

	/**
	 * Returns the number of tests that were failed
	 * @return
	 */
	public int failedTests() {
		int count = 0;
		for (Test test : records) {
			if (!test.result) count++;
		}
		return count;
	}

	/**
	 * Method that displays the outcome of the tests to the console
	 * @param suite
	 */
	public static void displayResults(TestSuite suite) {
		System.out.println("\n\nTEST RESULTS:\n====================================================");
		for (Test test : suite.records) {
			System.out.println(test);
		}
		System.out.println("====================================================");
		System.out.println("results: passed " + suite.passedTests() + " of " + suite.totalTests() + " tests");
	}
}

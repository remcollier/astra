package astra.ast.core;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import astra.ast.element.FunctionElement;
import astra.ast.element.InferenceElement;
import astra.ast.element.InitialElement;
import astra.ast.element.ModuleElement;
import astra.ast.element.TypesElement;
import astra.ast.element.PackageElement;
import astra.ast.element.PlanElement;
import astra.ast.element.RuleElement;
import astra.ast.visitor.Utilities;

public class ASTRAClassElement implements IElement {
	static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	PackageElement packageElement;
	List<ImportElement> imports = new LinkedList<ImportElement>();
	ClassDeclarationElement declaration;
	List<TypesElement> ontologies = new LinkedList<TypesElement>();
	List<InitialElement> initials = new LinkedList<InitialElement>();
	List<PlanElement> plans = new LinkedList<PlanElement>();
	List<FunctionElement> functions = new LinkedList<FunctionElement>();
	List<InferenceElement> inferences = new LinkedList<InferenceElement>();
	List<RuleElement> rules = new LinkedList<RuleElement>();
	List<ModuleElement> modules = new LinkedList<ModuleElement>();

	private Token first, last;
	private boolean local;
	
	public ASTRAClassElement(String source, String contents) throws ParseException {
		this(source, new ByteArrayInputStream(contents.getBytes()));
	}
	
	/**
	 * Main contstructor for ASTRAClassElement.  This constructor parses the given
	 * input stream and generates the ASTRA model for the class.  Exceptions are
	 * created for any parse errors.
	 * 
	 * NOTE: Upon completion, this will be a coarse grained AST for ASTRA, but
	 * it will not necessarily be semantically valid.  You should use the
	 * associated visitors to ensure semantic correctness.
	 *  
	 * @param in the input stream
	 * @throws ParseException - generated for any parsing error
	 */
	public ASTRAClassElement(String source, InputStream in) throws ParseException {
			this(source, in, true);
	}
	
	public ASTRAClassElement(String source, InputStream in, boolean local) throws ParseException {
		this.local = local;
		ADTTokenizer tokenizer = new ADTTokenizer(in);
		ASTRAParser parser = new ASTRAParser(tokenizer);
		
		Token tok = tokenizer.nextToken();
		first = tok;
		if (tok.type == Token.PACKAGE) {
			List<Token> list = parser.readTo(Token.SEMI_COLON);
			packageElement = parser.createPackage(list.subList(0, list.size()-1));
			tok = tokenizer.nextToken();
		} else {
			packageElement = new PackageElement("", null, null, "");
		}

		while (!(tok.type == Token.AGENT || tok.type == Token.ABSTRACT)) {
			switch (tok.type) {
			case Token.PACKAGE:
				throw new ParseException("Package declaration must be the first line of the file", tok, tok);
			case Token.IMPORT:
				List<Token> list = parser.readTo(Token.SEMI_COLON);
				imports.add(parser.createImport(list.subList(0, list.size()-1)));
				tok = tokenizer.nextToken();
				break;
			default:
				throw new ParseException("Unexpected statement: " + tok.token, tok, tok);
			}
		}

		// Re-add the token because there are multiple possible first keywords for this class
		List<Token> list = parser.readTo(Token.LEFT_BRACE);
		list.add(0, tok);
		declaration = parser.createClassDeclaration(list);
		declaration.setParent(this);

		// Here we check that the Agent Name and Package correspond to the source name
		Utilities.validatePackageAndClassName(this, source);
		
		boolean finished = false;
		while (!finished) {
			try {
				tok = tokenizer.nextToken();
//				System.out.println("tok: " + tok.token);
				switch (tok.type) {
				case Token.MODULE:
					list = parser.readTo(Token.SEMI_COLON);
					modules.add((ModuleElement) parser.createModule(list.subList(0, list.size()-1)).setParent(this));
					break;
				case Token.TYPES:
					list = parser.readTo(Token.RIGHT_BRACE);
					ontologies.add(parser.createTypes(list.subList(0, list.size()-1)));
					break;
				case Token.INITIAL:
					list = parser.readTo(Token.SEMI_COLON);
					initials.addAll(parser.createInitial(list.subList(0, list.size()-1)));
					break;
				case Token.INFERENCE:
					list = parser.readTo(Token.SEMI_COLON);
					inferences.add((InferenceElement) parser.createInference(list.subList(0, list.size()-1)).setParent(this));
					break;
				case Token.RULE:
					list = parser.readTo(Token.RIGHT_BRACE);
					rules.add((RuleElement) parser.createRule(list).setParent(this));
					break;
				case Token.FUNCTION:
					list = parser.readTo(Token.RIGHT_BRACE);
					functions.add((FunctionElement) parser.createFunction(list).setParent(this));
					break;
				case Token.PLAN:
					list = parser.readTo(Token.RIGHT_BRACE);
					plans.add((PlanElement) parser.createPlan(list).setParent(this));
					break;
				case Token.RIGHT_BRACE:
					last = tok;
				case Token.EOF:
					finished = true;
					break;
				default:
					throw new ParseException("Unknown token: " + tok.token, tok);
				}
			} catch (ParseException e) {
				store(e);
			} catch (Throwable th) {
				store(new ParseException("Unexpected Error: " + th.getMessage(), th, 1, 1, 0));
				th.printStackTrace();
				logger.log(Level.SEVERE, "Unexpected Error", th);
			}
		}
		
		Token token = null;
		List<Token> endList = new LinkedList<Token>();
		while((token = tokenizer.nextToken()) != Token.EOF_TOKEN) {
			endList.add(token);
		}
		if (!endList.isEmpty()) errorList.add( new ParseException("Code is outside the agent declaration", endList.get(0), endList.get(endList.size()-1)));
		
		for(InitialElement elem : initials) {
			elem.setParent(this);
		}
	}


	List<ParseException> errorList = new LinkedList<ParseException>();
	
	public void store(ParseException e) {
		errorList.add(e);
	}
	
	public List<ParseException> getErrorList() {
		return errorList;
	}
	
	public IElement[] getElements() {
		List<IElement> elements = new ArrayList<IElement>();
		elements.addAll(modules);
		elements.addAll(initials);
		elements.addAll(rules);
		elements.addAll(plans);
		elements.addAll(functions);
		return elements.toArray(new IElement[elements.size()]);
	}
	
	/**
	 * Returns all the plans specified in the agent class
	 * @return
	 */
	public PlanElement[] getPlans() {
		return plans.toArray(new PlanElement[] {});
	}
	
	/**
	 * Returns all the rules specified in the agent class
	 * @return
	 */
	public RuleElement[] getRules() {
		return rules.toArray(new RuleElement[] {});
	}
	
	public ModuleElement[] getModules() {
		return modules.toArray(new ModuleElement[] {});
	}
	
	public InitialElement[] getInitials() {
		return initials.toArray(new InitialElement[] {});
	}



	/**
	 * TODO: Refactor this into new ASTRAParser

//		case Token.FUNCTION:
//			if (!inAgent) throw new ParseException("you cannot declare a teleoreactive function outside an agent", tok);
//			formula = processFormula();
//			tok2 = tokenizer.nextToken();
//			if (tok2.type != Token.LEFT_BRACE) {
//				throw new ParseException("Invalid token: expected { but got '" + tok2.token +"'", tok2);
//			}
//			TRRuleElement[] rules = processRules();
//			tok2 = tokenizer.nextToken();
//			if (tok2.type != Token.RIGHT_BRACE) {
//				throw new ParseException("Invalid token: expected } but got '" + tok2.token +"'", tok2);
//			}
//			return new FunctionElement((PredicateFormula) formula, rules, tok, tok2, tokenizer.getSource(tok, tok2));
	private TRRuleElement[] processRules() throws ParseException {
		List<TRRuleElement> rules = new LinkedList<TRRuleElement>();
		Token tok = tokenizer.peek();
		while (tok.type != Token.RIGHT_BRACE) {
			IFormula formula = processFormula();
			tok = tokenizer.nextToken();
			if (tok.type != Token.MINUS) {
				throw new ParseException("Invalid token: expected -> but got '" + tok.token +"'", tok);
			}
			tok = tokenizer.nextToken();
			if (tok.type != Token.GREATER_THAN) {
				throw new ParseException("Invalid token: expected -> but got '" + tok.token +"'", tok);
			}
			IAction action = processAction();
			Token tok2 = tokenizer.getLastToken();
			rules.add(new TRRuleElement(formula, action, tok, tok2, tokenizer.getSource(tok, tok2)));
			tok = tokenizer.peek();
		}
		
		return rules.toArray(new TRRuleElement[rules.size()]);
	}
	
	 * Process action types:
	 * - block
	 * - module
	 * - EIS
	 * - CARTAGO
	 * - Belief Update
	 * @return
	 * @throws ParseException 
	private IAction processAction() throws ParseException {
		Token tok = tokenizer.nextToken();
		if (tok.type == Token.LEFT_BRACE) {
			List<IAction> actions = new LinkedList<IAction>();
			
			Token tok2 = tokenizer.peek();
			while (tok2.type != Token.RIGHT_BRACE) {
				actions.add(processAction());
				Token tok3 = tokenizer.getLastToken();
				if (semiColonCheck()) throw new ParseException("Missing ; got '" + tokenizer.getLastToken().token +"'", tok2, tok3);
				tok2 = tokenizer.peek();
			}
			tokenizer.nextToken();
			return new BlockAction(actions, tok, tok2, tokenizer.getSource(tok, tok2));
		} else if (tok.type == Token.PLUS || tok.type == Token.MINUS) {
			PredicateFormula predicate = (PredicateFormula) processUnaryFormula();
			return new UpdateAction(tok.token, predicate, tok,tokenizer.getLastToken(), tokenizer.getSource(tok, tokenizer.getLastToken()));
		} else if (tok.type == Token.TR_START || tok.type == Token.TR_STOP) {
			Token tok2 = tokenizer.nextToken();
			if (tok2.type != Token.LEFT_BRACKET) {
				throw new ParseException("Illegal token: expected ( but got: " + tok.token, tok);
			}
			
			PredicateFormula predicate = (PredicateFormula) processUnaryFormula();

			tok2 = tokenizer.nextToken();
			if (tok2.type != Token.RIGHT_BRACKET) {
				throw new ParseException("Illegal token: expected ) but got: " + tok.token, tok);
			}
			return new TRAction(tok.token, predicate, tok, tok2, tokenizer.getSource(tok, tok2));
		} else if (tok.type == Token.EIS) {
			Token tok2 = tokenizer.nextToken();
			if (tok2.type != Token.PERIOD) {
				throw new ParseException("Illegal token: expected . but got: " + tok.token, tok);
			}
			PredicateFormula predicate = (PredicateFormula) processUnaryFormula();
			return new EISAction(predicate, tok, tok2, tokenizer.getSource(tok, tok2));
		} else if (tok.type == Token.CARTAGO) {
			ITerm artifact = null;
			Token tok2 = tokenizer.nextToken();
			if (tok2.type == Token.LEFT_BRACKET) {
				artifact = processUnaryTerm(); 
				tok2 = tokenizer.nextToken();
				if (tok2.type != Token.RIGHT_BRACKET) {
					throw new ParseException("Illegal token: expected ) but got: " + tok.token, tok);
				}
				tok2 = tokenizer.nextToken();
			}
			
			if (tok2.type != Token.PERIOD) {
				throw new ParseException("Illegal token: expected ( but got: " + tok.token, tok);
			}
			
			PredicateFormula predicate = (PredicateFormula) processUnaryFormula();
			return new CartagoAction(artifact, predicate, 
					tok, tok2, tokenizer.getSource(tok, tok2));
		} else if (tok.type == Token.IDENTIFIER) {
			Token tok2 = tokenizer.nextToken();
			if (tok2.type == Token.PERIOD) {
				PredicateFormula formula = processPredicateFormula();
				return new TRModuleCallAction(tok.token, formula, tok, tok2, tokenizer.getSource(tok, tok2));
			} else if (tok2.type == Token.LEFT_BRACKET) {
				tokenizer.back(tok2);
				tokenizer.back(tok);
				PredicateFormula predicate = (PredicateFormula) processUnaryFormula();
				return new FunctionCallAction(predicate, 
						tok, tok2, tokenizer.getSource(tok, tok2));
			} else {
				throw new ParseException("Illegal token: expected . or ( but got: " + tok2.token, tok2);
			}
		}
		return null;
	}
	 */

	@Override
	public String getSource() {
		return null;
	}

	@Override
	public Object accept(IElementVisitor visitor, Object data) throws ParseException {
		return visitor.visit(this, data);
	}

	public PackageElement packageElement() {
		return packageElement;
	}

	public ImportElement[] imports() {
		return imports.toArray(new ImportElement[] {});
		
	}

	public ClassDeclarationElement getClassDeclaration() {
		return declaration;
	}

	public InferenceElement[] getInferences() {
		return inferences.toArray(new InferenceElement[] {});
	}

	public FunctionElement[] getFunctions() {
		return functions.toArray(new FunctionElement[] {});
	}

	public String getFilename() {
		if (packageElement == null) return declaration.name() + ".java";
		return packageElement.packageName().replace(".", "/") + "/" + declaration.name() + ".java";
	}

	@Override
	public IElement getParent() {
		return null;
	}

	@Override
	public IElement setParent(IElement parent) {
		return this;
	}

	@Override
	public int getBeginLine() {
		return first.beginLine;
	}

	public String getQualifiedName() {
		return (packageElement.packageName().equals("") ? "":(packageElement.packageName() + ".")) + declaration.name();
	}

	@Override
	public int getBeginColumn() {
		return first.beginColumn;
	}

	@Override
	public int charStart() {
		return first.charStart;
	}

	@Override
	public int charEnd() {
		return last.charEnd;
	}
	
	public String toString() {
		return this.getQualifiedName();
	}

	public List<TypesElement> getOntologies() {
		return this.ontologies;
	}
	
	public boolean local() {
		return local;
	}
}

package astra.ast.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import astra.ast.definition.FormulaDefinition;
import astra.ast.definition.TypeDefinition;
import astra.ast.element.FunctionElement;
import astra.ast.element.GRuleElement;
import astra.ast.element.InferenceElement;
import astra.ast.element.InitialElement;
import astra.ast.element.ModuleElement;
import astra.ast.element.PackageElement;
import astra.ast.element.PlanElement;
import astra.ast.element.RuleElement;
import astra.ast.element.TypesElement;
import astra.ast.event.MessageEvent;
import astra.ast.event.ModuleEvent;
import astra.ast.event.UpdateEvent;
import astra.ast.formula.AndFormula;
import astra.ast.formula.BindFormula;
import astra.ast.formula.BracketFormula;
import astra.ast.formula.ComparisonFormula;
import astra.ast.formula.FormulaVariable;
import astra.ast.formula.GoalFormula;
import astra.ast.formula.IsDoneFormula;
import astra.ast.formula.ModuleFormula;
import astra.ast.formula.NOTFormula;
import astra.ast.formula.OrFormula;
import astra.ast.formula.PredicateFormula;
import astra.ast.formula.ScopedGoalFormula;
import astra.ast.statement.AssignmentStatement;
import astra.ast.statement.BlockStatement;
import astra.ast.statement.DeclarationStatement;
import astra.ast.statement.ForAllStatement;
import astra.ast.statement.ForEachStatement;
import astra.ast.statement.IfStatement;
import astra.ast.statement.MaintainBlockStatement;
import astra.ast.statement.MinusMinusStatement;
import astra.ast.statement.ModuleCallStatement;
import astra.ast.statement.PlanCallStatement;
import astra.ast.statement.PlusPlusStatement;
import astra.ast.statement.QueryStatement;
import astra.ast.statement.ScopedStatement;
import astra.ast.statement.SendStatement;
import astra.ast.statement.SpawnGoalStatement;
import astra.ast.statement.SubGoalStatement;
import astra.ast.statement.SynchronizedBlockStatement;
import astra.ast.statement.TRStatement;
import astra.ast.statement.TryRecoverStatement;
import astra.ast.statement.UpdateStatement;
import astra.ast.statement.WaitStatement;
import astra.ast.statement.WhileStatement;
import astra.ast.term.AtIndexTerm;
import astra.ast.term.Brackets;
import astra.ast.term.CountTerm;
import astra.ast.term.Function;
import astra.ast.term.HeadTerm;
import astra.ast.term.InlineVariableDeclaration;
import astra.ast.term.ListSplitterTerm;
import astra.ast.term.ListTerm;
import astra.ast.term.Literal;
import astra.ast.term.ModuleTerm;
import astra.ast.term.Operator;
import astra.ast.term.QueryTerm;
import astra.ast.term.TailTerm;
import astra.ast.term.Variable;
import astra.ast.tr.FunctionCallAction;
import astra.ast.tr.TRModuleCallAction;
import astra.ast.tr.TRRuleElement;
import astra.ast.tr.UpdateAction;
import astra.ast.type.BasicType;
import astra.ast.type.ObjectType;

public class ASTRAParser {
	private static final int[] OPERATOR_PRECEDENCE = { Token.MULTIPLY, Token.DIVIDE, Token.MODULO, Token.PLUS, Token.MINUS };
	private static final int[] BOOLEAN_OPERATOR_PRECEDENCE = { Token.AND, Token.OR };
	private static final int[][] COMPARISON_OPERATOR_PRECEDENCE = { 
		new int[] {Token.ASSIGNMENT, Token.ASSIGNMENT}, 
		new int[] {Token.NOT, Token.ASSIGNMENT}, 
		new int[] {Token.GREATER_THAN, Token.ASSIGNMENT}, 
		new int[] {Token.GREATER_THAN}, 
		new int[] {Token.LESS_THAN, Token.ASSIGNMENT}, 
		new int[] {Token.LESS_THAN}
	};
	static Map<Integer, Integer> BRACKET_PAIRINGS = new HashMap<Integer, Integer>();
	static {
		BRACKET_PAIRINGS.put(Token.LEFT_BRACKET, Token.RIGHT_BRACKET);
		BRACKET_PAIRINGS.put(Token.LEFT_BRACE, Token.RIGHT_BRACE);
		BRACKET_PAIRINGS.put(Token.LEFT_SQ_BRACKET, Token.RIGHT_SQ_BRACKET);
	}
	
	static Map<Integer, String> BRACKET_STRINGS = new HashMap<Integer, String>();
	static {
		BRACKET_STRINGS.put(Token.LEFT_BRACE, "{");
		BRACKET_STRINGS.put(Token.LEFT_BRACKET, "(");
		BRACKET_STRINGS.put(Token.LEFT_SQ_BRACKET, "[");
		BRACKET_STRINGS.put(Token.RIGHT_SQ_BRACKET, "]");
		BRACKET_STRINGS.put(Token.RIGHT_BRACKET, ")");
		BRACKET_STRINGS.put(Token.RIGHT_BRACE, "}");
	}
	
	private ADTTokenizer tokenizer;
	
	public ASTRAParser(ADTTokenizer tokenizer) {
		this.tokenizer = tokenizer;
	}
	
	public List<Token> readTo(int type) throws ParseException {
		List<Token> list = new ArrayList<Token>();
		Token token = tokenizer.nextToken();
		Token first = token;
		
		boolean finished = false;
		Stack<Token> bracketStack = new Stack<Token>();
		while ( token.type != Token.EOF && !finished) {
			if (BRACKET_PAIRINGS.containsKey(token.type)) {
				bracketStack.push(token);
				if (token.type == type && bracketStack.size() == 1 ) {
					finished = true;
				}
			} else if (BRACKET_PAIRINGS.containsValue(token.type)) {
				if (bracketStack.isEmpty()) {
					throw new ParseException("Too many brackets", token, token);
				}
				Token t = bracketStack.pop();
				if (BRACKET_PAIRINGS.get(t.type) != token.type)
					throw new ParseException("Mismatched Brackets: expected " + BRACKET_STRINGS.get(BRACKET_PAIRINGS.get(t.type)) + " but got: " + token.token, first, token);
			}
			
			if (token.type == type && bracketStack.isEmpty() ) {
				finished = true;
			}
			list.add(token);
			if (!finished) token = tokenizer.nextToken();
		}
		return list;
	}

	public ClassDeclarationElement createClassDeclaration(List<Token> tokens) throws ParseException {
		Token first = tokens.get(0);
		Token last = getLast(tokens);
		
		boolean _abstract = false;
		boolean _final = false;
		String name = null;
		
		Token tok = tokens.remove(0);
		while (tok.type != Token.AGENT) {
			switch (tok.type) {
			case Token.ABSTRACT:
				_abstract = true;
			case Token.FINAL:
				_final = true;
			default:
				new ParseException("Unknown modifier: " + tok.token, tok, tok);
			}
			tok = tokens.remove(0);
		}
		name = tokens.remove(0).token;
		
		List<String> parents = new ArrayList<String>();
		tok = tokens.remove(0);
		if (tok.type == Token.EXTENDS) {
			while (!tokens.isEmpty() && tok.type != Token.LEFT_BRACE) {
				List<Token> list = splitAt(tokens, new int[] {Token.COMMA});
				parents.add(getQualifiedName(list.subList(0, list.size()-1)));
				tok = list.get(list.size()-1);
			}
		}
		
		if (parents.isEmpty()) {
			parents.add("astra.lang.Agent");
		}
		
		if (tok.type != Token.LEFT_BRACE) {
			throw new ParseException("Invalid Class Declaration", first, last);
		}
		return new ClassDeclarationElement(name, parents.toArray(new String[parents.size()]),
				_abstract, _final, first, last, tokenizer.getSource(first,last));
	}
	
	public ImportElement createImport(List<Token> tokens) throws ParseException {
		Token first = tokens.get(0);
		Token last = tokens.get(tokens.size() - 1);
		return new ImportElement(getQualifiedName(tokens), first, last, tokenizer.getSource(first, last));
	}

	public PackageElement createPackage(List<Token> tokens) throws ParseException {
		Token first = tokens.get(0);
		Token last = tokens.get(tokens.size() - 1);
		return new PackageElement(getQualifiedName(tokens), first, last, tokenizer.getSource(first, last));
	}
	
	/**
	 * Creates a plan rule node of the ASTRA AST.
	 * 
	 * @param tokens
	 * @return
	 * @throws ParseException
	 */
	public RuleElement createRule(List<Token> tokens) throws ParseException {
		Token first = tokens.get(0);
//		Token last = tokens.get(tokens.size() - 1);
		List<Token> list = splitAt(tokens, new int[] {Token.COLON, Token.LEFT_BRACE});
		Token tok = list.remove(list.size()-1);

		IEvent event = createEvent(list);

		// If we split on a COLON, then we have a context...
		IFormula context = new PredicateFormula("true", new LinkedList<ITerm>(), tok, tok, tokenizer.getSource(tok, tok));;
		if (tok.type == Token.COLON) {
			list = splitAt(tokens, new int[] {Token.LEFT_BRACE});
			// Now we have to have terminated with a LEFT_BRACE, so get it...
			tok = list.remove(list.size()-1);
			if (list.isEmpty()) throw new ParseException("Unexpected token: ':'", tok, tok);
			context = createFormula(list);
		}
		
		// Re-insert left brace and process the statement...
		tokens.add(0, tok);
		return new RuleElement(event, context, createStatement(tokens),
				first, tok, tokenizer.getSource(first, tok));
	}
	
	public GRuleElement createGRule(List<Token> tokens) throws ParseException {
		Token first = tokens.get(0);
		Token last = tokens.get(tokens.size() - 1);
		List<Token> list = splitAt(tokens, new int[] {Token.COLON, Token.LESS_THAN, Token.LEFT_BRACE});
		Token tok = list.remove(list.size()-1);

		IEvent event = createEvent(list);
//		System.out.println("Event: " + event);
		
		// If we split on a COLON, then we have a context...
		IFormula context = new PredicateFormula("true", new LinkedList<ITerm>(), tok, tok, tokenizer.getSource(tok, tok));;
		if (tok.type == Token.COLON) {
			list = splitAt(tokens, new int[] {Token.LEFT_BRACE, Token.LESS_THAN});
			// Now we have to have terminated with a LEFT_BRACE, so get it...
			tok = list.remove(list.size()-1);
			if (list.isEmpty()) throw new ParseException("Unexpected token: ':'", tok, tok);
			context = createFormula(list);
		} 
//		System.out.println("Context: " + context);
		
		IFormula dropCondition = new IsDoneFormula(tok, tok, "");
		if (tok.type == Token.LESS_THAN) {
			// Remove the next token to check for a COLON (:)
			tok = tokens.remove(0);
			if (tok.type != Token.COLON) throw new ParseException("Unexpected token: "+tok.token, tok, tok);
			
			list = splitAt(tokens, new int[] {Token.LEFT_BRACE});
			// Now we have to have terminated with a LEFT_BRACE, so get it...
			tok = list.remove(list.size()-1);
			if (list.isEmpty()) throw new ParseException("Unexpected token: ':'", tok, tok);
			dropCondition = createFormula(list);
		}
//		System.out.println("Drop Condition: " + dropCondition);
		
		IStatement body = null;
		List<RuleElement> rules = new LinkedList<RuleElement>();
		
		// Re-insert left brace and process the statement...
		// tokens.add(0, tok);
		tok = tokens.remove(0);
		while (tok.type != Token.RIGHT_BRACE) {
			switch (tok.type) {
			case Token.BODY:
				body = createStatement(tokens);
//				System.out.println("body: " + body);
				break;
			case Token.RULE:
				RuleElement rule = this.createRule(tokens);
//				System.out.println("rule: " + rule);
				rules.add(rule);
				break;
			case Token.GRULE:
				rule = this.createGRule(tokens);
//				System.out.println("grule: " + rule);
				rules.add(rule);
				break;
			default:
				throw new ParseException("Not a valid rule: " +tok.token, tok);

			}
			
			tok = tokens.remove(0);
		}
		if (body == null) {
			body = new BlockStatement(new LinkedList<IStatement>(), first, last, "");
		}
		
		System.out.println("FINAL: " + event + " / " + context + " / " + dropCondition + " / " + body);
		return new GRuleElement(event, context, dropCondition, body, rules,
				first, tok, tokenizer.getSource(first, tok));
	}
	
	
	public RuleElement createSynchronizedRule(List<Token> tokens) throws ParseException {
		if (tokens.remove(0).type != Token.RULE) {
			throw new ParseException("Expected rule keyword, but got: " + tokens.get(0).token, tokens.get(0), tokens.get(0));
		}
		
		Token first = tokens.get(0);
		Token last = tokens.get(tokens.size() - 1);
		List<Token> list = splitAt(tokens, new int[] {Token.COLON, Token.LEFT_BRACE});
		Token tok = list.remove(list.size()-1);

		IEvent event = createEvent(list);

		// If we split on a COLON, then we have a context...
		IFormula context = new PredicateFormula("true", new LinkedList<ITerm>(), tok, tok, tokenizer.getSource(tok, tok));;
		if (tok.type == Token.COLON) {
			list = splitAt(tokens, new int[] {Token.LEFT_BRACE});
			// Now we have to have terminated with a LEFT_BRACE, so get it...
			tok = list.remove(list.size()-1);
			if (list.isEmpty()) throw new ParseException("Unexpected token: ':'", tok, tok);
			context = createFormula(list);
		}
		
		// Re-insert left brace and process the statement...
		tokens.add(0, tok);
		
		// Convert the block into a synchronized block...
		BlockStatement block = (BlockStatement) createStatement(tokens);
		List<IStatement> statements = new LinkedList<IStatement>();
		for(IStatement statement: block.statements()) {
			statements.add(statement);
		}
		SynchronizedBlockStatement sblock = 
				new SynchronizedBlockStatement("synchronized", statements, block.start, block.end, block.getSource());
		return new RuleElement(event, context, sblock,
				first, last, tokenizer.getSource(first, last));
	}

	public FunctionElement createFunction(List<Token> tokens) throws ParseException {
		Token first = tokens.get(0);
		Token last = tokens.get(tokens.size() - 1);

		List<Token> list = splitAt(tokens, new int[] {Token.LEFT_BRACE});
		list.remove(list.size()-1);
		
		if (last.type != Token.RIGHT_BRACE) throw new ParseException("Mismatched brackets", first, last);
		tokens.remove(tokens.size()-1);
		
		List<TRRuleElement> rules = new ArrayList<TRRuleElement>();
		
		// Construct the TR Function, reading each action-selection rule
		if (!tokens.isEmpty()) {
			do {
				List<Token> list2 = splitAt(tokens, new int[] {Token.MINUS});
				while (!tokens.isEmpty() && tokens.get(0).type != Token.GREATER_THAN) {
					list2.addAll(splitAt(tokens, new int[] {Token.MINUS}));
				}
				if (!tokens.isEmpty()) {
					list2.remove(list2.size()-1);
					tokens.remove(0);
					Token l = tokens.get(tokens.size()-1);
					Token f = list2.get(0);
					List<Token> list3 =  splitAt(tokens, new int[] {Token.RIGHT_BRACKET});
					rules.add(new TRRuleElement(createFormula(list2), createAction(list3), f, l, tokenizer.getSource(f, l)));
				}
			} while (!tokens.isEmpty());
		}
		
//		System.out.println("list: "+ list);
		PredicateFormula pred = createPredicate(list);
//		System.out.println("Pred: " + pred);
		return new FunctionElement(pred,
				rules.toArray(new TRRuleElement[rules.size()]),
				first, last, tokenizer.getSource(first, last));
	}
	
	public IAction createAction(List<Token> tokens) throws ParseException {
		Token first = tokens.get(0);
		Token last = tokens.get(tokens.size() - 1);

		if (first.type == Token.IDENTIFIER) {
			if (tokens.get(1).type == Token.PERIOD) {
				// Primitive Action
				tokens.remove(0);
				tokens.remove(0);
				List<Token> t_list = splitAt(tokens, new int[] {Token.RIGHT_BRACKET});
				last = getLast(t_list);

				return new TRModuleCallAction(first.token, createPredicate(t_list), 
						first, last, tokenizer.getSource(first, last));
			} else {
				return new FunctionCallAction(createPredicate(tokens), first, last,tokenizer.getSource(first,last));
			}
		} else if (first.type == Token.PLUS || first.type == Token.MINUS) {
			tokens.remove(0);
			return new UpdateAction(first.token, createPredicate(tokens),
						first, last, tokenizer.getSource(first, last));
		}
		
		return null;
	}
	
	public PlanElement createPlan(List<Token> tokens) throws ParseException {
		Token first = tokens.get(0);
		Token last = tokens.get(tokens.size() - 1);

		List<Token> list = splitAt(tokens, new int[] {Token.LEFT_BRACE});
		tokens.add(0, list.remove(list.size()-1));
		return new PlanElement(createPredicate(list),
				createStatement(tokens),first, last, tokenizer.getSource(first, last));
	}
	
	public InferenceElement createInference(List<Token> tokens) throws ParseException {
		Token first = tokens.get(0);
		Token last = tokens.get(tokens.size() - 1);

		List<Token> list = splitAt(tokens, new int[] {Token.COLON});
		if (list.remove(list.size()-1).type != Token.COLON || tokens.remove(0).type != Token.MINUS) {
			throw new ParseException("Malformed Inference", first, last);
		}
		
		return new InferenceElement(createPredicate(list), createFormula(tokens),
				first, last, tokenizer.getSource(first, last));
	}

	public List<InitialElement> createInitial(List<Token> tokens) throws ParseException {
		Token first = tokens.get(0);
		Token last = tokens.get(tokens.size() - 1);

		List<InitialElement> initials = new ArrayList<InitialElement>();
		while (!tokens.isEmpty()) {
			List<Token> list = splitAt(tokens, new int[] {Token.COMMA} );
			if (getLast(list).type == Token.COMMA) list.remove(list.size()-1);
			initials.add(new InitialElement(createPredicateOrBelief(list),
				first, last, tokenizer.getSource(first, last)));
		}
		return initials;
	}

	public ModuleElement createModule(List<Token> tokens) throws ParseException {
		Token first = tokens.get(0);
		Token last = tokens.remove(tokens.size() - 1);
		
		return new ModuleElement(getQualifiedName(tokens), last.token,
				first, last, tokenizer.getSource(first, last));
	}

	public IStatement createStatement(List<Token> tokens) throws ParseException {
		Token first = tokens.get(0);
		Token last = tokens.get(tokens.size() - 1);

		// HANDLE Variable Declaration (with Initialization)
		Token tok = tokens.remove(0);
		if (Token.isType(tok.type)) {
			IType type = new BasicType(Token.resolveType(tok.type));
			Token tok2 = tokens.remove(0);
			
			if (tok2.type != Token.IDENTIFIER) {
				throw new ParseException("Expected an identifier, but got: " + tok2.token, tok2);
			}
			
			if (tokens.get(0).type == Token.SEMI_COLON) {
				tokens.remove(0);
				return new DeclarationStatement(type, tok2.token,
						first, last, tokenizer.getSource(first, last));
			}

			List<Token> list = splitAt(tokens, new int[] {Token.SEMI_COLON});
			
			if (list.remove(list.size()-1).type != Token.SEMI_COLON) {
				throw new ParseException("Missing Semi Colon", list.get(0), list.get(list.size()-1));
			}
			Token tok3 = list.remove(0);
			if (tok3.type == Token.ASSIGNMENT) {
				ITerm term = createTerm(list);
				return new DeclarationStatement(type, tok2.token, term, 
						first, last, tokenizer.getSource(first, last));
			}
			throw new ParseException("Expected = or ;, but got: " + tok3.token, tok3);
		}
		
		String token = null;
		switch ( tok.type ) {
		case Token.MAINTAIN:
			List<Token> list = this.splitAt(tokens, new int[]{Token.RECOVER,Token.RIGHT_BRACE});
			if (list.get(list.size()-1).type == Token.RECOVER) {
				list.remove(list.size()-1);
			}
//			System.out.println("list: " + list);
//			System.out.println("tokens: " + tokens);
			Token tok2 = list.remove(0);
			if (tok2.type != Token.LEFT_BRACKET) {
				throw new ParseException("Illegal token: expected ( but got: " + tok2.token, tok2);
			}
			IFormula formula = createFormula(splitAt(list, new int[] {Token.RIGHT_BRACKET}));
			tok2 = list.remove(0);
			if (tok2.type != Token.RIGHT_BRACKET) {
				throw new ParseException("Illegal token: expected ) but got: " + tok2.token, tok2);
			}
			tok = list.remove(0);
			if (tok.type != Token.LEFT_BRACE) {
				throw new ParseException("Illegal token: expected { but got: " + tok.token, tok);
			}

			List<IStatement> statements = new LinkedList<IStatement>();
			if (getLast(list).type != Token.RIGHT_BRACE) {
				throw new ParseException("Illegal token: expected } but got "+getLast(list).token, first, getLast(list));
			}
			
			tok2 = list.get(0);
			while ( tok2.type != Token.RIGHT_BRACE ) {
				statements.add(createStatement(list));
				if (list.isEmpty()) break;
				tok2 = list.get(0);
			}

			List<IStatement> recover = new LinkedList<IStatement>();
			if (!tokens.isEmpty() && tokens.get(0).type == Token.RECOVER) {
				tok = tokens.remove(0);
				if (tok.type != Token.LEFT_BRACE) {
					throw new ParseException("Illegal token: expected { but got: " + tok.token, tok);
				}
	
				if (getLast(tokens).type != Token.RIGHT_BRACE) {
					throw new ParseException("Illegal token: expected } but got "+getLast(tokens).token, tok, getLast(tokens));
				}
				
				tok2 = tokens.get(0);
				while ( tok2.type != Token.RIGHT_BRACE ) {
					recover.add(createStatement(tokens));
					if (tokens.isEmpty()) break;
					tok2 = tokens.get(0);
				}
			}			
			return new MaintainBlockStatement(formula, statements, recover, first, last, tokenizer.getSource(first, last));
		case Token.SYNCHRONIZED:
			tok2 = tokens.remove(0);
			if (tok2.type != Token.LEFT_BRACKET) {
				throw new ParseException("Illegal token: expected ( but got: " + tok2.token, tok2);
			}
			token = tokens.remove(0).token;
			tok2 = tokens.remove(0);
			if (tok2.type != Token.RIGHT_BRACKET) {
				throw new ParseException("Illegal token: expected ) but got: " + tok2.token, tok2);
			}
			tok = tokens.remove(0);
		case Token.LEFT_BRACE:
			// THERE IS AN ERROR IN THIS PART OF THE CODEBASE...
			// we have a block...
			statements = new LinkedList<IStatement>();
			tokens.add(0, tok);
			List<Token> t_list = splitAt(tokens, new int[] {Token.RIGHT_BRACE});
			if (getLast(t_list).type != Token.RIGHT_BRACE) {
				throw new ParseException("Missing closing braces", first, last);
			}
			
			t_list.remove(0);
			tok2 = t_list.get(0);
			while ( tok2.type != Token.RIGHT_BRACE ) {
				statements.add(createStatement(t_list));
				if (t_list.isEmpty()) break;
				tok2 = t_list.get(0);
			}
			
			if (token != null) {
				return new SynchronizedBlockStatement(token, statements, first, last, tokenizer.getSource(first, last));
			}
			return new BlockStatement(statements, first, last, tokenizer.getSource(first, last));
		case Token.SEND:
			list = splitAt(tokens, new int[] {Token.SEMI_COLON});
			semiColonCheck(list);
			
			last = getLast(list);
			tok2 = list.remove(0);
			if (tok2.type != Token.LEFT_BRACKET) {
				throw new ParseException("Malformed Statement: send(<performative>, <receiver>, <formula>)", first, last);
			}
			if (last.type != Token.RIGHT_BRACKET) {
				throw new ParseException("Malformed Statement: send(<performative>, <receiver>, <formula>)", first, last);
			}
				
			List<ITerm> terms = getTermList(list.subList(0, list.size()-1), false);
			if (terms.size() < 3 || terms.size() > 4) {
				throw new ParseException("Malformed Statement: send(<performative>, <receiver>, <formula> [,<params>])", first, last);
			}
			
			IFormula content = this.convertToPredicate(terms.get(2));
			ITerm params = null;
			if (terms.size() == 4) params = terms.get(3);
			list.remove(list.size()-1);
			return new SendStatement(terms.get(0), terms.get(1), content, params, 
					first, last, tokenizer.getSource(first, last));
		case Token.QUERY:
			tok2 = tokens.get(0);
			if (tok2.type != Token.LEFT_BRACKET) {
				throw new ParseException("Malformed Statement: query(<formula>)", first, last);
			}
			list = splitAt(tokens, new int[] {Token.SEMI_COLON});
			semiColonCheck(list);
			
			last = getLast(list);
			if (last.type != Token.RIGHT_BRACKET) {
				throw new ParseException("Malformed Statement: query(<formula>)", first, last);
			}
			list.remove(0);
			list.remove(list.size()-1);
			return new QueryStatement(createFormula(list), 
					first, last, tokenizer.getSource(first, last));
		case Token.IF:
			tok2 = tokens.get(0);
			if (tok2.type != Token.LEFT_BRACKET) {
				throw new ParseException("Malformed Statement: if(<formula>) <statement> [else <statement>]", first, last);
			}
			
			list = splitAt(tokens, new int[] {Token.RIGHT_BRACKET});
			if (list.size() < 2) {
				throw new ParseException("Malformed Statement - NO GUARD: if(<formula>) <statement> [else <statement>]", first, last);
			}
			
			IFormula guard = createFormula(list.subList(1, list.size()-1));
			
			if (tokens.get(0).type == Token.LEFT_BRACE) {
				list = splitAt(tokens, new int[] {Token.RIGHT_BRACE});
			} else {
				list = tokens;
//				list = splitAt(tokens, new int[] {Token.SEMI_COLON});
			}
			IStatement statement = createStatement(list);

			if (!tokens.isEmpty() && tokens.get(0).type == Token.ELSE) {
				tokens.remove(0);
				
				return new IfStatement(guard, statement, createStatement(tokens),
						first, last, tokenizer.getSource(first, last));
			} else {
				return new IfStatement(guard, statement, 
						first, last, tokenizer.getSource(first, last));
			}
		case Token.WHILE:
			tok2 = tokens.get(0);
			if (tok2.type != Token.LEFT_BRACKET) {
				throw new ParseException("Malformed Statement: while(<formula>) <statement>", first, last);
			}
			
			list = splitAt(tokens, new int[] {Token.RIGHT_BRACKET});
			guard = createFormula(list.subList(1, list.size()-1));
			
			list = splitAt(tokens, new int[] {Token.RIGHT_BRACE, Token.SEMI_COLON});
			return new WhileStatement(guard, createStatement(list), 
					first, last, tokenizer.getSource(first, last));

		case Token.WAIT:
			list = splitAt(tokens, new int[] {Token.SEMI_COLON});
			if (getLast(list).type == Token.SEMI_COLON) list.remove(list.size()-1);
			if (list.get(0).type != Token.LEFT_BRACKET) {
				throw new ParseException("Malformed Statement: wait(<formula>)", first, last);
			}
			if (getLast(list).type != Token.RIGHT_BRACKET) {
				throw new ParseException("Malformed Statement: wait(<formula>)", first, last);
			}
			
			return new WaitStatement(createFormula(list.subList(1, list.size()-1)),
					first, last, tokenizer.getSource(first, last));
		case Token.FOREACH:
			tok2 = tokens.get(0);
			if (tok2.type != Token.LEFT_BRACKET) {
				throw new ParseException("Malformed Statement: when(<formula>) <statement>", first, last);
			}
			
			list = splitAt(tokens, new int[] {Token.RIGHT_BRACKET});
			guard = createFormula(list.subList(1, list.size()-1));
			
			list = splitAt(tokens, new int[] {Token.RIGHT_BRACE, Token.SEMI_COLON});
			return new ForEachStatement(guard, createStatement(list), 
					first, last, tokenizer.getSource(first, last));
		case Token.FORALL:
			t_list = splitAt(tokens, new int[] {Token.RIGHT_BRACKET});
			last = getLast(t_list);
			if (t_list.remove(0).type != Token.LEFT_BRACKET)
				throw new ParseException("Malformed Statement: missing left bracket\n\tExpected Syntax: forall(<term> : <formula>))", first, last);
			if (getLast(t_list).type != Token.RIGHT_BRACKET)
				throw new ParseException("Malformed Statement: missing right bracket\n\tExpected Syntax: forall(<term> : <formula>))", first, last);
			
			list = splitAt(t_list, new int[] {Token.COLON});
			if (getLast(list).type != Token.COLON)
				throw new ParseException("Malformed Statement: missing colon\n\tExpected Syntax: forall(<term> : <formula>))", first, last);
			list.remove(list.size()-1);
			ITerm term = createTerm(list);
			if (term instanceof InlineVariableDeclaration) {
				ITerm listTerm = createTerm(t_list.subList(0, t_list.size()-1));
				if (listTerm instanceof ListTerm || (listTerm instanceof Variable)) {
					return new ForAllStatement(term, listTerm, createStatement(tokens), 
							first, last, tokenizer.getSource(first, last));
				}
				throw new ParseException("Malformed Statement: there should be a list after the colon\n\tExpected Syntax: forall(<term> : <formula>))", first, last);
			}
			throw new ParseException("Malformed Statement: there should be a variable before the colon\n\tExpected Syntax: forall(<term> : <formula>))", first, last);
		case Token.TR_START:
			t_list = splitAt(tokens, new int[] {Token.SEMI_COLON});
			last = getLast(t_list);
			if (last.type != Token.SEMI_COLON) throw new ParseException("Missing Semi-colon", first, last);
			t_list.remove(t_list.size()-1);
			
			if (t_list.remove(0).type != Token.LEFT_BRACKET) {
				throw new ParseException("Malformed Statement: missing left bracket\n\t" + tok.token + "(<formula>)", first, last);
			}
			if (t_list.remove(t_list.size()-1).type != Token.RIGHT_BRACKET) {
				throw new ParseException("Malformed Statement: missing right bracket\n\t" + tok.token + "wait(<formula>)", first, last);
			}
			return new TRStatement(tok.token, createPredicateOrVariableFormula(t_list), first, last, tokenizer.getSource(first, last));
		case Token.TR_STOP:
			t_list = splitAt(tokens, new int[] {Token.SEMI_COLON});
			last = getLast(t_list);
			if (last.type != Token.SEMI_COLON) throw new ParseException("Missing Semi-colon", first, last);
			t_list.remove(t_list.size()-1);
			if (t_list.size() != 2) {
				throw new ParseException("Malformed Statement: stop() should contain no parameters", first, last);
			}
			
			if (t_list.remove(0).type != Token.LEFT_BRACKET) {
				throw new ParseException("Malformed Statement: missing left bracket\n\t" + tok.token + "(<formula>)", first, last);
			}
			if (t_list.remove(t_list.size()-1).type != Token.RIGHT_BRACKET) {
				throw new ParseException("Malformed Statement: missing right bracket\n\t" + tok.token + "wait(<formula>)", first, last);
			}
			return new TRStatement(tok.token, null, first, last, tokenizer.getSource(first, last));
		case Token.TRY:
			list = splitAt(tokens, new int[] {Token.RECOVER});
			if (list.get(list.size()-1).type != Token.RECOVER) {
				throw new ParseException("Malformed Statement: try <statement> recover <statement>", first, last);
			}
			return new TryRecoverStatement(createStatement(list.subList(0, list.size()-1)),
					createStatement(tokens), first, last, tokenizer.getSource(first, last));
		case Token.BANG:
			list = splitAt(tokens, new int[] {Token.SEMI_COLON});
			semiColonCheck(list);

			if (list.get(0).type == Token.BANG) {
				return new SpawnGoalStatement(createGoal(list),
						first, last, tokenizer.getSource(first, last));
			} else {
				list.add(0, tok);
				return new SubGoalStatement(createGoal(list),
						first, last, tokenizer.getSource(first, last));
			}
		case Token.PLUS:
		case Token.MINUS:
			list = splitAt(tokens, new int[] {Token.SEMI_COLON});
			semiColonCheck(list);
			if (tok.type == Token.MINUS && list.get(0).type == Token.PLUS) {
				list.remove(0);
				PredicateFormula predicate = createPredicate(list);
				// special case of -+update statement
				return new UpdateStatement("-+", predicate,
						first, last, tokenizer.getSource(first, last));
			} else {
				PredicateFormula predicate = createPredicate(list);
				return new UpdateStatement(tok.token, predicate,
						first, last, tokenizer.getSource(first, last));
			}
		case Token.IDENTIFIER:
			// need to refine this further...
			// now consider scoped operator + plan module call...
			List<Token> m_list = splitAt(tokens, new int[] {Token.SEMI_COLON});
//			This is pointless: semiColonCheck(m_list);
			
			list = splitAt(m_list, new int[] {Token.COLON});
			if (list.get(list.size()-1).type == Token.COLON && m_list.get(0).type == Token.COLON) {
				m_list.remove(0);
				list.remove(list.size()-1);
				list.add(0, tok);
				
				// Re-insert the semi-colon for consistency
				return new ScopedStatement(getQualifiedName(list),
						createStatement(m_list), first, last, tokenizer.getSource(first, last));
			}
	
			String qualifiedName = tok.token;
			Token l = null;
			Token t = tok2 = list.remove(0);
//			System.out.println("t='" + t.token+"'");
			while (list.size() > 1 && (t.type == Token.PERIOD)) {
				l = list.remove(0);
				t = list.remove(0);
				qualifiedName += "."+l.token;
			}
//			System.out.println("QName: " + qualifiedName);
			
			if (t.type != Token.IDENTIFIER) {
//				System.out.println("Not a qualified name!");
				if (l != null) {
					list.add(0, t);
					list.add(0, l);
				}
//				list.add(0, t);
//				if (l != null) list.add(0, l);
//				System.out.println("list: "+list);
			} else {
				// We have a Java class declaration...
				IType type = new ObjectType(Token.OBJECT_TYPE, qualifiedName);

				if (list.remove(list.size()-1).type != Token.SEMI_COLON) {
					throw new ParseException("Missing Semi Colon", list.get(0), list.get(list.size()-1));
				}
				if (list.isEmpty()) {
					// we have a declaration (minus assignment)
					return new DeclarationStatement(type, t.token, 
							first, last, tokenizer.getSource(first, last));
				}
				
				// Check if it is an assignment...
				Token tok3 = list.remove(0);
				if (tok3.type == Token.ASSIGNMENT) {
					return new DeclarationStatement(type, t.token, createTerm(list), 
							first, last, tokenizer.getSource(first, last));
				} else {
					throw new ParseException("Incomplete Statement", first, last);
				}
			}

			
			switch (tok2.type) {
			case Token.PERIOD:
				//its either <module_id>.<action> or <type> <id> =
				
				if (list.isEmpty())
					throw new ParseException("Incomplete Statement", first, last);
				
				// need to iterate through the tokens until the PERIOD <Id> pattern stops. If the
				// pattern is followed by a ( we have a predicate; if we have and <Id> it is an
				// assignment otherwise it is a syntax error...
				
				// Code below assumes predicate...
				t_list = splitAt(list, new int[] {Token.RIGHT_BRACKET});
				if (list.get(0).type != Token.SEMI_COLON) {
					throw new ParseException("Syntax Error: Missing semi-colon.", t_list.get(0), t_list.get(t_list.size()-1));
				}
				last = getLast(t_list);
//				System.out.println("tokens: " + tokens);
//				System.out.println("list: " + list);
				if (tokens.get(0).type == Token.SEMI_COLON) tokens.remove(0);
//				System.out.println("t_list: " + t_list);
				PredicateFormula predicate = createPredicate(t_list);
//				System.out.println("t_list: " + t_list);
				if (!t_list.isEmpty()) throw new ParseException("Unexpected end of statement", t_list.get(0), t_list.get(t_list.size()-1));
				// we have a module call
				return new ModuleCallStatement(tok.token, predicate, 
						first, last, tokenizer.getSource(first, last));
			case Token.PLUS:
//				System.out.println("list: " + list);
				if (list.get(0).type == Token.PLUS) {
					return new PlusPlusStatement(tok.token, first, list.get(0), tokenizer.getSource(first, list.get(0)));
				}
			case Token.MINUS:
				if (list.get(0).type == Token.MINUS) {
					return new MinusMinusStatement(tok.token, first, list.get(0), tokenizer.getSource(first, list.get(0)));
				}
			case Token.ASSIGNMENT:
				semiColonCheck(list);
				return new AssignmentStatement(tok.token, createTerm(list),
						first, last, tokenizer.getSource(first, last));
			default:
				// its just a predicate, so push the two tokens back onto the
				// tokenizer and process the predicate
				list.add(0, tok2);
				list.add(0, tok);
				last = getLast(list);
				return new PlanCallStatement(createPredicate(list), 
						first, last, tokenizer.getSource(first, last));
			}
		default:
			throw new ParseException("Malformed Statement: did not expect: " + tok.token, first, last);
		}
	}

	private IFormula convertToPredicate(ITerm term) throws ParseException {
		if (term instanceof Function) {
			Function function = (Function) term;
			return new PredicateFormula(function.functor(), function.terms(), function.start, function.end, function.getSource());
		} else if (term instanceof ModuleTerm) {
			ModuleTerm mterm = (ModuleTerm) term;
			return new ModuleFormula(mterm.module(), mterm.method(), mterm.start, mterm.end, mterm.getSource());  
		} else if (term instanceof InlineVariableDeclaration) {
			InlineVariableDeclaration var = (InlineVariableDeclaration) term;
			return new FormulaVariable(var.identifier(),var.start, var.end, var.getSource());
		} else if (term instanceof Variable) {
			Variable var = (Variable) term;
			return new FormulaVariable(var.identifier(),var.start, var.end, var.getSource());
		}
		throw new ParseException("Unable to convert term to predicate: " + term.getClass().getCanonicalName(), term);
	}

	private void semiColonCheck(List<Token> list) throws ParseException {
		Token token;
		if ((token = list.remove(list.size()-1)).type != Token.SEMI_COLON) {
			throw new ParseException("Missing semi-colon", list.get(0), token);
		}
	}

	public IFormula createPredicateOrVariableFormula(List<Token> tokens) throws ParseException {
		Token first = tokens.get(0);
		Token last = getLast(tokens);

		if (first.type == Token.FORMULA) {
			tokens.remove(0);
			return new FormulaVariable(tokens.remove(0).token, 
					first, last, tokenizer.getSource(first, last));
		} else {
			return createPredicate(tokens);
		}
	}
	
	public IEvent createEvent(List<Token> tokens) throws ParseException {
		Token first = tokens.get(0);
		Token last = tokens.get(tokens.size() - 1);

		Token tok = tokens.remove(0);
		if (tokens.get(0).type == Token.DOLLAR) {
			// We have a symbol prefixing an event token....
			// first refers to this prefix, so update tok to refer to
			// the dollar token so the switch goes to the correct section...
			tok = tokens.remove(0);
		}
		
		switch ( tok.type ) {
		case Token.PLUS:
		case Token.MINUS:
			return new UpdateEvent(tok.token, createPredicateOrBelief(tokens), 
					first, last, tokenizer.getSource(first, last));
		case Token.MESSAGE:
			Token tok2 = tokens.get(0);
			if (tok2.type != Token.LEFT_BRACKET) {
				throw new ParseException("Malformed Message Event: @message(<performative>, <sender>, <formula>)", first, last);
			}
			if (last.type != Token.RIGHT_BRACKET) {
				throw new ParseException("Malformed Message Event: @message(<performative>, <sender>, <formula>)", first, last);
			}

			tokens.remove(0);
			List<ITerm> terms = getTermList(tokens.subList(0, tokens.size()-1), false);

			if (terms.size() < 3 || terms.size() > 4) {
				throw new ParseException("Malformed Message Event: @message(<performative>, <sender>, <formula>)", first, last);
			}

			IFormula content = this.convertToPredicate(terms.get(2));

			ITerm params = null;
			if (terms.size() == 4) params = terms.get(3);
			return new MessageEvent(terms.get(0), terms.get(1), content, params, 
					first, last, tokenizer.getSource(first, last));		
		case Token.DOLLAR:
			tok2 = tokens.remove(0);
			if (tokens.remove(0).type != Token.PERIOD) {
				throw new ParseException("Invalid Module Event format expected: [<symbol>]$<module>.<predicate>", first, last);
			}
			
			PredicateFormula evt = this.createPredicate(tokens);
			return new ModuleEvent(first.equals(tok) ? null:first.token,tok2.token, evt, tok, last, tokenizer.getSource(first, last));
		}
		throw new ParseException("Unexpected Event: " + first.token, first, last);
	}
	
	private IFormula createPredicateOrBelief(List<Token> tokens) throws ParseException {
		if (tokens.get(0).type == Token.BANG) {
			return createGoal(tokens);
		}
		return createPredicate(tokens);
	}

	public IFormula createFormula(List<Token> tokens) throws ParseException {
		Token first = tokens.get(0);
		Token last = tokens.get(tokens.size() - 1);

		for (int type : BOOLEAN_OPERATOR_PRECEDENCE) {
			int i = 0;
			Stack<Token> bracketStack = new Stack<Token>();
			while (i < tokens.size()) {
				Token token = tokens.get(i);
				if (BRACKET_PAIRINGS.containsKey(token.type)) {
					bracketStack.push(token);
				} else if (BRACKET_PAIRINGS.containsValue(token.type)) {
					if (bracketStack.isEmpty()) {
						throw new ParseException("Too many brackets", token, token);
					}
					Token t = bracketStack.pop();
					if (BRACKET_PAIRINGS.get(t.type) != token.type)
						throw new ParseException("Mismatched Brackets", first, last);  
				} else if (token.type == type && bracketStack.isEmpty()) {
					if (i == 0 || i == tokens.size()-1) throw new ParseException("Unexpected Operator: " + tokens.get(i).token, token, token);
					
					IFormula left = createFormula(new ArrayList<Token>(tokens.subList(0, i)));
					IFormula  right = createFormula(new ArrayList<Token>(tokens.subList(i+1, tokens.size())));
					if (type == Token.AND) {
						return new AndFormula(left, right, first, last, tokenizer.getSource(first, last));
					} else if (type == Token.OR) {
						return new OrFormula(left, right, tokens.get(0), token, tokenizer.getSource(tokens.get(0), token));
					}
				}
				i++;
			}
		}

		// AND / OR discounted...
		for (int[] types : COMPARISON_OPERATOR_PRECEDENCE) {
			int i = 0;
			Stack<Token> bracketStack = new Stack<Token>();
			while (i < tokens.size()) {
				String op = "";
				Token token = tokens.get(i);
				if (BRACKET_PAIRINGS.containsKey(token.type)) {
					bracketStack.push(token);
				} else if (BRACKET_PAIRINGS.containsValue(token.type)) {
					if (bracketStack.isEmpty()) {
						throw new ParseException("Too many brackets", token, token);
					}
					Token t = bracketStack.pop();
					if (BRACKET_PAIRINGS.get(t.type) != token.type)
						throw new ParseException("Mismatched Brackets", first, last);  
				} else if (token.type == types[0] && tokens.size() > i + types.length && bracketStack.isEmpty()) {
					boolean match = true;
					int j = 1;
					op += token.token;
					while (match && j < types.length) {
						match = types[j] == tokens.get(i+j).type; 
						op += tokens.get(i+j).token;
						j++;
					}
					if (match) {
						ITerm left = createTerm(new ArrayList<Token>(tokens.subList(0, i)));
						ITerm right = createTerm(new ArrayList<Token>(tokens.subList(i+j, tokens.size())));
						return new ComparisonFormula(op, left, right, tokens.get(0), getLast(tokens), tokenizer.getSource(tokens.get(0), getLast(tokens)));
					}
				}
				i++;
			}
		}

		Token tok = tokens.remove(0);
		if (tok.type == Token.BANG) {
			tokens.add(0, tok);
			return createGoal(tokens);
		} else if (tok.type == Token.BIND) {
			Token tok2 = tokens.remove(0);
			if (tok2.type == Token.LEFT_BRACKET) {
				if (getLast(tokens).type != Token.RIGHT_BRACKET)
					throw new ParseException("Unexpected Tokens", tok2, getLast(tokens));
				tokens.remove(tokens.size()-1);
				List<ITerm> list = getTermList(tokens, false);
				if (list.size() > 2) throw new ParseException("Malformed bind formula: bind(<variable>,<value>)", first, last);
				if (!Variable.class.isInstance(list.get(0))) 
					throw new ParseException("First argument of bind should be a variable", first, last);
				return new BindFormula((Variable) list.get(0), list.get(1), first, last, tokenizer.getSource(first, last));
			}
		} else if (tok.type == Token.LEFT_BRACKET) {
			if (last.type != Token.RIGHT_BRACKET) {
				throw new ParseException("Malformed brackets", first, last);
			}
			tokens.remove(tokens.size()-1);
			return new BracketFormula(createFormula(tokens), first, last, tokenizer.getSource(first, last));
		} else if (tok.type == Token.NOT) {
			return new NOTFormula(createFormula(tokens), 
					first, last, tokenizer.getSource(first, last));
		} else if (tok.type == Token.BOOLEAN) {
			return new PredicateFormula(tok.token, new ArrayList<ITerm>(), 
					first, last, tokenizer.getSource(first, last));
		} else if (tok.type == Token.FORMULA) {
			return new FormulaVariable(tokens.remove(0).token, 
					first, last, tokenizer.getSource(first, last));
		} else if (tok.type == Token.IDENTIFIER) {
			if (tokens.size() == 0) {
				throw new ParseException("Expected Formula, but got: " + tok.token, first, last);
			}
			Token tok2 = tokens.get(0);
			if (tok2.type == Token.PERIOD) {
				return new ModuleFormula(tok.token, createPredicate(tokens.subList(1, tokens.size())), 
						first, last, tokenizer.getSource(first, last));
			}
			
			// Akshot: This is where the code for handling the event scope operator was put...
			tokens.add(0, tok);
			String name = getQualifiedName(tokens);
			tok2 = tokens.get(0);
			if (tok2.type == Token.COLON) {
				tok2 = tokens.get(1);
				if (tok2.type != Token.COLON) {
					throw new ParseException("Malformed Scope Operator.", first, last);
				}
				
				IFormula formula = createFormula(tokens.subList(2, tokens.size()));
				if (formula instanceof GoalFormula) {
					return new ScopedGoalFormula(name, (GoalFormula) formula,
							first, last, tokenizer.getSource(first, last));
				} else {
					throw new ParseException("Malformed Scope Operator.", first, last);
				}
			}
		}
		tokens.add(0, tok);

		return createPredicate(tokens);
	}

	private GoalFormula createGoal(List<Token> tokens) throws ParseException {
		Token first = tokens.get(0);
		Token last = tokens.get(tokens.size() - 1);
		tokens.remove(0);
		
		return new GoalFormula(createPredicate(tokens), 
				first, last, tokenizer.getSource(first, last));
	}

	private List<Token> splitAt(List<Token> tokens, int[] types) throws ParseException {
		Token first = tokens.get(0);
		Token last = tokens.get(tokens.size() - 1);

		List<Token> list = new ArrayList<Token>();
		Stack<Token> bracketStack = new Stack<Token>();
		while (!tokens.isEmpty()) {
			// Problem here - if a type is a bracket type...
			Token token = tokens.remove(0);
			if (BRACKET_PAIRINGS.containsKey(token.type)) {
				bracketStack.push(token);
			} else if (BRACKET_PAIRINGS.containsValue(token.type)) {
				// If we have a close bracket and the bracketStack is
				// empty, then we have a problem...
				if (bracketStack.isEmpty()) {
//					throw new ParseException("Unexpected Bracket: " + token.token, first, last);
					return list;
				}
				
				// Check if the closing bracket type matches the opening bracket type on top
				// of the bracket stack - if not, we have a problem
				Token t = bracketStack.pop();
				if (BRACKET_PAIRINGS.get(t.type) != token.type)
					throw new ParseException("Mismatched Brackets", first, last);
			}
			
			for (int t : types) {
				// Special case for matching an opening bracket...
				if (BRACKET_PAIRINGS.containsKey(t)) {
					if (token.type == t && bracketStack.size() == 1) {
						list.add(token);
						return list;
					}
				} else {
					// General matching case
					if (token.type == t && bracketStack.isEmpty()) {
						list.add(token);
						return list;
					}
				}
			}
			list.add(token);
		}
		return list;
	}

	public PredicateFormula createPredicate(List<Token> tokens) throws ParseException {
//		System.out.println("tokens: " + tokens);
		Token first = tokens.get(0);
		Token last = tokens.get(tokens.size()-1);
		
		Token tok = tokens.remove(0);
		String predicate = tok.token;
		
		if (tokens.isEmpty()) {
			throw new ParseException("Invalid Predicate Formula", first, last);
		}
		if (tokens.remove(0).type != Token.LEFT_BRACKET) {
			throw new ParseException("Missing Left Bracket", first, last);
		}
		if (tokens.remove(tokens.size()-1).type != Token.RIGHT_BRACKET) {
			throw new ParseException("Missing Right Bracket", first, last);
		}
		return new PredicateFormula(predicate, getTermList(tokens, false), 
				first, last, tokenizer.getSource(first, last));
	}
	
	public ITerm createTerm(List<Token> tokens) throws ParseException {
//		System.out.println("createTerm: " + tokens);
		Token first = tokens.get(0);
		Token last = tokens.get(tokens.size()-1);

		if (first.type == Token.MINUS && tokens.size() == 2) {
			//Special Case: negative literal
			return new Literal("-"+last.token, new BasicType(last.type), first, last, tokenizer.getSource(first, last));
		}
		List<Token> list = tokens;
		for (int type : OPERATOR_PRECEDENCE) {
			List<Token> list2 = splitAt(list, new int[] {type});
			while (!list.isEmpty()) {
				Token token = list2.remove(list2.size()-1);
				if (token.type == type) {
					ITerm left = createTerm(new ArrayList<Token>(list2));
					ITerm right = createTerm(new ArrayList<Token>(list));
					return new Operator(token.token, left, right, token, last, tokenizer.getSource(token, last));
				} else {
					list2.add(token);
					list2.addAll(splitAt(list, new int[] {type}));
				}
				
			}
			list = list2;
		}

		tokens = list;
		Token tok = tokens.remove(0);
		if (tokens.isEmpty()) {
			if (Token.isLiteral(tok.type)) {
				return new Literal(tok.token, new BasicType(tok.type), tok, tok, tokenizer.getSource(tok, tok));
			} else if (tok.type == Token.IDENTIFIER) {
				return new Variable(tok.token, tok, tok, tokenizer.getSource(tok, tok));
			} else {
				throw new ParseException("Unexpected token in term list: " + tok.token, tok, tok);
			}
		} else {
			if (Token.isType(tok.type)) {
				IType type = new BasicType(Token.resolveType(tok.type));
				if (tokens.isEmpty()) throw new ParseException("Expected variable identifier", tok, tok);
				Token tok2 = tokens.remove(0);
				if (tok2.type != Token.IDENTIFIER) {
					throw new ParseException("Expected variable identifier", tok, tok2);
				}
				if (!tokens.isEmpty()) throw new ParseException("Unexpected Tokens after inline variable declaration" , getLast(tokens));
				return new InlineVariableDeclaration(type, tok2.token, tok, tok2, tokenizer.getSource(tok, tok2));
			} else if (tok.type == Token.COUNT) {
				Token tok2 = tokens.remove(0);
				if (tok2.type == Token.LEFT_BRACKET) {
					if (getLast(tokens).type != Token.RIGHT_BRACKET)
						throw new ParseException("Unexpected Tokens", tok2, getLast(tokens));
					tokens.remove(tokens.size()-1);
					List<ITerm> terms = this.getTermList(tokens, false);
					if (terms.size() != 1) throw new ParseException("Expected: count(<variable>|<list>)", first, last);
					return new CountTerm(terms.get(0), first, last, tokenizer.getSource(first, last));
				}
			} else if (tok.type == Token.HEAD) {
				Token tok2 = tokens.remove(0);
				if (tok2.type == Token.LEFT_BRACKET) {
					if (getLast(tokens).type != Token.RIGHT_BRACKET)
						throw new ParseException("Unexpected Tokens", tok2, getLast(tokens));
					tokens.remove(tokens.size()-1);
					List<List<Token>> terms = this.getTermParts(tokens, false);
					
//					System.out.println("terms: " + terms);
					if (terms.size() != 2) throw new ParseException("Expected: head(<list>, <type>)", first, last);
					if (terms.get(1).size() != 1) throw new ParseException("Expected: head(<list>, <type>)", first, last);
					return new HeadTerm(createTerm(terms.get(0)), new BasicType(Token.resolveType(terms.get(1).get(0).type)), first, last, tokenizer.getSource(first, last));
				}
			} else if (tok.type == Token.TAIL) {
				Token tok2 = tokens.remove(0);
				if (tok2.type == Token.LEFT_BRACKET) {
					if (getLast(tokens).type != Token.RIGHT_BRACKET)
						throw new ParseException("Unexpected Tokens", tok2, getLast(tokens));
					tokens.remove(tokens.size()-1);
					List<List<Token>> terms = this.getTermParts(tokens, false);
					
//					System.out.println("terms: " + terms);
					if (terms.size() != 1) throw new ParseException("Expected: tail(<list>)", first, last);
					return new TailTerm(createTerm(terms.get(0)), first, last, tokenizer.getSource(first, last));
				}
			} else if (tok.type == Token.AT_INDEX) {
				Token tok2 = tokens.remove(0);
				if (tok2.type == Token.LEFT_BRACKET) {
					if (getLast(tokens).type != Token.RIGHT_BRACKET)
						throw new ParseException("Unexpected Tokens", tok2, getLast(tokens));
					tokens.remove(tokens.size()-1);
					List<List<Token>> terms = this.getTermParts(tokens, false);
					if (terms.size() != 3) throw new ParseException("Expected: at_index(<list>, <index>, <type>)", first, last);
					return new AtIndexTerm(
							createTerm(terms.get(0)), 
							createTerm(terms.get(1)),
							new BasicType(Token.resolveType(terms.get(2).get(0).type)), 
							first, last, tokenizer.getSource(first, last));
				}
			} else if (tok.type == Token.LEFT_BRACKET) {
				if (tokens.get(tokens.size()-1).type != Token.RIGHT_BRACKET) {
					throw new ParseException("Bracket mismatch for term", tok, tokens.get(tokens.size()-1));
				}
				tokens.remove(tokens.size()-1);
				return new Brackets(createTerm(tokens), first, last, tokenizer.getSource(first, last));
			} else if (tok.type == Token.QUERY) {
				Token tok2 = tokens.remove(0);
				if (tok2.type != Token.LEFT_BRACKET) {
					throw new ParseException("Invalid syntax: expected query( <formula> )", tok, getLast(tokens));
				}
				if (getLast(tokens).type != Token.RIGHT_BRACKET) {
					throw new ParseException("Invalid syntax: expected query( <formula> )", tok, getLast(tokens));
				}

				tokens.remove(tokens.size()-1);
				IFormula query = createFormula(tokens);
				return new QueryTerm(query, tok, tok2, tokenizer.getSource(tok, tok2));				
			} else if (tok.type == Token.IDENTIFIER) {
				Token tok2 = tokens.get(0);
				if (tok2.type == Token.PERIOD) {
					if ((tokens.size() > 2) && (tokens.get(2).type == Token.LEFT_BRACKET)) {
						tokens.remove(0);
						PredicateFormula formula = createPredicate(tokens);
						return new ModuleTerm(tok.token, formula, tok, tokenizer.getLastToken(), tokenizer.getSource(tok, tok));
					} else {
						tokens.add(0, tok);
						IType type = new ObjectType(Token.OBJECT_TYPE, getQualifiedName(tokens));
						if (tokens.isEmpty()) throw new ParseException("Expected variable identifier", tok, tok);
						tok2 = tokens.remove(0);
						if (tok2.type != Token.IDENTIFIER) {
							throw new ParseException("Expected variable identifier", tok, tok2);
						}
						if (!tokens.isEmpty()) throw new ParseException("Unexpected Tokens after inline variable declaration" , getLast(tokens));
						return new InlineVariableDeclaration(type, tok2.token, tok, tok2, tokenizer.getSource(tok, tok2));
						
					}
				} else if (tok2.type == Token.LEFT_BRACKET) {
					if (getLast(tokens).type != Token.RIGHT_BRACKET) {
						throw new ParseException("Invalid syntax: missing right bracket for functional term", tok, getLast(tokens));
					}
					// strip outer brackets...
					tokens.remove(0);
					tokens.remove(tokens.size()-1);
					return new Function(tok.token, getTermList(tokens, false), tok, last, tokenizer.getSource(tok, last));
				} else {
					IType type = new ObjectType(Token.OBJECT_TYPE, tok.token);
					if (tokens.isEmpty()) throw new ParseException("Expected variable identifier", tok, tok);
					tok2 = tokens.remove(0);
					if (tok2.type != Token.IDENTIFIER) {
						throw new ParseException("Expected variable identifier", tok, tok2);
					}
					if (!tokens.isEmpty()) throw new ParseException("Unexpected Tokens after inline variable declaration" , getLast(tokens));
					return new InlineVariableDeclaration(type, tok2.token, tok, tok2, tokenizer.getSource(tok, tok2));
				}
			} else if (tok.type == Token.LEFT_SQ_BRACKET) {
				if (getLast(tokens).type != Token.RIGHT_SQ_BRACKET) {
					throw new ParseException("Malformed ASTRA list", tok, getLast(tokens));
				}
				tokens.remove(tokens.size()-1);
//				System.out.println("{A} tokens: " + tokens);
				int sIndex = getSplitterIndex(tokens);
				if (sIndex > -1) {
//				if (tokens.size() > 2 && tokens.get(2).type == Token.OR) {
//					System.out.println("left: " + tokens.subList(0, sIndex));
//					System.out.println("right: " + tokens.subList(sIndex+1, tokens.size()));
					return createListSplitter(new LinkedList<Token>(tokens.subList(0, sIndex)), new LinkedList<Token>(tokens.subList(sIndex+1, tokens.size())), first, last, tokenizer.getSource(first, last));
				}
				return new ListTerm(getTermList(tokens, false), tok, last, tokenizer.getSource(tok, last));
			} else if (tok.type == Token.MINUS) {
				Token tok2 = tokens.remove(0);
				if (Token.isLiteral(tok2.type)) {
					return new Literal(tok.token+tok2.token, new BasicType(tok2.type), first, last, tokenizer.getSource(first, last));
				}
				throw new ParseException("Malformed literal: " + tok.token+tok2.token, tok, tok2);
			} else if (tok.type == Token.RETURNS) {
				ITerm term = createTerm(tokens);
				if (term instanceof InlineVariableDeclaration) {
					((InlineVariableDeclaration) term).returns(true);
				} else {
					throw new ParseException("Attempt to return a non variable value.", tok, tok);
				}
				return term;
			}
		}
		System.out.println("tok: " + tok.token);
		System.out.println("tokens: " + tokens);
		throw new ParseException("Unknown Term", first, last);
	}
	
	private int getSplitterIndex(List<Token> tokens) {
		int i = 0;
		int brackets = 0;
		while (i < tokens.size()) {
			switch (tokens.get(i).type) {
			case Token.LEFT_SQ_BRACKET: 
				brackets++;
				break;
			case Token.RIGHT_SQ_BRACKET: 
				brackets--;
				break;
			case Token.OR:
				if (brackets == 0) return i;
			}
			i++;
		}
		return -1;
	}

	private ITerm createListSplitter(List<Token> subList, List<Token> subList2, Token first, Token last, String source) throws ParseException {
		ITerm head = createTerm(subList);
		if (head instanceof InlineVariableDeclaration  || head instanceof Function) {
			ITerm tail = createTerm(subList2);
			if (tail instanceof InlineVariableDeclaration && tail.type().type()==Token.LIST) {
				return new ListSplitterTerm(head, tail, first, last, source);
			}
		}
		throw new ParseException("List Splitter should contain new variables.", first, last);
	}

	private Token getLast(List<Token> tokens) {
		return tokens.get(tokens.size()-1);
	}
	
	private List<ITerm> getTermList(List<Token> tokens, boolean ignoreLast) throws ParseException {
//		System.out.println("term tokens: " + tokens);
		List<ITerm> list = new LinkedList<ITerm>();
		if (tokens.isEmpty()) return list;
		
		List<List<Token>> termParts = getTermParts(tokens, ignoreLast);
				
		for (List<Token> termPart : termParts) {
			list.add(createTerm(termPart));
		}

		return list;
	}

	private List<List<Token>> getTermParts(List<Token> tokens, boolean ignoreLast) throws ParseException {
		List<List<Token>> termParts = new ArrayList<List<Token>>();
		List<Token> term = new ArrayList<Token>();
		
//		System.out.println("tokens: " + tokens);
		int i = 0;
		Stack<Token> bracketStack = new Stack<Token>();
		while (i < tokens.size()) {
//			System.out.println("bracket stack: " + bracketStack);
			Token token = tokens.remove(i);
			if (BRACKET_PAIRINGS.containsKey(token.type)) {
				bracketStack.push(token);
				term.add(token);
			} else if (BRACKET_PAIRINGS.containsValue(token.type)) {
				if (bracketStack.isEmpty()) {
					throw new ParseException("Too many brackets", token, token);
				}
				Token t = bracketStack.pop();
				if (BRACKET_PAIRINGS.get(t.type) != token.type)
					throw new ParseException("Mismatched Bracket", t, t);  
				term.add(token);
			} else if (token.type == Token.COMMA && bracketStack.isEmpty()) {
				if (term.isEmpty()) {
					throw new ParseException("Duplicate Comma: " + tokens, token, token);
				}

				termParts.add(term);
				term = new ArrayList<Token>();
			} else {
				term.add(token);
			}
		}
		
		if (!term.isEmpty()) {
			if (ignoreLast) {
				while (!term.isEmpty()) {
					tokens.add(0, term.remove(term.size()-1));
				}
			} else {
				termParts.add(term);
			}
		}
		return termParts;
	}
	
//	private IType createType(Token tok, List<Token> tokens) throws ParseException {
//		Token tok2 = tokens.remove(0);
//		
//		if (tok2.type == Token.LESS_THAN) {
//			String clazz = getQualifiedName(tokens);
//			tok2 = tokens.remove(0);
//			if (tok2.type != Token.GREATER_THAN) {
//				throw new ParseException("Invalid Type: Expected >, but got: " + tok2.token, tok, tok2);
//			}
//			return new ObjectType(tok.type, clazz);
//		} else {
//			tokens.add(0, tok2);
//		}
//		return new BasicType(Token.resolveType(tok.type));
//	}
	
	private String getQualifiedName(List<Token> tokens) throws ParseException {
		Token tok = tokens.get(0);
		StringBuffer buf = new StringBuffer();
		Token tok2 = null;
		do {
			if (tok2 != null) buf.append(tok2.token);
			tok2 = tokens.remove(0);
			if (tok2.type != Token.IDENTIFIER) throw new ParseException("Illegal character: '" + tok2.token + "' ", tok, tok2);
			buf.append(tok2.token);

			if (!tokens.isEmpty()) tok2 = tokens.remove(0);
		} while (tok2.type == Token.PERIOD && !tokens.isEmpty());
		tokens.add(0, tok2);
		return buf.toString();
	}

	public TypesElement createTypes(List<Token> subList) throws ParseException {
		Token start = subList.remove(0);
		Token end = subList.get(subList.size()-1);
		
		if (start.type != Token.IDENTIFIER) throw new ParseException("Identifier expected, but got: " + start.token, start);
		String name = start.token;
		
		Token tok = subList.remove(0);
		if (tok.type != Token.LEFT_BRACE) {
			throw new ParseException("Missing Left Brace", tok);
		}
		
		List<ILanguageDefinition> definitions = new ArrayList<ILanguageDefinition>();
		while (!subList.isEmpty()) {
			List<Token> list = this.splitAt(subList, new int[] {Token.SEMI_COLON});
			
			start = list.remove(0);
			end = list.get(list.size()-1);
			switch (start.type) {
			case Token.FORMULA:
				tok = list.remove(0);
				if (tok.type != Token.IDENTIFIER) throw new ParseException("Identifier expected, but got: " +tok.token, start, end);
				String identifier = tok.token;
				
				List<TypeDefinition> types = new ArrayList<TypeDefinition>();
				tok = list.remove(0);
				if (tok.type != Token.LEFT_BRACKET) throw new ParseException("Missing left bracket, got: " + tok.token, start, end);
				tok = list.remove(0);
				while (!list.isEmpty() && tok.type != Token.RIGHT_BRACKET) {
					if (tok.type==20) {
						// This is an identifier - so the type should be a Java classname
						String cls = tok.token;
						while ((list.get(0).type != Token.COMMA) && (list.get(0).type != Token.RIGHT_BRACKET)) {
							cls += list.remove(0).token;
						}
						types.add(new TypeDefinition(cls, Token.OBJECT_TYPE));
					} else {
						if (Token.isType(tok.type))  {
							types.add(new TypeDefinition(tok.token, tok.type));
						} else {
							throw new ParseException("The arguments of a formula definition must be types, but got: " + tok.token, start, end);
						}
					}
					tok = list.remove(0);
					if (tok.type != Token.RIGHT_BRACKET) {
						if (tok.type != Token.COMMA)
							throw new ParseException("Error in formula definition: expected a close bracket or a comma, but got: " + tok.token, start, end);
						tok = list.remove(0);
					}
				}
				
				if (tok.type != Token.RIGHT_BRACKET) 
					throw new ParseException("Malformed formula description", start, end);
				
				if (list.size() == 1 && list.get(0).type == Token.SEMI_COLON) 
					definitions.add(new FormulaDefinition(identifier, types.toArray(new TypeDefinition[types.size()]) , start, end, tokenizer.getSource(start, end)));
				else
					throw new ParseException("Unexpected termination of formula definition", start, end);
				break;
			default:
				throw new ParseException("Unknown type: " + tok.token, start, end);
			}
		}
		return new TypesElement(name, definitions.toArray(new ILanguageDefinition[definitions.size()]), start, end, tokenizer.getSource(start, end));
	}
}

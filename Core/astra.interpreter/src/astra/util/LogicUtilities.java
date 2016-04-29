package astra.util;

import java.util.ArrayList;
import java.util.List;

import astra.event.BeliefEvent;
import astra.event.Event;
import astra.event.GoalEvent;
import astra.formula.Goal;
import astra.formula.Predicate;
import astra.term.Primitive;
import astra.term.Term;

public class LogicUtilities {
	public static Predicate toPredicate(String content) {
		int index = content.indexOf('(');
		if (index == -1) {
			return new Predicate(content, new Term[0]);
		} else {
			char ch;
			String identifier = content.substring(0, index);
			List<Term> terms = new ArrayList<Term>();
			index++;
			while ((ch = content.charAt(index)) != ')') {
				if (ch == ',') {
					index++;
					continue;
				}
				if (ch == '"') {
					// Have a string, so add it...
					int i = content.indexOf('"', index+1);
					terms.add(Primitive.newPrimitive(content.substring(index+1, i)));
					index = i+1;
				} else {
					StringBuffer buf = new StringBuffer();
					while (ch != ',' && ch != ')') {
						buf.append(ch);
						ch = content.charAt(index++);
					}
					index++;
					String number = buf.toString();
					
					try {
						int val = Integer.parseInt(number);
						terms.add(Primitive.newPrimitive(val));
					} catch (NumberFormatException nfe) {
						try {
							long val = Long.parseLong(number);
							terms.add(Primitive.newPrimitive(val));
						} catch (NumberFormatException nfe2) {
							try {
								float val = Float.parseFloat(number);
								terms.add(Primitive.newPrimitive(val));
							} catch (NumberFormatException nfe3) {
								try {
									double val = Double.parseDouble(number);
									terms.add(Primitive.newPrimitive(val));
								} catch (NumberFormatException nfe4) {
									System.out.println("[ACREService.toPredicate] failed to parse: " + number + " with: " + ch);
								}
							}
						}
					}
					
					if (ch == ')') break;
				}
			}
			
			return new Predicate(identifier, terms.toArray(new Term[terms.size()]));
		}
	}
	
	public static Event toEvent(String eventString) {
		if (eventString.startsWith("+") || eventString.startsWith("-")) {
			char type = eventString.charAt(0);
			
			if (eventString.charAt(1) == '!') {
				Goal goal = new Goal(toPredicate(eventString.substring(2)));
				return new GoalEvent(type, goal);
			} else {
				Predicate belief = toPredicate(eventString.substring(1));
				return new BeliefEvent(type, belief);
			}
			
		}
		
		System.out.println("Event type not handled: " + eventString);
		return null;
	}

	public static void main(String[] args) {
		System.out.println(toPredicate("likes(\"rem\",\"beer\")"));
		System.out.println(toPredicate("likes(3,44444444444444)"));
		System.out.println(toPredicate("likes(3.33435,44444444444444)"));
	}

}

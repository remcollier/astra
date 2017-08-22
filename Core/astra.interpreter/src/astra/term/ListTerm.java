package astra.term;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import astra.reasoner.util.LogicVisitor;
import astra.type.Type;

public class ListTerm implements Term, List<Term> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6657715322908439651L;
	private List<Term> terms;
	
	public ListTerm(Term[] terms) {
		this.terms = new ArrayList<Term>();
		this.terms.addAll(Arrays.asList(terms));
	}

	public ListTerm() {
		this.terms = new ArrayList<Term>();
	}
	
	private ListTerm(List<Term> terms) {
		this.terms = terms;
	}
	
	@Override
	public Type type() {
		return Type.LIST;
	}

	@Override
	public Object accept(LogicVisitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public boolean matches(Term right) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String signature() {
		return "LST";
	}

	public int size() {
		return terms.size();
	}
	
	@Override
	public Term get(int i) {
		return terms.get(i);
	}

	public Term[] terms() {
		return terms.toArray(new Term[terms.size()]);
	}
	
	public boolean equals(Object object) {
		if (object instanceof ListTerm) {
			ListTerm term = (ListTerm) object;
			if (terms.size() != term.terms.size()) return false;
			for(int i = 0; i < term.terms.size(); i++) {
				if (!terms.get(i).equals(term.terms.get(i))) return false;
			}
			return true;
		}
		return false;
	}

	public Object merge(ListTerm r) {
		Term[] newTerms = new Term[terms.size() + r.terms.size()];
		int i;
		for (i=0; i<Math.min(terms.size(), r.terms.size()); i++) {
			newTerms[i] = terms.get(i);
			newTerms[i+terms.size()] = r.terms.get(i);
		}
		while (i < terms.size()) {
			newTerms[i] = terms.get(i);
			i++;
		}
		while (i < r.terms.size()) {
			newTerms[i+terms.size()] = r.terms.get(i);
			i++;
		}
		return new ListTerm(newTerms);
	}
	
	public String toString() {
		String out = "[";
		for (int i=0;i<terms.size();i++) {
			if (i>0) out+=",";
			out += terms.get(i).toString();
		}
		return out + "]";
	}


	@Override
	public void clear() {
		terms.clear();
	}
	
	@Override
	public int indexOf(Object object) {
		return terms.indexOf(object);
	}

	@Override
	public boolean isEmpty() {
		return terms.isEmpty();
	}

	@Override
	public Iterator<Term> iterator() {
		return terms.iterator();
	}

	@Override
	public int lastIndexOf(Object object) {
		return terms.lastIndexOf(object);
	}

	@Override
	public ListIterator<Term> listIterator() {
		return terms.listIterator();
	}

	@Override
	public ListIterator<Term> listIterator(int index) {
		return terms.listIterator(index);
	}

	@Override
	public boolean remove(Object arg0) {
		return terms.remove(arg0);
	}

	@Override
	public ListTerm subList(int arg0, int arg1) {
		return new ListTerm(terms.subList(arg0, arg1));
	}

	@Override
	public Term[] toArray() {
		return terms.toArray(new Term[size()]);
	}

	@Override
	public boolean add(Term e) {
		return terms.add(e);
	}

	@Override
	public void add(int index, Term element) {
		terms.add(index, element);
	}

	@Override
	public boolean addAll(Collection<? extends Term> c) {
		return terms.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends Term> c) {
		return terms.addAll(index, c);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return terms.containsAll(c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return terms.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return terms.retainAll(c);
	}

	@Override
	public Term set(int index, Term element) {
		return terms.set(index, element);
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return terms.toArray(a);
	}

	@Override
	public boolean contains(Object o) {
		return terms.contains(o);
	}

	@Override
	public Term remove(int index) {
		return terms.remove(index);
	}
	
	public ListTerm clone() {
		ListTerm values = new ListTerm();
		for (Term term : this) {
			values.add(term.clone());
		}
		return values;
	}
}

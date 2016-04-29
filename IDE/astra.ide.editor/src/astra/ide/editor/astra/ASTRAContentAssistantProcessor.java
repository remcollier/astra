package astra.ide.editor.astra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;

import astra.ast.core.ASTRAClassElement;
import astra.ast.core.ParseException;
import astra.ast.jdt.ASTRAProject;
import astra.ast.jdt.JDTHelper;

public class ASTRAContentAssistantProcessor implements IContentAssistProcessor {
	private static final ICompletionProposal[] NO_PROPOSALS = new ICompletionProposal[] {};
	static Map<Character, Integer> set = new HashMap<Character, Integer>();
	static Map<Character, String> autos = new HashMap<Character, String>();
	static Map<String, String> prefixes = new HashMap<String, String>();
	
	static {
		set.put('(', JDTHelper.TERM);
		set.put(',', JDTHelper.TERM);
		set.put('+', JDTHelper.TERM);
		set.put('-', JDTHelper.TERM);
		set.put('*', JDTHelper.TERM);
		set.put('/', JDTHelper.TERM);
		set.put('%', JDTHelper.TERM);
		set.put('=', JDTHelper.TERM);
		set.put('&', JDTHelper.FORMULA);
		set.put('|', JDTHelper.FORMULA);
		set.put('~', JDTHelper.FORMULA);
		set.put(';', JDTHelper.ACTION);
		set.put('{', JDTHelper.ACTION);
		set.put('}', JDTHelper.ACTION);
		
		autos.put('(', "()");
		autos.put('{', "{}");
		autos.put('[', "[]");
		autos.put('\'', "\'\'");
		autos.put('\"', "\"\"");
		
		prefixes.put("ag", "agent");
		prefixes.put("mod", "module");
		prefixes.put("ru", "rule");
		prefixes.put("fe", "foreach()");
		prefixes.put("fa", "forall()");
		prefixes.put("@m", "@message");
		prefixes.put("@e", "@eis");
		prefixes.put("wh", "while");
	}
	
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {
		IDocument doc = viewer.getDocument();

		// Get the context for the content assist
		String context = "",prefix="";
		int i = offset-1;
		char ch = ' ';
		try {
			ch = doc.getChar(i);
			
			// Check for character driven auto complete
			String result = autos.get(ch);
			if (result != null) {
				viewer.getDocument().replace(i, 1, result);
				viewer.setSelectedRange(i+1, 0);
				return NO_PROPOSALS;
			}

			while (ch != '.') {
				prefix = ch + prefix;
				
				// Check for prefix driven autocomplete
				if (prefixes.containsKey(prefix)) {
					String cmd = prefixes.get(prefix);
					int cursor_pos = cmd.length();
					if (cmd != null) {
						if (cmd.endsWith(")")) cursor_pos--;
						return new ICompletionProposal[] {new CompletionProposal(cmd, i, prefix.length(), cursor_pos)};
					}
				}
				ch = doc.getChar(--i);
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		
		int dot = i;
		try {
			ch = doc.getChar(--i);
			while (!set.containsKey(ch)) {
				context = ch + context;
				ch = doc.getChar(--i);
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		context = context.trim();
		int atype = set.get(ch); 
				
		// Extract the agent class name
		String text = doc.get();
		String cls = "";
		int index = text.indexOf("package");
		if (index > -1) {
			int index2 = text.indexOf(";", index);
			cls = text.substring(index+8, index2).trim() + ".";
		}
		index = text.indexOf("agent", index);
		int index2 = text.indexOf("extends", index);
		if (index2 == -1) {
			index2 = text.indexOf("{", index);
		}
		cls += text.substring(index+6, index2-1).trim();


		// Get the relevant methods...
		List<ICompletionProposal> list = new ArrayList<ICompletionProposal>();
		IEditorPart  editorPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if(editorPart != null) {
		    IFileEditorInput input = (IFileEditorInput)editorPart.getEditorInput();
		    IFile file = input.getFile();

			JDTHelper helper = new JDTHelper(file.getProject());
			ASTRAProject project = null;
			try {
				project = ASTRAProject.getProject(file.getProject());
			    ASTRAClassElement element = project.getASTRAClassElement(file);
			    helper.setup(element.packageElement(), element.imports());
			} catch (CoreException e1) {
				e1.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				String moduleClass = project.getModuleClassName(cls, context);
				if (moduleClass != null) {
					System.out.println("moduleClass: " + moduleClass);
					IType type = helper.getType(moduleClass);
					for (IMethod mthd : type.getMethods()) {
						for (IAnnotation ann : mthd.getAnnotations()) {
							String annot = JDTHelper.ANNOTATIONS.get(atype);
							if (ann.getElementName().equals(annot) || ann.getElementName().equals(JDTHelper.ALTERNATE.get(atype))) {
								String template = mthd.getElementName() + "(";
								boolean first = true;
								for (String name : mthd.getParameterNames()) {
									if (first) first=false; else template+=",";
									template += name;
								}
								template += ")";
								if (template.startsWith(prefix)) {
									int cursor_pos = (mthd.getNumberOfParameters() > 0) ? mthd.getElementName().length()+1:template.length();
									
									list.add(new CompletionProposal(template, dot+1, prefix.length(), cursor_pos));
								}
							}
						}
					}
				}				
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (JavaModelException e) {
				e.printStackTrace();
			}
		}

//		ICompletionProposal[] completionProposals = new ICompletionProposal[1];
//		completionProposals[0] = new CompletionProposal(context, 0, 1, 5);
		return list.toArray(new ICompletionProposal[list.size()]);
	}

	public IContextInformation[] computeContextInformation(ITextViewer viewer, int offset) {
		return null;
	}

	public char[] getCompletionProposalAutoActivationCharacters() {
		return new char[] { '.', '(', '{','[', '\'', '\"' };
	}

	public char[] getContextInformationAutoActivationCharacters() {
		return null;
	}

	public String getErrorMessage() {
		return null;
	}

	public IContextInformationValidator getContextInformationValidator() {
		return null;
	}

}

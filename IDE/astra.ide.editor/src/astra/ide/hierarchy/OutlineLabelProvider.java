package astra.ide.hierarchy;

import org.eclipse.jdt.ui.ISharedImages;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.navigator.IDescriptionProvider;

import astra.ast.core.ASTRAClassElement;
import astra.ast.element.InitialElement;
import astra.ast.element.ModuleElement;
import astra.ast.element.PackageElement;
import astra.ast.element.PlanElement;
import astra.ast.element.RuleElement;

public class OutlineLabelProvider extends LabelProvider implements ILabelProvider, IDescriptionProvider {
	public String getText(Object element) {
//		if (element instanceof SimpleNode) {
//			SimpleNode node = (SimpleNode) element;
//			if (node.toString().equals("Predicate")) {
//				return (String) new PredicateBuilderVisitor().visit(node, null) + " : Belief";
//			} else if (node.toString().equals("SensorExpression")) {
//				return "" + new PredicateBuilderVisitor().visit((SimpleNode) node.jjtGetChild(0), null) +
//						" [" + ((SimpleNode) node.jjtGetChild(1)).jjtGetValue() +
//						"] : Sensor";
//			} else if (node.toString().equals("ModuleExpression")) {
//				return "" + new PredicateBuilderVisitor().visit((SimpleNode) node.jjtGetChild(0), null) +
//						" [" + ((SimpleNode) node.jjtGetChild(1)).jjtGetValue() +
//						"] : Library";
//			} else if (node.toString().equals("ActionExpression")) {
//				return "" + new PredicateBuilderVisitor().visit((SimpleNode) node.jjtGetChild(0), null) +
//						" [" + ((SimpleNode) node.jjtGetChild(1)).jjtGetValue() +
//						"] : Action";
//			} else if (node.toString().equals("InferenceExpression")) {
//				return (String) new PredicateBuilderVisitor().visit((SimpleNode) node.jjtGetChild(1), null) + " : Inference";
//			} else if (node.toString().equals("AbstractRuleExpression")) {
//				String label = "@" + new PredicateBuilderVisitor().visit((SimpleNode) node.jjtGetChild(0), null);
//				if ("#partial".equals(node.jjtGetValue())) {
//					return label + " [partial] : Abstract Rule";
//				} else {
//					return label + " : Abstract Rule";
//				}
//			} else if (node.toString().equals("LabelledRuleExpression")) {
//				SimpleNode typeNode = (SimpleNode) node.jjtGetChild(0);
//				String label = "@" + new PredicateBuilderVisitor().visit((SimpleNode) typeNode.jjtGetChild(0), null);
//				if (typeNode.toString().equals("Full")) {
//					SimpleNode eventNode = (SimpleNode) typeNode.jjtGetChild(1);
//					String predicateEvent = "" + eventNode.jjtGetValue();
//					SimpleNode evt = (SimpleNode) eventNode.jjtGetChild(0);
//					if (evt.toString().equals("Goal")) {
//						predicateEvent += "!";
//						evt = (SimpleNode) evt.jjtGetChild(0);
//					}
//					predicateEvent += new PredicateBuilderVisitor().visit(evt, null);
//					return label + " [" + predicateEvent + "] : Rule";
//				} else { 
//					return label + " [partial] : Rule"; 
//				}
//			} else if (element.toString().equals("UnlabelledRuleExpression")) {
//				SimpleNode eventNode = (SimpleNode) node.jjtGetChild(0);
//				String predicateEvent = "" + eventNode.jjtGetValue();
//				SimpleNode evt = (SimpleNode) eventNode.jjtGetChild(0);
//				if (evt.toString().equals("Goal")) {
//					predicateEvent += "!";
//					evt = (SimpleNode) evt.jjtGetChild(0);
//				}
//				predicateEvent += new PredicateBuilderVisitor().visit(evt, null);
//				return predicateEvent + " [private] : Rule";
//			} else if (element.toString().equals("FuncExpression")) {
//				return (String) new PredicateBuilderVisitor().visit((SimpleNode) node.jjtGetChild(0), null) + " : TR Function";
//			}
//		}
		if (element instanceof ASTRAClassElement) {
			return ((ASTRAClassElement) element).getClassDeclaration().name();
		} else if (element instanceof PackageElement) {
			return ((PackageElement) element).packageName();
		} else if (element instanceof ModuleElement) {
			return ((ModuleElement) element).name() + " : " + ((ModuleElement) element).className();
		} else if (element instanceof InitialElement) {
			return ((InitialElement) element).formula().toString();
		} else if (element instanceof RuleElement) {
			return ((RuleElement) element).event().toString() + " : " + ((RuleElement) element).context().toString();
		} else if (element instanceof PlanElement) {
			return ((PlanElement) element).signature().toString();
		}
        return element.toString();
    }
 
    public String getDescription(Object element) {
        String text = getText(element);
        return "This is a description of " + text;
    }
 
    public Image getImage(Object element) {
		if (element instanceof ASTRAClassElement) {
          return JavaUI.getSharedImages().getImage(ISharedImages.IMG_OBJS_CLASS_DEFAULT);
		} else if (element instanceof PackageElement) {
	          return JavaUI.getSharedImages().getImage(ISharedImages.IMG_OBJS_PACKDECL);
		} else if (element instanceof ModuleElement) {
			return JavaUI.getSharedImages().getImage(ISharedImages.IMG_OBJS_IMPCONT);
		} else if (element instanceof InitialElement) {
    		return JavaUI.getSharedImages().getImage(ISharedImages.IMG_FIELD_PRIVATE);
		} else if (element instanceof RuleElement) {
            return JavaUI.getSharedImages().getImage(ISharedImages.IMG_OBJS_PUBLIC);
		} else if (element instanceof PlanElement) {
			return JavaUI.getSharedImages().getImage(ISharedImages.IMG_OBJS_PROTECTED);
		}
 
//    	if (element instanceof SimpleNode) {
//    		if (element.toString().equals("Predicate")) {
//        		return JavaUI.getSharedImages().getImage(ISharedImages.IMG_FIELD_PUBLIC);
//			} else if ((element.toString().equals("SensorExpression")) ||
//					(element.toString().equals("ActionExpression"))) {
//	            return JavaUI.getSharedImages().getImage(ISharedImages.IMG_OBJS_IMPDECL);
//			} else if (element.toString().equals("ModuleExpression")) {
//				return JavaUI.getSharedImages().getImage(ISharedImages.IMG_OBJS_IMPCONT);
//			} else if (element.toString().equals("InferenceExpression")) {
//	    		return JavaUI.getSharedImages().getImage(ISharedImages.IMG_FIELD_PRIVATE);
//			} else if (element.toString().equals("AbstractRuleExpression") ||
//					element.toString().equals("LabelledRuleExpression")) {
//				if ("#partial".equals(((SimpleNode) element).jjtGetValue())) {
//					return JavaUI.getSharedImages().getImage(ISharedImages.IMG_OBJS_PROTECTED);
//	        	} else {
//	                return JavaUI.getSharedImages().getImage(ISharedImages.IMG_OBJS_PUBLIC);
//	        	}
//			} else if (element.toString().equals("LabelledRuleExpression")) {
//				if ("#partial".equals(((SimpleNode) element).jjtGetValue())) {
//					return JavaUI.getSharedImages().getImage(ISharedImages.IMG_OBJS_PROTECTED);
//				} else {
//					return JavaUI.getSharedImages().getImage(ISharedImages.IMG_OBJS_PUBLIC);
//				}
//			} else if (element.toString().equals("UnlabelledRuleExpression")) {
//	            return JavaUI.getSharedImages().getImage(ISharedImages.IMG_OBJS_PRIVATE);
//			} else if (element.toString().equals("FuncExpression")) {
//	    		return JavaUI.getSharedImages().getImage(ISharedImages.IMG_FIELD_DEFAULT);
//    		}
//    	}
//    	
//    	if (element instanceof OutlineContentProvider.ComponentRef) {
//            return JavaUI.getSharedImages().getImage(ISharedImages.IMG_OBJS_IMPDECL);
//    	}
//        if (element instanceof AgentSpeakCompilationUnit) {
//            return JavaUI.getSharedImages().getImage(ISharedImages.IMG_OBJS_CLASS_DEFAULT);
//        }
        
        
        return null;
    }
}

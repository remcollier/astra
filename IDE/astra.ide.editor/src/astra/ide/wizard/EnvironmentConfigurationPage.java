package astra.ide.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

public class EnvironmentConfigurationPage extends WizardPage {

	protected EnvironmentConfigurationPage() {
		super("Envirionment Configuration");
	}

	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 9;
		Label label = new Label(container, SWT.NULL);
		label.setText("&Environment:");
		new Label(container, SWT.NULL);
		new Label(container, SWT.NULL);
		
		Group group1 = new Group(container, SWT.SHADOW_IN);
	    group1.setText("&Environment:");
	    group1.setLayout(new RowLayout(SWT.VERTICAL));
	    new Button(group1, SWT.RADIO).setText("CartAgo");
	    Group group2 = new Group(group1, SWT.NONE);
	    group2.setLayout(new RowLayout(SWT.HORIZONTAL));
	    new Button(group2, SWT.RADIO).setText("EIS");
	    Combo box = new Combo(group2, SWT.READ_ONLY);
	    box.setItems(new String[] {"v0.3", "v0.5"});

//		label.setText("&Agent Class Name:");
//
//		fileText = new Text(container, SWT.BORDER | SWT.SINGLE);
//		gd = new GridData(GridData.FILL_HORIZONTAL);
//		fileText.setLayoutData(gd);
//		fileText.addModifyListener(new ModifyListener() {
//			public void modifyText(ModifyEvent e) {
//				dialogChanged();
//			}
//		});
//		initialize();
//		dialogChanged();
		setControl(container);
	}

}

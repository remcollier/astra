package astra.ide.launch;

import java.util.Arrays;
import java.util.Comparator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.FilteredItemsSelectionDialog;

import astra.ide.Activator;

public class ASTRADeployFilterDialog extends FilteredItemsSelectionDialog {
	private static final String SETTINGS = ASTRADeployFilterDialog.class
			.getCanonicalName();

	private IFile[] files;
	
	public ASTRADeployFilterDialog(Shell shell, IFile[] files, String title) {
		super(shell);

		this.files = files;
		this.setTitle(title);
	}

	@Override
	protected Control createExtendedContentArea(Composite parent) {
		return null;
	}

	@Override
	protected IDialogSettings getDialogSettings() {
		IDialogSettings settings = Activator.getDefault().getDialogSettings()
				.getSection(SETTINGS);

		if (settings == null) {
			settings = Activator.getDefault().getDialogSettings()
					.addNewSection(SETTINGS);
		}

		return settings;
	}

	@Override
	protected IStatus validateItem(Object item) {
		return Status.OK_STATUS;
	}

	@Override
	protected ItemsFilter createFilter() {
		return new ASTRAFilter();
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected Comparator getItemsComparator() {
		// TODO Auto-generated method stub
		return new Comparator() {
			public int compare(Object o1, Object o2) {
				if (o1 instanceof IFile && o2 instanceof IFile) {
					return ((IFile) o1).getName().compareTo(((IFile) o2).getName());
				}
				return -1;
			}
		};
	}

	@Override
	protected void fillContentProvider(AbstractContentProvider contentProvider,
			ItemsFilter itemsFilter, IProgressMonitor progressMonitor)
			throws CoreException {

		// Loop over all projects
		progressMonitor.beginTask("Looking for targets...", files.length);
		for (IFile file : files) {
			contentProvider.add(file, itemsFilter);
			progressMonitor.worked(1);
		}
	}

	@Override
	public String getElementName(Object item) {
		return ((IFile) item).getProjectRelativePath().toOSString();
	}

	class ASTRAFilter extends ItemsFilter {
		@Override
		public boolean matchItem(Object item) {
			if(!(item instanceof IFile) || !Arrays.asList(files).contains(item)) {
				return false;
			}
			return matches(((IFile)item).getName());
		}
		
		@Override
		public boolean isConsistentItem(Object item) {
			return item instanceof IFile;
		}

	}
}

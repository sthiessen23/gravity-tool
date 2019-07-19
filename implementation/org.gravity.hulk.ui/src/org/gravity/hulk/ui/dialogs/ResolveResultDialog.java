package org.gravity.hulk.ui.dialogs;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.gravity.hulk.HDetector;
import org.gravity.hulk.antipatterngraph.HAnnotation;

/**
 * A dialog for showing possible refactorings for anti-pattern elimination
 * 
 * @author speldszus
 *
 */
public class ResolveResultDialog extends Dialog {

	private static final Logger LOGGER = Logger.getLogger(ResolveResultDialog.class);

	Iterable<HDetector> selection;
	Iterable<HDetector> executed;
	Shell pShell;

	/**
	 * Creates a new dialog
	 * 
	 * @param parentShell       The parent shell in which the dialog should be shown
	 * @param selectedDetectors All selected detectors
	 * @param executedDetectors All executed detectors
	 */
	public ResolveResultDialog(Shell parentShell, Iterable<HDetector> selectedDetectors,
			Iterable<HDetector> executedDetectors) {
		super(parentShell);
		pShell = parentShell;
		this.selection = selectedDetectors;
		this.executed = executedDetectors;
	}

	@Override
	protected Point getInitialSize() {
		return new Point(450, 600);
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);

		CTabFolder folder = new CTabFolder(container, SWT.TOP);
		folder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		addContents(folder, this.selection);

		final Button button = new Button(container, SWT.CHECK);
		button.setText("Show all executed detectors.");
		button.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				CTabItem selectedItem = folder.getSelection();
				String selected = selectedItem.getText();
				for (CTabItem item : folder.getItems()) {
					item.dispose();
				}
				if (button.getSelection()) {
					addContents(folder, ResolveResultDialog.this.executed);
				} else {
					addContents(folder, ResolveResultDialog.this.selection);
				}
				for (CTabItem item : folder.getItems()) {
					if (item.getText().equals(selected)) {
						folder.setSelection(item);
						break;
					}
				}
				if (folder.getSelection() == null) {
					folder.setSelection(0);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		final Button saveButton = new Button(container, SWT.PUSH);
		saveButton.setText("save");
		saveButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fdialog = new FileDialog(pShell, SWT.SAVE);
				fdialog.setFilterExtensions(new String[] { "*.txt", "*" });
				String saveFile = fdialog.open();
				if (!saveFile.isEmpty()) {
					save(folder, saveFile);
				}

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);

			}
		});

		return container;
	}

	/**
	 * Modified addContents for Anti-Pattern resolving. Requires a list of executed
	 * moves, will sort the results by source classes 
	 */
	void addContents(CTabFolder folder, Iterable<HDetector> items) {

		for (HDetector eClass : items) {
			CTabItem tab = new CTabItem(folder, getShellStyle());
			tab.setText(eClass.eClass().getName());

			Tree tree = new Tree(folder, SWT.V_SCROLL | SWT.H_SCROLL);
			ScrollBar vBar = tree.getVerticalBar();
			vBar.setEnabled(true);

			if (eClass.getHAnnotation().size() > 0) {
				for (HAnnotation annotation : eClass.getHAnnotation()) {
					annotation.getTreeItem(tree, SWT.NONE);
				}
			} else {
				new TreeItem(tree, SWT.NONE).setText("No " + eClass.getGuiName() + " has been found.");
			}

			folder.showItem(folder.getItems()[0]);
			tab.setControl(tree);

		}
	}

	private class DetectionContent {

		private String detector;
		private List<String> results;

		public DetectionContent(String detector) {
			this.detector = detector;
		}

		public String getDetector() {
			return detector;
		}

		public List<String> getSortedResult() {
			if (results == null) {
				results = new ArrayList<String>();
			}
			Collections.sort(results);
			return results;
		}

		public void addResult(String result) {
			if (results == null) {
				results = new ArrayList<String>();
			}
			results.add(result);
		}
	}

	private void save(CTabFolder folder, String filePath) {
		List<DetectionContent> contents = new ArrayList<DetectionContent>();
		for (CTabItem tabItem : folder.getItems()) {
			contents.add(createContent(tabItem));
		}

		StringBuilder builder = new StringBuilder();
		for (DetectionContent content : contents) {
			builder.append(content.getDetector());
			for (String result : content.getSortedResult()) {
				builder.append(System.lineSeparator());
				builder.append(result);
			}
			builder.append(System.lineSeparator());
			builder.append("--------------------------------------------------------------------");
			builder.append(System.lineSeparator());
		}

		try {
			PrintWriter out = new PrintWriter(filePath);
			out.print(builder);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			LOGGER.log(Level.ERROR, e.getLocalizedMessage(), e);
		}

	}

	/**
	 * Created a detection content object for the tab item
	 * 
	 * @param tabItem The tab item
	 * @return The content object
	 */
	private DetectionContent createContent(CTabItem tabItem) {
		DetectionContent content = new DetectionContent(tabItem.getText());
		Tree tree = (Tree) tabItem.getControl();
		if (tree == null) {
			return content;
		}

		for (TreeItem treeItem : tree.getItems()) {
			String text = treeItem.getText();
			content.addResult(text);
			if (text.startsWith("Solved")) {
				content.addResult(treeItem.getItem(0).getText());
				for (TreeItem child : treeItem.getItem(2).getItems()) {
					content.addResult(child.getText());
					for (TreeItem r : child.getItems()) {
						content.addResult("\t" + r.getText());
					}
				}
			}
		}
		return content;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Hulk Anti-pattern Elemination Results");
	}
}
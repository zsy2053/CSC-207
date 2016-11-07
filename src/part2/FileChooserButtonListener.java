package part2;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;

/**
 * The listener for the button to choose a directory. This is where most of the
 * work is done.
 */
public class FileChooserButtonListener implements ActionListener {

	/** The window the button is in. */
	private JFrame directoryFrame;
	/** The label for the full path to the chosen directory. */
	private JLabel directoryLabel;
	/** The file chooser to use when the user clicks. */
	private JFileChooser fileChooser;
	/** The area to use to display the nested directory contents. */
	private JTextArea textArea;

	/**
	 * An action listener for window dirFrame, displaying a file path on
	 * dirLabel, using fileChooser to choose a file.
	 *
	 * @param dirFrame
	 *            the main window
	 * @param dirLabel
	 *            the label for the directory path
	 * @param fileChooser
	 *            the file chooser to use
	 */
	public FileChooserButtonListener(JFrame dirFrame, JLabel dirLabel, JTextArea textArea, JFileChooser fileChooser) {
		this.directoryFrame = dirFrame;
		this.directoryLabel = dirLabel;
		this.textArea = textArea;
		this.fileChooser = fileChooser;
	}

	/**
	 * Handle the user clicking on the open button.
	 *
	 * @param e
	 *            the event object
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		int returnVal = fileChooser.showOpenDialog(directoryFrame.getContentPane());

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			if (file.exists()) {
				directoryLabel.setText("Selected File" + file.getAbsolutePath());
				// Display a temporary message while the directory is
				// traversed.
				this.textArea.setText("Building file tree");

				// Make the root.
				FileNode fileTree = new FileNode(file.getName(), null, FileType.DIRECTORY);
				FileChooserButtonListener.buildTree(file, fileTree);

				// Build the string representation and put it into the text
				// area.
				StringBuffer contents = new StringBuffer();
				buildDirectoryContents(fileTree, contents, "");
				this.textArea.setText(contents.toString());
			}
		} else {
			directoryLabel.setText("No Path Selected");
		}
	}

	/**
	 * Build the tree of nodes rooted at file in the file system; note curr is
	 * the FileNode corresponding to file, so this only adds nodes for children
	 * of file to the tree. Precondition: file represents a directory.
	 * 
	 * @param file
	 *            the file or directory we are building
	 * @param curr
	 *            the node representing file
	 */
	private static void buildTree(File file, FileNode curr) {
		
		if(file.isDirectory()) {
			File[] files = file.listFiles();
			
			for(File tmp : files) {
				FileNode tmp_node = null;
				
				if(tmp.isDirectory()) {
					tmp_node = new FileNode(tmp.getName(), null, FileType.DIRECTORY);
					tmp_node.setParent(curr);
					curr.addChild(tmp_node.getName(), tmp_node);
					buildTree(tmp, curr.findChild(tmp_node.getName()));
				}
				else {
					tmp_node = new FileNode(tmp.getName(), null, FileType.FILE);
					tmp_node.setParent(curr);
					curr.addChild(tmp_node.getName(), tmp_node);
				}
					
			}
		} 
	}

	/**
	 * Build a string buffer representation of the contents of the tree rooted
	 * at n, prepending each file name with prefix, and adding and additional
	 * DirectoryExplorer.PREFIX for subdirectory contents.
	 *
	 * @param fileNode
	 *            the root of the subtree
	 * @param contents
	 *            the string to display
	 * @param prefix
	 *            the prefix to prepend
	 */
	private static void buildDirectoryContents(FileNode fileNode, StringBuffer contents, String prefix) {
		contents.append(prefix);
		contents.append(fileNode.getName() + "\n");
		
		if (fileNode.isDirectory())
		{
			for(FileNode tmp_node : fileNode.getChildren()) {
				buildDirectoryContents(tmp_node, contents, prefix + "--");
			}
			
		}
	}
}

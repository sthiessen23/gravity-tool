package org.gravity.eclipse.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * 
 * Helpful Utils when working with files
 * 
 * @author speldszus
 *
 */
public class FileUtils {

	private static final Logger LOGGER = Logger.getLogger(FileUtils.class);

	/**
	 * Replaces the line endings with the endings of the current system
	 * 
	 * @param file - The file
	 * @return true, if the endings have been replaced successfully
	 * @throws IOException Iff the original file has been lost due to an error
	 */
	public static boolean changeToOSEncoding(File file) throws IOException {
		Path tempFile = null;
		try {
			// move the file to a temporary file
			tempFile = Files.move(file.toPath(), Files.createTempFile("gravity", null), StandardCopyOption.REPLACE_EXISTING);
			tempFile.toFile().deleteOnExit();
			// print all lines to the original location using system encoding
			try (PrintWriter stream = new PrintWriter(new FileWriter(file, true))) {
				Files.lines(tempFile).forEach(s->{
					stream.println(s);
				});
			}
		} catch (IOException e) {
			LOGGER.log(Level.ERROR, "Replacing line endings of file failed: " + e.getMessage(), e);
			try {
				// Try to recover file
				if(tempFile != null) {
					Files.move(tempFile, file.toPath());
				}
			} catch (IOException e2) {
				// Iff recover wasn't possible throw original error
				throw new IOException("A copy of the orgiginal file is maybe present at: "+tempFile.toString(), e);
			}
			return false;
		}
		// delete the temp file
		try {
			Files.deleteIfExists(tempFile);
		}
		catch(IOException e) {
			/* 
			 * As the temporal files will be deleted anyways we currently only log a warning.
			 * However, sensitive data might be leaked this way!
			 */ 
			LOGGER.log(Level.WARN, "The temporal copy of a file couldn't be deleted: "+e.getMessage(), e);
		}
		return true;
	}

	/**
	 * Reads the contents from the given file and returns them as single string
	 * 
	 * @param file The file containing contents
	 * @return The content of the file
	 * @throws IOException           If an I/O error occurs
	 * @throws FileNotFoundException Iff the file doesn't exists
	 */
	public static String getContentsAsString(File file) throws IOException, FileNotFoundException {
		String settingsContentString;
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

			StringBuilder contents = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				contents.append(line);
				contents.append('\n');
			}
			settingsContentString = contents.toString();
		}
		return settingsContentString;
	}
}

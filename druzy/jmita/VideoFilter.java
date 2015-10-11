package druzy.jmita;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.swing.filechooser.FileFilter;


public class VideoFilter extends FileFilter {

	public VideoFilter() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean accept(File file) {
		if (file.isDirectory()) return true;
		else{
			try {
				return Files.probeContentType(file.toPath()).indexOf("video")==0;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

}

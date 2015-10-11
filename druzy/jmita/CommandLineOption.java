package druzy.jmita;
import java.io.File;
import java.util.List;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;


public class CommandLineOption {
	
	@Option(name="-f", aliases={"--file"}, usage="Fichier(s) à envoyer")
	private List<File> files;
	
	@Option(name="-d", aliases={"--device"}, usage="Appareil où envoyer")
	private String device=null;
	
	@Option(name="-h", aliases={"-help"}, usage="Imprime cette aide")
	private boolean help;
	
	@Option(name="-a", aliases={"--about"}, usage="Ouvre la fenêtre 'about' de JMita")
	private boolean about;
	
	@Option(name="-q", aliases={"--quit","exit"}, usage="Quitte JMita")
	private boolean quit;
	
	@Argument
	private List<String> arguments;

	public List<File> getFiles() {
		return files;
	}

	public void setFiles(List<File> files) {
		this.files = files;
	}

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public List<String> getArguments() {
		return arguments;
	}

	public void setArguments(List<String> arguments) {
		this.arguments = arguments;
	}

	
	public boolean isHelp() {
		return help;
	}

	
	public void setHelp(boolean help) {
		this.help = help;
	}

	
	public boolean isAbout() {
		return about;
	}

	
	public void setAbout(boolean about) {
		this.about = about;
	}

	public boolean isQuit() {
		return quit;
	}

	public void setQuit(boolean quit) {
		this.quit = quit;
	}
}

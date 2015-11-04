package druzy.jmita;
import java.io.File;

import druzy.version.Version;

public class Global {
	
	public static final String IMAGE="druzy/jmita/image";
	public static final String MEDIA_STOP_OUTLINE=IMAGE+"/media_stop_outline.png";
	public static final String MEDIA_PAUSE_OUTLINE=IMAGE+"/media_pause_outline.png";
	public static final String MEDIA_PLAY_OUTLINE = IMAGE+"/media_play_outline.png";
	public static final String MEDIA_REWIND_OUTLINE = IMAGE+"/media_rewind_outline.png";
	public static final String POWER_OUTLINE=IMAGE+"/power_outline.png";
	public static final String VOLUME_UP=IMAGE+"/plus.png";
	public static final String VOLUME_DOWN=IMAGE+"/minus.png";
	public static final String VOLUME=IMAGE+"/volume.png";
	public static final String MAIN_ICON=IMAGE+"/jmita2.png";
	
	public static final File USER_HOME=new File(System.getProperty("user.home"));
	public static final File USER_FOLDER=new File(USER_HOME,".jmita2");
	public static final File FAVORIS=new File(USER_FOLDER,"favoris");
	
	public static final Version VERSION=new Version("0.7.8");
	
	//pas de constructeur
	private Global(){}
	
	//création des dossier s'il n'existe pas
	static{
		if (!USER_FOLDER.exists()) USER_FOLDER.mkdir();
		if (!FAVORIS.exists()) FAVORIS.mkdir();
	}
		
}

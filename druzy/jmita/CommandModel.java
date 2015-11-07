package druzy.jmita;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import druzy.mvc.AbstractModel;
import druzy.protocol.Renderer;


public class CommandModel extends AbstractModel{

	//variables	
	private boolean exit=false;
	
	private Renderer renderer=null;
	private List<File> playlist=null;
	
	private File currentlyFile=null;
	private int currentlyIndex=-1;
	
	public static final int PUSH_STOP_BUTTON=0;
	public static final int PUSH_EXIT=1;
	public static final int PUSH_PAUSE_BUTTON = 2;
	public static final int PUSH_PLAY_BUTTON = 3;
	public static final int PUSH_REWIND_BUTTON = 4;
	public static final int DISPLAY=5;
	public static final int PUSH_VOLUME=6;
	public static final int DOUBLE_CLICK=7;
	public static final int PUSH_VOLUME_DOWN=8;
	public static final int PUSH_VOLUME_UP=9;
	public static final int CLICK_SLIDER=10;
	
	public CommandModel(Renderer renderer, List<File> playlist) {
		if (playlist==null || playlist.isEmpty()) throw new IllegalArgumentException("playlist cannot be null or empty");
		
		this.renderer=renderer;
		this.playlist=Collections.synchronizedList(playlist);
	}
	
	public CommandModel(Renderer renderer, File file) {
		this(renderer,new ArrayList<File>(Arrays.asList(file)));
	}

	public Renderer getRenderer() {
		return renderer;
	}

	public void setRenderer(Renderer renderer) {
		Renderer old=this.renderer;
		this.renderer=renderer;
		firePropertyChange(new PropertyChangeEvent(this,"renderer",old,renderer));
	}


	public List<File> getPlaylist() {
		return playlist;
	}

	public void setPlaylist(List<File> playlist) {
		List<File> old=this.playlist;
		this.playlist=playlist;
		firePropertyChange(new PropertyChangeEvent(this,"playlist",old,playlist));
	}
	
	public void addToPlaylist(File file){
		ArrayList<File> files=new ArrayList<File>();
		files.add(file);
		addAllToPlaylist(files);
	}
	
	public void addAllToPlaylist(List<File> files){
		Object old=null;
		this.playlist.addAll(files);
		firePropertyChange(new PropertyChangeEvent(this,"addAllToPlaylist",old,files));
	}
	
	public boolean isExit() {
		return exit;
	}

	public void setExit(boolean exit) {
		boolean old=this.exit;
		this.exit = exit;
		firePropertyChange(new PropertyChangeEvent(this,"exit",old,exit));
	}

	public File getCurrentlyFile() {
		return currentlyFile;
	}

	public void setCurrentlyFile(File currentlyFile) {
		File old=this.currentlyFile;
		this.currentlyFile = currentlyFile;
		firePropertyChange(new PropertyChangeEvent(this,"currentlyFile",old,currentlyFile));
	}

	public int getCurrentlyIndex() {
		return currentlyIndex;
	}

	public void setCurrentlyIndex(int currentlyIndex) {
		int old=this.currentlyIndex;
		this.currentlyIndex = currentlyIndex;
		firePropertyChange(new PropertyChangeEvent(this,"currentlyIndex",old,currentlyIndex));
	}

}

package druzy.jmita;
import java.beans.IndexedPropertyChangeEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import druzy.mvc.AbstractModel;
import druzy.protocol.Renderer;


public class SendToModel extends AbstractModel {
	//variables
	private List<File> fileList=null;
	private List<Renderer> rendererList=null;
	private int milliSecondElapsed = 0; 
	private int milliSecondWait = 0;
	private List<Command> commandList=null;
	
	//variables static
	public static final int PUSH_CANCEL_BUTTON=0;
	public static final int PUSH_SEND_BUTTON=1;
	public static final int PUSH_EXIT=2;
	public static final int CLICK_RENDERER=3;
	
	public SendToModel(List<File> fileList){
		super();
		
		if (fileList==null) throw new NullPointerException("files cannot be null");
		else{
			this.fileList=Collections.synchronizedList(fileList);
			rendererList=Collections.synchronizedList(new ArrayList<Renderer>());
			milliSecondWait=10000;

			commandList=new ArrayList<Command>();
		}
	}
	
	public SendToModel(File file){
		this (new ArrayList<File>(Arrays.asList(file)));
	}
	
	public List<File> getFileList() {
		return fileList;
	}


	public void setFileList(List<File> fileList) {
		List<File> old=this.fileList;
		this.fileList = fileList;
		PropertyChangeEvent pce=new PropertyChangeEvent(this,"fileList",old,fileList);
		firePropertyChange(pce);
	}


	public void setRendererList(int i, Renderer device) {
		if (!rendererList.contains(device)){
			rendererList.add(i,device);
			PropertyChangeEvent pce=new IndexedPropertyChangeEvent(this, "rendererList", null, device,i);
			firePropertyChange(pce);
		}
	}
	
	public void setRendererList(Renderer device){
		setRendererList(getRendererList().size(),device);
	}
	
	public List<Renderer> getRendererList(){return rendererList;}
	public Renderer getRendererList(int i){return rendererList.get(i);}
	public Object getRendererList(String identifier){
		Object res=null;
		for (int i=0;i<rendererList.size() && res==null;i++){
			if (rendererList.get(i).getIdentifier().equals(identifier)){
				res=rendererList.get(i);
			}
		}
		
		return res;
	}
	
	public void setRenderer(List<Renderer> devices){
		List<Renderer> old=this.rendererList;
		this.rendererList=Collections.synchronizedList(devices);
		firePropertyChange(new PropertyChangeEvent(this,"rendererList",old,rendererList));
	}
	
	public int getMilliSecondElapsed() {
		return milliSecondElapsed;
	}


	public void setMilliSecondElapsed(int milliSecondElapsed) {
		int old=this.milliSecondElapsed;
		this.milliSecondElapsed = milliSecondElapsed;
		firePropertyChange(new PropertyChangeEvent(this,"milliSecondElapsed",old,milliSecondElapsed));
	}


	public int getMilliSecondWait() {
		return milliSecondWait;
	}

	public void setMilliSecondWait(int milliSecondWait) {
		int old=this.milliSecondWait;
		this.milliSecondWait = milliSecondWait;
		firePropertyChange(new PropertyChangeEvent(this,"milliSecondWait",old,milliSecondWait));
	}

	public boolean isFavorite(Renderer r){
		boolean res=false;
		File[] files=Global.FAVORIS.listFiles();
		for (int i=0;i<files.length && !res;i++){
			if (r.getIdentifier().equals(files[i].getName())) res=true;
		}
		return res;
	}

	public void addFavorite(Renderer r){
		saveFavorite(r);
	}
	
	public void removeFavorite(Renderer r){
		deleteFavorite(r);
	}
	
	public void addCommand(final Command command){
		if (!commandList.contains(command)){
			command.getModel().addPropertyChangeListener(new PropertyChangeListener(){

				@Override
				public void propertyChange(PropertyChangeEvent event) {
					if (event.getPropertyName().equals("exit")){
						boolean exit=(Boolean)event.getNewValue();
						if (exit){
							SendToModel.this.removeCommand(command);
							if (SendToModel.this.getCommandNumbers()==0){
								System.exit(0);
							}
						}
					}
				}
				
			});
			commandList.add(command);
		}
	}
	
	public boolean containsCommand(Command command){
		return commandList.contains(command);
	}
	
	public void removeCommand(Command command){
		commandList.remove(command);
	}
	
	public Command getCommand(Renderer device){
		Command res=null;
		for (int i=0; i<commandList.size() && res==null;i++){
			if (commandList.get(i).getModel().getRenderer().equals(device)) res=commandList.get(i);
		}
		
		return res;
	}
	
	public int getCommandNumbers(){
		return commandList.size();
	}
	
	private void saveFavorite(Renderer r){
		
		Properties prop=new Properties();
		prop.setProperty("protocol", r.getProtocol());
		prop.setProperty("identifier", r.getIdentifier());
		prop.setProperty("name", r.getName());
		try {
			File f=new File(Global.FAVORIS,prop.getProperty("identifier"));
			if (!f.exists()) f.createNewFile();
			FileOutputStream out=new FileOutputStream(f);
			prop.store(out, null);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private void deleteFavorite(Renderer r){
		File f=new File(Global.FAVORIS,r.getIdentifier());
		f.delete();
	}
	

	
}

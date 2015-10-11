package druzy.jmita;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import druzy.littleframe.WaitDevice;
import druzy.mvc.AbstractController;
import druzy.mvc.AbstractModel;
import druzy.mvc.AbstractView;
import druzy.mvc.Model;
import druzy.mvc.View;
import druzy.protocol.AbstractRenderer;
import druzy.protocol.Device;
import druzy.protocol.Discoverer;
import druzy.protocol.DiscovererFactory;
import druzy.protocol.DiscoveryListener;
import druzy.protocol.Renderer;


public class SendTo extends AbstractController{

	private boolean stopSearch=false;
	private Thread threadSearch=null;
	
	public SendTo(Model model) {
		super(model);
		this.addView(new SendToView(this));
	}
	
	public SendTo(List<File> files){
		this(new SendToModel(files));
	}
	
	public SendTo(File file){
		this(new SendToModel(file));
	}
	
	@Override
	public SendToModel getModel(){return (SendToModel)super.getModel();}

	@Override
	public void notifyAction(View view, Object[] objects, int type) {
		switch(type){
		case SendToModel.PUSH_CANCEL_BUTTON :
			exit();
			break;
		
		case SendToModel.PUSH_SEND_BUTTON :
			if (objects.length>0) closeViews();
			for (int i=0;i<objects.length;i++){
				if (objects[i] instanceof Renderer){
					Renderer r=(Renderer)objects[i];
					
					
					
					Command command=getModel().getCommand(r);
					if (command==null){
						command=new Command(r,getModel().getFileList());
						getModel().addCommand(command);
					}else{
						command.getModel().addAllToPlaylist(getModel().getFileList());
					}
					command.displayViews();
				}
			
			}
			getModel().setFileList(new ArrayList<File>());
			break;
		
		case SendToModel.PUSH_EXIT :
			exit();
			break;
			
		case SendToModel.CLICK_RENDERER :
			Object o1=objects[0];
			Object o2=objects[1];
			if (o1 instanceof Renderer && o2 instanceof Boolean){
				Renderer r=(Renderer)o1;
				boolean value=(Boolean)o2;
				
				if (value) getModel().addFavorite(r);
				else getModel().removeFavorite(r);
			}
			
		}
			
	}

	public void searchRenderer() {
		List<Discoverer> discoList=DiscovererFactory.getDiscoverers();
		for (Discoverer disco:discoList){
			disco.startDiscovery(getModel().getMilliSecondWait(), new DiscoveryListener(){

				@Override
				public void deviceDiscovery(Device d) {
					if (d instanceof Renderer){
						getModel().setRendererList((Renderer)d);
					}
				}
				
			});
		}
		
		threadSearch=new Thread(){
			@Override
			public void run(){
				stopSearch=false;
				for (int i=0;i<=100 && !stopSearch ;i++){
					try {
						sleep(100);
						getModel().setMilliSecondElapsed(i*100);
					} catch (InterruptedException e) {
						stopSearch=true;
					}
				}
			}
			
		};
		threadSearch.start();
	}
	
	public void stopSearch(){
		stopSearch=true;
	}
	
	public void stopSearchAndWait(){
		stopSearch();
		if (threadSearch!=null){
			try {
				threadSearch.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void restartSearch(){
		stopSearchAndWait();
		searchRenderer();
	}
	
	public void sendTo(final String identifier, final List<File> listFiles){
		final WaitDevice wait=new WaitDevice();
		wait.displayViews();
		
		getModel().setFileList(listFiles);
		
		List<Discoverer> discos=DiscovererFactory.getDiscoverers();
		for (Discoverer disco : discos){
			disco.startDiscovery(getModel().getMilliSecondWait(), identifier, new DiscoveryListener(){

				@Override
				public void deviceDiscovery(Device d) {
					wait.closeViews();
					notifyAction(null,d,SendToModel.PUSH_SEND_BUTTON);
				}
				
			});
		}
		
		new Thread(){
			public void run(){
				try {
					Thread.sleep(getModel().getMilliSecondWait());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (!wait.getViews().get(0).isDisplaying()){
					wait.closeViews();
					
				}
					
				
			}
		}.start();
	}
	
	private void exit(){
		stopSearch=true;
		closeViews();
		System.exit(0);
	}

}

package druzy.jmita;

import java.io.File;
import java.util.List;

import druzy.mvc.AbstractController;
import druzy.mvc.AbstractModel;
import druzy.mvc.View;
import druzy.protocol.Renderer;
import druzy.utils.TimeUtils;


public class Command extends AbstractController{

 	public Command(AbstractModel model) {
		super(model);
		this.addView(new CommandView(this));
	}
	
	public Command(Renderer renderer, List<File> playlist){
		this(new CommandModel(renderer,playlist));
	}
	
	public Command(Renderer renderer, File file){
		this(new CommandModel(renderer,file));
	}
	
	@Override
	public void notifyAction(View view, Object[] objects, int type) {
		
		int volume=getModel().getRenderer().getVolume();
		int more=(int)(getModel().getRenderer().getVolumeMax()/10);
		
		switch (type){
		case CommandModel.PUSH_STOP_BUTTON:
			getModel().getRenderer().stop();
			break;
			
		case CommandModel.PUSH_EXIT:
			exit();
			break;
			
		case CommandModel.PUSH_PAUSE_BUTTON:
			getModel().getRenderer().pause();
			break;
		
		case CommandModel.PUSH_PLAY_BUTTON:
			int index=(Integer)objects[0];
			File f=(File)objects[1];

			if (!getModel().getRenderer().isStop() && f.equals(getModel().getCurrentlyFile())) getModel().getRenderer().play();
			else{
				getModel().setCurrentlyIndex(index);
				getModel().setCurrentlyFile(f);
				if (getModel().getRenderer().send(f)){
					getModel().getRenderer().play();
				}
			}
			break;
			
		case CommandModel.PUSH_REWIND_BUTTON :
			break;
			
		case CommandModel.DISPLAY:
			notifyAction(view,objects,CommandModel.PUSH_PLAY_BUTTON);
			
			break;
		
		case CommandModel.PUSH_VOLUME:
			if (objects[0] instanceof Integer){
				getModel().getRenderer().setVolume((Integer)objects[0]);
			}
			break;
			
		case CommandModel.DOUBLE_CLICK:
			notifyAction(view,objects,CommandModel.PUSH_PLAY_BUTTON);
			break;
			
		case CommandModel.PUSH_VOLUME_DOWN:
			getModel().getRenderer().setVolume(volume-more);
			break;
		
		case CommandModel.PUSH_VOLUME_UP:
			getModel().getRenderer().setVolume(volume+more);
			break;
		
		case CommandModel.CLICK_SLIDER:
			if (objects[0] instanceof Integer){
				getModel().getRenderer().setTimePosition(TimeUtils.secondsToTime((Integer)objects[0]));
			}
			break;
		}

	}
	
	@Override
	public CommandModel getModel(){return (CommandModel)super.getModel();}

	private void exit(){
		
		closeViews();
		
		getModel().getRenderer().shutdown();
		//supprime cette instance de toutes les instances
		getModel().setExit(true);
		
		
		/*getModel().getUpnpService().shutdown();
		RestrictedFileServer.getInstance(Global.PORT).stop();
		JMita2.exit=true;
		System.exit(0);*/
	}

	@Override
	public void addView(View view){
		super.addView(view);
		getModel().getRenderer().addPropertyChangeListener(view);
	}
	
}

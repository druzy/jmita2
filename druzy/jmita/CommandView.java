package druzy.jmita;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.sql.Time;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import com.google.common.io.Files;

import druzy.mvc.AbstractView;
import druzy.mvc.Controller;
import druzy.utils.ComponentUtils;
import druzy.utils.ImageIconUtils;
import druzy.utils.TimeUtils;


public class CommandView extends AbstractView {

	//variables
	private JFrame commandFrame=null;
	private GridBagLayout layout=null;
	private JButton rewind=null;
	private JButton stop=null;
	private JButton play=null;
	private JButton pause=null;
	private JButton exit=null;
	private JPanel playPausePanel=null;
	private JSlider slider=null;
	private JLabel labelMin=null;
	private JLabel labelMax=null;
	private JList<File> playlist=null;
	private DefaultListModel<File> playlistModel=null;
	private JLabel labelVolume=null;
	private JProgressBar volume=null;
	private JButton volumeUp=null;
	private JButton volumeDown=null;
	
	private int dim=44;
	//private int littleDim=22;
	
	@SuppressWarnings("serial")
	public CommandView(Controller controller) {
		super(controller);
		
		try {
			SwingUtilities.invokeAndWait(new Runnable(){
				public void run(){
					try {
						UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					} catch (Exception e) {
						e.printStackTrace();
					}
					//initialisation des composants swing
					layout=new GridBagLayout();
					
					commandFrame=new JFrame();
					commandFrame.setTitle(getController().getModel().getRenderer().getName());
					commandFrame.addWindowListener(new WindowAdapter(){

						@Override
						public void windowClosing(WindowEvent arg0) {
							new Thread(){
								public void run(){
									getController().notifyAction(CommandView.this, null, CommandModel.PUSH_EXIT);
								}
								
							}.start();
						}

					});
					commandFrame.setLayout(layout);
					ComponentUtils.setMaximumSizeToMaximumWindowBounds(commandFrame);
					ImageIcon ii=getController().getModel().getRenderer().getIcon();
					if (ii!=null) commandFrame.setIconImage(ii.getImage());
					
					rewind=new JButton(ImageIconUtils.resize(new ImageIcon(ClassLoader.getSystemResource(Global.MEDIA_REWIND_OUTLINE)), dim));
					
					stop=new JButton(ImageIconUtils.resize(new ImageIcon(ClassLoader.getSystemResource(Global.MEDIA_STOP_OUTLINE)), dim));
					stop.addActionListener(new ActionListener(){

						@Override
						public void actionPerformed(ActionEvent event) {
							new Thread(){
								public void run(){
									getController().notifyAction(CommandView.this, null, CommandModel.PUSH_STOP_BUTTON);
								}
							}.start();
						}
						
					});
					
					play=new JButton(ImageIconUtils.resize(new ImageIcon(ClassLoader.getSystemResource(Global.MEDIA_PLAY_OUTLINE)), dim));
					play.addActionListener(new ActionListener(){

						@Override
						public void actionPerformed(ActionEvent event) {
							new Thread(){
								public void run(){
									int index=playlist.getSelectedIndex();
									if (index>=0){
										File f=playlist.getSelectedValue();
										getController().notifyAction(CommandView.this,new Object[]{index,f},CommandModel.PUSH_PLAY_BUTTON);
									}
								}
							}.start();
						}
						
					});
					
					pause=new JButton(ImageIconUtils.resize(new ImageIcon(ClassLoader.getSystemResource(Global.MEDIA_PAUSE_OUTLINE)), dim));
					pause.addActionListener(new ActionListener(){
						
						@Override
						public void actionPerformed(ActionEvent event) {
							new Thread(){
								public void run(){
									getController().notifyAction(CommandView.this,null,CommandModel.PUSH_PAUSE_BUTTON);
								}
							}.start();
						}
					});
					
					exit=new JButton(ImageIconUtils.resize(new ImageIcon(ClassLoader.getSystemResource(Global.POWER_OUTLINE)), dim));
					exit.addActionListener(new ActionListener(){
						
						 @Override
						 public void actionPerformed(ActionEvent event){
							 new Thread(){
								 public void run(){
									 getController().notifyAction(CommandView.this, null, CommandModel.PUSH_EXIT);
								 }
							 }.start();
						 }
					});
					
					playPausePanel=new JPanel(new GridBagLayout());
					
					playlistModel=new DefaultListModel<File>();
					for (File f: getController().getModel().getPlaylist()) playlistModel.addElement(f);
					
					playlist=new JList<File>(playlistModel);
					playlist.setCellRenderer(new DefaultListCellRenderer(){
						
						@Override
						public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus){
							JLabel ret=(JLabel)super.getListCellRendererComponent(list, null, index, isSelected, cellHasFocus);
							if (index==getController().getModel().getCurrentlyIndex()){
								if (getController().getModel().getRenderer().isPlay()){
									ret.setIcon(ImageIconUtils.resize(new ImageIcon(ClassLoader.getSystemResource(Global.MEDIA_PLAY_OUTLINE)), dim));
								}else if (getController().getModel().getRenderer().isPause()){
									ret.setIcon(ImageIconUtils.resize(new ImageIcon(ClassLoader.getSystemResource(Global.MEDIA_PAUSE_OUTLINE)), dim));
								}
							}
							ret.setText(Files.getNameWithoutExtension(((File)value).getName()));
							return ret;
						}
					});
					playlist.setSelectedIndex(0);
					playlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
					playlist.setFont(new Font(playlist.getFont().getName(),playlist.getFont().getStyle(),dim));
					playlist.addMouseListener(new MouseAdapter(){
						
						@Override
						public void mouseClicked(MouseEvent event){
							if (event.getClickCount()==2){
								final int index=playlist.locationToIndex(event.getPoint());
								final File f=playlistModel.elementAt(index);
								new Thread(){
									public void run(){
										CommandView.this.getController().notifyAction(CommandView.this,new Object[]{index,f}, CommandModel.DOUBLE_CLICK);
									}
								}.start();
							}
						}
						
					});
					
					slider=new JSlider();
					slider.setMinimum(0);
					slider.setMaximum(TimeUtils.timeToSeconds(getController().getModel().getRenderer().getDuration()));
					slider.setValue(0);
					slider.setPaintLabels(false);
					slider.addMouseListener(new MouseAdapter(){
						@Override
						public void mouseReleased(MouseEvent event){
							
							new Thread(){
								public void run(){
									getController().notifyAction(CommandView.this, slider.getValue(), CommandModel.CLICK_SLIDER);
								}
							}.start();
						}
					});
					
					labelMin=new JLabel();
					Time min=getController().getModel().getRenderer().getTimePosition();
					
					if (min!=null) labelMin.setText(min.toString());
					
					labelMax=new JLabel();
					Time max=getController().getModel().getRenderer().getDuration();
					if (max!=null) labelMax.setText(max.toString());
						
					labelVolume=new JLabel(ImageIconUtils.resize(new ImageIcon(ClassLoader.getSystemResource(Global.VOLUME)), dim));
					
					volume=new JProgressBar(0,getController().getModel().getRenderer().getVolumeMax());
					volume.addMouseListener(new MouseAdapter(){
						public void mouseClicked(MouseEvent event){
							int mouseX=event.getX();
							final int value=(int)Math.round(((double)mouseX / (double)volume.getWidth()) * volume.getMaximum());
							new Thread(){
								public void run(){
									getController().notifyAction(CommandView.this, new Object[]{value}, CommandModel.PUSH_VOLUME);
								}
							}.start();
							
						}
					});
					
					volumeUp=new JButton(ImageIconUtils.resize(new ImageIcon(ClassLoader.getSystemResource(Global.VOLUME_UP)), dim));
					volumeUp.addActionListener(new ActionListener(){

						@Override
						public void actionPerformed(ActionEvent event) {
							new Thread(){
								public void run(){
									getController().notifyAction(CommandView.this, null, CommandModel.PUSH_VOLUME_UP);
								}
							}.start();
						}
					});
					
					volumeDown=new JButton(ImageIconUtils.resize(new ImageIcon(ClassLoader.getSystemResource(Global.VOLUME_DOWN)),dim));
					volumeDown.addActionListener(new ActionListener(){

						@Override
						public void actionPerformed(ActionEvent event) {
							new Thread(){
								public void run(){
									getController().notifyAction(CommandView.this, null, CommandModel.PUSH_VOLUME_DOWN);
								}
							}.start();
						}
						
					});
					
					//ajout des composants les uns les autres
					GridBagConstraints c=new GridBagConstraints();
					c.insets=new Insets(5,5,5,5);
					c.anchor=GridBagConstraints.LINE_START;
					
					c.gridx=0;
					c.gridy=0;
					commandFrame.add(rewind,c);
					
					c.gridx++;
					commandFrame.add(stop,c);
					
					c.gridx++;
					c.gridwidth=GridBagConstraints.RELATIVE;
					c.weightx=1;
					commandFrame.add(playPausePanel,c);
					playPausePanel.add(pause);
					c.gridwidth=1;
					c.weightx=0;
					
					c.gridx++;
					c.gridwidth=GridBagConstraints.REMAINDER;
					commandFrame.add(exit,c);
					c.gridwidth=1;
					
					c.gridx=0;
					c.gridy=1;
					c.gridwidth=GridBagConstraints.REMAINDER;
					c.fill=GridBagConstraints.BOTH;
					c.weightx=1;
					c.weighty=1;
					commandFrame.add(new JScrollPane(playlist),c);
					c.gridwidth=1;
					c.fill=GridBagConstraints.NONE;
					c.weightx=0;
					c.weighty=0;
					
					c.gridx=0;
					c.gridy=2;
					commandFrame.add(labelMin,c);
				
					c.gridx++;
					c.gridwidth=GridBagConstraints.RELATIVE;
					c.fill=GridBagConstraints.HORIZONTAL;
					c.weightx=1;
					commandFrame.add(slider,c);
					c.fill=GridBagConstraints.NONE;
					c.weightx=0;
					c.gridwidth=1;
					
					c.gridx++;
					c.gridwidth=GridBagConstraints.REMAINDER;
					c.anchor=GridBagConstraints.LINE_END;
					commandFrame.add(labelMax,c);
					c.gridwidth=1;
					c.anchor=GridBagConstraints.LINE_START;
					
					c.gridy=3;
					c.gridx=0;
					commandFrame.add(labelVolume,c);
					
					c.gridx++;
					commandFrame.add(volumeDown,c);
					
					c.gridx++;
					c.fill=GridBagConstraints.HORIZONTAL;
					c.gridwidth=GridBagConstraints.RELATIVE;
					c.weightx=1;
					commandFrame.add(volume,c);
					c.fill=GridBagConstraints.NONE;
					c.weightx=0;
					c.gridwidth=1;
					
					c.gridx++;
					c.gridwidth=GridBagConstraints.REMAINDER;
					commandFrame.add(volumeUp,c);
					c.gridwidth=1;
					
					commandFrame.pack();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	@Override
	public void close() {
		commandFrame.setVisible(false);
	}

	@Override
	public void display() {
		commandFrame.setVisible(true);
		final File f=playlistModel.firstElement();
		new Thread(){
			public void run(){
				getController().notifyAction(CommandView.this, new Object[]{0,f}, CommandModel.DISPLAY);
			}
			
		}.start();
	}
	
	@Override
	public void onTop(){
		display();
		commandFrame.setEnabled(true);
		commandFrame.toFront();
		
	}

	@Override 
	public Command getController(){return (Command)super.getController();}
	
	@Override
	public void propertyChange(final PropertyChangeEvent pce) {
		switch(pce.getPropertyName()){
			case "pause" :
				if (pce.getNewValue().equals(true)){
					SwingUtilities.invokeLater(new Runnable(){
						public void run(){
							upToFront(play, playPausePanel);
							playlist.repaint();
						}
					});
				}
				break;
		
			case "play" :
				if (pce.getNewValue().equals(true)){
					SwingUtilities.invokeLater(new Runnable(){
						public void run(){
							upToFront(pause,playPausePanel);
							stop.setEnabled(true);
							playlist.repaint();
							commandFrame.pack();
						}
					});
				}
				break;
				
			case "timePosition" :
				if (pce.getNewValue() instanceof Time){
					final Time t=(Time)pce.getNewValue();
					SwingUtilities.invokeLater(new Runnable(){
						public void run(){
							labelMin.setText(t.toString());
							slider.getModel().setValue(TimeUtils.timeToSeconds(t));
							//commandFrame.pack();
						}
					});
				}
				break;
				
			case "duration" :
				if (pce.getNewValue() instanceof Time){
					final Time t=(Time)pce.getNewValue();
					SwingUtilities.invokeLater(new Runnable(){
						public void run(){
							labelMax.setText(t.toString());
							slider.getModel().setMaximum(TimeUtils.timeToSeconds(t));
							//commandFrame.pack();
						}
					});
				}
				
			case "playlist" :
				if (pce.getNewValue() instanceof List){
					SwingUtilities.invokeLater(new Runnable(){
						
						@Override
						public void run(){
							@SuppressWarnings("unchecked")
							List<File> files=(List<File>)pce.getNewValue();
							playlistModel.removeAllElements();
							for (File f:files) playlistModel.addElement(f);
							commandFrame.pack();
						}
					});
				}
				break;
				
			case "addAllToPlaylist" :
				if (pce.getNewValue() instanceof List){
					SwingUtilities.invokeLater(new Runnable(){

						@Override
						public void run() {
							if (pce.getNewValue() instanceof List){
								@SuppressWarnings("unchecked")
								List<File> files=(List<File>)pce.getNewValue();
								for (File f:files) playlistModel.addElement(f);
								commandFrame.pack();
							}
						}
						
					});
				}
				break;
				
			case "stop" : 
				if (pce.getNewValue() instanceof Boolean){
					SwingUtilities.invokeLater(new Runnable(){
						public void run(){
							if ((Boolean)pce.getNewValue()){
								stop.setEnabled(false);
								upToFront(play,playPausePanel);
								playlist.repaint();
								commandFrame.pack();
							}
						}
					});
				}
				break;
				
			case "volume" :
				if (pce.getNewValue() instanceof Integer){
					SwingUtilities.invokeLater(new Runnable(){
						public void run(){
							volume.setValue((Integer)pce.getNewValue());
						}
					});
					
				}
				break;
				
			case "currentlyIndex":
				if (pce.getNewValue() instanceof Integer){
					SwingUtilities.invokeLater(new Runnable(){
						public void run(){
							playlist.repaint();
						}
					});
				}
				break;
				
			case "mute":
				if (pce.getNewValue() instanceof Boolean){
					SwingUtilities.invokeLater(new Runnable(){
						public void run(){
							boolean b=(Boolean)pce.getNewValue();
							if (!b){
								
							}
						}
					});
				}
		}
	}

	@Override
	public boolean isDisplaying() {
		// TODO Auto-generated method stub
		return false;
	}

	private void upToFront(JButton button, JPanel panel){
		panel.removeAll();
		panel.add(button);
		panel.revalidate();
		panel.repaint();
	}

}

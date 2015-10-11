package druzy.jmita;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.IndexedPropertyChangeEvent;
import java.beans.PropertyChangeEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import druzy.mvc.AbstractView;
import druzy.mvc.Controller;
import druzy.protocol.Renderer;
import druzy.utils.ImageIconUtils;


public class SendToView extends AbstractView {
	
	//variables
	private JFrame mainFrame=null;
	private GridBagLayout layout=null;
	private JList<Renderer> renderers=null;
	private DefaultListModel<Renderer> listModel=null;
	private JButton cancel=null;
	private JButton send=null;
	private JProgressBar searchBar=null;
	private JPopupMenu rightClick=null;
	private JMenuItem addFavorite=null;
	private JMenuItem removeFavorite=null;
	private int dimIcon=44;
	
	private boolean displaying=false;
	
	public SendToView(Controller controller) {
		super(controller);
		
		try {
			SwingUtilities.invokeAndWait(new Runnable(){
				@SuppressWarnings("serial")
				public void run(){
					try {
						UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
						UIManager.put("Slider.paintValue", false);
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					//initialistation des composants
					mainFrame=new JFrame("Recherche des Renderer sur le réseau");
					mainFrame.addWindowListener(new WindowAdapter(){

						@Override
						public void windowClosing(WindowEvent arg0) {
							new Thread(){
								public void run(){
									getController().notifyAction(SendToView.this, null, SendToModel.PUSH_EXIT);
								}
								
							}.start();
						}

					});
					mainFrame.setIconImage(new ImageIcon(ClassLoader.getSystemResource(Global.MAIN_ICON)).getImage());
					
					layout=new GridBagLayout();
					
					listModel=new DefaultListModel<Renderer>();
					
					renderers=new JList<Renderer>(listModel);
					renderers.setCellRenderer(new DefaultListCellRenderer(){
						
						@Override
						public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus){
							final Renderer r=(Renderer)value;
							
							GridLayout layout=new GridLayout(1,2);

							JPanel panel=new JPanel();
							panel.setLayout(layout);
							
							JLabel label=(JLabel)super.getListCellRendererComponent(list,null, index, isSelected, cellHasFocus);
							
							//icone
							label.setIcon(ImageIconUtils.resize(r.getIcon(),dimIcon));
							//texte
							String texte=r.getName()+" via "+r.getProtocol();
							if (getController().getModel().isFavorite(r)) texte=texte+" (favoris)";
							label.setText(texte);
							
							//favoris
							
							//ajout des composants les uns les autres
							panel.add(label);
							
							return panel;
						}
					});
					renderers.addMouseListener(new MouseAdapter(){
						@Override
						public void mouseClicked(MouseEvent event){
							if (event.getButton()==MouseEvent.BUTTON3){
								rightClick=new JPopupMenu();
								Renderer r=listModel.get(renderers.locationToIndex(event.getPoint()));
								if (getController().getModel().isFavorite(r)){
									rightClick.add(removeFavorite);
								}else{
									rightClick.add(addFavorite);
								}
								rightClick.show(renderers, event.getX(), event.getY());
							}
						}
					});
					
					cancel=new JButton("Annuler");
					cancel.addActionListener(new ActionListener(){
						@Override
						public void actionPerformed(ActionEvent ae) {
							new Thread(){
								public void run(){
									getController().notifyAction(SendToView.this, null, SendToModel.PUSH_CANCEL_BUTTON);
								}
							}.start();
						}
						
					});
					
					send=new JButton("Envoyer");
					send.addActionListener(new ActionListener(){

						@Override
						public void actionPerformed(ActionEvent event) {
							new Thread(){
								public void run(){
									List<Renderer> devices=renderers.getSelectedValuesList();
									if (devices.size()>0){
										getController().notifyAction(SendToView.this, devices.toArray(), SendToModel.PUSH_SEND_BUTTON);
									}
									
									
								}
							}.start();
						}
						
					});
					
					searchBar=new JProgressBar(SwingConstants.HORIZONTAL);
					searchBar.setMinimum(0);
					searchBar.setMaximum(getController().getModel().getMilliSecondWait());
					
					rightClick=new JPopupMenu();
					
					addFavorite=new JMenuItem("Ajouter aux favoris");
					addFavorite.addActionListener(new ActionListener(){
						@Override
						public void actionPerformed(ActionEvent event){
							new Thread(){
								public void run(){
									getController().notifyAction(SendToView.this, new Object[]{listModel.get(renderers.locationToIndex(rightClick.getLocation())),true} , SendToModel.CLICK_RENDERER);
									SwingUtilities.invokeLater(new Runnable(){
										public void run(){
											renderers.repaint();
										}
									});
								}
							}.start();
						}
					});
					
					removeFavorite=new JMenuItem("Supprimer des favoris");
					removeFavorite.addActionListener(new ActionListener(){
						@Override
						public void actionPerformed(ActionEvent event){
							new Thread(){
								public void run(){
									getController().notifyAction(SendToView.this, new Object[]{listModel.get(renderers.locationToIndex(rightClick.getLocation())),false} , SendToModel.CLICK_RENDERER);
									SwingUtilities.invokeLater(new Runnable(){
										public void run(){
											renderers.repaint();
										}
									});
								}
							}.start();
						}
					});
					
					//ajout des composant les uns les autres
					mainFrame.setLayout(layout);
					
					GridBagConstraints c=new GridBagConstraints();
					c.gridx=0;
					c.gridy=0;
					c.gridwidth=GridBagConstraints.REMAINDER;
					c.fill=GridBagConstraints.BOTH;
					c.weightx=1.;
					c.weighty=1.;
					mainFrame.getContentPane().add(renderers,c);
					
					c.gridx=0;
					c.gridy=1;
					c.gridwidth=1;
					c.fill=GridBagConstraints.NONE;
					mainFrame.getContentPane().add(cancel,c);
					
					c.gridx=1;
					c.gridy=1;
					mainFrame.getContentPane().add(send,c);
					
					c.gridx=0;
					c.gridy=2;
					c.fill=GridBagConstraints.HORIZONTAL;
					c.gridwidth=GridBagConstraints.REMAINDER;
					mainFrame.getContentPane().add(searchBar,c);
					
					mainFrame.pack();
				}
			});
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
				
	@Override
	public void close() {
		mainFrame.setVisible(false);
	}

	@Override
	public void display() {
		mainFrame.setVisible(true);
		displaying=true;
	}						
		
	@Override
	public void propertyChange(final PropertyChangeEvent pce) {
		String propertyName=pce.getPropertyName();
		if (propertyName.equals("rendererList")){
			if (pce instanceof IndexedPropertyChangeEvent){
				if (pce.getNewValue() !=null){ //device ajouter
					final int index=((IndexedPropertyChangeEvent)pce).getIndex();
					SwingUtilities.invokeLater(new Runnable(){
						public void run(){
							listModel.add(index, (Renderer)pce.getNewValue());
							mainFrame.pack();
						}
					});
				}
			}else{
				if (pce.getNewValue() instanceof List){
					SwingUtilities.invokeLater(new Runnable(){
						@SuppressWarnings("unchecked")
						public void run(){
							List<Renderer> list=(List<Renderer>) pce.getNewValue();
							
							listModel.removeAllElements();
							
							for (int i=0;i<list.size();i++){
								listModel.addElement(list.get(i));
							}
							mainFrame.pack();
						}
					});
				}
			}
		}else if (propertyName.equals("milliSecondElapsed")){
				SwingUtilities.invokeLater(new Runnable(){
			
					public void run(){
						searchBar.setValue((Integer)pce.getNewValue());
					}
				});
		}		
	}
	
	@Override
	public SendTo getController(){return (SendTo)super.getController();}

	@Override
	public void onTop(){
		display();
		mainFrame.setEnabled(true);
		mainFrame.toFront();
		
	}
	
	@Override
	public boolean isDisplaying() {
		return displaying;
	}

}

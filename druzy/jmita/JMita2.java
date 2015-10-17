package druzy.jmita;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import javax.swing.JFileChooser;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import druzy.littleframe.AskFiles;
import druzy.protocol.DiscovererFactory;

public class JMita2 {

	static boolean exit=false;
	static int port=6666;
	static private SendTo sendTo=null;
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(final String[] args) {
		
		final CommandLineOption opt=new CommandLineOption();
		CmdLineParser parser=new CmdLineParser(opt);
		
		try{
			parser.parseArgument(args);
			
			try {
				//première instance de JMita2
				@SuppressWarnings("resource")
				final ServerSocket server=new ServerSocket(port);
				//thread du serveur local
				new Thread(){
					public void run(){
						try {
							while (true){
								Socket socket=server.accept();
								ObjectInputStream in=new ObjectInputStream(socket.getInputStream());
								try {
									String[] arguments=(String[])in.readObject();
									argsTraitement(arguments);
								} catch (ClassNotFoundException e) {
									e.printStackTrace();
									System.exit(1);
								}
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}.start();
				
				argsTraitement(args);
			} catch (IOException e) {
				System.out.println("une instance de jmita est déjà ouverte");
				try {
					Socket socket=new Socket(InetAddress.getLocalHost(),port);
					ObjectOutputStream out=new ObjectOutputStream(socket.getOutputStream());
					
					out.writeObject(args);
					out.flush();
					out.close();
					socket.close();
					System.exit(0);
				} catch (Exception e1) {
					e1.printStackTrace();
					System.exit(1);
				}
			}
		}catch(CmdLineException e){
			parser.printUsage(System.out);
		}
	}
	
	static private void argsTraitement(String[] args){
		final CommandLineOption opt=new CommandLineOption();
		CmdLineParser parser=new CmdLineParser(opt);
		try {
			parser.parseArgument(args);
			
			if (opt.isQuit()){
				System.exit(0);
			}else if(opt.isAbout()){
				if (sendTo==null) System.exit(0);
			}else{
				if (opt.getFiles()==null || opt.getFiles().size()==0){
					AskFiles ask=new AskFiles(new VideoFilter());
					ask.displayViews();
					List<File> choosen=ask.getModel().getChoosenFiles();
					if (choosen.size()==0) System.exit(0);
					else opt.setFiles(ask.getModel().getChoosenFiles());
				}
				
				if (sendTo==null){
					sendTo=new SendTo(opt.getFiles());
				}
				
				if (opt.getDevice()==null){ //si aucun device n'est demandé
					if (sendTo.getViews().get(0).isDisplaying()){
						sendTo.getModel().getFileList().addAll(opt.getFiles());
					}else{
						sendTo.getModel().setFileList(opt.getFiles());
					}
					sendTo.viewsOnTop();
					sendTo.searchRenderer();
					
				}else{
					//trouver le device
					sendTo.sendTo(opt.getDevice(), opt.getFiles());
				}
			}			
		} catch (CmdLineException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

}

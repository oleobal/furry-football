package footstats.view;
import footstats.model.*;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;

public class MyFrame extends JFrame
{
	JPanel threeDpanel, controlPanel, settingsPane, trianglePane, sliderPane, sliderButtonsPane;
	JButton buttonPlay, buttonFastForward, buttonSlowDown, buttonLoad, buttonHelp;
	/**
	 * whether paths are drawn or not
	 */
	JCheckBox checkBoxPath;
	JSlider sliderProgress;
	JLabel labelSpeed;
	
	FieldView threeDview;
	Canvas    canvas;

	public MyFrame(String title)
	{
		super(title);
		this.setPreferredSize(new Dimension(800, 625));
		this.setResizable(true);
		this.setLocation(200,200);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel contentPane = new JPanel(new BorderLayout());
		trianglePane  = new JPanel();
		trianglePane.setLayout(new BoxLayout(trianglePane, BoxLayout.X_AXIS));
		sliderPane    = new JPanel(new BorderLayout());
		
		contentPane.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		
		threeDpanel = new JPanel();
		threeDpanel.setBackground(Color.BLACK);
		controlPanel = new JPanel();
		controlPanel.setBorder(BorderFactory.createEmptyBorder(10,0,0,0));
		controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
		
		settingsPane = new JPanel();
		//settingsPane.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		settingsPane.setBorder(null);
		settingsPane.setPreferredSize(new Dimension(0,75)); //on dirait pas mais ça marche en fait TODO: gros bricolage
		
		checkBoxPath = new JCheckBox("Paths");
		checkBoxPath.addItemListener(new ItemListener(){ //that's what is in the javadoc, who am I to contradict Sun ?
			public void itemStateChanged(ItemEvent e)
			{
				threeDview.togglePaths();
			}
			
		});
		settingsPane.add(checkBoxPath);
		
		
		buttonPlay = new JButton(">");
		buttonPlay.setFont(new Font("Arial", Font.PLAIN, 40));
		buttonPlay.setPreferredSize(new Dimension(70,50));
		
		buttonFastForward = new JButton("faster");
		buttonFastForward.setFont(new Font("Arial", Font.PLAIN, 20));
		
		buttonSlowDown = new JButton("slower");
		buttonSlowDown.setFont(buttonFastForward.getFont());
		
		labelSpeed = new JLabel("1x");
		labelSpeed.setFont(buttonPlay.getFont());
		labelSpeed.setPreferredSize(new Dimension(120,75));
		
		buttonHelp = new JButton("HELP");
		buttonHelp.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				JOptionPane.showMessageDialog(MyFrame.this, "Use the LOAD button to select a file. They're in the data/ folder.\n\nOnce a file is being played:\nBlue icons are players, red icons means a player who's been lost.\n\nYou can use the Play Faster/Slower buttons and the progress bar at the bottom to play out the match as you like.\n\nYou can hold down the left mouse button to drag the camera around the field.", "Help", JOptionPane.INFORMATION_MESSAGE);
				
			}
		});
		
		buttonLoad = new JButton("LOAD");
		buttonLoad.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				// Open file chooser to current directory
				JFileChooser fc = new JFileChooser(".");
				int returnVal = fc.showOpenDialog(MyFrame.this);

		        if (returnVal == JFileChooser.APPROVE_OPTION)
		        {
		        	File gameFile = fc.getSelectedFile();
		        	try
		        	{
		            	threeDview.setGame(new Game(gameFile.getPath()));
			        } catch (FileNotFoundException e) {
						System.err.println("Couldn't find file " + gameFile.getName());
						JOptionPane.showMessageDialog(MyFrame.this,
							    "Couldn't find file " + gameFile.getName(),
							    "Error",
							    JOptionPane.ERROR_MESSAGE);
					} catch (IOException e) {
						System.err.println(e.getMessage());
						JOptionPane.showMessageDialog(MyFrame.this,
							    e.getMessage(),
							    "Error",
							    JOptionPane.ERROR_MESSAGE);
					} catch (ParseException e) {
						System.err.println(e.getMessage());
						JOptionPane.showMessageDialog(MyFrame.this,
							    "Unsupported file format for file " + gameFile.getName(),
							    "Error",
							    JOptionPane.ERROR_MESSAGE);
					}
		            buttonPlay.setText(">");
		            enableButtons();
		        }
			}
		});
		
		
		sliderProgress = new JSlider(0,2);
		
		
		sliderButtonsPane = new JPanel();
		sliderButtonsPane.add(buttonHelp);
		sliderButtonsPane.add(buttonLoad);
		sliderPane.add(sliderButtonsPane, BorderLayout.WEST);
		sliderPane.add(sliderProgress, BorderLayout.CENTER);
		
		controlPanel.add(trianglePane);
		controlPanel.add(sliderPane);
		threeDpanel.setPreferredSize(new Dimension(500,500));
		//trianglePane.add(new JLabel(){{this.setPreferredSize(new Dimension(120,75));}}); //padding (yes, Box.createRigidArea)
		trianglePane.add(settingsPane);
		trianglePane.add(Box.createHorizontalGlue());
		trianglePane.add(buttonSlowDown);
		trianglePane.add(buttonPlay);
		trianglePane.add(buttonFastForward);
		trianglePane.add(Box.createHorizontalGlue());
		trianglePane.add(labelSpeed);
		
		contentPane.add(controlPanel, BorderLayout.SOUTH);
		
		
		AppSettings settings = new AppSettings(true);
		settings.setSamples(8);
		
		threeDview = new FieldView();
		
		
		threeDview.setSettings(settings);
		threeDview.setDisplayStatView(false);
		threeDview.setDisplayFps(false);
		threeDview.setShowSettings(false);
		
		threeDview.createCanvas(); // create canvas!
		threeDview.setPauseOnLostFocus(false);
		
		
		JmeCanvasContext ctx = (JmeCanvasContext) threeDview.getContext();
		canvas = ctx.getCanvas();
		
		contentPane.add(canvas, BorderLayout.CENTER);
		
		threeDview.giveFrame(this);
		
		this.setContentPane(contentPane);
		this.pack();
		this.setVisible(true);
	}

	
	/**
	 * enables the buttons and all so that you can't click before you've loaded something
	 */
	private void enableButtons()
	{
		
		buttonPlay.addActionListener(new ActionListener()
		{
				public void actionPerformed(ActionEvent e)
				{
					threeDview.playPause();
					if(buttonPlay.getText().equals("II"))
					{
						buttonPlay.setText(">");
					}
					else
					{
						buttonPlay.setText("II");
					}
				}
		});
		
		
		
		
		
		buttonFastForward.addActionListener(new ActionListener()
		{	
			public void actionPerformed(ActionEvent e)
			{
				if (threeDview.playbackRate>3)
				{
					threeDview.playbackRate*=0.5;
				}
				if ((int)(50.0/(float)threeDview.playbackRate) == 0)
					labelSpeed.setText(new DecimalFormat("#.##").format((50.0/(float)threeDview.playbackRate))+"x");
				else
					labelSpeed.setText((int)(50.0/(float)threeDview.playbackRate)+"x");
				//System.err.println(threeDview.playbackRate);
			}
		});
		
		
		
		
		
		buttonSlowDown.addActionListener(new ActionListener()
		{	
			public void actionPerformed(ActionEvent e)
			{
				if (threeDview.playbackRate>1 && threeDview.playbackRate<800)
				{
					
					if (threeDview.playbackRate==12) //ahem
						threeDview.playbackRate=25;
					else
						threeDview.playbackRate*=2;
				}
				else if (threeDview.playbackRate == 1)
				{
					threeDview.playbackRate=3;
				}
				if ((int)(50.0/(float)threeDview.playbackRate) == 0)
					labelSpeed.setText(new DecimalFormat("#.##").format((50.0/(float)threeDview.playbackRate))+"x");
				else
					labelSpeed.setText((int)(50.0/(float)threeDview.playbackRate)+"x");
				//System.err.println(threeDview.playbackRate);
			}
		});
		
		
		
		
		
		
		
		
		
		
		
		sliderProgress.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				threeDview.setTime(sliderProgress.getValue());			
			} 
		});
		
		
		
	}

}

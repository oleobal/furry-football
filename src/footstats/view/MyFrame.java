package footstats.view;
import footstats.model.*;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;

public class MyFrame extends JFrame
{
	JPanel threeDpanel, controlPanel, settingsPane, settingsFollowPane, trianglePane, sliderPane, sliderButtonsPane;
	JButton buttonPlay, buttonFastForward, buttonSlowDown, buttonLoad, buttonHelp, buttonNextPlayer, buttonPreviousPlayer;
	/**
	 * whether paths are drawn or not
	 */
	JCheckBox checkBoxPath, checkBoxHeat;
	JSlider sliderProgress;
	JLabel labelSpeed, labelPlayer;
	
	FieldView threeDview;
	Canvas    canvas;

	boolean buttonsEnabled;
	
	public MyFrame(String title)
	{
		super(title);
		this.setPreferredSize(new Dimension(800, 625));
		this.setMinimumSize(new Dimension(550, 300));
		this.setResizable(true);
		this.setLocation(200,200);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		buttonsEnabled=false;
		
		JPanel contentPane = new JPanel(new BorderLayout());
		trianglePane  = new JPanel();
		trianglePane.setLayout(new BoxLayout(trianglePane, BoxLayout.X_AXIS));
		sliderPane    = new JPanel(new BorderLayout());
		
		contentPane.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		
		// 3D view of the field
		threeDpanel = new JPanel();
		threeDpanel.setBackground(Color.BLACK);
		
		controlPanel = new JPanel();
		controlPanel.setBorder(BorderFactory.createEmptyBorder(10,0,0,0));
		controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
		
		settingsPane = new JPanel();
		settingsPane.setLayout(new BoxLayout(settingsPane, BoxLayout.Y_AXIS));
		
		// Checkbox for toggling path drawing
		checkBoxPath = new JCheckBox("Paths");
		checkBoxPath.setEnabled(false);
		checkBoxPath.addItemListener(new ItemListener(){ //that's what is in the javadoc, who am I to contradict Sun ?
			public void itemStateChanged(ItemEvent e)
			{
				threeDview.togglePaths();
			}
			
		});
		
		checkBoxHeat = new JCheckBox("Player heatmap");
		checkBoxHeat.setEnabled(false);
		checkBoxHeat.addItemListener(new ItemListener(){ //that's what is in the javadoc, who am I to contradict Sun ?
			public void itemStateChanged(ItemEvent e)
			{
				if (checkBoxHeat.isSelected())
				{
					if (labelPlayer.getText().equals("#"))
					{
						//threeDview.showHeatmap(0);
					}
					else
					{
						threeDview.showHeatmap(Integer.parseInt(labelPlayer.getText()));
					}
				}
				else
				{
					threeDview.showHeatmap(0);
				}
			}
			
		});
		
		// Camera focus settings
		buttonPreviousPlayer = new JButton("<");
		buttonPreviousPlayer.setEnabled(false);
		buttonPreviousPlayer.setFont(new Font("Arial", Font.PLAIN, 10));
		buttonPreviousPlayer.setPreferredSize(new Dimension(20,20));
		buttonPreviousPlayer.setMargin(new Insets(0,0,0,0));
		buttonNextPlayer = new JButton(">");
		buttonNextPlayer.setEnabled(false);
		buttonNextPlayer.setFont(new Font("Arial", Font.PLAIN, 10));
		buttonNextPlayer.setPreferredSize(new Dimension(20,20));
		buttonNextPlayer.setMargin(new Insets(0,0,0,0));
		labelPlayer = new JLabel("#");
	
		
		settingsFollowPane = new JPanel();
		settingsFollowPane.add(new JLabel("Camera:"));
		settingsFollowPane.add(buttonPreviousPlayer);
		settingsFollowPane.add(labelPlayer);
		settingsFollowPane.add(buttonNextPlayer);
		
		settingsPane.add(checkBoxPath);
		settingsPane.add(checkBoxHeat);
		settingsPane.add(settingsFollowPane);
		
		// Main controls (play, faster, slower)
		buttonPlay = new JButton(">");
		buttonPlay.setEnabled(false);
		buttonPlay.setFont(new Font("Arial", Font.PLAIN, 40));
		buttonPlay.setPreferredSize(new Dimension(70,50));
		
		buttonFastForward = new JButton("faster");
		buttonFastForward.setEnabled(false);
		buttonFastForward.setFont(new Font("Arial", Font.PLAIN, 20));
		
		buttonSlowDown = new JButton("slower");
		buttonSlowDown.setEnabled(false);
		buttonSlowDown.setFont(buttonFastForward.getFont());
		
		// Label displaying current speed
		JPanel speedPane = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		labelSpeed = new JLabel("1x");
		labelSpeed.setFont(buttonPlay.getFont());
		speedPane.add(labelSpeed);
		speedPane.setPreferredSize(settingsPane.getPreferredSize());
		
		// Lower controls (load button, slider)
		buttonHelp = new JButton("HELP");
		buttonHelp.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				JOptionPane.showMessageDialog(MyFrame.this, "Use the LOAD button to select a file.\n\nOnce a file is being played:\n\nBlue icons are players, red icons means a player who's been lost.\n\nYou can use the Play Faster/Slower buttons and the progress bar at the bottom to manage speed.\n\nYou can use the \"Show paths\" toggle to draw the paths players take.\n\nYou can use \"Camera\" to select a player to follow.\n\nYou can hold down the left mouse button to drag the camera around the field.", "Help", JOptionPane.INFORMATION_MESSAGE);
				
			}
		});
		
		buttonLoad = new JButton("LOAD");
		buttonLoad.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				
				// Give user loading feedback
				buttonPlay.setText("Loading...");
				buttonPlay.setPreferredSize(null);
				
				// Open file chooser to current directory
				JFileChooser fc = new JFileChooser("./data");
				int returnVal = fc.showOpenDialog(MyFrame.this);

		        if (returnVal == JFileChooser.APPROVE_OPTION)
		        {
		        	
		        	File gameFile = fc.getSelectedFile();
		        	try
		        	{
		        		
		            	threeDview.setGame(new Game(gameFile.getPath()));
		            	
		            	

			        }
		        	catch (FileNotFoundException e)
		        	{
						System.err.println("Couldn't find file " + gameFile.getName());
						JOptionPane.showMessageDialog(MyFrame.this,
							    "Couldn't find file " + gameFile.getName(),
							    "Error",
							    JOptionPane.ERROR_MESSAGE);
						return;
					}
		        	catch (IOException e)
		        	{
						System.err.println(e.getMessage());
						JOptionPane.showMessageDialog(MyFrame.this,
							    e.getMessage(),
							    "Error",
							    JOptionPane.ERROR_MESSAGE);
						return;
					}
		        	catch (ParseException e)
		        	{
						System.err.println(e.getMessage());
						JOptionPane.showMessageDialog(MyFrame.this,
							    "Unsupported file format for file " + gameFile.getName(),
							    "Error",
							    JOptionPane.ERROR_MESSAGE);
						return;
					}
		            finally
		            {
		            	buttonPlay.setText(">");
		            	buttonPlay.setPreferredSize(new Dimension(70,50));
		            }
		        	
		        	// if loading is successful, enable all buttons
		            enableButtons();
		        }
			}
		});
		
		
		sliderProgress = new JSlider(0,0);
		sliderProgress.setEnabled(false);
		
		
		sliderButtonsPane = new JPanel();
		sliderButtonsPane.add(buttonHelp);
		sliderButtonsPane.add(buttonLoad);
		sliderPane.add(sliderButtonsPane, BorderLayout.WEST);
		sliderPane.add(sliderProgress, BorderLayout.CENTER);
		
		trianglePane.setPreferredSize(new Dimension(0,0));
		trianglePane.add(Box.createHorizontalGlue());
		trianglePane.add(buttonSlowDown);
		trianglePane.add(buttonPlay);
		trianglePane.add(buttonFastForward);
		trianglePane.add(Box.createHorizontalGlue());
		
		JPanel upperControls = new JPanel(new BorderLayout());
		upperControls.add(settingsPane, BorderLayout.WEST);
		upperControls.add(trianglePane, BorderLayout.CENTER);
		upperControls.add(speedPane, BorderLayout.EAST);
		
		controlPanel.add(upperControls);
		controlPanel.add(sliderPane);
		threeDpanel.setPreferredSize(new Dimension(500,500));
		
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
		if (!buttonsEnabled)
		{
			buttonsEnabled = true;
			
			buttonPlay.setEnabled(true);
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
			
			
			
			
			buttonFastForward.setEnabled(true);
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
			
			
			
			
			buttonSlowDown.setEnabled(true);
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
			
			
			
			
			sliderProgress.setEnabled(true);
			sliderProgress.addChangeListener(new ChangeListener()
			{
				public void stateChanged(ChangeEvent e)
				{
					threeDview.setTime(sliderProgress.getValue());	
				} 
			});
			
			
			buttonPreviousPlayer.setEnabled(true);
			buttonPreviousPlayer.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					if (!labelPlayer.getText().equals("#"))
					{
						int lol = Integer.parseInt(labelPlayer.getText());
						if (lol == 1)
						{
							labelPlayer.setText("#");
							checkBoxHeat.setEnabled(false);
							threeDview.followPlayer(0);
						}
						else
						{
							labelPlayer.setText(""+(lol-1));
							threeDview.followPlayer(lol-1);
						}
					}
					
				}
				
			});
			
			buttonNextPlayer.setEnabled(true);
			buttonNextPlayer.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					int lol;
					if (!labelPlayer.getText().equals("#"))
					{
						lol = Integer.parseInt(labelPlayer.getText());
					}
					else
					{
						lol = 0;
						checkBoxHeat.setEnabled(true);
					}
					
					if (lol != 15)
					{
						labelPlayer.setText(""+(lol+1));
						threeDview.followPlayer(lol+1);
					}
						
					
					
				}
				
			});
			
			checkBoxPath.setEnabled(true);
		}
		
		
	}

}

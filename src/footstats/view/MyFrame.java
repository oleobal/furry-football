package footstats.view;
import footstats.model.*;

import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;

public class MyFrame extends JFrame
{
	JPanel threeDpanel, controlPanel, trianglePane, sliderPane;
	JButton buttonPlay, buttonFastForward, buttonSlowDown, buttonLoad;
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
		buttonPlay = new JButton("II");
		buttonPlay.setFont(new Font("Arial", Font.PLAIN, 40));
		buttonPlay.setPreferredSize(new Dimension(70,50));
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
		buttonFastForward = new JButton("faster");
		buttonFastForward.setFont(new Font("Arial", Font.PLAIN, 20));
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
		buttonSlowDown = new JButton("slower");
		buttonSlowDown.setFont(buttonFastForward.getFont());
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
		labelSpeed = new JLabel("1x");
		labelSpeed.setFont(buttonPlay.getFont());
		labelSpeed.setPreferredSize(new Dimension(120,75));
		
		buttonLoad = new JButton("LOAD");
		sliderProgress = new JSlider(0,2);
		sliderProgress.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				threeDview.setTime(sliderProgress.getValue());			
			} 
		});
		
		sliderPane.add(buttonLoad, BorderLayout.WEST);
		sliderPane.add(sliderProgress, BorderLayout.CENTER);
		
		controlPanel.add(trianglePane);
		controlPanel.add(sliderPane);
		threeDpanel.setPreferredSize(new Dimension(500,500));
		trianglePane.add(new JLabel(){{this.setPreferredSize(new Dimension(120,75));}}); //padding (yes, Box.createRigidArea)
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


}

package footstats.view;
import footstats.model.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MyFrame extends JFrame
{
	JPanel threeDpanel, controlPanel, trianglePane, sliderPane;
	JButton buttonPlay, buttonLoad;
	JSlider sliderProgress;

	public MyFrame(String title)
	{
		super(title);
		this.setPreferredSize(new Dimension(1066, 625));
		this.setResizable(true);
		this.setLocation(200,200);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel contentPane = new JPanel(new BorderLayout());
		trianglePane  = new JPanel();
		sliderPane    = new JPanel(new BorderLayout());
		
		contentPane.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		
		threeDpanel = new JPanel();
		threeDpanel.setBackground(Color.BLACK);
		controlPanel = new JPanel();
		controlPanel.setBorder(BorderFactory.createEmptyBorder(10,0,0,0));
		controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
		buttonPlay = new JButton("▶");
		buttonPlay.setFont(new Font("Arial", Font.PLAIN, 40));
		buttonPlay.addActionListener(new ActionListener()
		{
				public void actionPerformed(ActionEvent e)
				{
					if(buttonPlay.getText().equals("▋▋"))
					{
						buttonPlay.setText("▶");
					}
					else
					{
						buttonPlay.setText("▋▋");
					}
				}
		});
		buttonLoad = new JButton("LOAD");
		sliderProgress = new JSlider(0,1000);
		
		sliderPane.add(buttonLoad, BorderLayout.WEST);
		sliderPane.add(sliderProgress, BorderLayout.CENTER);
		
		controlPanel.add(trianglePane);
		controlPanel.add(sliderPane);
		threeDpanel.setPreferredSize(new Dimension(500,500));
		trianglePane.add(buttonPlay);
		
		contentPane.add(threeDpanel, BorderLayout.CENTER);
		contentPane.add(controlPanel, BorderLayout.SOUTH);
		

		
		this.setContentPane(contentPane);
		this.pack();
		this.setVisible(true);
	}


}

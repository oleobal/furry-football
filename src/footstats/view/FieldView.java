package footstats.view;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.ZipLocator;
import com.jme3.font.*;
import com.jme3.input.ChaseCamera;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.*;
import com.jme3.system.AppSettings;

import footstats.model.*;

import javax.swing.JSlider;

public class FieldView extends SimpleApplication
{
	private MyFrame theFrame;

	private Geometry cube1, cube2, cube3;
	private Geometry[] cube;
	private BitmapText[] num;
	private Material mat, mat2, mat3;
	
	private Game game;
	private int i;
	private float timer;
	int playbackRate;
	boolean playbackPaused;
	
	@Override
	public void simpleInitApp()
	{		
		
		mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat2 = mat.clone(); mat3 = mat.clone();
		
		
		Box b = new Box(1, 1, 1);
		cube1 = new Geometry("Box", b);
		cube2 = cube1.clone();
		cube3 = cube1.clone();
		
		mat.setColor("Color", ColorRGBA.Blue);
		cube1.setMaterial(mat);
		mat2.setColor("Color", ColorRGBA.Green);
		cube2.setMaterial(mat2);
		//mat3.setColor("Color", ColorRGBA.Red);
		mat3.setColor("Color", new ColorRGBA(1f,0.3f,0.3f,1.f));
		cube3.setMaterial(mat3);
		
		
		DirectionalLight directionalLight = new DirectionalLight(new Vector3f(-2, -10, 1));
		directionalLight.setColor(ColorRGBA.White.mult(1.3f));
		rootNode.addLight(directionalLight);
		
		assetManager.registerLocator("stade.zip", ZipLocator.class);
		Spatial field_geom = assetManager.loadModel("stade/soccer.obj");
		Node field_node = new Node("field");
		field_node.attachChild(field_geom);
		rootNode.attachChild(field_node);
		
		cube2.setLocalTranslation(0, 3, 0);
		cube3.setLocalTranslation(3, 0, -3);
		
		//rootNode.attachChild(cube1);
		//rootNode.attachChild(cube2);
		//rootNode.attachChild(cube3);
		
		Dome d = new Dome(30, 100, 200);
		Geometry skybox = new Geometry("Dome", d);
		Material matSky = mat.clone();
		matSky.setColor("Color",new ColorRGBA(0.7f,0.7f,1.0f,1.0f));
		skybox.setMaterial(matSky);
		rootNode.attachChild(skybox);
		viewPort.setBackgroundColor(new ColorRGBA(0.4f,0.5f,0.4f,1f));
		
		BitmapFont fnt = assetManager.loadFont("Interface/Fonts/Default.fnt");
		
		cube = new Geometry[15];
		num = new BitmapText[15];
		for (int ko=0;ko<15;ko++)
		{
			Node playerNode = new Node("player"+ko);
			num[ko]=new BitmapText(fnt, false);
			num[ko].setBox(new Rectangle(0, 0, 6, 3));
			num[ko].setLocalTranslation(ko*3,4,40);
			num[ko].setQueueBucket(Bucket.Transparent);
			num[ko].setSize(2.0f);
			num[ko].setText(""+(ko+1));
			
			playerNode.attachChild(num[ko]);
			
			
			cube[ko] = new Geometry("Box", b);
			cube[ko].setMaterial(mat);
			cube[ko].setLocalTranslation(ko*3,1,40);
			playerNode.attachChild(cube[ko]);
			rootNode.attachChild(playerNode);
		}
		

		
		flyCam.setEnabled(false);
		
		ChaseCamera chaseCam = new ChaseCamera(cam, field_geom, inputManager);
		chaseCam.setDragToRotate(true);
		
		chaseCam.setInvertVerticalAxis(true);
		chaseCam.setRotationSpeed(10);
		chaseCam.setMinVerticalRotation((float) Math.PI/12);
		chaseCam.setMaxVerticalRotation((float) (Math.PI/2));
		chaseCam.setMinDistance(5f);
		chaseCam.setMaxDistance(150);
		
		// Default camera position and orientation
		chaseCam.setDefaultVerticalRotation((float)(Math.PI/2-0.0001)); // directly perpendicular
		chaseCam.setDefaultHorizontalRotation((float)(Math.PI/2));      // horizontally aligned
		chaseCam.setDefaultDistance(100);
		
		chaseCam.setDownRotateOnCloseViewOnly(false);
		chaseCam.setSmoothMotion(true);
		
		playbackRate = 50;
		playbackPaused = true;
		
		game = null;
		i = 0;
		timer = 0;
		
		theFrame.sliderProgress = theFrame.sliderProgress;

	}
	
	// Set a new game to read data from, reinitializing everything
	public void setGame(Game g)
	{
		this.game = g;
		i = 0;
		timer = 0;
		playbackPaused = true;
		playbackRate = 50;
		
		theFrame.sliderProgress.setMaximum(game.getSnapshotCount());
		theFrame.sliderProgress.setMinimum(0);
		theFrame.sliderProgress.setValue(0);
	}
	
	public void setTime(int time)
	{
		if (time<0)
			i=0;
		if (time>game.getSnapshotCount())
			i=game.getSnapshotCount();
		i = time;
	}
	
	public void giveFrame(MyFrame frame)
	{
		theFrame = frame;
	}
	
	public void playPause()
	{
		playbackPaused ^= true; //XOR me dit-on
	}
	
	@Override
	public void simpleUpdate(float tpf)
	{	
		for(BitmapText label : num)
		{
			// Make quaternion that looks at the cam to rotate the text to face the screen
			Quaternion q = new Quaternion();
			q.lookAt(cam.getLocation(), cam.getUp());
			label.setLocalRotation(q);
		}
		
		if (game != null)
		{
			// Test of positioning of a player using data from game
			if(i < game.getSnapshotCount() && timer >= playbackRate)
			{
				Snapshot s = game.getSnapshotByIndex(i);
				for (int p=0; p<15;p++)
				{
					Trace y = s.getTraceOfPlayer(p+1);
					if(y != null)
					{
						// transform shapes
						cube[p].setLocalTranslation(y.posX - 105/2, 1, y.posY - 68/2);
						num[p].setLocalTranslation(y.posX - 105/2, 4, y.posY - 68/2);
						
						// Color active players blue
						cube[p].setMaterial(mat);
					}
					else
						// Color inactive players red
						cube[p].setMaterial(mat3);
				}
				if(!playbackPaused) i++;
				timer -= playbackRate;
				
				theFrame.sliderProgress.setValue(i);
			}
			timer += 1000*tpf;
		}
	}
}

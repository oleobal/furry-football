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
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.ssao.SSAOFilter;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.*;
import com.jme3.shadow.BasicShadowRenderer;
import com.jme3.shadow.PssmShadowRenderer;
import com.jme3.system.AppSettings;

import footstats.model.*;
import sun.java2d.loops.DrawPath;

import javax.swing.JSlider;

public class FieldView extends SimpleApplication
{
	private MyFrame theFrame;

	private ChaseCamera chaseCam;
	private Geometry smallCube;
	private Spatial[] cube;
	private Spatial field_geom;
	private BitmapText[] num;
	private Material matActive, matInactive, matPath;
	private Node pathNode;
	
	private Game game;
	private int i;
	private float timer;
	int playbackRate;
	boolean playbackPaused, pathsDrawn, removeDrawnPaths;
	
	@Override
	public void simpleInitApp()
	{		
		
		matActive = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		matInactive = matActive.clone();
		matPath = matActive.clone();
		
		Sphere smallBox = new Sphere(10, 10, 0.1f, true, true);//new Box(0.2f,0.2f,0.2f); //it's not my fault if I only know how to make cubes
		smallCube = new Geometry("Sphere", smallBox);
		
		matActive.setColor("Color", ColorRGBA.Blue);
		matInactive.setColor("Color", new ColorRGBA(1f,0.3f,0.3f,1.f));
		matPath.setColor("Color", ColorRGBA.Yellow);
		
		smallCube.setMaterial(matPath);
		
		DirectionalLight directionalLight = new DirectionalLight(new Vector3f(-2, -10, 1));
		directionalLight.setColor(ColorRGBA.White.mult(1.3f));
		rootNode.addLight(directionalLight);
		
		assetManager.registerLocator("stade.zip", ZipLocator.class);
		field_geom = assetManager.loadModel("stade/soccer.obj");
		field_geom.setShadowMode(ShadowMode.Receive);
		Node field_node = new Node("field");
		field_node.attachChild(field_geom);
		rootNode.attachChild(field_node);
		
		Dome d = new Dome(30, 100, 200);
		Geometry skybox = new Geometry("Dome", d);
		Material matSky = matActive.clone();
		matSky.setColor("Color",new ColorRGBA(0.7f,0.7f,1.0f,1.0f));
		skybox.setMaterial(matSky);
		rootNode.attachChild(skybox);
		viewPort.setBackgroundColor(new ColorRGBA(0.4f,0.5f,0.4f,1f));
		
		
		BitmapFont fnt = assetManager.loadFont("Interface/Fonts/Default.fnt");
		
		
				
		cube = new Spatial[15];
		num = new BitmapText[15];
		for (int ko=0;ko<15;ko++)
		{
			Node playerNode = new Node("player"+ko);
			num[ko]=new BitmapText(fnt, false);
			num[ko].setBox(new Rectangle(0, 0, 6, 3));
			num[ko].setLocalTranslation(ko*3,5,40);
			num[ko].setQueueBucket(Bucket.Transparent);
			num[ko].setSize(2.0f);
			num[ko].setText(""+(ko+1));
			
			playerNode.attachChild(num[ko]);
			
			/*
			cube[ko] = new Geometry("Box", b); */
			//cube[ko] = assetManager.loadModel("stade/player.obj");
			cube[ko] = assetManager.loadModel("stade/trex.obj");
			//cube[ko] = assetManager.loadModel("stade/T-800.obj");
			cube[ko].setLocalScale(0.7f);
			cube[ko].setMaterial(matActive);
			cube[ko].setShadowMode(ShadowMode.Cast);
			cube[ko].setLocalTranslation(ko*3,0f,40);
			cube[ko].setLocalRotation(new Quaternion().fromAngles(0, (float)Math.PI, 0)); // make them start facing the field
			playerNode.attachChild(cube[ko]);
			rootNode.attachChild(playerNode);
		}
		
		Node pathNode = new Node("paths");
		rootNode.attachChild(pathNode);
		//pathNode.attachChild(smallCube);

		
		flyCam.setEnabled(false);
		
		chaseCam = new ChaseCamera(cam, field_geom, inputManager);
		chaseCam.setDragToRotate(true);
		
		chaseCam.setInvertVerticalAxis(true);
		chaseCam.setRotationSpeed(10);
		chaseCam.setMinVerticalRotation((float) Math.PI/12);
		chaseCam.setMaxVerticalRotation((float) (Math.PI/2));
		chaseCam.setMinDistance(20f);
		chaseCam.setMaxDistance(150);
		
		// Default camera position and orientation
		chaseCam.setDefaultVerticalRotation((float)(Math.PI/2-0.0001)); // directly perpendicular
		chaseCam.setDefaultHorizontalRotation((float)(Math.PI/2));      // horizontally aligned
		chaseCam.setDefaultDistance(100);
		
		chaseCam.setDownRotateOnCloseViewOnly(false);
		chaseCam.setSmoothMotion(true);
		
		chaseCam.setLookAtOffset(new Vector3f(0, 0, 0));
		

		/*
		BasicShadowRenderer bsr = new BasicShadowRenderer(assetManager, 256);
		bsr.setDirection(new Vector3f(-1, -1, -1).normalizeLocal());
		viewPort.addProcessor(bsr);
		*/
		
		// if you want shadows uncomment this :
		// but it's deprecated and resources-hungry so..
		/*
		PssmShadowRenderer pssmRenderer = new PssmShadowRenderer(assetManager,1024,4);
		pssmRenderer.setDirection(new Vector3f(-1, -1, -1).normalizeLocal());
		viewPort.addProcessor(pssmRenderer);
		*/
		
		
		/*
		FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
		SSAOFilter ssaoFilter = new SSAOFilter(12.94f, 43.92f, 0.33f, 0.61f);
		fpp.addFilter(ssaoFilter);
		viewPort.addProcessor(fpp);
		*/
		
		playbackRate = 50;
		playbackPaused = true;
		pathsDrawn = false;
		removeDrawnPaths = false;
		
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
	
	public void togglePaths()
	{
		pathsDrawn ^= true;
		if (!pathsDrawn)
			removeDrawnPaths = true;
		/*
		if (!pathsDrawn)
		{
			pathNode.removeFromParent();
			pathNode = null;
			//pathNode = new Node("paths");
			//rootNode.attachChild(pathNode);
		}
		*/
	}
	
	/**
	 * @param playerID : from 1 to 15, or 0 to center
	 */
	public void followPlayer(int playerID)
	{
		if (playerID<0)
			playerID=0;
		if (playerID>15)
			playerID=15;
		
		if (playerID == 0)
		{
			chaseCam.setSpatial(field_geom);
		}
		
		else
		{
			chaseCam.setSpatial(cube[playerID-1]);
			
		}
		
	}
	
	@Override
	public void simpleUpdate(float tpf)
	{	
		
		for(BitmapText label : num)
		{
			// no need for any fancy quaternions, lol
			label.lookAt(cam.getLocation(), cam.getUp());
		}
		
		if (removeDrawnPaths)
		{
			removeDrawnPaths = false;
			try
			{
				pathNode.removeFromParent();
				pathNode = null;
			}
			catch (Exception e)
			{
				System.err.println("pathNode exception, please ask Jean-Marc for help");
			}
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
						if (pathsDrawn)
						{
							Geometry lol = smallCube.clone();
							try
							{
								pathNode.attachChild(lol);
							}
							catch (NullPointerException e)
							{
								//System.err.println("God dammit");
								// now it's intentional ! No more errors !
								pathNode = new Node("paths");
								rootNode.attachChild(pathNode);
								pathNode.attachChild(lol);
							}
							lol.setLocalTranslation(y.posX - 105/2, 0.1f, y.posY - 68/2);
						}
						// transform shapes
						cube[p].setLocalTranslation(y.posX - 105/2, 0f, y.posY - 68/2);
						cube[p].setLocalRotation(new Quaternion().fromAngles(0, y.heading, 0));
						cube[p].setShadowMode(ShadowMode.Cast);
						
						num[p].setLocalTranslation(y.posX - 105/2, 5, y.posY - 68/2);
						
						// Color active players blue
						cube[p].setMaterial(matActive);
					}
					else
						// Color inactive players red
						cube[p].setMaterial(matInactive);
				}
				if(!playbackPaused) i++;
				timer -= playbackRate;
				
				theFrame.sliderProgress.setValue(i);
			}
			timer += 1000*tpf;
		}
	}
}

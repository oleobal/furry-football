package footstats.view;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.ZipLocator;
import com.jme3.input.ChaseCamera;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;

public class FieldView extends SimpleApplication {

	private Geometry cube1, cube2, cube3;
	
	@Override
	public void simpleInitApp() {		
		Box b = new Box(1, 1, 1);
		cube1 = new Geometry("Box", b);
		cube2 = cube1.clone();
		cube3 = cube1.clone();
		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		Material mat2 = mat.clone(), mat3 = mat.clone();
		mat.setColor("Color", ColorRGBA.Blue);
		cube1.setMaterial(mat);
		mat2.setColor("Color", ColorRGBA.Green);
		cube2.setMaterial(mat2);
		mat3.setColor("Color", ColorRGBA.Red);
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
		
		rootNode.attachChild(cube1);
		rootNode.attachChild(cube2);
		rootNode.attachChild(cube3);
		
		flyCam.setEnabled(false);
		
		ChaseCamera chaseCam = new ChaseCamera(cam, field_geom, inputManager);
		chaseCam.setDragToRotate(true);
		
		chaseCam.setInvertVerticalAxis(true);
		chaseCam.setRotationSpeed(10);
		chaseCam.setMinVerticalRotation((float) Math.PI/12);
		chaseCam.setMaxVerticalRotation((float) (Math.PI/2));
		chaseCam.setMinDistance(2.5f);
		chaseCam.setMaxDistance(150);
	}
	
	@Override
	public void simpleUpdate(float tpf)
	{
		cube1.rotate(2*tpf, 2*tpf, tpf);
		cube2.rotate(-2*tpf, 5*tpf, tpf);
		cube3.rotate(2*tpf, -2*tpf, 7*tpf);
	}
}

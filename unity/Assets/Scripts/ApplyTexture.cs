using UnityEngine;
using System.Collections;

public class ApplyTexture : MonoBehaviour {
  
	GameObject sphere;
	GameObject debugText;
	Texture2D photoTexture;
	
	void Start () {
		sphere = GameObject.Find("Inverted Sphere");
		debugText = GameObject.Find("/Canvas/Text");
		photoTexture = new Texture2D(4096, 4096, TextureFormat.DXT1, false);

		
		string url = "file://DCIM/Camera/PANO_20150102_150346.jpg";
		SphereTextureFromFile(url);
	}
	
	IEnumerator SphereTextureFromFile(string url) {	
	  	printToScreen("Getting file...");
		WWW file = new WWW(url);
		printToScreen("Waiting for file to load...");
		yield return file;
		printToScreen("Turning photo into texture...");
		file.LoadImageIntoTexture(photoTexture);
		printToScreen("Applying texture to spheres...");
		sphere.renderer.material.mainTexture = photoTexture;
		printToScreen("Done!");
	}

	void printToScreen(string msg) {
		debugText.guiText.text = msg;
	}
}

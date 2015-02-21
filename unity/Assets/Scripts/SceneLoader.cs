using UnityEngine;
using System.Collections;

public class SceneLoader: MonoBehaviour {
	public GameObject loadingImage;
	private bool isLoading = true;

	public void LoadScene(int level) {
		StartLoading ();
		Application.LoadLevel(level);
	}
	
	void StartLoading() {
		isLoading = true;
		loadingImage.SetActive (true);
	}

	void StopLoading() {
		isLoading = false;
		loadingImage.SetActive (false);
	}

	void OnLevelWasLoaded(int level) {
		StopLoading();
	}
	
	// Update is called once per frame
	void Update () {
		if(Input.GetKeyDown(KeyCode.Escape))
		{
			int currentScene = Application.loadedLevel;
			if (currentScene == 0 || isLoading) {
				Application.Quit();
			} else {
				StartLoading ();
				Application.LoadLevel(0);
			}
		}
	}
}

using UnityEngine;
using System.Collections;
using UnityEngine.UI;
using System.IO;

public class ApplyTexture : MonoBehaviour
{
  
    public GameObject sphere;
    public Text debugText;
    public InputField textInput;
    public GameObject canvas;
    public GameObject loadingImage;
    private Texture2D photoTexture;
    private WWW www;
    private bool wasDone;
    private bool canvasActive;
    private string debugMsg;
  
    void Start()
    {
        photoTexture = new Texture2D(2, 2, TextureFormat.DXT1, false);
        canvas.SetActive(true);
        wasDone = false;
        canvasActive = true;
        debugMsg = "";
    }

    void Update() 
    {
        if (www != null && !wasDone && www.isDone) {
            if (www.error == null)
            {
                printToScreen("File load completed.");
                wasDone = true;
                StartCoroutine(LoadTexture(www));                
            } else 
            {
                printToScreen("Error while loading: " + www.error);
            }
            www = null;
        }

        // Update UI based on variables
        if (debugText.text != debugMsg) 
        {
            debugText.text = debugMsg;
        }
        // If the canvas is hidden, update textures
        if (!canvasActive && canvas.activeSelf) 
        {
            printToScreen("Applying texture to sphere...");
            try
            {
                sphere.GetComponent<Renderer>().material.mainTexture = photoTexture;
                printToScreen("");
                canvas.SetActive(false);
            } catch (System.Exception ex)
            {
                printToScreen("Failed to load photo as texture.");
                printToScreen(ex.Message + ex.ToString());
            }
        }
    }

    IEnumerator LoadTexture(WWW file) 
    {
        try
        {
            printToScreen("Turning photo into texture...");
            file.LoadImageIntoTexture(photoTexture);
            if (!Mathf.IsPowerOfTwo(photoTexture.width) || !Mathf.IsPowerOfTwo(photoTexture.height)) 
            {
                throw new System.Exception("Please resize your photo to 4096x4096.");
            }
            printToScreen("Rendering new scene...");
            canvasActive = false;
        } catch (System.Exception ex)
        {
            printToScreen("Failed to load photo as texture: \n" + ex.Message);
        }
        yield return null;
    }
  
    public void SphereTextureFromFile()
    {
        loadingImage.SetActive(true);
        printToScreen("Getting file...");

        if (File.Exists(textInput.text)) {
            string url = "file://" + textInput.text;
            printToScreen("Waiting for file to load...");
            wasDone = false;
            www = new WWW(url);
        }
        else 
        {
            printToScreen("That file does not exist.");
            loadingImage.SetActive(false);
        }

//        StartCoroutine(orly());
//      
//      FileStream fs = new FileStream(url, FileMode.Open);
//      System.Byte[] ImageBytes = new System.Byte[fs.Length];
//      fs.Read(ImageBytes, 0, (int) fs.Length);
//      fs.Close();
//      printToScreen("Turning photo into texture...");
//      photoTexture.LoadImage(ImageBytes);
//      printToScreen("Applying texture to spheres...");
//      sphere.renderer.material.mainTexture = photoTexture;
//      printToScreen("Done!");
//      loadingImage.SetActive (false);
//      canvas.SetActive(false);
    }

//    IEnumerator orly()
//    {
//            loadingImage.SetActive(true);
//            printToScreen("Getting file...");
//            string url = "file://" + textInput.text;
//            printToScreen("Waiting for file to load...");
//            WWW file = new WWW(url);
//            yield return file;
//        
//        try
//        {
//            printToScreen("Turning photo into texture...");
//            file.LoadImageIntoTexture(photoTexture);
//            printToScreen("Applying texture to spheres...");
//            sphere.renderer.material.mainTexture = photoTexture;
//            printToScreen("Done!");
//            canvas.SetActive(false);
//            loadingImage.SetActive(false);
//            
//        } catch (System.Exception ex)
//        {
//            printToScreen(ex.Message + ex.ToString());
//        }
//    }

    void printToScreen(string msg)
    {
        debugMsg = msg;
        Debug.Log("SphereViewer: " + msg);
    }
}

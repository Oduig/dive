using UnityEngine;
using System.Collections;

[AddComponentMenu("Camera-Control/Mouse Look")]
public class MouseLook : MonoBehaviour
{
    public float sensitivityX = 5F;
    public float sensitivityY = 5F;
    public float sensitivityZ = .7F;

    public float minimumX = -360F;
    public float maximumX = 360F;

    public float minimumY = -90F;
    public float maximumY = 90F;

    public float minimumZ = -90F;
    public float maximumZ = 90F;

    float rotationX = 0F;
    float rotationY = 0F;
    float rotationZ = 0F;

    Quaternion originalRotation;

    void Update()
    {
        rotationX += Input.GetAxis("Mouse X") * sensitivityX;
        rotationX = ClampAngle(rotationX, minimumX, maximumX);
        Quaternion xQuaternion = Quaternion.AngleAxis(rotationX, Vector3.up);

        rotationY += Input.GetAxis("Mouse Y") * sensitivityY;
        rotationY = ClampAngle(rotationY, minimumY, maximumY);
        Quaternion yQuaternion = Quaternion.AngleAxis(-rotationY, Vector3.right);

        rotationZ += Input.GetAxis("Mouse ScrollWheel") * sensitivityZ;
        rotationZ = ClampAngle(rotationZ, minimumZ, maximumZ);
        Quaternion zQuaternion = Quaternion.AngleAxis(rotationZ, Vector3.back);

        transform.localRotation = originalRotation * xQuaternion * yQuaternion * zQuaternion;
    }

    void Start()
    {
        // Make the rigid body not change rotation
        if (rigidbody)
            rigidbody.freezeRotation = true;
        originalRotation = transform.localRotation;
    }

    public static float ClampAngle(float angle, float min, float max)
    {
        if (angle < -360F)
            angle += 360F;
        if (angle > 360F)
            angle -= 360F;
        return Mathf.Clamp(angle, min, max);
    }
}
# Dive VR for PC

A company called [Durovis](https://www.durovis.com) created a VR headset known as **Dive**. What's special about this headset is that it uses a smartphone as a side-by-side 3D screen.
Some dedicated apps have been created, but they are limited by smartphone apps and performance.


Goal
-----
My idea is to use the Dive in combination with a remote-desktop viewer like [Splashtop](http://www.splashtop.com) to watch and participate in 3D content without the limitations of smartphone hardware.
This would allow one to play e.g. Portal or FPS games while wearing the Dive.

There is a reason why most VR headsets, including the Oculus Rift, require a cable attached to your computer. 
It seems like if they have latency issues with dedicated hardware, I could never accomplish the same thing with my phone. 
But hey, how do we know if we don't try?

Progress
-----

What you can do with it so far:

* Use Splashtop to remote-view your PC screen. It's free for this purpose. Make sure that you:
	* Are close to your router.
	* Have set the Splashtop app's mode to *smooth* rather than *sharp*.
	* Open a port on your firewall so the signal stays in your LAN (for latency).
	* Streaming 1080p takes about 50 Mbit. Wireless 802.11n is recommended, 802.11g is the minimum. You do not need a fast internet connection.
* Use the client app on Android together with the server app. It currently supports yaw and pitch by moving the mouse and roll (optionally) by scrolling. Connect with one of:
	* UDP, straightforward.
	* Bluetooth. I implemented it so that the Android app acts as the client. Make sure you enter your devices' bluetooth IDs into the program.
	* TCP. It is slower than the other two, but not by a shocking amount.
* Try or buy Tridef 3D. It works on both NVIDIA and ATI cards, and allows you to render off-the-shelf games in 3D side-by-side mode. Your game needs at least:
	* DirectX support so that Tridef can render 3D.
	* Windowed (fullscreen or not) support so that Splashtop can stream it.
* Buy an Xbox360 controller for PC, or use your own. Top tip for minimum entanglement: it should be wireless.
* Viewing photos using a Unity 3D project. It requires a photo which is exactly 360 degrees wide and 180 degrees high, like those that can be made using the latest Android Sphere photos. Import the image in Unity and drag it to both spheres. If you want to go really far, you can make two sphere photos slightly apart for a 3d effect.

What's on my todo list:

* [Compensate for vertical and horizontal drift](http://www.oculus.com/blog/magnetometer/). Horizontal drift is OK since you don't really notice which way you face, but vertical drift can be a bit annoying. Especially when jumping through portals. Right now, I use the Xbox controller every so often to correct drift. 
* Jump and crouch detection as an optional setting. I need to do some signal processing research to find out how to distinguish gestures, e.g. tell a quick uncrouch apart from a jump.
* A talk on this project for my company, about the applications and challenges of VR.

What I dropped from my todo list:

* Lean forward/left/rightt/back to walk. It's really difficult to derive the phone's location from acceleration values, and implementing it would effectively mean the user has to be really careful not to move his head much while turning.


License
-----
This project is an experiment conducted in my free time. I don't think I will ever do an official release for non-developers.
If you want to use it, grab the code and compile it yourself - it's a standard SBT project, so it should be relatively easy.

The code may be used under the [Apache License, Version 2.0](http://opensource.org/licenses/Apache-2.0)
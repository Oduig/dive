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

What I have achieved so far:

* Use Splashtop to remote-view your PC screen. It's free for this purpose. Make sure that you:
	* Are close to your router.
	* Have set the Splashtop app's mode to *smooth* rather than *sharp*.
	* Open a port on your firewall so the signal stays in your LAN (for latency).
	* Streaming 1080p takes about 50 Mbit. Wireless 802.11n is recommended, 802.11g is the minimum. You do not need a fast internet connection.
* Use the client app on Android together with the server app, and connect with one of:
	* UDP, straightforward.
	* Bluetooth. I implemented it so that the Android app acts as the client. Make sure you enter your devices' bluetooth IDs into the program.
	* TCP. At the moment, it is *really* slow, so I recommend the other two.
* Try or buy Tridef 3D. It works on both NVIDIA and ATI cards, and allows you to render off-the-shelf games in 3D side-by-side mode. Your game needs at least:
	* DirectX support so that Tridef can render 3D.
	* Windowed (fullscreen or not) support so that Splashtop can stream it.
* Buy an Xbox360 controller for PC, or use your own. Top tip for minimum entanglement: it should be wireless.

What's on my todo list:
* ![Compensate for vertical and horizontal drift](http://www.oculus.com/blog/magnetometer/). Horizontal drift is OK since you don't really notice which way you face, but vertical drift can be a bit annoying. Especially when jumping through portals. Right now, I use the Xbox controller every so often to correct drift. 
* See if I can use the Z axis of the gyro so that it rolls the camera when you tilt your head.
* Lean forward/left/right/back to walk
* Jump to jump

License
-----
This project is an experiment conducted in my free time. I don't think I will ever do an official release for non-developers.
If you want to use it, grab the code and compile it yourself - it's a standard SBT project, so it should be relatively easy.

The code may be used under the [Apache License, Version 2.0](http://opensource.org/licenses/Apache-2.0)
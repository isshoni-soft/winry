Isshoni Winry Bootstrapper
==========================
The bootstrapper designed and developed for Isshoni, with the intention to support a wide-range
of Java applications to facilitate innovation while not wasting time on boilerplate design requirements.

**This tool is currently in beta until the version number says 1.0.0, please expect bugs/instability when using it.**

Table of Contents
-----------------
- [Basic Usage](#basic-usage)
- [Repository](#repository)
- [About](#about)
- [How it Works](#how-it-works)
- [Advanced Usage](#advanced-usage)

Basic Usage
-----------
Requires JDK 17  
TODO

[Return to Table of Contents](#table-of-contents)

Repository
----------
I have a maven repository where I publish builds, here is how you would import it as a dependency with gradle:
```groovy
repositories {
    maven {
        url = 'https://repo.isshoni.institute'
    }
}

dependencies {
    implementation 'tv.isshoni:winry:0.59.1'
}
```

[Return to Table of Contents](#table-of-contents)

About
-----
Design inspired by SpringBoot and previous projects I have worked on. This is a library designed to make
bootstrapping **any type** of Java application as painless and as easy as using SpringBoot. Some major
features are:
* Singleton dependency management, register & inject dependencies with ease.
* Event based, winry is backed by an event bus that can be configured to fire specific events during the lifecycle.
* Integer-based weighting system, to keep things running smoothly and in order, all "executables" in the winry bootstrapping lifecycle are integer weighted.
* Built to be extended, register new annotations, add more functionality to pre-existing ones, or add new lifecycle events.
    * Import libraries that add their own lifecycle events and gain specialized functionality, i.e. game engine, or, rest webserver.
  
TODO  
  
[Return to Table of Contents](#table-of-contents)

How it Works
------------
TODO  
  
[Return to Table of Contents](#table-of-contents)

Advanced Usage
--------------
TODO  
  
[Return to Table of Contents](#table-of-contents)

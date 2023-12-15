Isshoni Winry Bootstrapper
==========================
The bootstrapper designed and developed for Isshoni, with the intention to support a wide-range
of Java applications to facilitate innovation while not wasting time on boilerplate design requirements.

**This tool was written with and requires Java 17**  
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
Usage varies slightly depending on if you intend to create a library that utilizes Winry, or an application
using Winry. A library will need to use an `@Loader` annotation, while an application must use an `@Bootstrap`
annotation on the "main class" of the program.  

A `@Bootstrap` annotation contains various program metadata and needs a nested `@Loader` annotation to determine load
targets. A library only needs to declare its load targets, because it's not an application and therefore doesn't
need the `@Bootstrap`. When using a library, make sure that you just load the class with the `@Loader` annotation
on it, the library should take care of the rest of the discovery and registration necessary.  
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
    api 'tv.isshoni:winry:0.62.2'
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
    * Import libraries that add their own lifecycle events and gain specialized functionality, i.e. game engine, or, [REST webserver](https://github.com/isshoni-soft/mishima).
  
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

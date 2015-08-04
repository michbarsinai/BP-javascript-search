Welcome to the Brood War Behavioral Programming page!

# Installation Instructions
1. Install Starcraft.
2. Install Brood War.
3. Install BWAPI. (https://github.com/bwapi/bwapi/releases/download/v4.1.0-Beta/BWAPI_410B_2_Setup.exe)
4. Install BroodWar path v1.161 (http://www.gamershell.com/download_38354.shtml)
5. Install Visual C++ Redistributable Packages for Visual Studio 2013 (https://www.microsoft.com/en-us/download/details.aspx?id=40784) (vcredist_x86.exe)
6. Install the latest 32-bit Java 7 SDK (http://download.oracle.com/otn-pub/java/jdk/7u80-b15/jdk-7u80-windows-i586.exe). JDK7 is the version required by Rhino as well as BWMirror, other JDKs are not promised to work.
7. Install Git.
8. Import this repository to your favorite IDE.


## Map Pack & ChaosLauncher Configuration
Install the map pack linked in this page and follow the instructions for configuring ChaosLauncher automatically.
http://www.sscaitournament.com/index.php?action=tutorial


# How it works
* Brood War is accessed via `BWAPI` and `BWMirror`. BWAPI triggers callbacks (in an implementation of the `BWEventListener` interface) that are registered in `BWMirror`'s `Mirror` global object. For BP, we just wrap the data from these callbacks into events that we fire into the `BPApplication`.
* 



## BWMirror Javadoc
http://bwmirror.jurenka.sk/javadoc/index.html

Seach is done via continuations in Rhino.









> PLEASE BUY THIS GREAT GAME.

> Implemented by Moshe Weinstock for his MS.C. thesis at Ben-Gurion University.

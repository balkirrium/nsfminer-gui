
# nsfminer-gui

**nsfminer-gui** is a desktop graphical user interface for [nsfminer](https://github.com/no-fee-ethereum-mining/nsfminer) made with Java Swing. 

Probably works also with ethminer and its forks provided the RPC api is not changed.

Both the source code and the UI are ugly but it might just work but beware : error handling is practically inexistent.
## Features
 - Manage from system tray
 - Execute custom script before Pause/Start mining, samples AHK scripts to change MSI afterburner profiles
 - Pause/Resume mining
 - Monitor hashrate
 - Start mining on startup (windows only)

## Usage
The JAR is executable.
You might want to configure gui.properties before launching it.

 - Change property file location (default is working directory) :

> -Dgui.properties.file=wherever/gui.properties

## Contribute/Issues
Because this software is sufficient for my needs as is, I will probably not monitor this repository very closely. If you need something done or fixed better fork it and do it yourself.

## Requirements
Java 16
Tested only on Windows, might works on other OS.

## License
Licensed under the [GNU Affero General Public License v3.0](LICENSE).

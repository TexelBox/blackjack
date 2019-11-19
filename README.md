# CPSC441BlackJack

How to run:

Server:
1) SSH into 199.116.235.39 with elvin.limpin@ucalgary.ca 's credentials
2) `source start.sh`
3) `cd CPSC441BlackJack`
4) `git checkout master`
5) `git pull`
6) `cd src`
7) `javac */*.java`
8) `java server/Controller.java`

GUI:
1) Open `http://https://cpsc441blackjack.web.app/`

Client GUI setup for each computer
1) Make sure you have `node` installed.
2) `sudo npm install -g firebase-tools`
3) `git clone https://github.com/elvinlimpin/CPSC441BlackJackGUI.git`on a new directory
4) `cd CPSC441BlackJack`
5) `firebase login`
6) login with your provided credentials
7) `firebase serve`

Client Java setup for each computer
1) Make sure you have `java` and `javac` installed
2) `git clone https://github.com/elvinlimpin/CPSC441BlackJack.git`on a new directory
3) `cd CPSC441BlackJack/src`
4) `javac */*.java`
5) `java client/User.java`

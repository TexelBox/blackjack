# CPSC441BlackJack

How to run:

Server:
1) SSH into 199.116.235.39 with amir.hussain1@ucalgary.ca's credentials
2) `source start.sh`
3) `cd CPSC441BlackJack`
4) `git checkout master`
5) `git pull`
6) `cd src`
7) `javac */*.java`
8) `java server/Controller.java`
9) Make sure you have `node` installed.
10) `sudo npm install -g firebase-tools`
11) `git clone https://github.com/elvinlimpin/CPSC441BlackJackGUI.git`on a new directory
12) `cd CPSC441BlackJack`
13) `firebase login`
14) login with your provided credentials
15) `firebase serve`

GUI:
1) Open `http://https://cpsc441blackjack.web.app/`

Client Java setup for each computer
1) Make sure you have `java` and `javac` installed
2) `git clone https://github.com/elvinlimpin/CPSC441BlackJack.git`on a new directory
3) `cd CPSC441BlackJack/src`
4) `javac */*.java`
5) `java client/User.java`

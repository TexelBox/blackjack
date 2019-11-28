# CPSC441BlackJack

## How to run:

- NOTE: follow "Java Server" and "Firebase Server" instructions on same computer.
- NOTE: follow "Java Client" instructions on any computer (even same as server).
- NOTE: If running servers in Cybera RAC on a VM with private IP, clients must also be running on a Cybera VM. 

### Java Server:
1) Make sure you have `java` and `javac` installed.
2) `git clone https://github.com/elvinlimpin/CPSC441BlackJack.git`
3) `cd CPSC441BlackJack/src/`
4) `javac */*.java`
5) `java server/Controller`
6) NOTE: you should see repeated error messages if firebase hasn't been served yet.

### Firebase Server:
1) Make sure you have `node/npm` installed.
2) `sudo npm install -g firebase-tools`
3) `git clone https://github.com/elvinlimpin/CPSC441BlackJackGUI.git`
4) `cd CPSC441BlackJackGUI/functions/`
5) `npm install && cd .. && npm install`
6) Either `firebase login` (auth from same host) or `firebase login --no-localhost` (auth from any host).
    * NOTE: must login with an admin-privilege google account (e.g. using our dummy account credentials).
7) `firebase serve`
n) In the future once everything is shut down, you should probably run `firebase logout`

### GUI(s):
1) Anybody can open `https://cpsc441blackjack.web.app/` in a browser (Chrome preferred).

### Java Client(s):
1) Make sure you have `java` and `javac` installed.
2) `git clone https://github.com/elvinlimpin/CPSC441BlackJack.git`
3) `cd CPSC441BlackJack/src/`
4) `javac */*.java`
5) `java client/User <server IP>`
    * NOTE: if client is running on same computer as servers, then just use `java client/User localhost`

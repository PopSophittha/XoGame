# XoGame
XoGame

For clone a XoGame project.
clone XoGame project in Android Studio then build the application into android mobile
or install by using this APK file -> https://drive.google.com/file/d/1YVUYgrEQr1R2sqswDJ_Yx66Hdw9L6mCV/view?usp=sharing

How to play.
1. When the application launches on a mobile screen, input a number of table sizes (maximum up to 8x8) then click on the start button to play a game.
2. Click on the reset button for restart to play game again or reset a table size.
3. Player able to replay previous game by clicking on the button in a history list.
remark: a game history will clear when close an application.

Application algorithm

  First, when the application receives a table size number. An application will setup a sqllite database.
Generate a gameState list, a gameState size will depend on num of table grids (default number in list is 2). 
And generate a winPosition list to collect a win position grid in the table. 
After setup a gameState and winPosition list is completed, it will display a table for starting to play.
  Then, when the player clicks on the table grid. The application will receive a grid position and collect it in gamePosition list and sqllite database.
Collect '0' for player1 position and '1' for player2(bot) position. After collecting a grid position in gameState list, 
an application will loop check gameState list with winPosition list to check a win player. 
  Moreover, if a player plays until full of table grid. an application will check a number of roundCount with allRoundCount.
if it equals an application will display draw game over.
  Last, when finished a game will record a game result in sqllite database and display data in the history list.
A user is able to click on the button in the list to view a game replay. 
  Finally, to restart a new game, a user is able to click on the reset button.

This is the second project in $\texttt{CSC1004}$.

1. This is a simple game of Gomoku (Five in a Row) based on the javaFX.
2. There are two mode in this project : Single Player Mode (AI) and Multi Player Mode. [AI Player ($10$ pts)]
3. There are four buttons in the game : `Pause`,`Save Game`,`Redo` and `Undo`. 
4. When you want to end the game and return to the main menu, or just want to pause the game, you can click the `Pause` button. 
5. When you want to save the game, you can click the `Save` button. At the same time, the main menu has a `Load Game` button to load the saved game. Note that in single player mode, you can only save the game in your own turn. And it is mandatory that only files with the suffix `.gomoku` can be recognized and used. [Save and Load ($5$ pts)]
6. In the single player mode, pressing `undo` will cancel the two-step chess pieces of your own side and the other side. Note that `redo` and `undo` will be disabled in the process of AI falling. [Undo and Redo (5$$ pts)]
7. There is a timer and a time bar to control the time of playing chess. The time limit for a single move is $30$ seconds, and the chess rights are exchanged over time. The time limit for a single game is $20$ minutes, and the overtime will be judged negative.[Time Limit ($5$ pts)]
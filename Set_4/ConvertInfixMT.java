/****	Sources
 * Marcus Turley
 * COSC-2436
 * Program Set #3
 * References
 * Myself:
 * External:
 * isNumber by Baeldung at: https://www.baeldung.com/java-check-string-number#:~:text=The%20NumberUtils.,parseInt(String)%2C%20Long.
 ****/
package Set_4;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Pattern;

import static Set_4.ConvertInfixMT.Game.Data.*;

public class ConvertInfixMT {
	public static void main(String[] args) {
		// Checks if user wants to execute program again
		do {
			SpaceScreen();
			ProblemOne();
			do {
				System.out.print("Run Again (Y / N) : ");
				ConvertInfixMT.User.UserChar();
			} while (ConvertInfixMT.User.UserInput().charAt(0) != 'Y' && ConvertInfixMT.User.UserInput().charAt(0) != 'N');
		} while (ConvertInfixMT.User.UserInput().charAt(0) != 'N');
	}
	
	// Solves the problem
	private static void ProblemOne() {
		// Inits the game states and gets the GameMode the user wants to play in
		SetGameMode(GameMode.None);
		
		// Sets up the GameBoard
		ConvertInfixMT.Game.Board _gameBoard = new ConvertInfixMT.Game.Board();
		//_gameBoard.InitializeBoards(_board, _useIndexLength);
		
		// Runs the algorithm the user input and answer
		//Algorithm.PutWordsOnBoard(_gameBoard);
		Play(_gameBoard);
		
		ConvertInfixMT.Game.Data.DisplayResults();
		SetGameState(GameState.End);
		SetGameResult(GameResult.None);
	}
	
	// Sets up the set of words to check against
	// It adds these words to the _board array then adds them to the GameBoard
	private static String[] _board;
	private static boolean _useIndexLength;
	
	// Plays the game
	private static void Play(ConvertInfixMT.Game.Board p_gameBoard) {
		for (int ___case = 1; ___case <= ConvertInfixMT.FileData.TestCases(); ___case++) {
			System.out.print("Enter expression: ");
			System.out.print("To Infix: ");
			String _line;
			String[] _lineArr;
			//do {
			//	_line = User.UserString();
			//} while (!Algorithm.IsInfix(_line));
			
			//_line = "A B + C / D - E -";
			//_lineArr = _line.split(" ");
			//System.out.println(Utils.ArrayU.ArrayToString(_lineArr));
			//Algorithm.ConvertToInfix(_line);
			
			_line = "* + 15 / + Z 7 9 / X 2";
			_lineArr = _line.split(" ");
			System.out.println(Utils.ArrayU.ArrayToString(_lineArr));
			Algorithm.ConvertToInfix(_line);
			
			_line = "/ * - + A B C - + A B D * * - A B C - A D";
			_lineArr = _line.split(" ");
			System.out.println(Utils.ArrayU.ArrayToString(_lineArr));
			Algorithm.ConvertToInfix(_line);
			
			//System.out.println();
			//_line = "* + 15 / + 7 9 / X 2";
			//_lineArr = _line.split(" ");
			//System.out.println(Utils.ArrayU.ArrayToString(_lineArr));
			//Algorithm.ConvertToInfix(_line);
			
			//SpaceScreen(2);
			//p_gameBoard.RenderBoard();
			//SpaceScreen();
			SpaceScreen();
		}
	}
	
	// Solves the problem with a given algorithm
	private static class Algorithm {
		/*
		private static TreeMap<Integer, String[]> m_opperators = new TreeMap<>() {{
			put(5, new String[] { "^" }); put(4, new String[] { "*", "/" }); put(2, new String[] { "+", "-" });
		}};
		*/
		private static final String[] m_opperators = new String[] { "^", "*", "/", "+", "-" };
		
		public static void ConvertToInfix(String p_string) {
			if(IsInfix(p_string)) {
				if(IsPrefix(p_string)) {
					System.out.println("Prefix: " + ComputeSolution(p_string));
					// For parentheses check if there are two number next to each other
				} else {
					System.out.println("PostFix" + ComputeSolution(p_string));
				}
			}
		}
		
		public static String ComputeSolution(String p_string) {
			String _result= "";
			List<String> _parts = new ArrayList<>(Arrays.stream(p_string.split(" ")).toList());
			
			// This allows for prefix and postfix to be done in one method
			// The left and right replace 'i' as the index access so that checks can be done starting from either left or right
			// It makes the code more compact as doing two methods takes a lot of extra lines
			boolean _isPrefix = IsPrefix(p_string);
			int __leftI, __rightI, _size = _parts.size();
			if(_isPrefix) {
				__leftI = _parts.size();
				__rightI = _parts.size();
			} else {
				__leftI = 0;
				__rightI = 0;
			}
			
			for (int i = 0; i < _size; i++) {
				// Updates left and right checks depending on if its prefix or not
				if(_isPrefix) {
					__leftI--; __rightI = _parts.size();
				} else __rightI++;
				
				String __part = "";
				if(_isPrefix) __part = _parts.get(__leftI);
				else __part = _parts.get(__rightI);
				if (Arrays.asList(m_opperators).contains((__part))) {
					// Use ignores and includes to determine what is an operator and what is not
					// This is used to get operands of the equation and put those on the ends of operators
					// This can also be achieved with while loops and a counter, but this is easier for me to read/write (though it's probably significantly less performant)
					List<String> _ignores = new ArrayList<>(Arrays.stream(m_opperators).toList());
					List<String> _includes = _parts.subList(__leftI, __rightI);
					
					// Finds the left operand
					String ___left = _parts.stream()
									.filter(x -> _includes.stream().anyMatch(x::equalsIgnoreCase))
									.filter(x -> _ignores.stream().noneMatch(x::equalsIgnoreCase)).findFirst().orElse(null);
					_ignores.add(___left);
					
					// Finds the right operand
					String ___right = _parts.stream()
									.filter(x -> _includes.stream().anyMatch(x::equalsIgnoreCase))
									.filter(x -> _ignores.stream().noneMatch(x::equalsIgnoreCase)).findFirst().orElse(null);
					_ignores.add(___right);
					
					//System.out.println("List: " + Utils.ArrayU.ArrayToString(_parts.toArray(String[]::new), 0) + " Length: " + _parts.size());
					// Writes out that part of the equation
					_result = "(" + ___left + " " + __part + " " + ___right + ")";
					
					
					//System.out.println("Includes: " + Utils.ArrayU.ArrayToString(_includes.toArray(String[]::new), 1));
					
					//System.out.println("Includes: " + _includes.get(0));
					//System.out.println("Includes: " + _includes.get(1));
					
					//System.out.println("Parts: " + Utils.ArrayU.ArrayToString(_parts.toArray(String[]::new), 0));
					// Replaces the original equation with the new one
					if(_isPrefix) {
						_parts.set(__leftI, _result);
						for (int ____j = 0; ____j < 2; ____j++)
							_parts.remove(__leftI + 1);
					} else {
						_parts.set(__rightI, _result);
						for (int ____j = 0; ____j < 2; ____j++)
							_parts.remove(__rightI - 1);
					}
					//System.out.println("Left: " + ___left);
					//System.out.println("Right: " + ___right);
					//System.out.println("Equation: " + ___equation);
					//System.out.println("I: " + i);
					//System.out.println("Parts: " + Utils.ArrayU.ArrayToString(_parts.toArray(String[]::new), 0));
					//System.out.println("Result: " + _result);
					//System.out.println();
				}
			}
			return _result.toString();
		}
		
		public static boolean IsInfix(String p_string) { return Arrays.stream(m_opperators).anyMatch(p_string::contains); }
		
		public static boolean IsPrefix(String p_string) { return Arrays.stream(m_opperators).anyMatch((p_string.charAt(0) + "")::equalsIgnoreCase); }
	}
	
	// Spaces the console a bit (replaces system('cls'))
	private static void SpaceScreen() { SpaceScreen(1, false); }
	private static void SpaceScreen(int p_size) { SpaceScreen(p_size, false); }
	private static void SpaceScreen(boolean p_newLine) { SpaceScreen(1, p_newLine); }
	private static void SpaceScreen(int p_size, boolean p_newLine) {
		if (p_newLine) System.out.println();
		for (int i = 0; i < p_size; i++) System.out.println("________________________________________________________________________________");
	}
	
	protected static class Game {
		// An enum for directional movement
		public enum Direction {
			Forward, Right, Back, Left, rUp, rDown, rRight, rLeft;
			
			public static ConvertInfixMT.Game.Direction toEnum(int p_val) {
				//p_val = p_val % MoveDirection.values().length;
				switch (p_val) {
					case 0 -> { return Forward; }
					case 1 -> { return Back; }
					case 2 -> { return Right; }
					case 3 -> { return Left; }
					case 4 -> { return rUp; }
					case 5 -> { return rDown; }
					case 6 -> { return rRight; }
					case 7 -> { return rLeft; }
				}
				return Forward;
			}
			
			public ConvertInfixMT.Game.Direction opposite() {
				switch (this) {
					case Forward -> { return Back; }
					case Back -> { return Forward; }
					case Right -> { return Left; }
					case Left -> { return Right; }
					case rUp -> { return rDown; }
					case rDown -> { return rUp; }
					case rRight -> { return rLeft; }
					case rLeft -> { return rRight; }
				}
				return Forward;
			}
			
			public int toInt() {
				switch (this) {
					case Forward -> { return 0; }
					case Back -> { return 1; }
					case Right -> { return 2; }
					case Left -> { return 3; }
					case rUp -> { return 4; }
					case rDown -> { return 5; }
					case rRight -> { return 6; }
					case rLeft -> { return 7; }
				}
				return 0;
			}
		}
		
		// Contains some general game data
		protected static class Data {
			public enum GameState { Playing, Initializing, Paused, End }
			public enum GameMode { None, Game, Demo }
			public enum GameResult { None, Won, Lost}
			
			private static ConvertInfixMT.Game.Data.GameState m_state = ConvertInfixMT.Game.Data.GameState.Initializing;
			private static ConvertInfixMT.Game.Data.GameMode m_mode = ConvertInfixMT.Game.Data.GameMode.None;
			private static ConvertInfixMT.Game.Data.GameResult m_result = ConvertInfixMT.Game.Data.GameResult.None;
			
			public static ConvertInfixMT.Game.Data.GameState GameState() { return m_state; }
			public static ConvertInfixMT.Game.Data.GameMode GameMode() { return m_mode; }
			public static ConvertInfixMT.Game.Data.GameResult GameResult() { return m_result; }
			
			public static void SetGameState(ConvertInfixMT.Game.Data.GameState p_state) { m_state = p_state; }
			public static void SetGameMode(ConvertInfixMT.Game.Data.GameMode p_mode) { m_mode = p_mode; }
			public static void SetGameResult(ConvertInfixMT.Game.Data.GameResult p_result) { m_result = p_result; }
			
			public static void Init() { Init(ConvertInfixMT.Game.Data.GameMode.None); }
			public static void Init(ConvertInfixMT.Game.Data.GameMode p_mode) {
				SetGameState(ConvertInfixMT.Game.Data.GameState.Initializing);
				SetGameMode(p_mode);
			}
			
			public static void GetGameMode() {
				do {
					System.out.println("Enter [D/d]-demo mode or [G/g]-game mode: ");
					ConvertInfixMT.User.UserChar();
					if (!ConvertInfixMT.User.IsLineEmpty()) {
						switch (Character.toUpperCase(ConvertInfixMT.User.UserInput().charAt(0))) {
							case 'G':
								SetGameMode(ConvertInfixMT.Game.Data.GameMode.Game);
								SetGameState(ConvertInfixMT.Game.Data.GameState.Playing);
								break;
							case 'D':
								SetGameMode(ConvertInfixMT.Game.Data.GameMode.Demo);
								SetGameState(ConvertInfixMT.Game.Data.GameState.Playing);
								break;
						}
					}
				} while (GameMode() == ConvertInfixMT.Game.Data.GameMode.None);
			}
			
			private static void DisplayResults() {
				switch (ConvertInfixMT.Game.Data.m_result) {
					case Won -> System.out.println("You won!!!\nCongradulations!!!");
					case Lost -> System.out.println("You lost...\nTry again!");
				}
			}
		}
		
		// Class that can generate and render a game board as text
		private static class Board {
			// Bunch of game variables
			public Board() {
				GameBoards.add(this);
				ActiveBoard = this;
			}
			
			public void Delete() {
				GameBoards.remove(this);
			}
			
			private static ConvertInfixMT.Game.Board ActiveBoard;
			
			public static ConvertInfixMT.Game.Board ActiveBoard() { return ActiveBoard; }
			
			
			private static List<ConvertInfixMT.Game.Board> GameBoards = new ArrayList<>();
			
			public static List<ConvertInfixMT.Game.Board> GameBoards() { return GameBoards; }
			// PlayBoard contains all moving assets that go on top of the GameBoard
			// GameBoard contains all the assets that are within the DisplayBoard's borders
			// DisplayBoard contains the parsed PlayBoard, GameBoard, and extra visual content (eg. margins, borders)
			
			private String[][] m_gameBoard, m_displayBoard, m_playBoard;
			
			public String[][] GetGameBoard() { return m_gameBoard; }
			public void GameBoardSetArray(String[][] p_arr) { m_gameBoard = p_arr; }
			public void GameBoardSetIndex(String p_str, int p_x, int p_y) { m_gameBoard[p_y][p_x] = p_str; }
			
			public String[][] DisplayBoard() { return m_displayBoard; }
			
			public String[][] PlayBoard() { return m_playBoard; }
			public void PlayBoardSetArray(String[][] p_arr) { m_playBoard = p_arr; }
			public void PlayBoardSetIndex(String p_str, int p_x, int p_y) { m_playBoard[p_y][p_x] = p_str; }
			public void PlayBoardRemoveIndex(String p_str) {
				int[] _index = ConvertInfixMT.Utils.ArrayU.Array2DFind(m_playBoard, p_str);
				if (_index[0] > -1 && _index[1] > -1) m_playBoard[_index[1]][_index[0]] = m_boardAssets[0];
			}
			public void PlayBoardSwapIndex(String p_str, int p_x, int p_y) {
				int[] _index = ConvertInfixMT.Utils.ArrayU.Array2DFind(m_playBoard, p_str);
				if (_index[0] > -1 && _index[1] > -1) ConvertInfixMT.Utils.ArrayU.Array2DSwap(m_playBoard, _index[0], _index[1], p_x, p_y);
			}
			
			private final String[] m_boardAssets = new String[]{ "?", "-", "|", "+", "." };
			public String[] BoardAssets() { return m_boardAssets; }
			private int m_gameWidth, m_gameHeight, m_displayWidth, m_displayHeight;
			public int Width() { return m_gameWidth; }
			public int Height() { return m_gameHeight; }
			public int DisplayWidth() { return m_displayWidth; }
			public int DisplayHeight() { return m_displayHeight; }
			
			private int m_gameMargins, m_gameBorder, m_gameRows, m_gameColumns;
			private int m_gameMin, m_gameMaxX, m_gameMaxY, m_displayMaxX, m_displayMaxY;
			public int Margins() { return m_gameMargins; }
			public int Border() { return m_gameBorder; }
			
			public int Rows() { return m_gameRows; }
			public int Columns() { return m_gameColumns; }
			
			public int Min() { return m_gameMin; }
			public int MaxX() { return m_gameMaxX; }
			public int DisplayMaxX() { return m_displayMaxX; }
			public int MaxY() { return m_gameMaxY; }
			public int DisplayMaxY() { return m_displayMaxY; }
			
			// Initializes and fills the GameBoard
			public void GenerateGameBoard(String[] p_arr) { GenerateGameBoard(p_arr, false); }
			public void GenerateGameBoard(String[] p_arr, boolean _useIndexLength) {
				m_gameBoard = new String[m_gameHeight][m_gameWidth];
				for (int _r = 0; _r < m_gameHeight; _r++) {
					for (int __c = 0; __c < m_gameWidth; __c++) {
						if(_useIndexLength) m_gameBoard[_r][__c] = p_arr[_r].charAt(__c) + "";
						else m_gameBoard[_r][__c] = p_arr[_r * m_gameWidth + __c];
					}
				}
			}
			
			// Initializes and fills the PlayBoard
			public void GeneratePlayBoard() {
				m_playBoard = new String[m_gameBoard.length][m_gameBoard[0].length];
				for (String[] _str : m_playBoard) Arrays.fill(_str, m_boardAssets[0]);
			}
			
			// Updates the DisplayBoard to contain certain assets from within the borders
			// Some assets can be ignored if specified by 'p_ignores' or 'p_ignore'
			public void UpdateDisplayBoard(String[][] p_board) { UpdateDisplayBoard(p_board, new String[]{ }); }
			
			public void UpdateDisplayBoard(String[][] p_board, String p_ignore) { UpdateDisplayBoard(p_board, new String[]{ p_ignore }); }
			
			public void UpdateDisplayBoard(String[][] p_board, String[] p_ignores) {
				int _notBlankCount = 0;
				for (int _r = m_gameMin; _r < m_displayMaxY; _r++) {
					if (Arrays.asList(m_displayBoard[_r]).contains(m_boardAssets[1])) {
						_notBlankCount++;
					}
					else {
						for (int __c = m_gameMin; __c < m_displayMaxX; __c++) {
							String __str = p_board[_r - m_gameMin - _notBlankCount][__c - m_gameMin];
							if (p_ignores.length > 0) {
								if (Arrays.stream(p_ignores).noneMatch(x -> Objects.equals(x, __str))) {
									m_displayBoard[_r][__c] = p_board[_r - m_gameMin - _notBlankCount][__c - m_gameMin];
								}
							}
							else m_displayBoard[_r][__c] = __str;
						}
					}
				}
			}
			
			// Prints the DisplayBoard to the screen
			// Also has an option to auto update the DisplayBoard with 'p_updateBoard' boolean
			public void RenderBoard() { RenderBoard(true); }
			
			public void RenderBoard(boolean p_updateBoard) {
				if (p_updateBoard) {
					UpdateDisplayBoard(m_gameBoard);
					UpdateDisplayBoard(m_playBoard, m_boardAssets[0]);
				}
				for (String[] _arr : m_displayBoard)
					System.out.println(ConvertInfixMT.Utils.ArrayU.ArrayToString(_arr, ConvertInfixMT.Utils.ArrayU.LongestStringIn2DArray(m_displayBoard)));
			}
			
			// Generates a template game board that can be resized with border and margins
			// Columns and Rows are a bit wanky but work fine if there are one or less of either
			public void InitializeBoards(String[] p_arr, boolean _useIndexLength) { InitializeBoards(p_arr, 2, 2, _useIndexLength); }
			public void InitializeBoards(String[] p_arr, int p_margins, int p_border, boolean _useIndexLength) { InitializeBoards(p_arr, 2, 2, 0, 0, _useIndexLength); }
			
			public void InitializeBoards(String[] p_arr) { InitializeBoards(p_arr, 2, 2); }
			public void InitializeBoards(String[] p_arr, int p_margins, int p_border) { InitializeBoards(p_arr, 2, 2, 0, 0, false); }
			
			public void InitializeBoards(String[] p_arr, int p_margins, int p_border, int p_rows, int p_columns, boolean _useIndexLength) {
				m_gameRows = p_rows;
				m_gameColumns = p_columns;
				m_gameMargins = p_margins;
				m_gameBorder = p_border;
				
				if (_useIndexLength) {
					m_gameWidth = p_arr[0].length();
					m_displayWidth = p_arr[0].length() + (m_gameColumns * m_gameBorder);
					m_gameHeight = p_arr.length;
					m_displayHeight = p_arr.length + (m_gameRows * m_gameBorder);
				} else {
					m_gameWidth = (int)Math.sqrt(p_arr.length);
					m_displayWidth = (int)Math.sqrt(p_arr.length + (m_gameColumns * m_gameBorder));
					m_gameHeight = (int)Math.sqrt(p_arr.length);
					m_displayHeight = (int)Math.sqrt(p_arr.length + (m_gameRows * m_gameBorder));
				}
				
				m_gameMin = m_gameBorder + m_gameMargins;
				m_gameMaxX = m_gameMin + m_gameWidth;
				m_gameMaxY = m_gameMin + m_gameHeight;
				m_displayMaxX = m_gameMin + m_displayWidth;
				m_displayMaxY = m_gameMin + m_displayHeight;
				
				//Sets size of game board
				m_displayBoard = new String[m_gameMin * 2 + m_displayHeight][m_gameMin * 2 + m_displayWidth];
				
				// Fills in margins
				for (String[] _str : m_displayBoard) Arrays.fill(_str, m_boardAssets[4]);
				
				//Fills in content
				for (int _r = m_gameMin; _r < m_displayMaxY; _r++) {
					for (int __c = m_gameMin; __c < m_displayMaxX; __c++) {
						m_displayBoard[_r][__c] = m_boardAssets[0];
					}
				}
				
				for (int _r = m_gameMin; _r < m_displayMaxY; _r++) {
					for (int __c = m_gameMin; __c < m_displayMaxX; __c++) {
						
						// Adds ceiling and floor
						if (_r == m_gameMin || _r == m_displayMaxY - 1) {
							for (int ___i = m_gameBorder; ___i > 0; ___i--) {
								m_displayBoard[_r + ___i * (_r > m_gameMin ? 1 : -1)][__c] = m_boardAssets[1];
							}
						}
						
						// Adds walls
						if (__c == m_gameMin || __c == m_displayMaxX - 1) {
							for (int ___i = m_gameBorder; ___i > 0; ___i--) {
								m_displayBoard[_r][__c + ___i * (__c > m_gameMin ? 1 : -1)] = m_boardAssets[2];
							}
						}
						
						// Adds corners
						if ((_r == m_gameMin || _r == m_displayMaxY - 1) || (__c == m_gameMin || __c == m_displayMaxX - 1)) {
							for (int ___i = m_gameBorder; ___i > 0; ___i--) {
								for (int ____j = m_gameBorder; ____j > 0; ____j--) {
									m_displayBoard[_r + ___i * (_r > m_gameMin ? 1 : -1)][__c + ____j * (__c > m_gameMin ? 1 : -1)] = m_boardAssets[3];
								}
							}
						}
						
						// Adds row dividers
						if (m_gameRows > 0 && ((_r + 1 - m_gameMin + (p_arr.length / (m_gameRows + 1))) % (p_arr.length / m_gameRows + 1)) / m_gameBorder == 0) {
							m_displayBoard[_r][__c] = m_boardAssets[1];
						}
						// Adds column dividers
						if (m_gameColumns > 0 && ((__c + 1 - m_gameMin + (p_arr[0].length() / (m_gameColumns + 1))) % (p_arr[0].length() / m_gameColumns + 1)) / m_gameBorder == 0) {
							m_displayBoard[_r][__c] = m_boardAssets[2];
						}
					}
				}
				
				GenerateGameBoard(p_arr, _useIndexLength);
				GeneratePlayBoard();
				UpdateDisplayBoard(m_gameBoard);
			}
			
			// Generates a template game board that can be resized with border and margins
			@Deprecated
			public void InitializeBoards(int p_width, int p_height, int p_rows, int p_columns, int p_margins, int p_border) {
				m_gameRows = p_rows;
				m_gameColumns = p_columns;
				m_gameMargins = p_margins;
				m_gameBorder = p_border;
				
				m_gameWidth = p_width + ((m_gameColumns) * m_gameBorder);
				m_gameHeight = p_height + (m_gameRows * m_gameBorder);
				
				m_gameMin = m_gameBorder + m_gameMargins;
				m_gameMaxX = m_gameMin + m_gameWidth;
				m_gameMaxY = m_gameMin + m_gameHeight;
				
				//Sets size of game board
				m_gameBoard = new String[m_gameMin * 2 + m_gameHeight][];
				for (int _r = 0; _r < m_gameBoard.length; _r++) {
					m_gameBoard[_r] = new String[m_gameMin * 2 + m_gameWidth];
				}
				
				// Fills in margins
				for (String[] strings : m_gameBoard) Arrays.fill(strings, m_boardAssets[4]);
				
				//Fills in content
				for (int _r = m_gameMin; _r < m_gameBoard.length - m_gameMin; _r++) {
					for (int __c = m_gameMin; __c < m_gameBoard[_r].length - m_gameMin; __c++) {
						m_gameBoard[_r][__c] = m_boardAssets[0];
					}
				}
				
				for (int _r = m_gameMin; _r < m_gameMaxY; _r++) {
					for (int __c = m_gameMin; __c < m_gameMaxX; __c++) {
						
						// Adds ceiling and floor
						if (_r == m_gameMin || _r == m_gameMaxY - 1) {
							for (int ___i = m_gameBorder; ___i > 0; ___i--) {
								m_gameBoard[_r + ___i * (_r > m_gameMin ? 1 : -1)][__c] = m_boardAssets[1];
							}
						}
						
						// Adds walls
						if (__c == m_gameMin || __c == m_gameMaxX - 1) {
							for (int ___i = m_gameBorder; ___i > 0; ___i--) {
								m_gameBoard[_r][__c + ___i * (__c > m_gameMin ? 1 : -1)] = m_boardAssets[2];
							}
						}
						
						// Adds corners
						if ((_r == m_gameMin || _r == m_gameMaxY - 1) || (__c == m_gameMin || __c == m_gameMaxX - 1)) {
							for (int ___i = m_gameBorder; ___i > 0; ___i--) {
								for (int ____j = m_gameBorder; ____j > 0; ____j--) {
									m_gameBoard[_r + ___i * (_r > m_gameMin ? 1 : -1)][__c + ____j * (__c > m_gameMin ? 1 : -1)] = m_boardAssets[3];
								}
							}
						}
						
						// Adds row dividers
						if (m_gameRows > 0 && ((_r + 1 - m_gameMin + (p_height / (m_gameRows + 1))) % (p_height / m_gameRows + 1)) / m_gameBorder == 0) {
							m_gameBoard[_r][__c] = m_boardAssets[1];
						}
						// Adds column dividers
						if (m_gameColumns > 0 && ((__c + 1 - m_gameMin + (p_width / (m_gameColumns + 1))) % (p_width / m_gameColumns + 1)) / m_gameBorder == 0) {
							m_gameBoard[_r][__c] = m_boardAssets[2];
						}
					}
				}
			}
		}
	}
	
	// Class that gives access to information from the user's file
	private static class FileData {
		private static String m_fileName;
		public static String FileName() { return m_fileName; }
		private static List<String> m_fileData;
		public static List<String> Data() { return m_fileData; }
		
		private static int m_testCaseCount = 1;
		public static int TestCases() { return m_testCaseCount; }
		public static int m_fileIndex;
		public static int FileIndex() { return m_fileIndex; }
		
		public static boolean m_beenRead;
		public static boolean BeenRead() { return m_beenRead; }
		
		// Gets the Data File name from the user
		public static void GetUserFileData() {
			m_beenRead = false;
			//Ensures file is read
			while (!m_beenRead) {
				try {
					m_fileData = new ArrayList<>();
					SpaceScreen();
					System.out.println("*Leave blank for default*");
					System.out.print("Enter filename: ");
					// Gets user file name
					m_fileName = ConvertInfixMT.User.UserString();
					System.out.println();
					
					// Default name for efficiency
					if (m_fileName.isEmpty()) //m_fileName = "bogglewords";
						m_fileName = "WordGameDictionary";
					if (m_fileName.contains(".")) m_fileName = m_fileName.substring(0, m_fileName.indexOf("."));
					
					try {
						File _file = new File(m_fileName + ".txt");
						Scanner _scanner = new Scanner(_file);
						while (_scanner.hasNextLine()) {
							m_fileData.add(_scanner.nextLine());
						}
						m_beenRead = true;
						_scanner.close();
						if (ConvertInfixMT.Utils.StringU.isStringNumber(m_fileData.get(0))) {
							m_testCaseCount = Integer.parseInt(m_fileData.get(0));
							m_fileIndex = 1;
						}
					} catch (FileNotFoundException e) {
						System.out.println("The file " + m_fileName + " could not be found.");
						System.out.println("Please try again!");
						m_fileName = "";
					}
				} catch (Exception ignored) { }
			}
		}
	}
	
	// Class that access user inputs
	private static class User {
		// Bunch of user input variables
		private static String m_userInput = "";
		private static String[] m_userStrings = { };
		private static final BufferedReader _bufferedReader = new BufferedReader(new InputStreamReader(System.in));
		
		public static String UserInput() { return m_userInput; }
		public static String[] UserInputs() { return m_userStrings; }
		
		public static String[] UserStrings() {
			List<String> _allUserStrings = new ArrayList<>();
			do {
				String _str = UserString();
				if(!IsLineEmpty()) _allUserStrings.add(_str);
			} while (!IsLineEmpty());
			m_userStrings = _allUserStrings.toArray(String[]::new);
			return _allUserStrings.toArray(String[]::new);
		}
		
		public ConvertInfixMT.User WaitForValid(String [] p_ignores) {
			return this;
		}
		
		// Gets the user's input as a string
		public static String UserString() {
			try { m_userInput = _bufferedReader.readLine(); } catch (Exception ignored) { }
			return m_userInput;
		}
		
		// Gets the user's input as a number
		public static float UserNum() {
			m_userInput = UserString();
			if (ConvertInfixMT.Utils.StringU.isStringNumber(m_userInput)) return Integer.parseInt(m_userInput);
			return -1;
		}
		
		// Gets the user's input as a char
		public static void UserChar() {
			String _str = UserString();
			if (_str == null || Objects.equals(_str, "")) m_userInput = "?";
			else m_userInput = _str.toUpperCase();
		}
		
		public static boolean IsLineEmpty() {
			return Objects.equals(m_userInput, "");
		}
	}
	
	protected static class Utils {
		// Class that has useful array manipulation methods
		private static class ArrayU {
			//	Swaps two indexes within an array
			public static <t_Any> boolean Array2DContains(t_Any[][] p_arr, t_Any p_value) {
				return Array2DContains(p_arr, p_value, 0, 0, p_arr.length, p_arr.length);
			}
			
			// Counts the number of times an item shows up in a 2D array
			public static <t_Any> int Array2DItemCount(t_Any[][] p_arr, t_Any p_value) { return Array2DItemCount(p_arr, p_value, 0, p_arr.length, 0, p_arr[0].length); }
			
			public static <t_Any> int Array2DItemCount(t_Any[][] p_arr, t_Any p_value, int p_x1, int p_y1, int p_x2, int p_y2) {
				int _counter = 0;
				for (int i = Math.max(p_y1, 0); i < Math.min(p_y2 + 1, p_arr.length); i++)
					for (int j = Math.max(p_x1, 0); j < Math.min(p_x2 + 1, p_arr[i].length); j++) {
						if (p_arr[i][j] == p_value) _counter++;
					}
				return _counter;
			}
			
			// Counts the number of times an item shows up in a 2D array
			public static <t_Any> int ArrayItemCount(t_Any[] p_arr, t_Any p_value) { return ArrayItemCount(p_arr, p_value, 0, p_arr.length); }
			
			public static <t_Any> int ArrayItemCount(t_Any[] p_arr, t_Any p_value, int p_y1, int p_y2) {
				int _counter = 0;
				for (int i = Math.max(p_y1, 0); i < Math.min(p_y2 + 1, p_arr.length); i++)
					if (p_arr[i] == p_value) _counter++;
				return _counter;
			}
			
			// Checks if an item of shows up in a 2D array
			public static <t_Any> boolean Array2DContains(t_Any[][] p_arr, t_Any p_value, int p_x1, int p_y1, int p_x2, int p_y2) {
				boolean _contains = false;
				for (int i = Math.max(p_y1, 0); i < Math.min(p_y2 + 1, p_arr.length); i++)
					for (int j = Math.max(p_x1, 0); j < Math.min(p_x2 + 1, p_arr[i].length); j++) {
						_contains |= p_arr[i][j] == p_value;
					}
				return _contains;
			}
			
			// Checks if an item of shows up in a 2D array
			public static <t_Any> int[] Array2DFind(t_Any[][] p_arr, t_Any p_value) {
				return Array2DFind(p_arr, p_value, 0, 0, p_arr[0].length, p_arr.length);
			}
			
			public static <t_Any> int[] Array2DFind(t_Any[][] p_arr, t_Any p_value, int p_x1, int p_y1, int p_x2, int p_y2) {
				int[] _index = { -1, -1 };
				for (int i = Math.max(p_y1, 0); i < Math.min(p_y2 + 1, p_arr.length); i++)
					for (int j = Math.max(p_x1, 0); j < Math.min(p_x2 + 1, p_arr[i].length); j++) {
						if (Objects.equals(p_arr[i][j], p_value)) {
							_index[0] = j;
							_index[1] = i;
							return _index;
						}
					}
				return _index;
			}
			
			//	Swaps two indexes within a 2D array
			public static <t_Any> void Array2DSwap(t_Any[][] p_arr, int p_x1, int p_y1, int p_x2, int p_y2) {
				t_Any _tmp = p_arr[p_y1][p_x1];
				p_arr[p_y1][p_x1] = p_arr[p_y2][p_x2];
				p_arr[p_y2][p_x2] = _tmp;
			}
			
			//	Swaps two indexes within an array
			public static <t_Any> void ArraySwap(t_Any[] p_arr, int p_index1, int p_index2) {
				t_Any _tmp = p_arr[p_index1];
				p_arr[p_index1] = p_arr[p_index2];
				p_arr[p_index2] = _tmp;
			}
			
			//	Converts an array to a String
			public static String ArrayToString(String[] p_arr) { return ArrayToString(p_arr, LongestStringInArray(p_arr)); }
			
			public static <t_Any> String ArrayToString(t_Any[] p_arr, int p_spacing) {
				if (p_arr == null || p_arr.length == 0) return "";
				String _result = "";
				for (t_Any __str : p_arr) _result += __str + " ";
				return ConvertInfixMT.Utils.StringU.SpaceEvenly(_result.substring(0, _result.length() - 1), " ", p_spacing);
			}
			
			// Finds the longest String within a 2D array
			public static int LongestStringIn2DArray(String[][] p_arr) {
				int _longestString = 0;
				for (String[] _arr : p_arr)
					_longestString = Math.max(_longestString, LongestStringInArray(_arr));
				return _longestString;
			}
			
			// Finds the longest String within an array
			public static int LongestStringInArray(String[] p_arr) {
				int _longestString = 0;
				for (String s : p_arr) if (s.length() > _longestString) _longestString = s.length();
				return _longestString;
			}
		}
		
		// Class that has useful string manipulation methods
		private static class StringU {
			// Spaces a String evenly
			@SuppressWarnings("SameParameterValue")
			public static String SpaceEvenly(String p_str, String p_split) { return SpaceEvenly(p_str, p_split, -1); }
			public static String SpaceEvenly(String p_str, String p_split, int p_spacing) {
				String _result = "";
				List<String> _items = Arrays.stream(p_str.split(p_split)).toList();
				if (p_spacing < 0) p_spacing = ConvertInfixMT.Utils.ArrayU.LongestStringInArray(_items.toArray(new String[0]));
				for (int i = 0; i < _items.size(); i++) {
					_result = _result.concat(_items.get(i));
					if (i < _items.size() - 1) for (int j = p_spacing + 1; j > _items.get(i).length(); j--)
						_result = _result.concat(" ");
				}
				return _result;
			}
			
			// Finds all the items within a string
			public static List<String> GetStringItems(String p_str, String p_splitStr) {
				List<String> _StringItems = new ArrayList<>();
				int _first = 0;
				if (p_str.contains(p_splitStr)) {
					for (int i = 0; i < p_str.length(); i++) {
						if (p_str.substring(i, p_splitStr.length()) == p_splitStr) {
							_StringItems.add(p_str.substring(_first, i - _first));
							_first = i + 1;
						}
					}
					_StringItems.add(p_str.substring(_first, p_str.length() - _first));
				}
				return _StringItems;
			}
			
			// Finds an item within a String.
			public static String GetStringItem(String p_str, String p_splitStr, int p_index) {
				p_index -= 1; // Increments the index by one so that when passing the index as a parameter it is from 1 - length. Only did this because an item number of 0 doesn't really make sense realistically
				String _result = "";
				_result = p_str.substring(FindAll(p_str, " ").get(p_index), FindAll(p_str, " ").get(Math.min(p_index + 1, FindAll(p_str, " ").size() - 1)) - 1);
				return _result;
			}
			
			// Finds the indexes of a split String
			public static List<Integer> FindAll(String p_str, String p_splitStr) {
				List<Integer> _indexes = new ArrayList<>();
				_indexes.add(0);
				int i = 0;
				while (i != -1) {
					_indexes.add(p_str.indexOf(p_splitStr, i) + 1);
					i = p_str.indexOf(p_splitStr, i);
					if (i >= p_str.lastIndexOf(p_splitStr)) break;
					i += 1;
				}
				_indexes.add(p_str.length() + 1);
				return _indexes;
			}
			
			//Checks if string is a number
			//Code by Baeldung at: https://www.baeldung.com/java-check-string-number#:~:text=The%20NumberUtils.,parseInt(String)%2C%20Long.
			public static boolean isStringNumber(String strNum) {
				final Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
				if (strNum == null) {
					return false;
				}
				return pattern.matcher(strNum).matches();
			}
			
			public static String[] WordsWithSubString;
			// Finds all words in an array containing a given substring
			// This functions like a searchbar with autofill
			public static String[] FindWordsWithSubstring(String[] p_words, String p_substr) {
				WordsWithSubString = Arrays.stream(p_words).filter(
								_word -> Objects.equals(_word.substring(0, Math.min(p_substr.length(), _word.length())), p_substr)
				).toArray(String[]::new);
				return WordsWithSubString;
			}
			
			private static class Arithmetic {
				public static int Add() {
					return 1;
				}
			}
		}
	}	
}

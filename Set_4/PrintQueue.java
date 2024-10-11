/****	Sources
 * Marcus Turley
 * COSC-2436
 * Program Set #4
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

import static Set_4.PrintQueue.Game.Data.*;

public class PrintQueue {
	public static void main(String[] args) {
		// Checks if user wants to execute program again
		do {
			SetMethods.SpaceScreen();
			ProblemOne();
			do {
				System.out.print("Run Again (Y / N) : ");
				PrintQueue.User.UserChar();
			} while (PrintQueue.User.UserInput().charAt(0) != 'Y' && PrintQueue.User.UserInput().charAt(0) != 'N');
		} while (PrintQueue.User.UserInput().charAt(0) != 'N');
	}
	
	// Solves the problem
	private static void ProblemOne() {
		// Inits the game states and gets the GameMode the user wants to play in
		SetGameMode(PrintQueue.Game.Data.GameMode.None);
		
		// Runs an algorithm to solve the given problem
		Solve();
		
		PrintQueue.Game.Data.DisplayResults();
		SetGameState(PrintQueue.Game.Data.GameState.End);
		SetGameResult(GameResult.None);
	}
	// Plays the game
	private static void Solve() {
		FileData.GetUserFileData();
		if(FileData.TestCasesCount() >= 1 && FileData.TestCasesCount() <= 100) {
			for (int __case = 1; __case <= PrintQueue.FileData.TestCasesCount(); __case++) {
				PriorityQueue<Integer> _jobQueue = new PriorityQueue<>();
				int _time = 0;
				
				String[] _queueData = FileData.GetCurrentLine().split(" ");
				if (Arrays.stream(_queueData).anyMatch((x -> !Utils.StringU.isStringNumber(x)))) {
					FileData.ChangeFileIndex(1);
					System.out.println("Could not process job because all inputs must be integers");
					continue;
				}
				int _size = Integer.parseInt(_queueData[0]), _position = Integer.parseInt(_queueData[1]);
				
				String[] _priorities = FileData.GetCurrentLine().split(" ");
				_size = Math.min(_size, _priorities.length);
				if (Arrays.stream(_priorities).anyMatch((x -> !Utils.StringU.isStringNumber(x)))) {
					System.out.println("Could not process job because all inputs must be integers");
					continue;
				}
				else if (_position > _size) {
					System.out.println("Could not process job because the position was greater than the job count");
					continue;
				}
				
				for (int i = 0; i < _size; i++) {
					int _priorityJob = Integer.parseInt(_priorities[_position]);
					int _currentJob = Integer.parseInt(_priorities[i]);
					_jobQueue.add(_currentJob);
					if(_currentJob <= _priorityJob) {
						_time++;
						_jobQueue.remove(_currentJob);
						System.out.print(_currentJob + " ");
					}
				}
				
				/*
				System.out.println("Queue: ");
				for (int _job : _jobQueue) {
					System.out.print(_job + " ");
				}
				System.out.println();
				*/
				if (_time > 1) System.out.println("Job " + __case + ": " + _time + " minutes");
				else System.out.println("Job " + __case + ": " + _time + " minute");
				//_jobQueue.add();
			}
		}
	}
	
	// Solves the problem with a given algorithm
	private static class Algorithm {
		
	}
	
	private static class SetMethods {
		// Spaces the console a bit (replaces system('cls'))
		private static void SpaceScreen () { SpaceScreen(1, false); }
		private static void SpaceScreen ( int p_size){ SpaceScreen(p_size, false); }
		private static void SpaceScreen ( boolean p_newLine){ SpaceScreen(1, p_newLine); }
		private static void SpaceScreen ( int p_size, boolean p_newLine){
			if (p_newLine) System.out.println();
			for (int i = 0; i < p_size; i++) System.out.println("________________________________________________________________________________");
		}
	}
	
	protected static class Game {
		// An enum for directional movement
		public enum Direction {
			Forward, Right, Back, Left, rUp, rDown, rRight, rLeft;
			
			public static PrintQueue.Game.Direction toEnum(int p_val) {
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
			
			public PrintQueue.Game.Direction opposite() {
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
			
			private static PrintQueue.Game.Data.GameState m_state = PrintQueue.Game.Data.GameState.Initializing;
			private static PrintQueue.Game.Data.GameMode m_mode = PrintQueue.Game.Data.GameMode.None;
			private static PrintQueue.Game.Data.GameResult m_result = PrintQueue.Game.Data.GameResult.None;
			
			public static PrintQueue.Game.Data.GameState GameState() { return m_state; }
			public static PrintQueue.Game.Data.GameMode GameMode() { return m_mode; }
			public static PrintQueue.Game.Data.GameResult GameResult() { return m_result; }
			
			public static void SetGameState(PrintQueue.Game.Data.GameState p_state) { m_state = p_state; }
			public static void SetGameMode(PrintQueue.Game.Data.GameMode p_mode) { m_mode = p_mode; }
			public static void SetGameResult(PrintQueue.Game.Data.GameResult p_result) { m_result = p_result; }
			
			public static void Init() { Init(PrintQueue.Game.Data.GameMode.None); }
			public static void Init(PrintQueue.Game.Data.GameMode p_mode) {
				SetGameState(PrintQueue.Game.Data.GameState.Initializing);
				SetGameMode(p_mode);
			}
			
			public static void GetGameMode() {
				do {
					System.out.println("Enter [D/d]-demo mode or [G/g]-game mode: ");
					PrintQueue.User.UserChar();
					if (!PrintQueue.User.IsLineEmpty()) {
						switch (Character.toUpperCase(PrintQueue.User.UserInput().charAt(0))) {
							case 'G':
								SetGameMode(PrintQueue.Game.Data.GameMode.Game);
								SetGameState(PrintQueue.Game.Data.GameState.Playing);
								break;
							case 'D':
								SetGameMode(PrintQueue.Game.Data.GameMode.Demo);
								SetGameState(PrintQueue.Game.Data.GameState.Playing);
								break;
						}
					}
				} while (GameMode() == PrintQueue.Game.Data.GameMode.None);
			}
			
			private static void DisplayResults() {
				switch (PrintQueue.Game.Data.m_result) {
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
			
			private static PrintQueue.Game.Board ActiveBoard;
			
			public static PrintQueue.Game.Board ActiveBoard() { return ActiveBoard; }
			
			
			private static List<PrintQueue.Game.Board> GameBoards = new ArrayList<>();
			
			public static List<PrintQueue.Game.Board> GameBoards() { return GameBoards; }
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
				int[] _index = PrintQueue.Utils.ArrayU.Array2DFind(m_playBoard, p_str);
				if (_index[0] > -1 && _index[1] > -1) m_playBoard[_index[1]][_index[0]] = m_boardAssets[0];
			}
			public void PlayBoardSwapIndex(String p_str, int p_x, int p_y) {
				int[] _index = PrintQueue.Utils.ArrayU.Array2DFind(m_playBoard, p_str);
				if (_index[0] > -1 && _index[1] > -1) PrintQueue.Utils.ArrayU.Array2DSwap(m_playBoard, _index[0], _index[1], p_x, p_y);
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
					System.out.println(PrintQueue.Utils.ArrayU.ArrayToString(_arr, PrintQueue.Utils.ArrayU.LongestStringIn2DArray(m_displayBoard)));
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
		public static int TestCasesCount() { return m_testCaseCount; }
		private static int m_fileIndex;
		public static int FileIndex() { return m_fileIndex; }
		public static void ChangeFileIndex(int p_amount) { m_fileIndex += p_amount; }
		public static String GetCurrentLine() {
			if(m_fileData.size() > m_fileIndex) {
				m_fileIndex++;
				return m_fileData.get(m_fileIndex - 1);
			}
			return "";
		};
		
		public static boolean m_beenRead;
		public static boolean BeenRead() { return m_beenRead; }
		
		// Gets the Data File name from the user
		public static void GetUserFileData() {
			m_beenRead = false;
			//Ensures file is read
			while (!m_beenRead) {
				try {
					m_fileData = new ArrayList<>();
					SetMethods.SpaceScreen();
					System.out.println("*Leave blank for default*");
					System.out.print("Enter filename: ");
					// Gets user file name
					m_fileName = PrintQueue.User.UserString();
					System.out.println();
					
					// Default name for efficiency
					if (m_fileName.isEmpty())
						m_fileName = "printer";
					if (m_fileName.contains(".")) m_fileName = m_fileName.substring(0, m_fileName.indexOf("."));
					
					try {
						File _file = new File(m_fileName + ".txt");
						Scanner _scanner = new Scanner(_file);
						while (_scanner.hasNextLine()) {
							m_fileData.add(_scanner.nextLine());
						}
						m_beenRead = true;
						_scanner.close();
						if (PrintQueue.Utils.StringU.isStringNumber(m_fileData.get(0))) {
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
		
		public PrintQueue.User WaitForValid(String [] p_ignores) {
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
			if (PrintQueue.Utils.StringU.isStringNumber(m_userInput)) return Integer.parseInt(m_userInput);
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
				return PrintQueue.Utils.StringU.SpaceEvenly(_result.substring(0, _result.length() - 1), " ", p_spacing);
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
				if (p_spacing < 0) p_spacing = PrintQueue.Utils.ArrayU.LongestStringInArray(_items.toArray(new String[0]));
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
	
	protected static class DataStructs {
		private static class Pair<t_Any1, t_Any2> {
			private t_Any1 _first;
			private t_Any2 _second;
			
			public t_Any1 First() { return _first; }
			public t_Any2 Second() { return _second; }
			
			public void SetFirst(t_Any1 p_first) { _first = p_first; }
			public void SetSecond(t_Any2 p_second) { _second = p_second; }
			
			Pair(t_Any1 p_first, t_Any2 p_second) {
				this._first = p_first; this._second = p_second;
			}
		}
	}
}

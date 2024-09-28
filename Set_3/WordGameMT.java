package Set_3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Pattern;


import static Set_3.RatEscapeMT.Entity.MoveDirection.rUp;
import static Set_3.WordGameMT.Game.Data.*;

public class WordGameMT {
	public static void main(String[] args) {
		// Checks if user wants to execute program again
		do {
			SpaceScreen();
			ProblemOne();
			do {
				System.out.print("Run Again (Y / N) : ");
				User.UserChar();
			} while (User.UserInput().charAt(0) != 'Y' && User.UserInput().charAt(0) != 'N');
		} while (User.UserInput().charAt(0) != 'N');
	}
	
	// Solves the problem
	private static void ProblemOne() {
		// Inits the game states and gets the GameMode the user wants to play in
		if(GameMode() == GameMode.None)
			GetGameMode();
		
		// Gets file data and ensures it exists
		// Sets up the dictionary and GameBoard
		WordDictionary.GetUserDictionaryData();
		Game.Board _gameBoard = new Game.Board();
		_gameBoard.InitializeBoards(_board, _useIndexLength);
		
		// Runs the algorithm the user input and answer
		//Algorithm.PutWordsOnBoard(_gameBoard);
		Play(_gameBoard);
		
		Game.Data.DisplayResults();
		SetGameState(GameState.End);
		SetGameResult(GameResult.None);
	}
	
	// Sets up the set of words to check against
	// It adds these words to the _board array then adds them to the GameBoard
	private static String[] _board;
	private static boolean _useIndexLength;
	
	private static void Play(Game.Board p_gameBoard) {
		for (int ___case = 1; ___case <= FileData.TestCases(); ___case++) {
			System.out.println("Generated Board: ");
			
			while (GameState() != GameState.End) {
				SpaceScreen(2);
				p_gameBoard.RenderBoard();
				SpaceScreen();
				System.out.println("Enter words or ** to quit: ");
				String _userInput = User.UserString();
				String[] _inputs = _userInput.split(" ");
				
				int _correctGuesses = 0;
				for(String _input : _inputs) {
					if (Arrays.stream(WordDictionary.CaptialWords()).anyMatch(_input.toUpperCase()::equalsIgnoreCase)) {
						_correctGuesses++;
					}
				}
				
				System.out.println();
				System.out.println("# of correct Guesses: " + _correctGuesses);
				System.out.println("# of incorrect guesses: " + (Algorithm.WordsAns().size() - _correctGuesses));
				System.out.println("# of words computer found: " + Algorithm.WordsAns().size());
				
				boolean _lose = _userInput.contains("**");
				boolean _won = _correctGuesses >= Algorithm.WordsAns().size();
				if (_lose || _won) {
					SetGameState(GameState.End);
					if(_lose) SetGameResult(GameResult.Lost);
					if(_won) SetGameResult(GameResult.Won);
				}
			}
			SpaceScreen();
		}
	}
	
	// Spaces the console a bit (replaces system('cls'))
	private static void SpaceScreen() { SpaceScreen(1); }
	private static void SpaceScreen(int p_size) {
		for (int i = 0; i < p_size; i++) System.out.println("________________________________________________________________________________");
	}
	
	private static class Algorithm {
		private static List<String> m_wordsAns;
		private static List<String> WordsAns() { return m_wordsAns; }
		private static List<String> m_lettersAns;
		private static List<String> LettersAnd() { return m_lettersAns; }
		public static void SetLetters(String[] p_words) { m_lettersAns = new ArrayList<>(); m_lettersAns.addAll(Arrays.asList(p_words)); }
		private static final String[] m_stringLetters = { "qu" };
		
		public static void CreatePuzzle(int p_size) {
			p_size = Math.min(p_size, (int)Math.sqrt(WordDictionary.WordCount()));
			int _area = (int)Math.pow(p_size, 2);
			m_wordsAns = new ArrayList<>();
			m_lettersAns = new ArrayList<>();
			List<Integer> _indexes = new ArrayList<>();
			
			for(int _i = 0; _i < Math.min(Math.pow(p_size, 3), WordDictionary.WordCount()); _i++) {
				int _value = (int)(Math.random() * WordDictionary.WordCount());
				if(!_indexes.contains(_value)) _indexes.add(_value);
			}
			Collections.shuffle(_indexes);
			
			int _areaLeft = _area - m_lettersAns.size();
			int _lastArea = _areaLeft;
			while (m_lettersAns.size() <= _area && !_indexes.isEmpty() && _areaLeft > 0) {
				if(p_size > 5 && _areaLeft < _lastArea) {
					System.out.println("Words found: " + _areaLeft);
					_lastArea = _areaLeft;
				}
				CheckIfAddable(_indexes.get(0), _areaLeft);
				_areaLeft = _area - m_lettersAns.size();
				_indexes.remove(0);
			}
			Collections.shuffle(m_lettersAns);
		}
		
		private static void CheckIfAddable(int p_index, int p_max) {
		String _word = WordDictionary.CaptialWords()[p_index];
			List<String> _tmpLetters = new ArrayList<>();
			if (!m_wordsAns.contains(_word)) {
				for (int _i = 0; _i < _word.length(); _i++) {
					String _letter = FindStringLetter(_word.substring(_i), _word.charAt(_i)).toUpperCase();
					if (Collections.frequency(m_lettersAns, _letter) < Collections.frequency(List.of(_word.split("")), _letter)) {
						_tmpLetters.add(_letter.toUpperCase());
					}
				}
				if (_tmpLetters.size() <= p_max) {
					m_wordsAns.add(_word.toUpperCase());
					_tmpLetters.forEach(_letter -> m_lettersAns.add(_letter.toUpperCase()));
				}
			}
		}
		
		private static String FindStringLetter(String p_word, char p_letter) {
			for (int _i = 0; _i < p_word.length(); _i++) {
				if(p_word.charAt(_i) == p_letter) {
					for (String _stringLetter : m_stringLetters) {
						if (Objects.equals(p_word.substring(_i, Math.min(_i + _stringLetter.length(), p_word.length())).toUpperCase(), _stringLetter.toUpperCase())) {
							return _stringLetter;
						}
					}
				}
			}
			return p_letter + "";
		}
		
		private static void AddNewLetters(int p_index) {
			String _word = m_wordsAns.get(p_index);
			
			for (int _i = 0; _i < m_wordsAns.get(p_index).length(); _i++) {
				String _letter = FindStringLetter(_word.substring(_i), _word.charAt(_i));
				
				
				int _wordLetterCount = Collections.frequency(Collections.singletonList(_word), _letter);
				int _lettersLetterCount = Collections.frequency(Collections.singletonList(_word), _letter);
				if (_wordLetterCount > _lettersLetterCount) m_lettersAns.add(_letter);
				_i += _letter.length() - 1;
			}
		}
		
		private static void PutWordsOnBoard(Game.Board p_gameBoard) {
			switch (Game.Data.GameMode()) {
				case Game: break;
				case Demo: Algorithm.LettersAnd().forEach(_letter -> {
					if(IsWordOnBoard(p_gameBoard, _letter))
						m_wordsAns.add(_letter);
				}); break;
			}
			System.out.println(Utils.ArrayU.ArrayToString(m_wordsAns.toArray(String[]::new)));
		}
		
		private static List<List<Game.Direction>> _previousDirections;
		private static boolean IsWordOnBoard(Game.Board p_gameBoard, String p_letter) {
				int[] _index = Utils.ArrayU.Array2DFind(p_gameBoard.GetGameBoard(), p_letter);
				Moveable _moveable = new Moveable(_index[0], _index[1]);
				
				String _partialWord = p_letter;
				Utils.StringU.WordsWithSubString = WordDictionary.CaptialWords();
				List<Game.Direction> _moveDirections = _moveable.AdjacentMoves(m_lettersAns.toArray(String[]::new));
				while (!_moveable.Adjacents().isEmpty()) {
					
					_moveDirections = _moveable.AdjacentMoves(
									m_lettersAns.toArray(String[]::new),
									_previousDirections.get(_partialWord.length() - 1).toArray(Game.Direction[]::new)
					);
					Game.Direction _moveDirection = _moveDirections.get(0);
					
					_moveable.Move(_moveDirection);
					_previousDirections.get(_partialWord.length() - 1).add(_moveDirection);
					_partialWord += p_gameBoard.GetGameBoard()[_moveable.Y()][_moveable.X()];
					
					if(_previousDirections.size() < _partialWord.length() - 1)_previousDirections.add(new ArrayList<>());
					for (Game.Direction _dir : _moveable.Adjacents()) {
						_previousDirections.get(_partialWord.length() - 1).add(_dir);
					}
					
					System.out.println(Utils.ArrayU.ArrayToString(Arrays.stream(_moveable.Pos()).boxed().toArray(), 2));
					for (Game.Direction _dir : _moveDirections)
						System.out.print(_dir.name());
					System.out.println();
					
					Utils.StringU.FindWordsWithSubstring(Utils.StringU.WordsWithSubString, _partialWord);
				}
				return false;
		}
		
	}
	
	private static class Moveable {
		public Moveable(int p_x, int p_y) {
			m_gameBoard = Game.Board.ActiveBoard();
			SetPos(p_x, p_y);
		}
		
		private int m_x, m_y;
		public int X() { return m_x; }
		public int Y() { return m_y; }
		public int[] Pos() { return new int[]{ m_x, m_y }; }
		
		public void SetX(int p_x) { m_x = p_x; }
		public void SetY(int p_y) { m_y = p_y; }
		public void SetPos(int p_x, int p_y) { SetX(p_x); SetY(p_y); }
		
		public void ChangeX(int p_x) { SetX(m_x + p_x); }
		public void ChangeY(int p_y) { SetY(m_y + p_y); }
		public void ChangePos(int p_x, int p_y) { ChangeX(p_x); ChangeY(p_y); }
		
		private int m_moveDistance;
		public int MoveDistance() { return m_moveDistance; }
		
		private Game.Direction m_moveDirection;
		public Game.Direction MoveDirection() { return m_moveDirection; }
		private int[] m_move = { 0, 0 }, m_roomMove = { 0, 0 }, m_rawRoomMove = { 0, 0 };
		public int[] Move() { return m_move; }
		public int[] MoveRoom() { return m_roomMove; }
		
		private Game.Board m_gameBoard;
		public Game.Board GameBoard() { return m_gameBoard; }
		
		public void Move(int p_dir) { Move(Game.Direction.toEnum(p_dir), 1); }
		public void Move(Game.Direction p_dir) { Move(p_dir, 1); }
		public void Move(Game.Direction p_dir, int p_distance) {
			if (p_dir.toInt() < rUp.toInt()) {
				m_move = GetMove(p_dir, p_distance);
				m_moveDirection = p_dir;
				m_moveDistance = p_distance;
				ChangePos(m_move[0], m_move[1]);
			}
			else {
				m_rawRoomMove = GetMove(p_dir, p_distance);
				m_moveDirection = p_dir;
				m_moveDistance = p_distance;
				ChangePos(m_rawRoomMove[0], m_rawRoomMove[1]);
			}
		}
		
		public boolean MoveContains(int[] p_move, String p_str) {
			if (p_move[1] + m_y > 0 && m_gameBoard.GetGameBoard().length > p_move[1] + m_y)
				if (p_move[0] + m_x > 0 && m_gameBoard.GetGameBoard()[p_move[1] + m_y].length > p_move[0] + m_x)
					return m_gameBoard.GetGameBoard()[p_move[1] + m_y][p_move[0] + m_x].equals(p_str);
			return false;
		}
		
		private int[] GetMove(Game.Direction p_dir, int m_dist) {
			int[] _move = { 0, 0 };
			switch (p_dir) {
				case Forward: _move[1] = -m_dist; break;
				case Back: _move[1] = m_dist; break;
				case Right: _move[0] = m_dist; break;
				case Left: _move[0] = -m_dist; break;
				case rUp: _move[1] = GetRoomMove(p_dir, m_dist); break;
				case rDown: _move[1] = -GetRoomMove(p_dir, m_dist); break;
				case rRight: _move[0] = GetRoomMove(p_dir, m_dist); break;
				case rLeft: _move[0] = -GetRoomMove(p_dir, m_dist); break;
			}
			return _move;
		}
		private int GetRoomMove(Game.Direction p_dir, int m_dist) {
			int _xMove = m_gameBoard.Width() / (m_gameBoard.Columns() + 1);
			int _yMove = m_gameBoard.Height() / (m_gameBoard.Rows() + 1);
			switch (p_dir) {
				case rUp: m_dist += _yMove; break;
				case rDown: m_dist += _yMove; break;
				case rRight: m_dist += _xMove; break;
				case rLeft: m_dist += _xMove; break;
			}
			return m_dist;
		}
		
		private List<Game.Direction> _adjacents;
		public List<Game.Direction> Adjacents() { return _adjacents; }
		public List<Game.Direction> AdjacentMoves(String[] p_checks) { return AdjacentMoves(p_checks, new Game.Direction[] { }); }
		public List<Game.Direction> AdjacentMoves(String[] p_checks, Game.Direction[] p_ignores) {
			_adjacents = new ArrayList<>();
			for (int _i = 0; _i < Game.Direction.values().length; _i++) {
				Game.Direction __dir = Game.Direction.toEnum(_i);
				if (Arrays.asList(p_ignores).contains(__dir)) continue;
				int[] _move = GetMove(__dir, 1);
				if (Arrays.stream(p_checks).anyMatch(x -> MoveContains(_move, x))) _adjacents.add(__dir);
			}
			for (int i = 0; i < Math.min(4, _adjacents.size()); i++)
				if (_adjacents.contains(Game.Direction.toEnum(i)) && _adjacents.contains(Game.Direction.toEnum(i + rUp.toInt())))
					Utils.ArrayU.ArraySwap(_adjacents.toArray(),
									_adjacents.indexOf(Game.Direction.toEnum(i)),
									_adjacents.indexOf(Game.Direction.toEnum(i + rUp.toInt())));
			for (Game.Direction _val : _adjacents)
				System.out.println(_val.name());
			return _adjacents;
		}
	}
	
	private static class WordDictionary {
		private static List<String> m_words = new ArrayList<>();
		public static String[] Words () { return m_words.stream().distinct().toArray(String[]::new); }
		public static String[] CaptialWords () {
			m_words.replaceAll(String::toUpperCase); return Words();
		}
		private static int m_wordCount;
		public static int WordCount () { return m_wordCount; }
		
		public static void Add(String p_word) { m_words.add(p_word); }
		public static void Add(String[] p_words) { m_words.addAll(Arrays.asList(p_words)); }
		public static void Clear() { m_words.clear(); }
		
		// Gets the dictionary the user wants to use
		private static void GetUserDictionaryData() {
			FileData.GetUserFileData();
			
			WordDictionary.Clear();
			WordDictionary.Add(FileData.Data().toArray(String[]::new));
			m_wordCount = m_words.size();
			System.out.println("Reading file: " + FileData.FileName() + ".txt");
			SpaceScreen();
			
			_board = new String[0];
			_useIndexLength = false;
			switch (GameMode()) {
				case Game:
					int ___boardSize = 4;
					while (___boardSize < 3 || ___boardSize > 10) {
						System.out.print("Enter the board size [3-10]: ");
						___boardSize = (int)User.UserNum();
						System.out.println();
						
						if(___boardSize < 1) {
							System.out.println("The board size " + ___boardSize + " is too small");
							System.out.println("It must be larger than 3");
							SpaceScreen();
						} else if (___boardSize > 10) {
							System.out.println("The board size " + ___boardSize + " is too large");
							System.out.println("It must be smaller than 10");
							SpaceScreen();
						}
					}
					System.out.println("Creating puzzle...\nPlease Wait.");
					SpaceScreen();
					Algorithm.CreatePuzzle(___boardSize);
					_board = Algorithm.LettersAnd().toArray(String[]::new);
					_useIndexLength = false;
					break;
				case Demo:
					System.out.println("Enter game board: ");
					String _tmp = "A B C D\n E F G H\n I J K L\n M N O P";
					_board = _tmp.split("\n");
					for (int _i = 0; _i < _board.length; _i++)
						_board[_i] = _board[_i].replace(" ", "").toUpperCase();
					_useIndexLength = true;
					Algorithm.SetLetters(_board);
					break;
			}
		}
	}
	
	protected static class Game {
		// An enum for directional movement
		public enum Direction {
			Forward, Right, Back, Left, rUp, rDown, rRight, rLeft;
			
			public static Direction toEnum(int p_val) {
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
			
			public Direction opposite() {
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
			
			private static GameState m_state = GameState.Initializing;
			private static GameMode m_mode = GameMode.None;
			private static GameResult m_result = GameResult.None;
			
			public static GameState GameState() { return m_state; }
			public static GameMode GameMode() { return m_mode; }
			public static GameResult GameResult() { return m_result; }
			
			public static void SetGameState(GameState p_state) { m_state = p_state; }
			public static void SetGameMode(GameMode p_mode) { m_mode = p_mode; }
			public static void SetGameResult(GameResult p_result) { m_result = p_result; }
			
			public static void Init() { Init(GameMode.None); }
			public static void Init(GameMode p_mode) {
				SetGameState(GameState.Initializing);
				SetGameMode(p_mode);
			}
			
			public static void GetGameMode() {
				do {
					System.out.println("Enter [D/d]-demo mode or [G/g]-game mode: ");
					User.UserChar();
					if (!User.IsLineEmpty()) {
						switch (Character.toUpperCase(User.UserInput().charAt(0))) {
							case 'G':
								SetGameMode(GameMode.Game);
								SetGameState(GameState.Playing);
								break;
							case 'D':
								SetGameMode(GameMode.Demo);
								SetGameState(GameState.Playing);
								break;
						}
					}
				} while (GameMode() == GameMode.None);
			}
			
			private static void DisplayResults() {
				switch (Data.m_result) {
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
		
		private static Board ActiveBoard;
		
		public static Board ActiveBoard() { return ActiveBoard; }
		
		;
		private static List<Board> GameBoards = new ArrayList<>();
		
		public static List<Board> GameBoards() { return GameBoards; }
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
				int[] _index = Utils.ArrayU.Array2DFind(m_playBoard, p_str);
				if (_index[0] > -1 && _index[1] > -1) m_playBoard[_index[1]][_index[0]] = m_boardAssets[0];
			}
			public void PlayBoardSwapIndex(String p_str, int p_x, int p_y) {
				int[] _index = Utils.ArrayU.Array2DFind(m_playBoard, p_str);
				if (_index[0] > -1 && _index[1] > -1) Utils.ArrayU.Array2DSwap(m_playBoard, _index[0], _index[1], p_x, p_y);
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
					System.out.println(Utils.ArrayU.ArrayToString(_arr, Utils.ArrayU.LongestStringIn2DArray(m_displayBoard)));
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
					m_fileName = User.UserString();
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
						if (Utils.StringU.isStringNumber(m_fileData.get(0))) {
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
		
		public User WaitForValid(String [] p_ignores) {
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
			if (Utils.StringU.isStringNumber(m_userInput)) return Integer.parseInt(m_userInput);
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
				return StringU.SpaceEvenly(_result.substring(0, _result.length() - 1), " ", p_spacing);
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
			public static String SpaceEvenly(String p_str, String p_split, int p_spacing) {
				String _result = "";
				List<String> _items = Arrays.stream(p_str.split(p_split)).toList();
				if (p_spacing == 0) p_spacing = ArrayU.LongestStringInArray(_items.toArray(new String[0]));
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
			public static String[] FindWordsWithSubstring(String[] p_words, String p_substr) {
				WordsWithSubString = Arrays.stream(p_words).filter(
									_word -> Objects.equals(_word.substring(0, Math.min(p_substr.length(), _word.length())), p_substr)
					).toArray(String[]::new);
				return WordsWithSubString;
			}
		}
	}
}
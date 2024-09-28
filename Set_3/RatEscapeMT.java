/****	Sources
 * Marcus Turley
 * COSC-2436
 * Program Set #2
 * References
 * Myself:
 * External:
 * isNumber by Baeldung at: https://www.baeldung.com/java-check-string-number#:~:text=The%20NumberUtils.,parseInt(String)%2C%20Long.
 ****/
package Set_3;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Pattern;

import static Set_3.RatEscapeMT.Entity.MoveDirection.*;

public class RatEscapeMT {
	public static void main(String[] args) {
		spaceScreen();
		// Checks if user wants to execute program again
		do {
			ProblemOne();
			do {
				System.out.print("Run Again (Y / N) : ");
				User.GetUserChar();
			} while (User.UserInput().charAt(0) != 'Y' && User.UserInput().charAt(0) != 'N');
		} while (User.UserInput().charAt(0) != 'N');
	}
	
	// Solves the problem
	private static void ProblemOne() {
		// Gets file data and ensures it exists
		while (FileData.FileName().isEmpty()) {
			FileData.GetUserFileData();
			if (!FileData.FileName().isEmpty()) {
				System.out.println("Reading file: " + FileData.FileName() + ".txt");
				for (int _case = 1; _case <= FileData.TestCases(); _case++) {
					GetBoardData();
					GameBoard _gameBoard = new GameBoard();
					_gameBoard.InitializeBoards(m_board, 1, 0, 2, 2);
					
					int[] _mouseIndex = ArrayUtilities.Array2DFind(_gameBoard.GetGameBoard(), "S");
					int[] _endIndex = ArrayUtilities.Array2DFind(_gameBoard.GetGameBoard(), "E");
					Entity mouse = new Entity("Mouse", "M", _mouseIndex[0], _mouseIndex[1]); // This will move the mouse to the goal;
					Entity start = new Entity("Start", "S", _mouseIndex[0], _mouseIndex[1]); // This will move the mouse to the goal;
					Entity end = new Entity("End", "E", _endIndex[0], _endIndex[1]); // This will be used to reverse engineer a pathway to the goal
					
					System.out.println("Board: ");
					_gameBoard.RenderBoard();
					
					_gameBoard.UpdateDisplayBoard(_gameBoard.GetGameBoard());
					/*
					mouse.Move(Right);
					_gameBoard.RenderBoard();
					mouse.Move(Back);
					_gameBoard.RenderBoard();
					mouse.Move(Back);
					_gameBoard.RenderBoard();
					mouse.Move(Back);
					_gameBoard.RenderBoard();
					*/
					System.out.println("Solved Board: ");
					// Path finds the fastest route to end by brute force
					Algorithm.m_gameBoard = _gameBoard;
					Algorithm.PathFind(start, end);
					
					// Macros are how I stored inputs for each path
					// Macro plays back inputs
					Macro.Play(mouse, 0);
					Macro.DeleteAll();
					
					_gameBoard.RenderBoard();
					_gameBoard.Delete();
				}
			}
		}
	}
	
	// Spaces the console a bit (replaces system('cls'))
	private static void spaceScreen() {
		for (int i = 0; i < 3; i++) System.out.println();
	}
	
	private static int m_layers, m_rows, m_columns;
	private static String[] m_board;
	
	private static void GetBoardData() {
		String _line = FileData.Data().get(FileData.m_fileIndex);
		
		m_layers = Integer.parseInt(StringUtilities.GetStringItem(_line, " ", 1));
		m_rows = Integer.parseInt(StringUtilities.GetStringItem(_line, " ", 2));
		m_columns = Integer.parseInt(StringUtilities.GetStringItem(_line, " ", 3)); // Columns are not really needed, but it's there
		
		FileData.m_fileIndex++;
		m_board = new String[m_layers * m_rows];
		for (int i = 0; i < m_board.length; i++) {
			_line = FileData.Data().get(FileData.m_fileIndex++);
			m_board[i] = _line;
		}
	}
	
	private static class Algorithm {
		public static void PathFind(Entity p_from, Entity p_to) {
			p_forks = new ArrayList<>();
			m_from = p_from;
			m_to = p_to;
		}
		
		public static GameBoard m_gameBoard;
		private static Entity m_from, m_to;
		private static List<int[]> p_forks;
		private static final Entity m_path = new Entity("Path", "P", 0, 0);
		
		public static void SearchPath(Macro p_macro) throws InterruptedException {
			m_path.SetPos(m_from.X(), m_from.Y());
			
			do {
				List<int[]> _previousPositions = new ArrayList<>();
				//System.out.println(m_to.Move()[0] + " " + m_to.Move()[1]);
				int _duplicateCount = 0;
				
				while (!Arrays.equals(m_path.Pos(), m_to.Pos()) && _duplicateCount < 2) {
					
					//for (int[] _arr : _previousPositions)
					//	System.out.println(ArrayUtilities.ArrayToString(Arrays.stream(_arr).boxed().toArray(), 1));
					//System.out.println(_previousPositions.forEach(x -> ArrayUtilities.ArrayItemCount(Arrays.stream(x).boxed().toArray(), _path.X())));
					_duplicateCount = 0;
					for (int[] _arr : _previousPositions)
						if (Arrays.equals(_arr, m_path.Pos())) _duplicateCount++;
					//System.out.println(_duplicateCount);
					
					if (m_path.AdjacentMoves().size() >= 2) p_forks.add(new int[]{ m_path.X(), m_path.Y(), 1 });
					
					//System.out.println(_previousPositions.contains(m_path.Pos()));
					System.out.println();
					System.out.println();
					Entity.MoveDirection _move = Forward;
					do {
						Entity.MoveDirection[] _ignores = { };
						if (m_path.MoveDirection() != null) _ignores = new Entity.MoveDirection[]{ m_path.MoveDirection().opposite() };
						if (m_path.MoveDirection() != null) System.out.println("T: " + _ignores[0].name());
						if (!m_path.AdjacentMoves(_ignores).isEmpty() && !_previousPositions.contains(m_path.Move())) {
							_move = m_path.AdjacentMoves(_ignores).get(0);
						}
						m_path.Move(_move);
					} while (_previousPositions.contains(m_path.Pos()));
					if (m_path.MoveDirection() != null) p_macro.AddInput(m_path.MoveDirection().toInt());
					//System.out.println(m_path.X());
					m_gameBoard.RenderBoard();
					Thread.sleep(100);
					_previousPositions.add(m_path.Pos());
				}
				//System.out.println(ArrayUtilities.ArrayItemCount(_previousPositions.toArray(), m_path.Pos()));
				
				if (!p_forks.isEmpty()) {
					p_forks.get(0)[2] += 1;
					m_path.SetPos(p_forks.get(0)[0], p_forks.get(0)[1]);
				}
				
			} while (!p_forks.isEmpty());
		}
	}
	
	private static class Macro {
		private static final ScoreBoard m_scoreBoard = new ScoreBoard();
		
		public static ScoreBoard ScoreBoard() { return m_scoreBoard; }
		
		private int m_recordingID;
		
		public int RecordingID() { return m_recordingID; }
		
		private void SetID(int p_id) { m_recordingID = p_id; }
		
		private final static List<Macro> Macros = new ArrayList<>();
		
		public static List<Macro> Macros() { return Macros; }
		
		private List<Integer> m_inputs;
		
		public List<Integer> Inputs() { return m_inputs; }
		
		public void AddInput(int p_input) {
			m_inputs.add(p_input);
			
		}
		
		public static void Record(int p_id) {
			Macro _macro = new Macro();
			_macro.m_recordingID = p_id;
			_macro.m_inputs = new ArrayList<>();
			
			Macros.add(_macro);
			
			try {
				Algorithm.SearchPath(_macro);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		/*
		if(p_id == 0) {
			_macro.AddInput(Right.toInt());
			_macro.AddInput(Back.toInt());
			_macro.AddInput(Right.toInt());
			_macro.AddInput(Right.toInt());
			_macro.AddInput(Right.toInt());
			_macro.AddInput(Right.toInt());
			_macro.AddInput(Forward.toInt());
			_macro.AddInput(Right.toInt());
			_macro.AddInput(rDown.toInt());
			m_scoreBoard.AddMoveCount(4);
			m_scoreBoard.AddMoveTime(5);
		} else if(p_id == 1) {
			_macro.AddInput(Right.toInt());
			_macro.AddInput(Back.toInt());
			_macro.AddInput(Right.toInt());
			m_scoreBoard.AddMoveCount(2);
			m_scoreBoard.AddMoveTime(6);
		} else {
			_macro.AddInput(Right.toInt());
			_macro.AddInput(Back.toInt());
			_macro.AddInput(Back.toInt());
			_macro.AddInput(Back.toInt());
			_macro.AddInput(Left.toInt());
			_macro.AddInput(rDown.toInt());
			m_scoreBoard.AddMoveCount(7);
			m_scoreBoard.AddMoveTime(3);
		}
		*/
			
			m_scoreBoard.AddMoveCount(1);
			m_scoreBoard.AddMoveTime(1);
			m_scoreBoard.AddBestMacro(_macro);
		}
		
		public static void Play(Entity p_entity, int p_index) {
			if (Macros().size() > p_index) {
				for (int _val : Macros().get(p_index).Inputs()) {
					p_entity.Move(_val);
				}
			}
		}
		
		public static void ShiftIDs(int p_index) {
			for (int _i = p_index; _i < Macros.size(); _i++) {
				Macro _macro = Macros.get(_i);
				_macro.SetID(_macro.RecordingID() - 1);
			}
		}
		
		public static void Delete(int p_id) {
			Macro _macro = Macros.get(p_id);
			m_scoreBoard.RemoveBestMacro(_macro);
			_macro.Inputs().clear();
			Macros.remove(_macro);
			ShiftIDs(_macro.RecordingID());
		}
		
		public static void DeleteAll() {
			for (int _i = 0; _i < Macros.size(); _i++) Delete(0);
		}
	}
	
	private static class ScoreBoard {
		public ScoreBoard() { }
		
		private int m_minMoveIndex;
		
		private int MinMovesIndex() { return m_minMoveIndex; }
		
		private final List<Integer> m_moveCounts = new ArrayList<Integer>();
		
		public List<Integer> MoveCounts() { return m_moveCounts; }
		
		public int MinMoves() {
			if (!m_moveCounts.isEmpty()) return m_moveCounts.stream().min(Integer::compare).get();
			return -1;
		}
		
		public void AddMoveCount(int p_moveCount) { m_moveCounts.add(p_moveCount); }
		
		private final List<Integer> m_moveTimes = new ArrayList<Integer>();
		
		public List<Integer> MoveTimes() { return m_moveTimes; }
		
		public int MinTime() {
			if (!m_moveCounts.isEmpty()) return m_moveTimes.stream().min(Integer::compare).get();
			return -1;
		}
		
		public void AddMoveTime(int p_moveTime) { m_moveTimes.add(p_moveTime); }
		
		public List<Macro> MacrosMinMoves = new ArrayList<>();
		public List<Macro> MacrosMinTimes = new ArrayList<>();
		
		public void AddBestMacro(Macro p_macro) {
			if (p_macro.Inputs().size() == MinMoves()) MacrosMinMoves = new ArrayList<>();
			if (p_macro.Inputs().size() < MinMoves() || MinMoves() < 1) MacrosMinMoves.add(p_macro);
			
			if (p_macro.Inputs().size() == MinTime()) MacrosMinTimes = new ArrayList<>();
			if (p_macro.Inputs().size() < MinTime() || MinMoves() < 1) MacrosMinTimes.add(p_macro);
		}
		
		public void RemoveBestMacro(Macro p_macro) {
			MacrosMinMoves.remove(p_macro);
			m_moveCounts.remove(p_macro.RecordingID());
			MacrosMinTimes.remove(p_macro);
			m_moveTimes.remove(p_macro.RecordingID());
		}
	}
	
	// Class that stores the mouse position
	protected static class Entity {
		public enum MoveDirection {
			Forward, Right, Back, Left, rUp, rDown, rRight, rLeft;
			
			public MoveDirection opposite() {
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
			public static MoveDirection toEnum(int p_val) {
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
		}
		
		public Entity(String p_name, String p_str, int p_x, int p_y) {
			m_gameBoard = GameBoard.ActiveBoard();
			m_name = p_name;
			m_sprite = p_str;
			SetPos(p_x, p_y);
		}
		
		private GameBoard m_gameBoard;
		public GameBoard GameBoard() { return m_gameBoard; }
		
		public void GameBoardSet(GameBoard p_gameBoard) { m_gameBoard = p_gameBoard; }
		
		private String m_name = "";
		public String Name() {
			return m_name;
		}
		private String m_sprite = "";
		public String Sprite() {
			return m_sprite;
		}
		private boolean m_visible = true;
		public void SetVisibility(boolean p_isVisible) { m_visible = p_isVisible; }
		public boolean Visible() { return m_visible; }
		private final String[] m_traversables = new String[]{ ".", "T" };
		public String[] Traversables() { return m_traversables; }
		
		private int m_x, m_y;
		public int X() { return m_x; }
		public int Y() { return m_y; }
		public int[] Pos() { return new int[]{ m_x, m_y }; }
		
		private MoveDirection m_moveDirection;
		public MoveDirection MoveDirection() { return m_moveDirection; }
		private int m_moveDistance;
		public int MoveDistance() { return m_moveDistance; }
		private int[] m_move = { 0, 0 }, m_roomMove = { 0, 0 }, m_rawRoomMove = { 0, 0 };
		public int[] Move() { return m_move; }
		public int[] MoveRoom() { return m_roomMove; }
		
		public void SetX(int p_x) { m_x = p_x; Render(); }
		public void SetY(int p_y) { m_y = p_y; Render(); }
		public void SetPos(int p_x, int p_y) { SetX(p_x); SetY(p_y); }
		
		public void ChangeX(int p_x) { SetX(m_x + p_x); Render(); }
		public void ChangeY(int p_y) { SetY(m_y + p_y); Render(); }
		public void ChangePos(int p_x, int p_y) { ChangeX(p_x); ChangeY(p_y); }
		
		public void Move(int p_dir) { Move(MoveDirection.toEnum(p_dir), 1); }
		public void Move(MoveDirection p_dir) { Move(p_dir, 1); }
		public void Move(MoveDirection p_dir, int p_distance) {
			if (p_dir.toInt() < rUp.toInt()) {
				m_moveDirection = p_dir;
				m_moveDistance = p_distance;
				if (CanMove()) {
					ChangePos(m_move[0], m_move[1]);
				}
			}
			else {
				m_moveDirection = p_dir;
				m_moveDistance = p_distance;
				if (CanSwitchingRooms()) {
					ChangePos(m_rawRoomMove[0], m_rawRoomMove[1]);
				}
			}
		}
		
		private void Render() {
			if (m_visible) {
				while (ArrayUtilities.Array2DContains(m_gameBoard.PlayBoard(), m_sprite)) {
					//System.out.print(ArrayUtilities.Array2DFind(m_gameBoard.PlayBoard(), m_sprite)[0] + " ");
					//System.out.println(ArrayUtilities.Array2DFind(m_gameBoard.PlayBoard(), m_sprite)[1]);
					m_gameBoard.PlayBoardRemoveIndex(m_sprite);
				}
				m_gameBoard.PlayBoardSetIndex(m_sprite, m_x, m_y);
				//if (ArrayUtilities.Array2DContains(m_gameBoard.PlayBoard(), m_sprite)) m_gameBoard.PlayBoardSwapIndex(m_sprite, m_x, m_y);
				//else m_gameBoard.PlayBoardSetIndex(m_sprite, m_x, m_y);
			}
		}
		
		public boolean CanMove() { return CanMove(m_moveDirection, m_moveDistance); }
		public boolean CanMove(MoveDirection p_dir, int p_distance) {
			m_move = GetMove(p_dir, p_distance);
			int[] _move = { m_move[0] + m_x, m_move[1] + m_y };
			
			int _sectionWidth = m_gameBoard.Width() / (m_gameBoard.Columns() + 1);
			int _sectionHeight = m_gameBoard.Height() / (m_gameBoard.Rows() + 1);
			
			int _minX = _sectionWidth * (_move[0] / _sectionWidth); int _maxX = _minX + _sectionWidth;
			int _minY = _sectionHeight * (_move[1] / _sectionHeight); int _maxY = _minY + _sectionHeight;
			
			boolean _canMove = true;
			_canMove &= _move[0] > _minX && _move[1] < _maxX;
			_canMove &= _move[1] > _minY && _move[1] < _maxY;
			if (_canMove) _canMove &= Arrays.stream(m_traversables).anyMatch(x -> MoveContains(m_move, x));
			return _canMove;
		}
		
		public boolean CanSwitchingRooms() { return CanSwitchingRooms(m_moveDirection, m_moveDistance); }
		public boolean CanSwitchingRooms(MoveDirection p_dir, int p_dist) {
			m_roomMove = GetMove(MoveDirection.toEnum(p_dir.toInt() % rUp.toInt()), p_dist);
			m_rawRoomMove = GetMove(p_dir, p_dist);
			boolean _switchingFloors = true;
			_switchingFloors &= m_rawRoomMove[0] + m_x >= 0 && m_rawRoomMove[0] + m_x < m_gameBoard.Width();
			_switchingFloors &= m_rawRoomMove[1] + m_y >= 0 && m_rawRoomMove[1] + m_y < m_gameBoard.Height();
			if (_switchingFloors) _switchingFloors &= Arrays.stream(m_traversables).anyMatch(x -> MoveContains(m_rawRoomMove, x));
			return _switchingFloors;
		}
		
		public List<MoveDirection> AdjacentMoves() { return AdjacentMoves(new MoveDirection[]{ }); }
		
		public List<MoveDirection> AdjacentMoves(MoveDirection[] p_ignores) {
			List<MoveDirection> _moves = new ArrayList<>();
			for (int _i = 0; _i < MoveDirection.values().length; _i++) {
				MoveDirection __dir = MoveDirection.toEnum(_i);
				//System.out.println("I: " + _i);
				if (Arrays.asList(p_ignores).contains(__dir)) continue;
				if (__dir.toInt() < rUp.toInt() && !CanMove(__dir, 1)) continue;
				if (__dir.toInt() >= rUp.toInt() && !CanSwitchingRooms(__dir, 1)) continue;
				//System.out.println();
				int[] _move = GetMove(__dir, 1);
				if (Arrays.stream(m_traversables).anyMatch(x -> MoveContains(_move, x))) _moves.add(__dir);
			}
			for (int i = 0; i < Math.min(4, _moves.size()); i++)
				if (_moves.contains(MoveDirection.toEnum(i)) && _moves.contains(MoveDirection.toEnum(i + rUp.toInt()))) ArrayUtilities.ArraySwap(_moves.toArray(), _moves.indexOf(MoveDirection.toEnum(i)), _moves.indexOf(MoveDirection.toEnum(i + rUp.toInt())));
			for (MoveDirection _val : _moves)
				System.out.println(_val.name());
			return _moves;
		}
		
		public boolean MoveContains(int[] p_move, String p_str) {
			if (p_move[1] + m_y > 0 && m_gameBoard.GetGameBoard().length > p_move[1] + m_y)
				if (p_move[0] + m_x > 0 && m_gameBoard.GetGameBoard()[p_move[1] + m_y].length > p_move[0] + m_x)
					return m_gameBoard.GetGameBoard()[p_move[1] + m_y][p_move[0] + m_x].equals(p_str);
			return false;
		}
		
		private int[] GetMove(MoveDirection p_dir, int m_dis) {
			int[] _move = { 0, 0 };
			switch (p_dir) {
				case Forward: _move[1] = -m_dis; break;
				case Back: _move[1] = m_dis; break;
				case Right: _move[0] = m_dis; break;
				case Left: _move[0] = -m_dis; break;
				case rUp: _move[1] = GetRoomMove(p_dir, m_dis); break;
				case rDown: _move[1] = -GetRoomMove(p_dir, m_dis); break;
				case rRight: _move[0] = GetRoomMove(p_dir, m_dis); break;
				case rLeft: _move[0] = -GetRoomMove(p_dir, m_dis); break;
			}
			return _move;
		}
		private int GetRoomMove(MoveDirection p_dir, int m_dist) {
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
	}
	
	// Class that can generate and render a game board as text
	private static class GameBoard {
		// Bunch of game variables
		public GameBoard() {
			GameBoards.add(this);
			ActiveBoard = this;
		}
		
		public void Delete() {
			GameBoards.remove(this);
		}
		
		private static GameBoard ActiveBoard;
		
		public static GameBoard ActiveBoard() { return ActiveBoard; }
		
		;
		private static List<GameBoard> GameBoards = new ArrayList<>();
		
		public static List<GameBoard> GameBoards() { return GameBoards; }
		
		;
		// PlayBoard contains all moving assets that go on top of the GameBoard
		// GameBoard contains all the assets that are within the DisplayBoard's borders
		// DisplayBoard contains the parsed PlayBoard, GameBoard, and extra visual content (eg. margins, borders)
		private String[][] m_gameBoard, m_displayBoard, m_playBoard;
		private final String[] m_boardAssets = new String[]{ "?", "-", "|", "+", "." };
		private int m_gameWidth, m_gameHeight, m_displayWidth, m_displayHeight;
		private int m_gameMargins, m_gameBorder, m_gameRows, m_gameColumns;
		private int m_gameMin, m_gameMaxX, m_gameMaxY, m_displayMaxX, m_displayMaxY;
		
		public String[][] PlayBoard() { return m_playBoard; }
		
		public void PlayBoardSetArray(String[][] p_arr) { m_playBoard = p_arr; }
		
		public void PlayBoardSetIndex(String p_str, int p_x, int p_y) { m_playBoard[p_y][p_x] = p_str; }
		
		public void PlayBoardRemoveIndex(String p_str) {
			int[] _index = ArrayUtilities.Array2DFind(m_playBoard, p_str);
			if (_index[0] > -1 && _index[1] > -1) m_playBoard[_index[1]][_index[0]] = m_boardAssets[0];
		}
		
		public void PlayBoardSwapIndex(String p_str, int p_x, int p_y) {
			int[] _index = ArrayUtilities.Array2DFind(m_playBoard, p_str);
			if (_index[0] > -1 && _index[1] > -1) ArrayUtilities.Array2DSwap(m_playBoard, _index[0], _index[1], p_x, p_y);
		}
		
		public String[][] GetGameBoard() { return m_gameBoard; }
		
		public void GameBoardSetArray(String[][] p_arr) { m_gameBoard = p_arr; }
		
		public void GameBoardSetIndex(String p_str, int p_x, int p_y) { m_gameBoard[p_y][p_x] = p_str; }
		
		public String[][] DisplayBoard() { return m_displayBoard; }
		
		public String[] BoardAssets() { return m_boardAssets; }
		
		public int Width() { return m_gameWidth; }
		
		public int DisplayWidth() { return m_displayWidth; }
		
		public int Height() { return m_gameHeight; }
		
		public int DisplayHeight() { return m_displayHeight; }
		
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
		public void GenerateGameBoard(String[] p_arr) {
			m_gameBoard = new String[m_gameHeight][m_gameWidth];
			for (int _r = 0; _r < m_gameHeight; _r++) {
				for (int __c = 0; __c < m_gameWidth; __c++) {
					m_gameBoard[_r][__c] = p_arr[_r].charAt(__c) + "";
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
				System.out.println(ArrayUtilities.ArrayToString(_arr, ArrayUtilities.LongestStringIn2DArray(m_displayBoard)));
		}
		
		// Generates a template game board that can be resized with border and margins
		// Columns and Rows are a bit wanky but work fine if there are one or less of either
		public void InitializeBoards(String[] p_arr, int p_rows, int p_columns, int p_margins, int p_border) {
			m_gameRows = p_rows;
			m_gameColumns = p_columns;
			m_gameMargins = p_margins;
			m_gameBorder = p_border;
			
			m_gameWidth = p_arr[0].length();
			m_displayWidth = p_arr[0].length() + (m_gameColumns * m_gameBorder);
			m_gameHeight = p_arr.length;
			m_displayHeight = p_arr.length + (m_gameRows * m_gameBorder);
			
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
			
			GenerateGameBoard(p_arr);
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
	
	// Class that gives access to information from the user's file
	private class FileData {
		private static String m_fileName = "";
		private static List<String> m_fileData;
		private static int m_testCaseCount;
		public static int m_fileIndex;
		
		public static String FileName() { return m_fileName; }
		
		public static List<String> Data() { return m_fileData; }
		
		public static int FileIndex() { return m_fileIndex; }
		
		public static int TestCases() { return m_testCaseCount; }
		
		// Gets the Data File name from the user
		public static void GetUserFileData() {
			try {
				m_fileData = new ArrayList<>();
				System.out.print("Enter filename (or enter blank for default): ");
				// Gets user file name
				m_fileName = User.GetUserString();
				// Default name for efficiency
				if (m_fileName.isEmpty()) if (Objects.equals(m_fileName, "")) m_fileName = "rat";
				if (m_fileName.contains(".")) m_fileName = m_fileName.substring(0, m_fileName.indexOf("."));
				
				try {
					File _file = new File(m_fileName + ".txt");
					Scanner _scanner = new Scanner(_file);
					while (_scanner.hasNextLine()) {
						m_fileData.add(_scanner.nextLine());
					}
					_scanner.close();
					m_testCaseCount = Integer.parseInt(m_fileData.get(0));
					m_fileIndex = 1;
				} catch (FileNotFoundException e) {
					System.out.println("The file " + m_fileName + " could not be found.");
					System.out.println("Please try again!");
					m_fileName = "";
				}
			} catch (Exception ignored) { }
		}
	}
	
	// Class that access user inputs
	private class User {
		// Bunch of user input variables
		private static String m_userInput = "";
		private static final BufferedReader _bufferedReader = new BufferedReader(new InputStreamReader(System.in));
		
		public static String UserInput() { return m_userInput; }
		
		// Gets the user's input as a string
		public static String GetUserString() {
			try { m_userInput = _bufferedReader.readLine(); } catch (Exception ignored) { }
			return m_userInput;
		}
		
		// Gets the user's input as a number
		public static float GetUserNum() {
			m_userInput = GetUserString();
			if (StringUtilities.isStringNumber(m_userInput)) return Integer.parseInt(m_userInput);
			return -1;
		}
		
		// Gets the user's input as a char
		public static void GetUserChar() {
			String _str = GetUserString();
			if (_str == null || Objects.equals(_str, "")) m_userInput = "?";
			else m_userInput = _str.toUpperCase();
		}
	}
	
	// Class that has useful array manipulation methods
	private class ArrayUtilities {
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
			return StringUtilities.SpaceEvenly(_result.substring(0, _result.length() - 1), " ", p_spacing);
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
	private class StringUtilities {
		// Spaces a String evenly
		@SuppressWarnings("SameParameterValue")
		public static String SpaceEvenly(String p_str, String p_split, int p_spacing) {
			String _result = "";
			List<String> _items = Arrays.stream(p_str.split(p_split)).toList();
			if (p_spacing == 0) p_spacing = ArrayUtilities.LongestStringInArray(_items.toArray(new String[0]));
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
	}
}
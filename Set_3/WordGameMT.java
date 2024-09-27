package Set_3;

public class WordGameMT {

//Enter [D/d]-demo mode or [G/g]-game mode: G
	
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
				m_board[i] =_line;
			}
		}
	}
}


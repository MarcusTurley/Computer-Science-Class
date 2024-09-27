package Set_2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Pattern;

/****	Sources
 * Marcus Turley
 * COSC-2436
 * Program Set #2
 * References
 * Myself:
 * External:
 	* isNumber by Baeldung at: https://www.baeldung.com/java-check-string-number#:~:text=The%20NumberUtils.,parseInt(String)%2C%20Long.
 	* Magic Square logic by Robert Gagnon At: https://www.1728.org/magicsq1.htm, https://www.1728.org/magicsq2.htm, https://www.1728.org/magicsq3.htm
 ****/

public class AllMagicSquaresMT {
	public static void main(String[] args) throws IOException {
		// Checks if user wants to execute program again
		do {
			spaceScreen();
			ProblemOne();
			
			do {
				System.out.print("Run Again (Y / N) : ");
				getUserChar();
			} while (m_userInput.charAt(0) != 'Y' && m_userInput.charAt(0) != 'N');
		} while (m_userInput.charAt(0) != 'N');
	}
	
	private static int m_boardSize;
	private static int m_magicNumber;
	// Solves the problem
	private static void ProblemOne() throws IOException {
		System.out.print("Enter size [3-20] : ");
		do {
			m_boardSize = (int)getUserNum();
		} while (!(m_boardSize >= 3) || !(m_boardSize <= 20));
		System.out.println("Magic Square Order : " + m_boardSize);
		
		m_magicNumber = (int)(m_boardSize * (Math.pow(m_boardSize, 2) + 1)) / 2;
		GenerateGameBoard(m_boardSize, m_boardSize, 1, 1, 2, 2);
		displayBoard();
		System.out.println("Magic Square Order : " + m_magicNumber + "\n");
	}
	
	// Spaces the console a bit (replaces system('cls'))
	private static void spaceScreen() {
		for (int i = 0; i < 3; i++) System.out.println();
	}
	
	// Updates game board visuals
	private static void displayBoard() {
		m_displayBoard = m_gameBoard;
		fillBoard();
		for(String[] _arr : m_displayBoard)
			System.out.println(arrayToString(_arr, longestStringInArray(m_displayBoard)));
	}
	
	// Fills the game board depending on magic number
	private static void fillBoard() {
		boolean _oddBoard = m_boardSize % 2 != 0, _singlyEvenBoard = (float)m_magicNumber / 2 == (float)(m_magicNumber / 2);
		if(m_boardSize % 2 != 0) {
			OddBoard(0, 0, m_boardSize, 1);
		} else {
			if (_singlyEvenBoard) {
				DoublyEvenBoard();
			} else {
				SinglyEvenBoard();
			}
		}
	}
	
	// Reorders the board as an odd magic square
	private static int OddBoard(int p_x, int p_y, int p_size, int p_counter) {
		int _xMin = p_x + m_outer, _xMax = p_x + p_size + m_outer - 1, _yMin = p_y + m_outer, _yMax = p_y + p_size + m_outer - 1;
		int _x = _xMin; int _y = _yMin;
		
		_x += (p_size - 1) / 2;
		m_displayBoard[_y][_x] = p_counter++ + "";
		do {
			boolean __yEdge = (_y - 1 < _yMin), ___xEdge = (_x + 1 > _xMax);
			boolean __isBlank = false;
			int __yWrap = p_y + ((p_size + _y - m_outer - 1) % p_size);
			int __xWrap = p_x + ((p_size + _x - m_outer + 1) % p_size);
			if(__yWrap + m_outer >= 0 && __xWrap + m_outer < m_displayBoard.length) __isBlank = !isNumber(m_displayBoard[__yWrap + m_outer][__xWrap + m_outer]);
			
			if(!___xEdge && __isBlank) _x += 1;
			if(!__yEdge && __isBlank) _y -= 1;
			if (!(__yEdge && ___xEdge) && ___xEdge) _x = _xMin;
			if(__yEdge || !__isBlank) {
				if (___xEdge || !__isBlank) {
					_y += 1;
					if(_y > _yMax) _y = _yMax - 1;
				} else _y = _yMax;
			}
			m_displayBoard[_y][_x] = p_counter++ + "";
		} while (array2DContains(m_displayBoard, m_boardAssets[0], _xMin, _yMin, _xMax, _yMax));
		return p_counter;
	}
	// Reorders the board as a singly even magic square
	private static void SinglyEvenBoard() {
		int _counter = 1;
		int _flipRange = (m_boardSize - 6) / 4 * 2 + 3;
		
		_counter = OddBoard(0, 0, _flipRange, _counter);
		_counter = OddBoard(m_boardSize / 2, m_boardSize / 2, _flipRange, _counter);
		_counter = OddBoard(m_boardSize / 2, 0, _flipRange, _counter);
		OddBoard(0, m_boardSize / 2, _flipRange, _counter);
		
		_flipRange = (m_boardSize - 6) / 4 + 2;
		
		for(int i = m_outer; i < _flipRange + m_outer; i++) {
			for (int j = m_outer; j < m_outer + m_boardSize / 2; j++) {
				if(i < _flipRange - 1 + m_outer) {
					if (j == m_outer + m_boardSize / 2 / 2)
						array2DSwap(m_displayBoard, i + 1, j, i + 1, j + m_boardSize / 2);
					else array2DSwap(m_displayBoard, i, j, i, j + m_boardSize / 2);
				}
			}
		}
		for(int i = m_outer + m_boardSize - _flipRange + 2; i < m_outer + m_boardSize; i++) {
			for (int j = m_outer; j < m_outer + m_boardSize / 2; j++) {
					array2DSwap(m_displayBoard, i, j, i, j + m_boardSize / 2);
			}
		}
	}
	// Reorders the board as a doubly even magic square
	private static void DoublyEvenBoard() {
		int _x = m_outer; int _y = m_outer; int _max = m_outer + m_boardSize - 1;
		int _counter = 1;
		
		boolean __down = true;
		boolean ___yEdge;
		boolean ___xEdge;
		do {
			if(__down) {
				___xEdge = (_x > _max);
				if(___xEdge) { _y += 1; _x = m_outer; }
				___yEdge = (_y > _max);
			} else {
				___xEdge = (_x < m_outer);
				if(___xEdge) { _y -= 1; _x = _max; }
				___yEdge = (_y < m_outer);
			}
			
			if(___yEdge) {
				_counter = 1;
				if(__down) {
					_x = _max;
					_y = _max;
				} else {
					_x = m_outer;
					_y = m_outer;
				}
				__down = !__down;
			}
			
			if(__down) {
				if(_x - m_outer < m_boardSize / 4 || _x - m_outer >= m_boardSize * 3 / 4 )
					if(_y - m_outer < m_boardSize / 4 || _y - m_outer >= m_boardSize * 3 / 4 )
						m_displayBoard[_y][_x] = _counter + "";
				if(_y - m_outer >= m_boardSize / 4 && _y - m_outer < m_boardSize * 3 / 4 )
					if(_x - m_outer >= m_boardSize / 4 && _x - m_outer < m_boardSize * 3 / 4 )
						m_displayBoard[_y][_x] = _counter + "";
			} else {
				if(_x - m_outer >= m_boardSize / 4 && _x - m_outer < m_boardSize * 3 / 4 )
					if(_y - m_outer < m_boardSize / 4 || _y - m_outer >= m_boardSize * 3 / 4 )
						m_displayBoard[_y][_x] = _counter + "";
				if(_y - m_outer >= m_boardSize / 4 && _y - m_outer < m_boardSize * 3 / 4 )
					if(_x - m_outer < m_boardSize / 4 || _x - m_outer >= m_boardSize * 3 / 4 )
						m_displayBoard[_y][_x] = _counter + "";
			}
			if(__down) _x += 1;
			else _x -= 1;
			_counter++;
		} while (array2DContains(m_displayBoard, m_boardAssets[0]));
	}
	
	// Bunch of game variables
	private static String[][] m_gameBoard, m_displayBoard;
	private static String[] m_boardAssets = new String[] {"?", "-", "|", "+", "."};
	private static int m_gameWidth, m_gameHeight, m_gameMargins, m_gameBorder, m_gameRows, m_gameColumns;
	private static int m_outer;
	// Generates a template game board that can be resized with border and margins
	private static void GenerateGameBoard(int p_width, int p_height, int p_rows, int p_columns, int p_margins, int p_border) {
		m_gameWidth = p_width; m_gameHeight = p_height; m_gameMargins = p_margins; m_gameBorder = p_border;
		m_gameRows = p_rows; m_gameColumns = p_columns;
		m_outer = m_gameBorder + m_gameMargins;
		int _rowSize = (m_gameHeight * m_gameRows);
		int _columnSize = (m_gameWidth * m_gameColumns);
		
		//Sets size of game board
		m_gameBoard = new String[(m_gameBorder * m_gameRows - m_gameBorder) + _rowSize + m_outer * 2][];
		for (int i = 0; i < m_gameBoard.length; i++) {
			m_gameBoard[i] = new String[(m_gameBorder * m_gameRows - m_gameBorder) + _rowSize + m_outer * 2];
		}
		
		// Fills in margins
		for (String[] strings : m_gameBoard) Arrays.fill(strings, m_boardAssets[4]);
		
		//Fills in content
		for (int i = m_outer; i < m_gameBoard.length - m_outer; i++) {
			for (int j = m_outer; j < m_gameBoard[i].length - m_outer; j++) {
				m_gameBoard[i][j] = m_boardAssets[0];
			}
		}
		
		for (int i = 0; i < m_gameBoard.length; i++) {
			// Fills in the columns
			if (i >= m_gameMargins && i < m_gameBoard.length - m_gameMargins) {
				for (int j = 0; j < m_gameBoard[i].length; j++) {
					if (j >= m_outer && j < m_gameBoard[i].length - m_outer) {
						if ((i < m_outer || i >= m_gameBoard.length - m_outer)) m_gameBoard[i][j] = m_boardAssets[1];
						if (p_border > 0 && (((i) % (m_gameHeight + 2)) / p_border) == 1) m_gameBoard[i][j] = m_boardAssets[1];
					}
				}
			}
			
			// Fills in the rows
			if (i >= m_outer && i < m_gameBoard.length - m_outer) {
				for (int j = 0; j < m_gameBoard[i].length; j++) {
					if (j >= m_gameMargins && j < m_gameBoard[i].length - m_gameMargins) {
						if ((j < m_outer || j >= m_gameBoard[i].length - m_outer)) m_gameBoard[i][j] = m_boardAssets[2];
						if (p_border > 0 && (((j) % (m_gameWidth + 2)) / p_border) == 1) m_gameBoard[i][j] = m_boardAssets[2];
					}
				}
			}
			
			// Fills in the corners
			if (i >= m_gameMargins && i < m_gameBoard.length - m_gameMargins) {
				if (i < m_outer || i > (1 + m_gameBoard.length) - (2 + m_outer))
					for (int j = 0; j < m_gameBoard[i].length; j++)
						if (j >= m_gameMargins && j < m_gameBoard[i].length - m_gameMargins)
							if (j < m_outer || j > (1 + m_gameBoard[i].length) - (2 + m_outer)) m_gameBoard[i][j] = m_boardAssets[3];
			}
		}
	}
	
	// Bunch of user input variables
	private static String m_userInput = "";
	private static final Scanner m_scanner = new Scanner(System.in);
	private static final BufferedReader _bufferedReader = new BufferedReader(new InputStreamReader(System.in));
	// Gets the user's input as a string
	private static String getUserString() throws IOException {
		m_userInput = _bufferedReader.readLine();
		return m_userInput;
	}
	private static float getUserNum() throws IOException {
		// Gets the user's input as a number
		m_userInput = getUserString();
		if(isNumber(m_userInput)) return Integer.parseInt(m_userInput);
		return -1;
	}
	// Gets the user's input as a char
	private static void getUserChar() throws IOException {
		String _str = getUserString();
		if(_str == null || Objects.equals(_str, "")) m_userInput = "?";
		else m_userInput = _str.toUpperCase();
	}
	//Checks if string is a number
	//Code by Baeldung at: https://www.baeldung.com/java-check-string-number#:~:text=The%20NumberUtils.,parseInt(String)%2C%20Long.
	private static boolean isNumber(String strNum) {
		final Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
		if (strNum == null) {
			return false;
		}
		return pattern.matcher(strNum).matches();
	}
	
	//	Swaps two indexes within an array
	private static <t_Any> boolean array2DContains(t_Any[][] p_arr, t_Any p_value) {
		return array2DContains(p_arr, p_value, 0, 0, p_arr.length, p_arr.length);
	}
	private static <t_Any> boolean array2DContains(t_Any[][] p_arr, t_Any p_value, int p_x1, int p_y1, int p_x2, int p_y2) {
		boolean _contains = false;
			for (int i = Math.max(p_y1, 0); i < Math.min(p_y2 + 1, p_arr.length); i++)
				for (int j = Math.max(p_x1, 0); j < Math.min(p_x2 + 1, p_arr[i].length); j++) {
					_contains |= p_arr[i][j] == p_value;
				}
		return _contains;
	}
	
	//	Swaps two indexes within an array
	private static <t_Any> void array2DSwap(t_Any[][] p_arr, int p_x1, int p_y1, int p_x2, int p_y2) {
		t_Any _tmp = p_arr[p_y1][p_x1];
		p_arr[p_y1][p_x1] = p_arr[p_y2][p_x2];
		p_arr[p_y2][p_x2]	= _tmp;
	}
	
	//	Swaps two indexes within an array
	private static <t_Any> void arraySwap(t_Any[] p_arr, int p_index1, int p_index2) {
		t_Any _tmp = p_arr[p_index1];
		p_arr[p_index1] = p_arr[p_index2];
		p_arr[p_index2]	= _tmp;
	}
	
	//	Converts an array to a String
	private static <t_Any> String arrayToString(t_Any[] p_arr, int p_spacing) {
		if(p_arr == null || p_arr.length == 0) return "";
		String _result = "";
		for(t_Any __str : p_arr) _result += __str + " ";
		return spaceEvenly(_result.substring(0, _result.length() - 1), " ", p_spacing);
	}
	
	// Spaces a String evenly
	@SuppressWarnings("SameParameterValue")
	private static String spaceEvenly(String p_str, String p_split, int p_spacing) {
		String _result = "";
		List<String> _items = Arrays.stream(p_str.split(p_split)).toList();
		if (p_spacing == 0) p_spacing = longestStringInArray(_items.toArray(new String[0]));
		for (int i = 0; i < _items.size(); i++) {
			_result = _result.concat(_items.get(i));
			if(i < _items.size() - 1) for (int j = p_spacing + 1; j > _items.get(i).length(); j--)
				 _result = _result.concat(" ");
		}
		return _result;
	}
	
	// Finds the longest String within a 2D array
	private static int longestStringInArray(String[][] p_arr) {
		int _longestString = 0;
		for (String[] _arr : p_arr)
			_longestString = Math.max(_longestString, longestStringInArray(_arr));
		return _longestString;
	}
	
	// Finds the longest String within an array
	private static int longestStringInArray(String[] p_arr) {
		int _longestString = 0;
		for (String s : p_arr) if (s.length() > _longestString) _longestString = s.length();
		return _longestString;
	}
}
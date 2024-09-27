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
 ****/

public class HitoriGameMT {
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
	
	// Solves the problem
	private static void ProblemOne() throws IOException {
		System.out.print("Enter size [4-15] : ");
		m_gameSize = (int)getUserNum();
		
		GenerateGameBoard(m_gameSize, m_gameSize, 1, 1, 2, 2);
		displayBoard();
	}
	
	// Spaces the console a bit (replaces system('cls'))
	private static void spaceScreen() {
		for (int i = 0; i < 3; i++) System.out.println();
	}
	
	// Updates game board visuals
	private static void displayBoard() throws IOException {
		m_displayBoard = m_gameBoard;
		fillBoard();
		System.out.println();
		removeMatches();
		
		for(String[] _arr : m_displayBoard)
			System.out.println(arrayToString(_arr, longestStringInArray(m_displayBoard)));
	}
	
	// Fills the game board depending on the board size
	private static void fillBoard() throws IOException {
		System.out.println("Enter puzzle : ");
		for(int _r = m_gameMin; _r < m_gameMax; _r++) {
			String _str = getUserString();
			for (int __c = 0; __c < Math.min(_str.length(), m_gameSize); __c++) {
				m_displayBoard[_r][__c + m_gameMin] = getStringItem(_str, " ", __c + 1);
			}
		}
	}
	
	// Removes any matching row/column numbers
	private static void removeMatches() {
		int _largestCount = 0;
		int[] _mostCheck = new int[m_gameSize];
		Arrays.fill(_mostCheck, -1);
		do {
			_largestCount = 0;
			for (int i = 1; i <= m_gameSize; i++) {
				int[] _matchIndex = findMostMatches(i + "", _mostCheck[i - 1]); // Searches for the number with the most amount of row/column matches
				if (_matchIndex[2] > 2) {
					m_displayBoard[_matchIndex[1]][_matchIndex[0]] = "#";
					_mostCheck[i - 1] = -1;
				}
				if (_matchIndex[0] != -1 && _matchIndex[1] != -1) {
					if (searchAdjacent(_matchIndex[0], _matchIndex[1], "#") >= 1) _mostCheck[i - 1] -= 1; // Checks if hashtag is adjacent
				}
				if(_matchIndex[2] > _largestCount)
					_largestCount = array2DItemCount(m_displayBoard, m_displayBoard[_matchIndex[1]][_matchIndex[0]],
								m_gameMin, m_gameMin, m_gameMax, m_gameMax);
			}
		} while (_largestCount > 1);
	}
	
	// Finds the most amount of string matches a row and column index
	private static int[] findMostMatches(String p_search, int p_max) {
		int _mostMatches = 0;
		int[] _index = {-1, -1, 0};
		for(int _r = m_gameMin; _r < m_gameMax; _r++) {
			for (int __c = m_gameMin; __c < m_gameMax; __c++) {
				if(m_displayBoard[_r][__c].equals(p_search)) {
					int _adjacentCount = searchRow(_r, p_search) + searchColumn(__c, p_search);
					if(_adjacentCount > _mostMatches) {
						if(_adjacentCount <= p_max || p_max == -1) {
							_mostMatches = _adjacentCount;
							_index = new int[]{ __c, _r, _mostMatches };
						}
					}
				}
			}
		}
		return _index;
	}
	
	// Searches indexes to the right, left, up, and down of an index to count the times a string occurs
	private static int searchAdjacent(int p_x, int p_y, String p_str) {
		int _count = 0;
		int _left = Math.max(p_x - 1, m_gameMin), _right = Math.min(p_x + 1, m_gameMax);
		int _down = Math.max(p_y - 1, m_gameMin), _up = Math.min(p_y + 1, m_gameMax);
		
		if(Objects.equals(m_displayBoard[p_y][_left], p_str)) _count++;
		if(Objects.equals(m_displayBoard[p_y][_right], p_str)) _count++;
		if(Objects.equals(m_displayBoard[_up][p_x], p_str)) _count++;
		if(Objects.equals(m_displayBoard[_down][p_x], p_str)) _count++;
		return _count;
	}
	
	// Counts the number of times a string appears in a row
	private static int searchRow(int p_row, String p_search) {
		int _count = 0;
		for (int _c = m_gameMin; _c < m_gameMax; _c++) {
			if(m_displayBoard[p_row][_c].equals(p_search)) _count++;
		}
		return _count;
	}
	
	// Counts the number of times a string appears in a column
	private static int searchColumn(int p_col, String p_search) {
		int _count = 0;
		for (int _r = m_gameMin; _r < m_gameMax; _r++) {
			if(m_displayBoard[_r][p_col].equals(p_search)) _count++;
		}
		return _count;
	}
	
	// Bunch of game variables
	private static String[][] m_gameBoard, m_displayBoard;
	private static String[] m_boardAssets = new String[] {"?", "-", "|", "+", "."};
	private static int m_gameWidth, m_gameHeight, m_gameMargins, m_gameBorder, m_gameRows, m_gameColumns;
	private static int m_gameSize;
	private static int m_gameMin, m_gameMax;
	// Generates a template game board that can be resized with border and margins
	private static void GenerateGameBoard(int p_width, int p_height, int p_rows, int p_columns, int p_margins, int p_border) {
		m_gameWidth = p_width; m_gameHeight = p_height; m_gameMargins = p_margins; m_gameBorder = p_border;
		m_gameRows = p_rows; m_gameColumns = p_columns;
		m_gameMin = m_gameBorder + m_gameMargins; m_gameMax = m_gameMin + m_gameSize;
		int _rowSize = (m_gameHeight * m_gameRows);
		int _columnSize = (m_gameWidth * m_gameColumns);
		
		//Sets size of game board
		m_gameBoard = new String[(m_gameBorder * m_gameRows - m_gameBorder) + _rowSize + m_gameMin * 2][];
		for (int i = 0; i < m_gameBoard.length; i++) {
			m_gameBoard[i] = new String[(m_gameBorder * m_gameRows - m_gameBorder) + _rowSize + m_gameMin * 2];
		}
		
		// Fills in margins
		for (String[] strings : m_gameBoard) Arrays.fill(strings, m_boardAssets[4]);
		
		//Fills in content
		for (int i = m_gameMin; i < m_gameBoard.length - m_gameMin; i++) {
			for (int j = m_gameMin; j < m_gameBoard[i].length - m_gameMin; j++) {
				m_gameBoard[i][j] = m_boardAssets[0];
			}
		}
		
		for (int i = 0; i < m_gameBoard.length; i++) {
			// Fills in the columns
			if (i >= m_gameMargins && i < m_gameBoard.length - m_gameMargins) {
				for (int j = 0; j < m_gameBoard[i].length; j++) {
					if (j >= m_gameMin && j < m_gameBoard[i].length - m_gameMin) {
						if ((i < m_gameMin || i >= m_gameBoard.length - m_gameMin)) m_gameBoard[i][j] = m_boardAssets[1];
						if (p_border > 0 && (((i) % (m_gameHeight + 2)) / p_border) == 1) m_gameBoard[i][j] = m_boardAssets[1];
					}
				}
			}
			
			// Fills in the rows
			if (i >= m_gameMin && i < m_gameBoard.length - m_gameMin) {
				for (int j = 0; j < m_gameBoard[i].length; j++) {
					if (j >= m_gameMargins && j < m_gameBoard[i].length - m_gameMargins) {
						if ((j < m_gameMin || j >= m_gameBoard[i].length - m_gameMin)) m_gameBoard[i][j] = m_boardAssets[2];
						if (p_border > 0 && (((j) % (m_gameWidth + 2)) / p_border) == 1) m_gameBoard[i][j] = m_boardAssets[2];
					}
				}
			}
			
			// Fills in the corners
			if (i >= m_gameMargins && i < m_gameBoard.length - m_gameMargins) {
				if (i < m_gameMin || i > (1 + m_gameBoard.length) - (2 + m_gameMin))
					for (int j = 0; j < m_gameBoard[i].length; j++)
						if (j >= m_gameMargins && j < m_gameBoard[i].length - m_gameMargins)
							if (j < m_gameMin || j > (1 + m_gameBoard[i].length) - (2 + m_gameMin)) m_gameBoard[i][j] = m_boardAssets[3];
			}
		}
	}
	
	// Bunch of user input variables
	private static String m_userInput = "";
	private static final BufferedReader _bufferedReader = new BufferedReader(new InputStreamReader(System.in));
	// Gets the user's input as a string
	private static String getUserString() throws IOException {
			m_userInput = _bufferedReader.readLine();
		return m_userInput;
	}
	// Gets the user's input as a number
	private static float getUserNum() throws IOException {
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
	
	
	// Counts the number of times an item shows up in a 2D array
	private static <t_Any> int array2DItemCount(t_Any[][] p_arr, t_Any p_value, int p_x1, int p_y1, int p_x2, int p_y2) {
		int _counter = 0;
		for (int i = Math.max(p_y1, 0); i < Math.min(p_y2 + 1, p_arr.length); i++)
			for (int j = Math.max(p_x1, 0); j < Math.min(p_x2 + 1, p_arr[i].length); j++) {
				if(p_arr[i][j] == p_value) _counter++;
			}
		return _counter;
	}
	
	// Checks if an item of shows up in a 2D array
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
	
	// Finds all the items within a string
	private static List<String> getStringItems(String p_str, String p_splitStr) {
		List<String> _StringItems = new ArrayList<>(); int _first = 0;
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
	private static String getStringItem(String p_str, String p_splitStr, int p_index) {
		p_index -= 1; // Increments the index by one so that when passing the index as a parameter it is from 1 - length. Only did this because an item number of 0 doesn't really make sense realistically
		String _result = "";
		_result = p_str.substring(findAll(p_str, " ").get(p_index), findAll(p_str, " ").get(Math.min(p_index + 1, findAll(p_str, " ").size() - 1)) - 1);
		return _result;
	}
	
	// Finds the indexes of a split String
	private static List<Integer> findAll(String p_str, String p_splitStr) {
		List<Integer> _indexes = new ArrayList<>();
		_indexes.add(0);
		int i = 0;
		while (i != -1) {
			_indexes.add(p_str.indexOf(p_splitStr, i) + 1);
			i = p_str.indexOf(p_splitStr, i);
			if(i >= p_str.lastIndexOf(p_splitStr)) break;
			i += 1;
		}
		_indexes.add(p_str.length() + 1);
		return _indexes;
	}
}
/****	Sources
 * Marcus Turley
 * COSC-2436
 * Program Set #4
 * References
 * Myself:
 * External:
 * isNumber by Baeldung at: https://www.baeldung.com/java-check-string-number#:~:text=The%20NumberUtils.,parseInt(String)%2C%20Long.
 ****/
package Set_1;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Pattern;

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
		// Runs an algorithm to solve the given problem
		Solve();
	}
	
	// Plays the game
	private static void Solve() {
		System.out.println("*Note: It automatically detects if it's Infix or Postfix*");
		System.out.println("Enter expression: ");
		String _line;
		String[] _lineArr;
		boolean _validInput;
		do {
			_line = User.UserString();
			_lineArr = _line.split(" ");
			
			// Helps account for user error
			_validInput = !_line.isEmpty();
			if(_validInput) {
				_validInput = Algorithm.IsValidEquation(_line);
				if(_validInput) {
					SpaceScreen();
					// Converts the string from Prefix to Infix
					// Added this for fun
					if (Algorithm.IsPrefix(_line)) System.out.print("From Prefix to Infix: ");
					// Converts the string from Postfix to Infix
					else if (Algorithm.IsPostfix(_line)) System.out.print("From Postfix to Infix: ");
					// Converts the string from Infix to Postfix
					else System.out.print("From Infix to Postfix: ");
					System.out.println(Algorithm.ConvertToInfix(_line));
				} else {
					System.out.println("This is not a valid equation");
					System.out.println("Please enter a valid equation:");
				}
			} else if(_line.isEmpty()) {
				System.out.println("You entered an empty string");
				System.out.println("Please enter a valid input:");
			}
		} while (!_validInput);
		
		SpaceScreen();
	}
	
	// Solves the problem with a given algorithm
	private static class Algorithm {
		private static final String[] m_operators = new String[] { "^", "*", "/", "+", "-" };
		
		public static String ConvertToInfix(String p_equation) {
			return SimplifyEquation(ComputeSolution(p_equation));
		}
		
		public static String ComputeSolution(String p_equation) {
			StringBuilder _result = new StringBuilder();
			List<String> _parts = new ArrayList<>(Arrays.stream(p_equation.split(" ")).toList());
			
			if(!p_equation.contains(" "))
				_parts = new ArrayList<>(Arrays.stream(p_equation.split("")).toList());
			
			// This allows for Infix to prefix, Infix to postfix and Postfix to Infix to be done in one method
			// It makes the code more compact as doing two methods takes a lot of extra lines
			boolean _isPrefix = IsPrefix(p_equation);
			boolean _isPostfix = IsPostfix(p_equation);
			for (int i = 0; i < _parts.size(); i++) {
				
				int _operatorIndex = i;
				if(_isPrefix) _operatorIndex = _parts.size() - i - 1;
				if (_parts.size() > _operatorIndex && Arrays.asList(m_operators).contains(_parts.get(_operatorIndex))) {
					String _operator = _parts.get(_operatorIndex);
					
					// Checks if the equation is in Prefix, Postfix, or Infix form and solves accordingly
					if (_isPrefix) {
						String left = _parts.get(_operatorIndex + 1);
						String right = _parts.get(_operatorIndex + 2);
						String part = "(" + left + " " + _operator + " " + right + ")";
						_parts.add(_operatorIndex + 3, part);
						for (int j = 0; j < 3; j++)
							_parts.remove(_operatorIndex);
						i -= 2;
					} else if (_isPostfix) {
						String left = _parts.get(_operatorIndex - 2);
						String right = _parts.get(_operatorIndex - 1);
						
						String part = "(" + left + " " + _operator + " " + right + ")";
						_parts.add(_operatorIndex + 1, part);
						for (int j = 0; j < 3; j++)
							_parts.remove(_operatorIndex - 2);
						i -= 2;
					} else {
						String left = _parts.get(_operatorIndex - 1);
						String right = _parts.get(_operatorIndex + 1);
						
						String part = left + " " + right + " " + _operator;
						_parts.add(_operatorIndex + 2, part);
						for (int j = 0; j < 3; j++)
							_parts.remove(_operatorIndex - 1);
						i -= 2;
					}
				}
			}
			
			for(String _str : _parts)
				_result.append(_str);
			return _result.toString();
		}
		
		public static String SimplifyEquation(String p_equation) {
			// Counter to get the amount of open and closed parenthesis
			int _openCount = 0;
			int _closedCount = 0;
			String _firstOperator = "";
			String _secondOperator = "";
			String _result = p_equation;
			
			for(int _i = 0; _i < p_equation.length(); _i++) {
				
				if (p_equation.charAt(_i) == '(') _openCount++;
				else if (p_equation.charAt(_i) == ')') _closedCount++;
				
				for (String _operator : m_operators) {
					if (Objects.equals(p_equation.substring(_i, Math.min(_i + _operator.length(), p_equation.length())), _operator)) {
						if(!_secondOperator.isEmpty())
							_firstOperator = _secondOperator;
						
						_secondOperator = _operator;
						
						if(OperatorPriority(_firstOperator) == OperatorPriority(_secondOperator)) {
							if (!_firstOperator.isEmpty() && _closedCount + 2 < Utils.StringU.FindAll(p_equation, ")").size()) {
								int _firstIndex = Utils.StringU.FindAll(p_equation, "(").get(_openCount - 2);
								int _secondIndex = Utils.StringU.FindAll(p_equation, ")").get(_closedCount);
								
								String _subString = p_equation.substring(_firstIndex, _secondIndex + 1);
								String _nSubString = _subString.replace("(", "").replace(")", "");
								_result = p_equation.replace(_subString, _nSubString);
							}
						}
					}
				}
			}
			return _result;
		}
		
		public static int OperatorPriority(String p_operator) {
			return switch (p_operator) {
				case "^" -> 4;
				case "*", "/" -> 3;
				case "+", "-" -> 2;
				default -> -1;
			};
		}
		
		public static boolean IsValidEquation(String p_string) {
			int _operandsCount = 0, _operatorsCount = 0;
			boolean _isValid = false;
			for(char _char : p_string.toCharArray()) {
				if(Arrays.stream(m_operators).anyMatch((_char + "")::equalsIgnoreCase))
					_operatorsCount++;
				else if(_char != ' ' && _char != '(' && _char != ')')
					_operandsCount++;
			}
			_isValid = (_operandsCount - 1 == _operatorsCount);
			if(!_isValid) {
				if(_operandsCount - 1 > _operatorsCount) System.out.println("There are too many operands.");
				if(_operatorsCount > _operandsCount - 1) System.out.println("There are too many operators.");
				SpaceScreen();
			}
			return _isValid;
		}
		
		public static boolean IsPrefix(String p_string) { return Arrays.stream(m_operators).anyMatch((p_string.charAt(0) + "")::equalsIgnoreCase); }
		
		public static boolean IsPostfix(String p_string) { return Arrays.stream(m_operators).anyMatch((p_string.charAt(p_string.length() - 1) + "")::equalsIgnoreCase); }
	}
	
	// Spaces the console a bit (replaces system('cls'))
	private static void SpaceScreen() { SpaceScreen(1, false); }
	private static void SpaceScreen(int p_size, boolean p_newLine) {
		if (p_newLine) System.out.println();
		for (int i = 0; i < p_size; i++) System.out.println("________________________________________________________________________________");
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
				
				List<Integer> _indexes = new ArrayList<>();
				_indexes.add(0);
				_indexes.addAll(FindAll(p_str, " "));
				_indexes.add(p_str.length());
				
				_result = p_str.substring(_indexes.get(p_index), _indexes.get(Math.min(p_index + 1, _indexes.size() - 1)) - 1);
				return _result;
			}
			
			// Finds the indexes of a split String
			public static List<Integer> FindAll(String p_str, String p_splitStr) {
				List<Integer> _indexes = new ArrayList<>();
				int _index = p_str.indexOf(p_splitStr);
				
				_indexes.add(_index);
				while (_index != -1) {
					_index = p_str.indexOf(p_splitStr, _index + 1);
					if(_index != -1) _indexes.add(_index);
				}
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

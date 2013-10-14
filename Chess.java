import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author Nikolay Feldman
 *
 */
public class Chess 
{
	private static Controller controller;
	private static Scanner scanner;
	private static boolean drawRequest, drawRequestTrigger;
	private static String pawnPromoTo;
	
	public static void main(String[] args) 
	{
		_init();
		
		while(true)
		{
			controller.printBoard();
			
			if (drawRequest)
				System.out.println("A draw was requested by the opponent. Type 'draw' to accept, or ignore it.");
			
			//check for checkmate, stalemate, and check
			boolean moveExists = false; //move exists for the pieces of users turn
			if (controller.turn == 'w') //whites turn
			{
				for (ChessPiece cp : controller.whitePieces)
				{
					ArrayList<Position> moves = controller.getValidMoves(cp);
					controller.updateValidMovesAvoidingCHECK(cp, moves);
					if (moves.size() > 0)
					{
						moveExists = true;
						break;
					}
				}
				
				if (!moveExists)
				{
					if (controller.checkWhite) //size is 0 so no moves, and we are checked, OPP WINS with checkmate
						System.out.println("CHECKMATE \nBlack wins!");
					else //no moves and we ARE NOT CHECKED, stalemate
						System.out.println("STALEMATE \nDraw!");
					
					System.exit(0);
				}
					
				if (controller.checkWhite)
					System.out.println("Check"); //check
				
				System.out.println("White's move: ");
			} else //blacks turn
			{
				for (ChessPiece cp : controller.blackPieces)
				{
					ArrayList<Position> moves = controller.getValidMoves(cp);
					controller.updateValidMovesAvoidingCHECK(cp, moves);
					if (moves.size() > 0)
					{
						moveExists = true;
						break;
					}
				}
				
				if (!moveExists)
				{
					if (controller.checkBlack) //size is 0 so no moves, and we are checked, OPP WINS with checkmate
						System.out.println("CHECKMATE \nWhite wins!");
					else //no moves and we ARE NOT CHECKED, stalemate
						System.out.println("STALEMATE \nDraw!");
					
					System.exit(0);
				}
					
				if (controller.checkBlack)
					System.out.println("Check"); //check
				
				System.out.println("Black's move: ");
			}
			
			
			
		
			Object[] coords = tokenize(scanner.nextLine());
			if (coords == null)
			{
				System.out.println("ERROR: Invalid input");
			} else if (coords[0] instanceof String)
			{
				if (coords[0].equals("resign"))
				{
					if (controller.turn == 'w')
						System.out.println("White resigns! Black wins!");
					else
						System.out.println("Black resigns! White wins!");
					System.exit(0);
				} else if (coords[0].equals("draw"))
				{
					if (drawRequest)
					{
						System.out.println("The game is a draw!");
						System.exit(0);
					} else
					{
						System.out.println("A draw was not requested. Request a draw by sending \"<pos1> <pos2> draw?\"");
						continue;
					}
				}		
			} else 
			{
				if (coords[2] != null)
				{
					if (coords[2].equals("draw") && drawRequest)
					{
						System.out.println("A draw was ALREADY requested, type in \"draw\" to accept. Replay your move.");
						continue;
					}
					
					//////////////////////////////////////////////////////////////////////////////////
					//last minute change, check if we are moving own piece before triggering draws
					ChessPiece cp = controller.getChessPieceAtPosition((Position)coords[0]);
					if (cp == null)
					{
						System.out.println("ERROR: You have selected to move a non-existant chess piece. Replay your move.");
						continue;
					}
					//check if own piece and not opponents
					if (cp.getColor() != controller.turn)
					{
						System.out.println("ERROR: You have selected your opponents chess piece. Replay your move.");
						continue;
					}
					/////////////////////////////////////////////////////////////////////////////////
					
					if (coords[2].equals("draw"))
					{
						drawRequest = true;
						drawRequestTrigger = true; //trigger that this draw is being asked by THIS CURRENT player... just my implementation...
					}
					
					//pawn promotion
					else if (cp.getType().equals("pawn"))
					{
						String p = ChessPiece.getType(coords[2].toString());
						if (p == null)
						{
							System.out.println("ERROR: ["+coords[2].toString()+"] is not a valid promotion type. Replay your move.");
							continue;
						}
						pawnPromoTo = p;
					}
				}  
				
				ChessPiece cp = controller.getChessPieceAtPosition((Position)coords[0]);
				
				if (cp == null)
				{
					System.out.println("ERROR: You have selected to move a non-existant chess piece. Replay your move.");
					continue;
				}
				//check if own piece and not opponents
				if (cp.getColor() != controller.turn)
				{
					System.out.println("ERROR: You have selected your opponents chess piece. Replay your move.");
					continue;
				}
				
				Position pn = (Position)coords[1];
				ArrayList<Position> validMoves = controller.getValidMoves(cp);
				controller.updateValidMovesAvoidingCHECK(cp, validMoves);
				
				//execute move for specific piece
				if (validMoves.size() > 0)
				{
					if (validMoves.contains(pn))
					{
						//check if pawn promotion
						if (cp.getType().equals("pawn") && (pn.getRow() == 0 || pn.getRow() == 7) )
						{
							cp.promote( (pawnPromoTo == null) ? "queen" : pawnPromoTo);
							pawnPromoTo = null;
						}
						
						controller.movePiece(cp, validMoves.get(validMoves.indexOf(pn)));
					} else
					{
						System.out.println("You have specified an invalid move! Here are valid moves for "+cp.getPosition()+" "+cp.getBoardCode()+": ");
						for (Position vm : validMoves)
							System.out.print(vm+" ");
						System.out.println();
						continue;
					}
				} else 
				{
					System.out.println("There are no valid moves for "+cp.getPosition()+" "+cp.getBoardCode()+". Enter another move.");
					continue;
				} 

				

				//check if we checked Opp
				if (controller.turn == 'w')
				{
					if (controller.isKingChecked('b')) //did we just check 
						controller.checkBlack = true;
					controller.checkWhite = false;
				} else
				{
					if (controller.isKingChecked('w')) //did we just check 
						controller.checkWhite = true;
					controller.checkBlack = false;
				}
				
				
				//switch turns
				controller.turn = (controller.turn == 'w') ? 'b' : 'w';
				if (drawRequest && !drawRequestTrigger) //needs to be in end
					drawRequest = false;
				else
					drawRequestTrigger = false;
			}
		}
	}
	
	/**
	 * tokenize a given input
	 * @param line - string to tokenize
	 * @return object array of tokens
	 */
	private static Object[] tokenize(String line)
	{
		Object[] tokens = new Object[3];
		if (line.equalsIgnoreCase("resign"))
		{
			tokens[0] = "resign";
			return tokens;
		}
		if (line.equalsIgnoreCase("draw"))
		{
			tokens[0] = "draw";
			return tokens;
		}
		Pattern reg = Pattern.compile("^ *([a-hA-H][1-8]) *([a-hA-H][1-8]) *$");
		Pattern drawAsk = Pattern.compile("^ *([a-hA-H][1-8]) *([a-hA-H][1-8]) *draw[?] *$");
		Pattern promo = Pattern.compile("^ *([a-hA-H][1-8]) *([a-hA-H][1-8]) *([qQrRbBnN]) *$");
		Matcher match = drawAsk.matcher(line);
		if (match.matches()) //draw was asked
		{
			tokens[0] = new Position(Integer.parseInt(match.group(1).toString().charAt(1)+"")-1, Controller.toIntFromChar(match.group(1).toString().toLowerCase().charAt(0)));
			tokens[1] = new Position(Integer.parseInt(match.group(2).toString().charAt(1)+"")-1, Controller.toIntFromChar(match.group(2).toString().toLowerCase().charAt(0)));
			tokens[2] = "draw";
			return tokens;
		}
		match = promo.matcher(line);
		if (match.matches()) //Promo was attempted
		{
			tokens[0] = new Position(Integer.parseInt(match.group(1).toString().charAt(1)+"")-1, Controller.toIntFromChar(match.group(1).toString().toLowerCase().charAt(0)));
			tokens[1] = new Position(Integer.parseInt(match.group(2).toString().charAt(1)+"")-1, Controller.toIntFromChar(match.group(2).toString().toLowerCase().charAt(0)));
			tokens[2] = match.group(3).toString();
			return tokens;
		}
		match = reg.matcher(line);
		if (match.matches()) //regular move
		{
			tokens[0] = new Position(Integer.parseInt(match.group(1).toString().charAt(1)+"")-1, Controller.toIntFromChar(match.group(1).toString().toLowerCase().charAt(0)));
			tokens[1] = new Position(Integer.parseInt(match.group(2).toString().charAt(1)+"")-1, Controller.toIntFromChar(match.group(2).toString().toLowerCase().charAt(0)));
			return tokens;
		}
		
		return null;
	}
	
	/**
	 * Initialization method - sets all initial settings and creates all ChessPieces
	 */
	public static void _init()
	{
		controller = new Controller();
		scanner = new Scanner(System.in);
		pawnPromoTo = null;
		
		//1 King, 1 Queen, 2 Rooks, 2 Bishops, 2 Knights and 8 Pawns
		controller.board[0][0] = new ChessPiece("rook", new Position(0,0), 'w');
		controller.board[0][1] = new ChessPiece("knight", new Position(0,1), 'w');
		controller.board[0][2] = new ChessPiece("bishop", new Position(0,2), 'w');
		controller.board[0][3] = new ChessPiece("queen", new Position(0,3), 'w');
		controller.board[0][4] = new ChessPiece("king", new Position(0,4), 'w');
		controller.board[0][5] = new ChessPiece("bishop", new Position(0,5), 'w');
		controller.board[0][6] = new ChessPiece("knight", new Position(0,6), 'w');
		controller.board[0][7] = new ChessPiece("rook", new Position(0,7), 'w');
		for (int i = 0; i < 8; i++) {
			controller.board[1][i] = new ChessPiece("pawn", new Position(1,i), 'w');
		}
		controller.board[7][0] = new ChessPiece("rook", new Position(7,0), 'b');
		controller.board[7][1] = new ChessPiece("knight", new Position(7,1), 'b');
		controller.board[7][2] = new ChessPiece("bishop", new Position(7,2), 'b');
		controller.board[7][3] = new ChessPiece("queen", new Position(7,3), 'b');
		controller.board[7][4] = new ChessPiece("king", new Position(7,4), 'b');
		controller.board[7][5] = new ChessPiece("bishop", new Position(7,5), 'b');
		controller.board[7][6] = new ChessPiece("knight", new Position(7,6), 'b');
		controller.board[7][7] = new ChessPiece("rook", new Position(7,7), 'b');
		for (int i = 0; i < 8; i++) {
			controller.board[6][i] = new ChessPiece("pawn", new Position(6,i), 'b');
		}
		
		for (int i = 0; i < 8; i++)
		{
			controller.whitePieces.add(controller.board[0][i]);
			controller.whitePieces.add(controller.board[1][i]);
			controller.blackPieces.add(controller.board[7][i]);
			controller.blackPieces.add(controller.board[6][i]);
		}
		controller.whiteKing = controller.board[0][4];
		controller.blackKing = controller.board[7][4];
	}
}

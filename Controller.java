import java.util.ArrayList;

/**
 * 
 * @author Nikolay Feldman
 *
 */
public class Controller {
	public ChessPiece[][] board;
	public ArrayList<ChessPiece> whitePieces, blackPieces; //all white/black pieces
	public ChessPiece whiteKing, blackKing; //reference to white/black kings
	public ChessPiece pawnEnpassant; //pawn moved the previous turn trigger FOR ENPASSANT
	public boolean checkWhite, checkBlack; //white or black is checked
	public char turn; //player's turn, either 'w' or 'b'
	
	public Controller()
	{
		board = new ChessPiece[8][8];
		turn = 'w';
		whitePieces = new ArrayList<ChessPiece>();
		blackPieces = new ArrayList<ChessPiece>();
		whiteKing = null;
		blackKing = null;
	}
	
	public void printBoard()
	{
		boolean hash = false;
		System.out.println();
		for (int r = 8; r > 0; r--)
		{
			for (int c = 0; c < 8; c++)
			{
				if (board[r-1][c] != null) 
					System.out.print(board[r-1][c].getBoardCode());
				else 
				{
					if (hash) 
						System.out.print("## ");
					else	
						System.out.print("   ");
				}
				
				hash = !hash;
			}
			System.out.println(r);
			hash = !hash;
		}
		System.out.println(" a  b  c  d  e  f  g  h");
		System.out.println();
	}
	
	public void movePiece(ChessPiece cp, Position pn)
	{

		
		//check for enpassant
		if (cp.getType().equals("pawn") && pawnEnpassant != null && board[(cp.getColor() == 'w')? pn.getRow()-1 : pn.getRow()+1][pn.getCol()] == pawnEnpassant)
		{
			ChessPiece opp = board[(cp.getColor() == 'w')? pn.getRow()-1 : pn.getRow()+1][pn.getCol()];
			board[(cp.getColor() == 'w')? pn.getRow()-1 : pn.getRow()+1][pn.getCol()] = null;
			if (opp.getColor() == 'w')
				whitePieces.remove(opp);
			else
				blackPieces.remove(opp);
		}
		
		//castling
		if (cp.getType().equals("king") && pn.getSpecialMoveWith() != null)
		{
			ChessPiece rook = pn.getSpecialMoveWith();
			if (cp.getPosition().getCol() < pn.getCol()) //castling right
			{
				board[pn.getRow()][pn.getCol()-1] = rook;
				board[rook.getPosition().getRow()][rook.getPosition().getCol()] = null;
				rook.updatePosition(new Position(pn.getRow(), pn.getCol()-1));
			} else { //castling left
				board[pn.getRow()][pn.getCol()+1] = rook;
				board[rook.getPosition().getRow()][rook.getPosition().getCol()] = null;
				rook.updatePosition(new Position(pn.getRow(), pn.getCol()+1));
			}
			
		}
		
		
		if (board[pn.getRow()][pn.getCol()] != null)
		{ //piece exists where we are moving, so remove it
			if (board[pn.getRow()][pn.getCol()].getColor() == 'w')
				whitePieces.remove(board[pn.getRow()][pn.getCol()]);
			else
				blackPieces.remove(board[pn.getRow()][pn.getCol()]);
			board[pn.getRow()][pn.getCol()] = null;
		}
		
		board[pn.getRow()][pn.getCol()] = cp;
		board[cp.getPosition().getRow()][cp.getPosition().getCol()] = null;
		cp.updatePosition(pn);
		
		pawnEnpassant = null;
		
		
		if (pn.checkPawnMoved2())
		{  //this cp is a pawn and moved 2
			this.pawnEnpassant = cp;
		}
	}
	
	
	
	
	
	
	
	
	/**
	 * get all valid moves that a chesspiece can do
	 * @param u - ChessPiece object
	 * @return
	 */
	public ArrayList<Position> getValidMoves(ChessPiece u)
	{
		ArrayList<Position> moves = new ArrayList<Position>();
		Position p = u.getPosition();
		int row = p.getRow();
		int col = p.getCol();
		char color = u.getColor();
		//if checked?
		if (u.getType().equals("pawn"))
		{
			if (color == 'w')
			{
				if (canMove(new Position(row+1, col), color, false)) 
				{
					moves.add(new Position(row+1, col));
					if (u.getNumMovements() == 0 && canMove(new Position(row+2, col), color, false))
						moves.add(new Position(row+2, col, true));
				}
				
				if (canMove(new Position(row+1, col+1), color, true))
				{
					if (pawnEnpassant != null && board[row][col+1] == pawnEnpassant)
						moves.add(new Position(row+1, col+1, pawnEnpassant));
					else if (board[row+1][col+1] != null && board[row+1][col+1].getColor() == 'b')
						moves.add(new Position(row+1, col+1));
				}
				if (canMove(new Position(row+1, col-1), color, true))
				{
					if (pawnEnpassant != null && board[row][col-1] == pawnEnpassant)
						moves.add(new Position(row+1, col-1, pawnEnpassant));
					else if (board[row+1][col-1] != null && board[row+1][col-1].getColor() == 'b')
						moves.add(new Position(row+1, col-1));
				}
			} else
			{
				if (canMove(new Position(row-1, col), color, false)) 
				{
					moves.add(new Position(row-1, col));
					if (u.getNumMovements() == 0 && canMove(new Position(row-2, col), color, false))
						moves.add(new Position(row-2, col, true));
				}
				if (canMove(new Position(row-1, col-1), color, true))
				{
					if (pawnEnpassant != null && board[row][col-1] == pawnEnpassant)
						moves.add(new Position(row-1, col-1, pawnEnpassant));
					else if (board[row-1][col-1] != null && board[row-1][col-1].getColor() == 'w')
						moves.add(new Position(row-1, col-1));
				}
				if (canMove(new Position(row-1, col+1), color, true))
				{
					if (pawnEnpassant != null && board[row][col+1] == pawnEnpassant)
						moves.add(new Position(row-1, col+1, pawnEnpassant));
					else if (board[row-1][col+1] != null && board[row-1][col+1].getColor() == 'w')
						moves.add(new Position(row-1, col+1));
				}
			}
		} 
		if (u.getType().equals("knight"))
		{
				if (canMove(new Position(row+2, col+1), color, true))
					moves.add(new Position(row+2, col+1));
				if (canMove(new Position(row+2, col-1), color, true))
					moves.add(new Position(row+2, col-1));
				if (canMove(new Position(row+1, col+2), color, true))
					moves.add(new Position(row+1, col+2));
				if (canMove(new Position(row-1, col+2), color, true))
					moves.add(new Position(row-1, col+2));
				if (canMove(new Position(row-2, col+1), color, true))
					moves.add(new Position(row-2, col+1));
				if (canMove(new Position(row-2, col-1), color, true))
					moves.add(new Position(row-2, col-1));
				if (canMove(new Position(row-1, col-2), color, true))
					moves.add(new Position(row-1, col-2));
				if (canMove(new Position(row+1, col-2), color, true))
					moves.add(new Position(row+1, col-2));
		} 
		if (u.getType().equals("rook") || u.getType().equals("queen"))
		{
			for (int r = row+1; true; r++)
			{
				if (canMove(new Position(r, col), color, false))
				{
					moves.add(new Position(r, col));
					continue;
				} else if (canMove(new Position(r, col), color, true))
				{
					moves.add(new Position(r, col));
					break;
				}
				else
					break;
			}
			for (int r = row-1; true; r--)
			{
				if (canMove(new Position(r, col), color, false))
				{
					moves.add(new Position(r, col));
					continue;
				} else if (canMove(new Position(r, col), color, true))
				{
					moves.add(new Position(r, col));
					break;
				}
				else
					break;
			}
			for (int c = col+1; true; c++)
			{
				if (canMove(new Position(row, c), color, false))
				{
					moves.add(new Position(row, c));
					continue;
				} else if (canMove(new Position(row, c), color, true))
				{
					moves.add(new Position(row, c));
					break;
				}
				else
					break;
			}
			for (int c = col-1; true; c--)
			{
				if (canMove(new Position(row, c), color, false))
				{
					moves.add(new Position(row, c));
					continue;
				} else if (canMove(new Position(row, c), color, true))
				{
					moves.add(new Position(row, c));
					break;
				}
				else
					break;
			}
		}
		if (u.getType().equals("bishop") || u.getType().equals("queen"))
		{
			for (int i = 1; true; i++)
			{
				if (canMove(new Position(row+i, col+i), color, false))
				{
					moves.add(new Position(row+i, col+i));
					continue;
				} else if (canMove(new Position(row+i, col+i), color, true))
				{
					moves.add(new Position(row+i, col+i));
					break;
				}
				else
					break;
			}
			for (int i = 1; true; i++)
			{
				if (canMove(new Position(row-i, col-i), color, false))
				{
					moves.add(new Position(row-i, col-i));
					continue;
				} else if (canMove(new Position(row-i, col-i), color, true))
				{
					moves.add(new Position(row-i, col-i));
					break;
				}
				else
					break;
			}
			for (int i = 1; true; i++)
			{
				if (canMove(new Position(row+i, col-i), color, false))
				{
					moves.add(new Position(row+i, col-i));
					continue;
				} else if (canMove(new Position(row+i, col-i), color, true))
				{
					moves.add(new Position(row+i, col-i));
					break;
				}
				else
					break;
			}
			for (int i = 1; true; i++)
			{
				if (canMove(new Position(row-i, col+i), color, false))
				{
					moves.add(new Position(row-i, col+i));
					continue;
				} else if (canMove(new Position(row-i, col+i), color, true))
				{
					moves.add(new Position(row-i, col+i));
					break;
				} 
				else
					break;
			}
		}
		if (u.getType().equals("king"))
		{
			if (canMove(new Position(row+1, col-1), color, true))
				moves.add(new Position(row+1, col-1));
			if (canMove(new Position(row+1, col), color, true))
				moves.add(new Position(row+1, col));
			if (canMove(new Position(row+1, col+1), color, true))
				moves.add(new Position(row+1, col+1));
			if (canMove(new Position(row, col+1), color, true))
				moves.add(new Position(row, col+1));
			if (canMove(new Position(row, col-1), color, true))
				moves.add(new Position(row, col-1));
			if (canMove(new Position(row-1, col-1), color, true))
				moves.add(new Position(row-1, col-1));
			if (canMove(new Position(row-1, col), color, true))
				moves.add(new Position(row-1, col));
			if (canMove(new Position(row-1, col+1), color, true))
				moves.add(new Position(row-1, col+1));
			
			if (u.getNumMovements() == 0 && canMove(new Position(row, col+1), color, false) && canMove(new Position(row, col+2), color, false))
			{ // check if can castle right
				ChessPiece rook = board[row][7];
				if (rook != null && rook.getType().equals("rook") && rook.getNumMovements() == 0)
				{
					ArrayList<Position> rookMoves = getValidMoves(rook);
					if (rookMoves.size() > 0 && rookMoves.contains(new Position(row, col+1)))
						moves.add(new Position(row, col+2, rook));
				}
			}
			
			if (u.getNumMovements() == 0 && canMove(new Position(row, col-1), color, false) && canMove(new Position(row, col-2), color, false))
			{ // check if can castle left
				ChessPiece rook = board[row][0];
				if (rook != null && rook.getType().equals("rook") && rook.getNumMovements() == 0)
				{
					ArrayList<Position> rookMoves = getValidMoves(rook);
					if (rookMoves.size() > 0 && rookMoves.contains(new Position(row, col-1)))
						moves.add(new Position(row, col-2, rook));
				}
			}
		}
		
		
		return moves;
	}
	
	/**
	 * Checks if king of a certain color is checked.
	 * @param color - color of king to check
	 * @return true if checked, false if not
	 */
	public boolean isKingChecked(char color)
	{
		ArrayList<ChessPiece> opposingTeam;
		ChessPiece king;
		if (color == 'w') //king to check is white
		{
			king = whiteKing;
			opposingTeam = blackPieces;
		} else
		{
			king = blackKing;
			opposingTeam = whitePieces;
		}
		
		for (ChessPiece opp : opposingTeam)
		{
			ArrayList<Position> oppPositions = getValidMoves(opp);
			if (oppPositions.contains(king.getPosition())) //position of current piece's king, so self check if true
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Validate the given moves and remove those that will set own player into a check state, or if already in check
	 * @param u - current ChessPiece to be moved
	 * @param moves - valid moves list
	 */
	public void updateValidMovesAvoidingCHECK(ChessPiece u, ArrayList<Position> moves)
	{
		//if (moves.size())
		
		Position p = u.getPosition();
		int row = p.getRow();
		int col = p.getCol();
		char color = u.getColor();
		ChessPiece king = (color == 'w') ? this.whiteKing : this.blackKing; // king of current piece
		ArrayList<ChessPiece> opposingTeam = (color == 'w') ? this.blackPieces : this.whitePieces;
		
		ArrayList<Position> movesTemp = new ArrayList<Position>();
		for (Position m : moves)
			movesTemp.add(m);
		
		if (u.getType().equals("king"))
		{
			for(Position pn : movesTemp)
			{
				//castling
				if (pn.getSpecialMoveWith() != null)
				{
					ChessPiece rook = pn.getSpecialMoveWith();
					if (col < pn.getCol()) //castling right
					{
						board[pn.getRow()][pn.getCol()-1] = rook;
						board[rook.getPosition().getRow()][rook.getPosition().getCol()] = null;
					} else { //castling left
						board[pn.getRow()][pn.getCol()+1] = rook;
						board[rook.getPosition().getRow()][rook.getPosition().getCol()] = null;
					}
				}
				
				
				//act out this move to see a lookahead
				boolean removedFromOpposingTeamList = false;
				ChessPiece pAtPN = board[pn.getRow()][pn.getCol()];
				if (pAtPN != null)
				{
					removedFromOpposingTeamList = true;
					opposingTeam.remove(pAtPN);
				}
					
				
				board[row][col] = null;
				board[pn.getRow()][pn.getCol()] = king; 
				
				for (ChessPiece opp : opposingTeam)
				{
					ArrayList<Position> oppPositions = getValidMoves(opp);
					if (oppPositions.contains(pn)) //pn bcause the king is in pn, so self check if this is true
					{
						moves.remove(pn);
						break;
					}
				}
				
				
				//reset everything back to original	
				//castling
				if (pn.getSpecialMoveWith() != null)
				{
					ChessPiece rook = pn.getSpecialMoveWith();
					if (col < pn.getCol()) //castling right
					{
						board[pn.getRow()][pn.getCol()-1] = null;
						board[rook.getPosition().getRow()][rook.getPosition().getCol()] = rook;
					} else { //castling left
						board[pn.getRow()][pn.getCol()+1] = null;
						board[rook.getPosition().getRow()][rook.getPosition().getCol()] = rook;
					}
				}
				
				if (removedFromOpposingTeamList)
					opposingTeam.add(pAtPN);
				
				board[row][col] = king;
				board[pn.getRow()][pn.getCol()] = pAtPN;
			}
		} else
		{
			for(Position pn : movesTemp)
			{
				//check for enpassant
				ChessPiece oppEnpassant = null;
				if (u.getType().equals("pawn") && pawnEnpassant != null && board[(color == 'w')? pn.getRow()-1 : pn.getRow()+1][pn.getCol()] == pawnEnpassant)
				{
					oppEnpassant = board[(color == 'w')? pn.getRow()-1 : pn.getRow()+1][pn.getCol()];
					board[(color == 'w')? pn.getRow()-1 : pn.getRow()+1][pn.getCol()] = null;
					if (oppEnpassant.getColor() == 'w')
						whitePieces.remove(oppEnpassant);
					else
						blackPieces.remove(oppEnpassant);
				}
				
				//act out this move to see a lookahead
				boolean removedFromOpposingTeamList = false;
				ChessPiece pAtPN = board[pn.getRow()][pn.getCol()];
				if (pAtPN != null)
				{
					removedFromOpposingTeamList = true;
					opposingTeam.remove(pAtPN);
				}
				
				
				board[row][col] = null;
				board[pn.getRow()][pn.getCol()] = u; 
				
				for (ChessPiece opp : opposingTeam)
				{
					ArrayList<Position> oppPositions = getValidMoves(opp);
					if (oppPositions.contains(king.getPosition())) //position of current piece's king, so self check if true
					{
						moves.remove(pn);
						break;
					}
				}
				
				//reset everything back to original	
				//enpassant
				if (oppEnpassant != null)
				{
					board[(color == 'w')? pn.getRow()-1 : pn.getRow()+1][pn.getCol()] = oppEnpassant;
					if (oppEnpassant.getColor() == 'w')
						whitePieces.add(oppEnpassant);
					else
						blackPieces.add(oppEnpassant);
				}
				
				if (removedFromOpposingTeamList)
					opposingTeam.add(pAtPN);
				
				board[row][col] = u;
				board[pn.getRow()][pn.getCol()] = pAtPN;
			}
		}
		
	}
	
	/**
	 * Extremely general "can move" method. Makes sure move isn't outside of board or not colliding with own piece, or can capture
	 * @param pn
	 * @return
	 */
	private boolean canMove(Position pn, char color, boolean canCapture)
	{
		int row = pn.getRow();
		int col = pn.getCol();
		if (row > 7 || row < 0 || col > 7 || col < 0)
			return false;
		if (board[row][col] != null && board[row][col].getColor() == color) // non empty and same color
			return false;
		if (board[row][col] != null && !canCapture) // non empty, diff color, but can't capture
			return false;
		
		return true;
	}
	
	public ChessPiece getChessPieceAtPosition(Position p) { return board[p.getRow()][p.getCol()]; }
	
	public static char toCharFromInt(int x)
	{
		switch (x)
		{
		case 0: return 'a';
		case 1: return 'b';
		case 2: return 'c';
		case 3: return 'd';
		case 4: return 'e';
		case 5: return 'f';
		case 6: return 'g';
		case 7: return 'h';
		default: return 'z';
		}
	}
	
	public static int toIntFromChar(char c)
	{
		if (c == 'a') return 0;
		else if (c == 'b') return 1;
		else if (c == 'c') return 2;
		else if (c == 'd') return 3;
		else if (c == 'e') return 4;
		else if (c == 'f') return 5;
		else if (c == 'g') return 6;
		else if (c == 'h') return 7;
		else return -1;
	}
}

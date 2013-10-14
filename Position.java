/**
 * 
 * @author Nikolay Feldman
 *
 */
public class Position {
	private int row,col;
	private ChessPiece specialMoveWith; //null, pawn for enpassant, rook for castle 
	private boolean pawnMoved2;
	
	public Position(int row, int col)
	{
		this.row = row;
		this.col = col;
		this.specialMoveWith = null;
		this.pawnMoved2 = false;
	}
	
	public Position(int row, int col, ChessPiece specialMoveWith)
	{
		this.row = row;
		this.col = col;
		this.specialMoveWith = specialMoveWith;
		this.pawnMoved2 = false;
	}
	
	public Position(int row, int col, boolean pawnMoved2)
	{
		this.row = row;
		this.col = col;
		this.specialMoveWith = null;
		this.pawnMoved2 = true;
	}
	
	public int getRow() { return row; }
	public int getCol() { return col; }
	public ChessPiece getSpecialMoveWith() { return specialMoveWith; }
	public boolean checkPawnMoved2() { return pawnMoved2; }
	
	public boolean equals(Object pp)
	{
		Position p = (Position)pp;
		return row == p.getRow() && col == p.getCol();
	}
	
	public String toString()
	{
		return "("+Controller.toCharFromInt(col)+","+(row+1)+")";
	}
}

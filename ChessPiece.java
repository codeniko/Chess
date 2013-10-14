/*
 * Pieces{
 * 1 King, 1 Queen, 2 Rooks, 2 Bishops, 2 Knights and 8 Pawns
 */
/**
 * 
 * @author Nikolay Feldman
 *
 */
public class ChessPiece 
{
	private String type;
	private char color;
	private Position pos;
	private int numMovements;
	
	
	public ChessPiece(String type, Position p, char color)
	{
		this.type = type;
		this.pos = p;
		this.color = color;
		this.numMovements = 0;
	}
	
	public void updatePosition(int row, int col)
	{
		this.pos = new Position(row, col);
		numMovements++;
	}
	public void updatePosition(Position pn)
	{
		this.pos = pn;
		numMovements++;
	}
	
	/**
	 * Get board code like wK or wB, etc....
	 * @return board code, String
	 */
	public String getBoardCode()
	{
		if (type.equals("king")) return color+"K ";
		else if (type.equals("queen")) return color+"Q ";
		else if (type.equals("rook")) return color+"R ";
		else if (type.equals("bishop")) return color+"B ";
		else if (type.equals("knight")) return color+"N ";
		else if (type.equals("pawn")) return color+"p ";
		else return null;
	}
	
	/**
	 * Static method used for promotion details. Given a string of 1 character length, get valid type for promotion
	 * @param c
	 * @return A string if successful and null if unsuccessful
	 */
	public static String getType(String c)
	{
		if (c.equalsIgnoreCase("q")) return "queen";
		else if (c.equalsIgnoreCase("r")) return "rook";
		else if (c.equalsIgnoreCase("b")) return "bishop";
		else if (c.equalsIgnoreCase("n")) return "knight";
		else return null;
	}
	
	public void promote(String type)
	{
		this.type = type;
	}
	
	public boolean equals(Object cpp)
	{
		ChessPiece cp = (ChessPiece)cpp;
		return type.equals(cp.getType()) && color == cp.getColor() && pos.equals(cp.getPosition()) && numMovements == cp.getNumMovements();
	}
	
	public String getType() { return type; }
	public char getColor() { return color; }
	public Position getPosition() { return pos; }
	public int getNumMovements() { return numMovements; };
}
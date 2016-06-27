import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.ImageIO;

public class TileMap{
	//Position
	private double x,y;
	//Bounds
	private int xmin,xmax,ymin,ymax;
	//Smooth scrolling
	private double smooth;
	//Map
	private int[][] map;
	private int tileSize;
	private int numRows,numCols;
	private int width,height;
	//Tileset
	private BufferedImage tileset;
	private int numTilesAcross,numTilesAcrossInput,numTilesHighInput; //# tiles in tileset (2-row-shrunk tileset); # tiles across from input tileset; # tiles high from input
	private Tile[][] tiles, tilesInput; //tilesInput = tiles inputted then 2-row-shrunk to tiles
	//Draw
	private int rowOffset,colOffset; //Draw only drawn on screeen
	private int numRowsToDraw,numColsToDraw;

	public TileMap(int tileSize){
		this.tileSize = tileSize;
		numRowsToDraw = GamePanel.HEIGHT / tileSize + 2;
		numColsToDraw = GamePanel.WIDTH / tileSize + 2;
		smooth = 0.1;
	}

	public void loadTiles(String s){ //Tiles on first row are open; second row: blocked
		try{
			tileset = ImageIO.read(getClass().getResourceAsStream(s));
			numTilesAcrossInput = tileset.getWidth() / tileSize;
			numTilesHighInput = tileset.getHeight() / tileSize;
			numTilesAcross = numTilesAcrossInput * (numTilesHighInput - 1);
			tiles = new Tile[2][numTilesAcross];
			tilesInput = new Tile[numTilesHighInput][numTilesAcrossInput];

			BufferedImage subimage;
			for(int row=0;row<numTilesHighInput;row++){ //Currently, 1st row = normal; 2-4 = blocked; 5+ = danger
				for(int col=0;col<numTilesAcrossInput;col++){
					subimage = tileset.getSubimage(col*tileSize,row*tileSize,tileSize,tileSize);
					if(row == 0) tilesInput[row][col] = new Tile(subimage, Tile.NORMAL); //First row is open blocks
					else if(row < 4) tilesInput[row][col] = new Tile(subimage, Tile.BLOCKED);
					else tilesInput[row][col] = new Tile(subimage, Tile.DANGER);
				}
			}
			for(int rowInput=0;rowInput<numTilesHighInput;rowInput++){
				for(int col=0;col<numTilesAcrossInput;col++){
					if(rowInput < 2) tiles[rowInput][col] = tilesInput[rowInput][col];
					else tiles[1][col+(rowInput-1)*numTilesAcrossInput] = tilesInput[rowInput][col];
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void loadMap(String s){
		try{
			InputStream in = getClass().getResourceAsStream(s);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			numCols = Integer.parseInt(br.readLine());
			numRows = Integer.parseInt(br.readLine());
			map = new int[numRows][numCols];
			width = numCols * tileSize;
			height = numRows * tileSize;

			xmin = GamePanel.WIDTH - width;
			xmax = 0;
			ymin = GamePanel.HEIGHT - height;
			ymax = 0;

			String delims = "\\s+";
			for(int row = 0;row<numRows;row++){
				String line = br.readLine();
				String[] tokens = line.split(delims);
				for(int col = 0;col<numCols;col++){
					map[row][col] = Integer.parseInt(tokens[col]);
				}
			}

		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	public int getTileSize(){
		return tileSize;
	}
	public double getX(){
		return x;
	}
	public double getY(){
		return y;
	}
	public int getWidth(){
		return width;
	}
	public int getHeight(){
		return height;
	}

	public int getType(int row,int col){
		int rc = map[row][col];
		int r = rc / numTilesAcross;
		int c = rc % numTilesAcross;
		return tiles[r][c].getType();
	}

	public void setSmooth(double d){
		smooth = d;
	}

	public void setPosition(double x,double y){
		this.x += (x - this.x) * smooth;
		this.y += (y - this.y) * smooth;

		//fixBounds();
		if(this.x < xmin) this.x = xmin;
		if(this.x > xmax) this.x = xmax;
		if(this.y < ymin) this.y = ymin;
		if(this.y > ymax) this.y = ymax;

		colOffset = (int)-this.x / tileSize;
		rowOffset = (int)-this.y / tileSize;
	}

	public void draw(Graphics2D g){
		for(int row = rowOffset;row < rowOffset + numRowsToDraw;row++){
			if(row >= numRows) break;
			for(int col = colOffset;col < colOffset + numColsToDraw;col++){
				if(col >= numCols) break;

				int rc = map[row][col];
				int r = rc / numTilesAcross;
				int c = rc % numTilesAcross;

				g.drawImage(tiles[r][c].getImage(),(int)x + col*tileSize,(int)y + row*tileSize,null);
			}
		}
	}
}

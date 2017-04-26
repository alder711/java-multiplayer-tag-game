import java.awt.Image;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import java.util.Random;

public class Coin 
{

    private int x;       //coin x coordinate
    private int y;       //coin y coordinate
    private Image image; //coin image

    
    
    /**
     * Constructor calls initCoin method with initial position (100,100)
     */
    public Coin() 
    {
        initCoin(100,100);
    }
  
    
    
    /**
     * Method to give coin an image and coordinates
     * @param x
     * @param y
     */
    private void initCoin(int x, int y) 
    {
        
        ImageIcon ii = new ImageIcon("coin.png");
        image = ii.getImage();
        this.x = x;
        this.y = y;        
    }



    
    /**
     * Method to return coin's X coordinate
     * @return
     */
    public int getX() 
    {
        return x;
    }

    
    
    
    /**
     * Method to return coin's Y coordinate
     * @return
     */
    public int getY() 
    {
        return y;
    }

    
    
    
    /**
     * Method to return coin's image
     * @return
     */
    public Image getImage() 
    {
        return image;
    }

    
    
    
    /**
     * Method to set X coordinate of coin randomly
     */
    public void setX()
    {
        Random randomNumber = new Random();
        int randomX = (int)((MovingSprite.WINDOW_SIZE_X-image.getWidth(null)) * randomNumber.nextDouble());   
        this.x = randomX;
    }

    
    
    
    /**
     * Method to set Y coordinate of coin randomly
     */
    public void setY()
    {
        Random randomNumber = new Random();
        int randomY = (int)((MovingSprite.WINDOW_SIZE_Y-image.getHeight(null)) * randomNumber.nextDouble());
        this.y = randomY;
    }
    
    
    
    
    /**
     * Method to hard set coordinates of coin
     * @param x
     * @param y
     */
    public void setHardCoordinates(int x, int y)
    {
    	this.x = x;
    	this.y = y;
    }


}
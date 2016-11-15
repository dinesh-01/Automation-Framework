package environment;

import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import common.Log;

import java.io.File;
import java.io.IOException;


public class Image {
	
	 public static BufferedImage img1 = null;
	 public static BufferedImage img2 = null;
	 
	 
	 /**
	  * Compare Image return status
	  * @param image1
	  * @param image2
	  * @return
	  * @throws IOException
	  */
	 
	 
	 public static Boolean validateImage(String image1,String image2,String compareStatus,String errorPath) throws IOException {
	
		 
	 if(compareStatus.equals("after_deploy")) {
		 
	  img1 = ImageIO.read(new File(image1));
	  img2 = ImageIO.read(new File(image2));
	  Double percentage = null;
	  String status = null;
		 
		int width1  = img1.getWidth(null);
	    int width2  = img2.getWidth(null);
	    int height1 = img1.getHeight(null);
	    int height2 = img2.getHeight(null);
	     
	    
	    if ((width1 != width2) || (height1 != height2)) {
	         Log.info("Image Dimension mismatch");
	        // return false;
	    }
	      
	    long diff = 0;
	    for (int y = 0; y < height1; y++) {
	      for (int x = 0; x < width1; x++) {
	        int rgb1 = img1.getRGB(x, y);
	        int rgb2 = img2.getRGB(x, y);
	        int r1 = (rgb1 >> 16) & 0xff;
	        int g1 = (rgb1 >>  8) & 0xff;
	        int b1 = (rgb1      ) & 0xff;
	        int r2 = (rgb2 >> 16) & 0xff;
	        int g2 = (rgb2 >>  8) & 0xff;
	        int b2 = (rgb2      ) & 0xff;
	        diff += Math.abs(r1 - r2);
	        diff += Math.abs(g1 - g2);
	        diff += Math.abs(b1 - b2);
	      }
	    }
	    double n = width1 * height1 * 3;
	    double p = diff / n / 255.0;
	    
	    percentage = (p * 100.0);  
	    status =  percentage.toString();
	    
	    if(status.equals("0.0")) {
	    	return true;
	    }else{
	    	Image.getDifferenceImage(img1, img2,errorPath);
	    	return false;
	    }
	    
	 } else {
		  return true; 
	 }
		 
	}
	 
	 private static void getDifferenceImage(BufferedImage img1, BufferedImage img2, String errorPath) throws IOException {
		    final int w = img1.getWidth(), h = img1.getHeight(), highlight = Color.MAGENTA.getRGB();
		    final int[] p1 = img1.getRGB(0, 0, w, h, null, 0, w);
		    final int[] p2 = img2.getRGB(0, 0, w, h, null, 0, w);
		    for (int i = 0; i < p1.length; i++) {
		        if (p1[i] != p2[i]){
		            p1[i] = highlight;
		        }
		    }
		    
		    BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		    out.setRGB(0, 0, w, h, p1, 0, w);
		    ImageIO.write(out, "png", new File(errorPath));
		    
		}
	

}

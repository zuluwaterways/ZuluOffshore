package com.app.zuluwaterways;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;

import org.osmdroid.util.BoundingBox;

class TileSystem{
    public static final double EarthRadius = 6378137;
    public static final double minLatitude = -85.05;
    public static final double maxLatitude = 85.05;
    public static final double minLongitude = -180;
    public static final double maxLongitude = 180;

    /// <summary>  
    /// Clips a number to the specified minimum and maximum values.  
    /// </summary>  
    /// <param name="n">The number to clip.</param>  
    /// <param name="minValue">minimum allowable value.</param>  
    /// <param name="maxValue">maximum allowable value.</param>  
    /// <returns>The clipped value.</returns>  
    private static double Clip(double n, double minValue, double maxValue)
    {
        return Math.min(Math.max(n, minValue), maxValue);
    }

    /// <summary>  
    /// Determines the map width and height (in pixels) at a specified level  
    /// of detail.  
    /// </summary>  
    /// <param name="levelOfDetail">Level of detail, from 1 (lowest detail)  
    /// to 23 (highest detail).</param>  
    /// <returns>The map width and height in pixels.</returns>  
    public static int MapSize(int levelOfDetail)
    {
        return 256 << levelOfDetail;
    }



    /// <summary>  
    /// Determines the ground resolution (in meters per pixel) at a specified  
    /// latitude and level of detail.  
    /// </summary>  
    /// <param name="latitude">Latitude (in degrees) at which to measure the  
    /// ground resolution.</param>  
    /// <param name="levelOfDetail">Level of detail, from 1 (lowest detail)  
    /// to 23 (highest detail).</param>  
    /// <returns>The ground resolution, in meters per pixel.</returns>  
    public static double GroundResolution(double latitude, int levelOfDetail)
    {
        latitude = Clip(latitude, minLatitude, maxLatitude);
        return Math.cos(latitude * Math.PI / 180) * 2 * Math.PI * EarthRadius / MapSize(levelOfDetail);
    }

    /// <summary>  
    /// Determines the map scale at a specified latitude, level of detail,  
    /// and screen resolution.  
    /// </summary>  
    /// <param name="latitude">Latitude (in degrees) at which to measure the  
    /// map scale.</param>  
    /// <param name="levelOfDetail">Level of detail, from 1 (lowest detail)  
    /// to 23 (highest detail).</param>  
    /// <param name="screenDpi">Resolution of the screen, in dots per inch.</param>  
    /// <returns>The map scale, expressed as the denominator N of the ratio 1 : N.</returns>  
    public static double MapScale(double latitude, int levelOfDetail, int screenDpi)
    {
        return GroundResolution(latitude, levelOfDetail) * screenDpi / 0.0254;
    }

    /// <summary>  
    /// Converts a point from latitude/longitude WGS-84 coordinates (in degrees)  
    /// into pixel XY coordinates at a specified level of detail.  
    /// </summary>  
    /// <param name="latitude">Latitude of the point, in degrees.</param>  
    /// <param name="longitude">Longitude of the point, in degrees.</param>  
    /// <param name="levelOfDetail">Level of detail, from 1 (lowest detail)  
    /// to 23 (highest detail).</param>  
    /// <param name="pixelX">Output parameter receiving the X coordinate in pixels.</param>  
    /// <param name="pixelY">Output parameter receiving the Y coordinate in pixels.</param>  
    public static int[] LatLongToPixelXY(double latitude, double longitude, int levelOfDetail)
    {
        latitude = Clip(latitude, minLatitude, maxLatitude);
        longitude = Clip(longitude, minLongitude, maxLongitude);

        double x = (longitude + 180) / 360;
        double sinLatitude = Math.sin(latitude * Math.PI / 180);
        double y = 0.5 - Math.log((1 + sinLatitude) / (1 - sinLatitude)) / (4 * Math.PI);

        int mapSize = MapSize(levelOfDetail);
        int pixelX = (int) Clip(x * mapSize + 0.5, 0, mapSize - 1);
        int pixelY = (int) Clip(y * mapSize + 0.5, 0, mapSize - 1);
        return new int[]{pixelX,pixelY};
    }

    /// <summary>  
    /// Converts a pixel from pixel XY coordinates at a specified level of detail  
    /// into latitude/longitude WGS-84 coordinates (in degrees).  
    /// </summary>  
    /// <param name="pixelX">X coordinate of the point, in pixels.</param>  
    /// <param name="pixelY">Y coordinates of the point, in pixels.</param>  
    /// <param name="levelOfDetail">Level of detail, from 1 (lowest detail)  
    /// to 23 (highest detail).</param>  
    /// <param name="latitude">Output parameter receiving the latitude in degrees.</param>  
    /// <param name="longitude">Output parameter receiving the longitude in degrees.</param>  
    public static double[] PixelXYToLatLong(int pixelX, int pixelY, int levelOfDetail)
    {
        double mapSize = MapSize(levelOfDetail);
        double x = (Clip(pixelX, 0, mapSize - 1) / mapSize) - 0.5;
        double y = 0.5 - (Clip(pixelY, 0, mapSize - 1) / mapSize);

        double latitude = 90 - 360 * Math.atan(Math.exp(-y * 2 * Math.PI)) / Math.PI;
        double longitude = 360 * x;

        return new double[]{latitude,longitude};
        
    }

    /// <summary>  
    /// Converts pixel XY coordinates into tile XY coordinates of the tile containing  
    /// the specified pixel.  
    /// </summary>  
    /// <param name="pixelX">Pixel X coordinate.</param>  
    /// <param name="pixelY">Pixel Y coordinate.</param>  
    /// <param name="tileX">Output parameter receiving the tile X coordinate.</param>  
    /// <param name="tileY">Output parameter receiving the tile Y coordinate.</param>  
    public static int[] PixelXYToTileXY(int pixelX, int pixelY)
    {
        int tileX = pixelX / 256;
        int tileY = pixelY / 256;
        return new int[]{tileX,tileY};
    }

    /// <summary>  
    /// Converts tile XY coordinates into pixel XY coordinates of the upper-left pixel  
    /// of the specified tile.  
    /// </summary>  
    /// <param name="tileX">Tile X coordinate.</param>  
    /// <param name="tileY">Tile Y coordinate.</param>  
    /// <param name="pixelX">Output parameter receiving the pixel X coordinate.</param>  
    /// <param name="pixelY">Output parameter receiving the pixel Y coordinate.</param>  
    public static int[] TileXYToPixelXY(int tileX, int tileY)
    {
        int pixelX = tileX * 256;
        int pixelY = tileY * 256;
        return new int[]{pixelX,pixelY};
    }

    /// <summary>  
    /// Converts tile XY coordinates into a QuadKey at a specified level of detail.  
    /// </summary>  
    /// <param name="tileX">Tile X coordinate.</param>  
    /// <param name="tileY">Tile Y coordinate.</param>  
    /// <param name="levelOfDetail">Level of detail, from 1 (lowest detail)  
    /// to 23 (highest detail).</param>  
    /// <returns>A string containing the QuadKey.</returns>  
    public static String TileXYToQuadKey(int tileX, int tileY, int levelOfDetail)
    {
        StringBuilder quadKey = new StringBuilder();
        for (int i = levelOfDetail; i > 0; i--)
        {
            char digit = '0';
            int mask = 1 << (i - 1);
            if ((tileX & mask) != 0)
            {
                digit++;
            }
            if ((tileY & mask) != 0)
            {
                digit++;
                digit++;
            }
            quadKey.append(digit);
        }
        return quadKey.toString();
    }

    public static BoundingBox boundsFromCenter(double lat, double lon, int zoom, int height,int width) {
        int[] centerpx = LatLongToPixelXY(lat,lon,zoom);
        double north = PixelXYToLatLong(centerpx[0],centerpx[1]-height/2,zoom)[0];
        double east = PixelXYToLatLong(centerpx[0]+width/2,centerpx[1],zoom)[1];
        double south = PixelXYToLatLong(centerpx[0],centerpx[1]+height/2,zoom)[0];
        double west = PixelXYToLatLong(centerpx[0]-width/2,centerpx[1],zoom)[1];
        return new BoundingBox(Math.max(Math.min(north,maxLatitude),minLatitude),
                Math.max(Math.min(east,maxLongitude),minLongitude),
                Math.max(Math.min(south,maxLatitude),minLatitude),
                Math.max(Math.min(west,maxLongitude),minLongitude));
    }

    public static boolean inImage(double lat, double lon, int zoom, int height,int width, double plat, double plon) {
        //Log.d("looking in ",boundsFromCenter(lat,lon,zoom,height,width).toString());
        return boundsFromCenter(lat,lon,zoom,height,width).contains(plat,plon);
    }

    public static double[] LatLongFromCenterOffset(double lat, double lon,int zoom, int x, int y) {
        int[] centerpx = LatLongToPixelXY(lat,lon,zoom);
            return PixelXYToLatLong(centerpx[0]+x,centerpx[1]+y,zoom);
    }

    /// <summary>  
    /// Converts a QuadKey into tile XY coordinates.  
    /// </summary>  
    /// <param name="quadKey">QuadKey of the tile.</param>  
    /// <param name="tileX">Output parameter receiving the tile X coordinate.</param>  
    /// <param name="tileY">Output parameter receiving the tile Y coordinate.</param>  
    /// <param name="levelOfDetail">Output parameter receiving the level of detail.</param>  
    public static int[] QuadKeyToTileXY(String quadKey)
    {
        int tileX = 0;
        int tileY = 0;
        int levelOfDetail = quadKey.length();
        for (int i = levelOfDetail; i > 0; i--){
            int mask = 1 << (i - 1);
            switch (quadKey.charAt(levelOfDetail - i)){
                case '0':
                    break;

                case '1':
                    tileX |= mask;
                    break;

                case '2':
                    tileY |= mask;
                    break;

                case '3':
                    tileX |= mask;
                    tileY |= mask;
                    break;

                default:
                    return null;
            }
        }
        return new int[]{tileX,tileY,levelOfDetail};
    }

    public static Bitmap loadBitmapFromView(View v) {
        if (v.getMeasuredHeight() <= 0) {
            v.measure(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
            Bitmap b = Bitmap.createBitmap(v.getMeasuredWidth(), v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(b);
            v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
            v.draw(c);
            return b;
        }
        return null;
    }
}  

  
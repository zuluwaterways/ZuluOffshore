package com.zulu.offshore;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;

import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.Iterator;

public class RadiusMarkerClusterer extends MarkerClusterer {

    private int mMaxClusteringZoomLevel = 17;
    private int mRadiusInPixels = 100;
    private double mRadiusInMeters;
    private final Paint mTextPaint;
    private ArrayList<Marker> mClonedMarkers;
    private boolean mAnimated;

    /** anchor point to draw the number of markers inside the cluster icon */
    public float mTextAnchorU = Marker.ANCHOR_CENTER, mTextAnchorV = Marker.ANCHOR_CENTER;

    public RadiusMarkerClusterer(Context ctx) {
        super();
        mTextPaint = new Paint();
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextSize(15 * ctx.getResources().getDisplayMetrics().density);
        mTextPaint.setFakeBoldText(true);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setAntiAlias(true);
        Drawable clusterIconD = ctx.getResources().getDrawable(R.drawable.marker_cluster);
        Bitmap clusterIcon = ((BitmapDrawable) clusterIconD).getBitmap();//Bitmap.createScaledBitmap(((BitmapDrawable) clusterIconD).getBitmap(),60,60,false);
        setIcon(clusterIcon);
        mAnimated = true;
    }

    /** If you want to change the default text paint (color, size, font) */
    public Paint getTextPaint(){
        return mTextPaint;
    }

    /** Set the radius of clustering in pixels. Default is 100px. */
    public void setRadius(int radius){
        mRadiusInPixels = radius;
    }

    /** Set max zoom level with clustering. When zoom is higher or equal to this level, clustering is disabled.
     * You can put a high value to disable this feature. */
    public void setMaxClusteringZoomLevel(int zoom){
        mMaxClusteringZoomLevel = zoom;
    }

    /** Radius-Based clustering algorithm */
    @Override public ArrayList<StaticCluster> clusterer(MapView mapView) {

        ArrayList<StaticCluster> clusters = new ArrayList<>();
        convertRadiusToMeters(mapView);

        mClonedMarkers = new ArrayList<>(mItems); //shallow copy
        while (!mClonedMarkers.isEmpty()) {
            Marker m = mClonedMarkers.get(0);
            StaticCluster cluster = createCluster(m, mapView);
            clusters.add(cluster);
        }
        return clusters;
    }

    private StaticCluster createCluster(Marker m, MapView mapView) {
        GeoPoint clusterPosition = m.getPosition();

        StaticCluster cluster = new StaticCluster(clusterPosition);
        cluster.add(m);

        mClonedMarkers.remove(m);

        if (mapView.getZoomLevelDouble()> mMaxClusteringZoomLevel) {
            //above max level => block clustering:
            return cluster;
        }

        Iterator<Marker> it = mClonedMarkers.iterator();
        while (it.hasNext()) {
            Marker neighbour = it.next();
            double distance = clusterPosition.distanceToAsDouble(neighbour.getPosition());
            if (distance <= mRadiusInMeters) {
                cluster.add(neighbour);
                it.remove();
            }
        }

        return cluster;
    }

    @Override public Marker buildClusterMarker(StaticCluster cluster, MapView mapView) {
        Marker m = new Marker(mapView);
        m.setPosition(cluster.getPosition());
        m.setInfoWindow(null);
        /** cluster icon anchor */
        float mAnchorU = Marker.ANCHOR_CENTER;
        float mAnchorV = Marker.ANCHOR_CENTER;
        m.setAnchor(mAnchorU, mAnchorV);

        Bitmap finalIcon = Bitmap.createBitmap(mClusterIcon.getWidth(), mClusterIcon.getHeight(), mClusterIcon.getConfig());
        Canvas iconCanvas = new Canvas(finalIcon);
        iconCanvas.drawBitmap(mClusterIcon, 0, 0, null);
        String text = "" + cluster.getSize();
        int textHeight = (int) (mTextPaint.descent() + mTextPaint.ascent());
        iconCanvas.drawText(text,
                mTextAnchorU * finalIcon.getWidth(),
                (float) (mTextAnchorV * finalIcon.getHeight() - textHeight / 2.0),
                mTextPaint);
        m.setIcon(new BitmapDrawable(mapView.getContext().getResources(), finalIcon));

        return m;
    }

    @Override public void renderer(ArrayList<StaticCluster> clusters, Canvas canvas, MapView mapView) {
        for (StaticCluster cluster : clusters) {
            if (cluster.getSize() == 1) {
                //cluster has only 1 marker => use it as it is:
                cluster.setMarker(cluster.getItem(0));
            } else {
                //only draw 1 Marker at Cluster center, displaying number of Markers contained
                Marker m = buildClusterMarker(cluster, mapView);
                cluster.setMarker(m);
            }
        }
    }

    private void convertRadiusToMeters(MapView mapView) {

        Rect mScreenRect = mapView.getIntrinsicScreenRect(null);

        int screenWidth = mScreenRect.right - mScreenRect.left;
        int screenHeight = mScreenRect.bottom - mScreenRect.top;

        BoundingBox bb = mapView.getBoundingBox();

        double diagonalInMeters = bb.getDiagonalLengthInMeters();
        double diagonalInPixels = Math.sqrt(screenWidth * screenWidth + screenHeight * screenHeight);
        double metersInPixel = diagonalInMeters / diagonalInPixels;

        mRadiusInMeters = mRadiusInPixels * metersInPixel;
    }

    public void setAnimation(boolean animate){
        mAnimated = animate;
    }

    private void zoomOnCluster(MapView mapView, StaticCluster cluster){
        BoundingBox bb = cluster.getBoundingBox();
        bb = bb.increaseByScale(1.15f);
        mapView.zoomToBoundingBox(bb, true);
    }

    @Override public boolean onSingleTapConfirmed(final MotionEvent event, final MapView mapView){
        for (final StaticCluster cluster : reversedClusters()) {
            if (cluster.getMarker().onSingleTapConfirmed(event, mapView)) {
                if (mAnimated && cluster.getSize() > 1)
                    zoomOnCluster(mapView, cluster);
                return true;
            }
        }
        return false;
    }

}
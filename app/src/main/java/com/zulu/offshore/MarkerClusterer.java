package com.zulu.offshore;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;

import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

public abstract class MarkerClusterer extends Overlay {

    /** impossible value for zoom level, to force clustering */
    private static final int FORCE_CLUSTERING = -1;

    final ArrayList<Marker> mItems = new ArrayList<>();
    private ArrayList<StaticCluster> mClusters = new ArrayList<>();
    private int mLastZoomLevel;
    Bitmap mClusterIcon;
    private String mName;
    private String mDescription;

    // abstract methods:

    /** clustering algorithm */
    protected abstract ArrayList<StaticCluster> clusterer(MapView mapView);
    /** Build the marker for a cluster. */
    public abstract Marker buildClusterMarker(StaticCluster cluster, MapView mapView);
    /** build clusters markers to be used at next draw */
    protected abstract void renderer(ArrayList<StaticCluster> clusters, Canvas canvas, MapView mapView);

    MarkerClusterer() {
        super();
        mLastZoomLevel = FORCE_CLUSTERING;
    }

    public void setName(String name){
        mName = name;
    }

    public String getName(){
        return mName;
    }

    public void setDescription(String description){
        mDescription = description;
    }

    public String getDescription(){
        return mDescription;
    }

    /** Set the cluster icon to be drawn when a cluster contains more than 1 marker.
     * If not set, default will be the default osmdroid marker icon (which is really inappropriate as a cluster icon). */
    public void setIcon(Bitmap icon){
        mClusterIcon = icon;
    }

    /** Add the Marker.
     * Important: Markers added in a MarkerClusterer should not be added in the map overlays. */
    public void add(Marker marker){
        mItems.add(marker);
    }

    /** Force a rebuild of clusters at next draw, even without a zooming action.
     * Should be done when you changed the content of a MarkerClusterer. */
    public void invalidate(){
        mLastZoomLevel = FORCE_CLUSTERING;
    }

    /** @return the Marker at id (starting at 0) */
    public Marker getItem(int id){
        return mItems.get(id);
    }

    /** @return the list of Markers. */
    public ArrayList<Marker> getItems(){
        return mItems;
    }

    private void hideInfoWindows(){
        for (Marker m : mItems){
            if (m.isInfoWindowShown())
                m.closeInfoWindow();
        }
    }

    @Override public void draw(Canvas canvas, MapView mapView, boolean shadow) {
        if (shadow)
            return;
        //long tic = System.currentTimeMillis();
        //if zoom has changed and mapView is now stable, rebuild clusters:
        int zoomLevel = (int) Math.round(mapView.getZoomLevelDouble());
        if (zoomLevel != mLastZoomLevel && !mapView.isAnimating()){
            hideInfoWindows();
            mClusters = clusterer(mapView);
            renderer(mClusters, canvas, mapView);
            mLastZoomLevel = zoomLevel;
        }

        for (StaticCluster cluster:mClusters){
            cluster.getMarker().draw(canvas, mapView.getProjection());
        }
        //Log.d("tocd",""+(System.currentTimeMillis()-tic));
    }

    Iterable<StaticCluster> reversedClusters() {
        return new Iterable<StaticCluster>() {
            @Override
            public Iterator<StaticCluster> iterator() {
                final ListIterator<StaticCluster> i = mClusters.listIterator(mClusters.size());
                return new Iterator<StaticCluster>() {
                    @Override
                    public boolean hasNext() {
                        return i.hasPrevious();
                    }

                    @Override
                    public StaticCluster next() {
                        return i.previous();
                    }

                    @Override
                    public void remove() {
                        i.remove();
                    }
                };
            }
        };
    }

    @Override public boolean onSingleTapConfirmed(final MotionEvent event, final MapView mapView){
        for (final StaticCluster cluster : reversedClusters()) {
            if (cluster.getMarker().onSingleTapConfirmed(event, mapView))
                return true;
        }
        return false;
    }

    @Override public boolean onLongPress(final MotionEvent event, final MapView mapView) {
        for (final StaticCluster cluster : reversedClusters()) {
            if (cluster.getMarker().onLongPress(event, mapView))
                return true;
        }
        return false;
    }

    @Override public boolean onTouchEvent(final MotionEvent event, final MapView mapView) {
        for (StaticCluster cluster : reversedClusters()) {
            if (cluster.getMarker().onTouchEvent(event, mapView))
                return true;
        }
        return false;
    }

    protected static class StaticCluster {
        final ArrayList<Marker> mItems = new ArrayList<>();
        GeoPoint mCenter;
        Marker mMarker;

        StaticCluster(GeoPoint center) {
            mCenter = center;
        }

        public void setPosition(GeoPoint center){
            mCenter = center;
        }

        GeoPoint getPosition() {
            return mCenter;
        }

        int getSize() {
            return mItems.size();
        }

        Marker getItem(int index) {
            return mItems.get(index);
        }

        void add(Marker t) {
            mItems.add(t);
        }

        /** set the Marker to be displayed for this cluster */
        void setMarker(Marker marker){
            mMarker = marker;
        }

        /** @return the Marker to be displayed for this cluster */
        Marker getMarker(){
            return mMarker;
        }

        BoundingBox getBoundingBox(){
            if (mItems.size()==0)
                return null;
            GeoPoint p = getItem(0).getPosition();
            BoundingBox bb = new BoundingBox(p.getLatitude(), p.getLongitude(), p.getLatitude(), p.getLongitude());
            for (int i=1; i<getSize(); i++) {
                p = getItem(i).getPosition();
                double minLat = Math.min(bb.getLatSouth(), p.getLatitude());
                double minLon = Math.min(bb.getLonWest(), p.getLongitude());
                double maxLat = Math.max(bb.getLatNorth(), p.getLatitude());
                double maxLon = Math.max(bb.getLonEast(), p.getLongitude());
                bb.set(maxLat, maxLon, minLat, minLon);
            }
            return bb;
        }
    }
}
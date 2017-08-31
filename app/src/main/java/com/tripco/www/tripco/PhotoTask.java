package com.tripco.www.tripco;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.Places;
import com.tripco.www.tripco.util.U;

abstract public class PhotoTask extends AsyncTask<String, Void, PhotoTask.AttributedPhoto> implements GoogleApiClient.OnConnectionFailedListener {
    private int mHeight;
    private int mWidth;

    protected PhotoTask(int width, int height) {
        mHeight = height;
        mWidth = width;
    }

    @Override
    protected AttributedPhoto doInBackground(String... params) {
        if (params.length != 1) {
            return null;
        }
        final String placeId = params[0];
        AttributedPhoto attributedPhoto = null;

        try {
            PlacePhotoMetadataResult result = Places.GeoDataApi
                    .getPlacePhotos(U.getInstance().getmGoogleApiClient(), placeId).await();

            if (result.getStatus().isSuccess()) {
                PlacePhotoMetadataBuffer photoMetadataBuffer = result.getPhotoMetadata();
                if (photoMetadataBuffer.getCount() > 0 && !isCancelled()) {
                    // Get the first bitmap and its attributions.
                    PlacePhotoMetadata photo = photoMetadataBuffer.get(0);
                    CharSequence attribution = photo.getAttributions();
                    // Load a scaled bitmap for this photo.
                    Bitmap image = photo
                            .getScaledPhoto(U.getInstance().getmGoogleApiClient(), mWidth, mHeight)
                            .await()
                            .getBitmap();

                    attributedPhoto = new AttributedPhoto(attribution, image);
                }
                // Release the PlacePhotoMetadataBuffer.
                photoMetadataBuffer.release();
            }
        } catch (Exception e) {
            e.printStackTrace();
            /*if(U.getInstance().getmGoogleApiClient() != null) {
                U.getInstance().getmGoogleApiClient().disconnect();
                U.getInstance().setmGoogleApiClient(null);
                U.getInstance().setmGoogleApiClient(new GoogleApiClient
                        .Builder(U.getInstance().getContext())
                        .addApi(Places.GEO_DATA_API)
                        .addApi(Places.PLACE_DETECTION_API)
                        .enableAutoManage((FragmentActivity) U.getInstance().getContext(), this)
                        .build()
                );
            }*/
            return null;
        }
        return attributedPhoto;
    }

    protected class AttributedPhoto {
        final CharSequence attribution;
        public final Bitmap bitmap;

        AttributedPhoto(CharSequence attribution, Bitmap bitmap) {
            this.attribution = attribution;
            this.bitmap = bitmap;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}
}

package com.example.tartlabs.facebooklearn;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.example.tartlabs.facebooklearn.model.Media;
import com.squareup.picasso.Picasso;

import java.util.List;


public class MediaGridView extends LinearLayout {
    String MEDIA_IMAGE = "image";
    String MEDIA_VIDEO = "video";
    String MEDIA_DOCUMENT = "pdf";
    private MediaGridClickListener listener;

    public void setListener(MediaGridClickListener listener) {
        this.listener = listener;
    }

    public MediaGridView(Context context) {
        super(context);
        init(context, null);
    }

    public MediaGridView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MediaGridView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

    }

    public void setImages(List<Media> mediaList) {
        removeAllViews();
        int imageSize = mediaList.size();
        if (imageSize != 0) {
            if (imageSize == 1) {
                setOneImage(mediaList);
            } else if (imageSize == 2) {
                setTwoImages(mediaList);
            } else if (imageSize == 3) {
                setThreeImages(mediaList);
            } else if (imageSize == 4) {
                setFourImages(mediaList);
            } else
                setFiveImage(mediaList);
        }
    }


    private void setOneImage(List<Media> mediaList) {
        setOrientation(HORIZONTAL);
        if (mediaList.get(0).getType().equalsIgnoreCase(MEDIA_IMAGE)) {
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            ImageView iv = new ImageView(getContext());
            iv.setLayoutParams(lp);
            Picasso.get().load(mediaList.get(0).getUrl()).into(iv);

            iv.setOnClickListener(view -> {
                if (listener != null)
                    listener.onImageClicked(mediaList, 0);
            });
            addView(iv);
        } else if (mediaList.get(0).getType().equalsIgnoreCase(MEDIA_VIDEO)) {
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
            lp.setMargins(5, 5, 5, 5);
            FrameLayout flBottom = new FrameLayout(getContext());
            flBottom.setLayoutParams(lp);

            ImageView imageView = new ImageView(getContext());
           /* GlideApp.with(getContext())
                    .load(mediaList.get(0).getMediaThumbnailUrl())
                    .centerCrop()
                    .into(imageView);*/
            Picasso.get().load(R.drawable.login_page_logo).into(imageView);
            flBottom.addView(imageView);

            View overlayView = new View(getContext());
            overlayView.setAlpha(0.4f);
            overlayView.setBackgroundColor(Color.parseColor("#000000"));
            flBottom.addView(overlayView);

            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER;
            ImageView ivPlay = new ImageView(getContext());
            ivPlay.setImageResource(R.drawable.play_button);
            ivPlay.setLayoutParams(layoutParams);
            flBottom.addView(ivPlay);

            addView(flBottom);

            imageView.setOnClickListener(view -> {
                if (listener != null)
                    listener.onImageClicked(mediaList, 0);
            });
            overlayView.setOnClickListener(view -> {
                if (listener != null)
                    listener.onImageClicked(mediaList, 0);
            });
            ivPlay.setOnClickListener(view -> {
                if (listener != null)
                    listener.onImageClicked(mediaList, 0);
            });
        } else if (mediaList.get(0).getType().equalsIgnoreCase(MEDIA_DOCUMENT)) {
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            //LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            //layoutParams.gravity = Gravity.CENTER;
            ImageView iv = new ImageView(getContext());
            /*ImageView ivPlay = new ImageView(getContext());
            ivPlay.setImageResource(R.drawable.play_button);
            ivPlay.setLayoutParams(layoutParams);*/
            iv.setLayoutParams(lp);
            if (Patterns.WEB_URL.matcher(mediaList.get(0).getUrl()).matches()) {
               /* GlideApp.with(getContext())
                        .load( R.drawable.pdfview1)
                        .centerCrop()
                        .into(iv);*/
                Picasso.get().load(R.drawable.login_page_logo).into(iv);
            } else {
                // If local storage url then set
               /* GlideApp.with(getContext())
                        .load( R.drawable.pdfview1)
                        .centerCrop()
                        .into(iv);*/
                Picasso.get().load(R.drawable.login_page_logo).into(iv);
            }

            iv.setOnClickListener(view -> {
                if (listener != null)
                    listener.onImageClicked(mediaList, 0);
            });
            addView(iv);
            //addView(ivPlay);
        }
    }

    private void setTwoImages(List<Media> mediaList) {
        setOrientation(HORIZONTAL);
        for (int i = 0; i < mediaList.size(); i++) {
            View mediaView = getMediaView(mediaList, mediaList.get(i).getUrl(),
                    mediaList.get(i).getType(), 2, i);
            addView(mediaView);
        }
    }

    private void setThreeImages(List<Media> mediaList) {
        setOrientation(VERTICAL);

        LinearLayout llTop = new LinearLayout(getContext());
        LinearLayout llBottom = new LinearLayout(getContext());

        llTop.setOrientation(HORIZONTAL);

        for (int i = 0; i < mediaList.size(); i++) {
            View mediaView = getMediaView(mediaList, mediaList.get(i).getUrl(),
                    mediaList.get(i).getType(), 3, i);
            if (i == 0 || i == 1) {
                llTop.addView(mediaView);
            } else {
                llBottom.addView(mediaView);
            }
        }

        addView(llTop);
        addView(llBottom);
    }

    private void setFourImages(List<Media> mediaList) {
        setOrientation(VERTICAL);
        LinearLayout llTop = new LinearLayout(getContext());
        LinearLayout llBottom = new LinearLayout(getContext());

        llTop.setOrientation(HORIZONTAL);
        llBottom.setOrientation(HORIZONTAL);

        for (int i = 0; i < mediaList.size(); i++) {
            View mediaView = getMediaView(mediaList, mediaList.get(i).getUrl(),
                    mediaList.get(i).getType(), 3, i);
            if (i == 0 || i == 1) {
                llTop.addView(mediaView);
            } else if (i == 2 || i == 3) {
                llBottom.addView(mediaView);
            }
        }
        addView(llTop);
        addView(llBottom);
    }

    private void setFiveImage(List<Media> mediaList) {
        setOrientation(VERTICAL);
        LinearLayout llTop = new LinearLayout(getContext());
        LinearLayout llBottom = new LinearLayout(getContext());
        llTop.setOrientation(HORIZONTAL);
        llBottom.setOrientation(HORIZONTAL);

        for (int i = 0; i < mediaList.size(); i++) {
            View mediaView = getMediaView(mediaList, mediaList.get(i).getUrl(),
                    mediaList.get(i).getType(), 3, i);
            if (i == 0 || i == 1) {
                llTop.addView(mediaView);
            } else if (i == 2 || i == 3 || i == 4) {
                llBottom.addView(mediaView);
            }
        }
        addView(llTop);
        addView(llBottom);
    }

    private View getMediaView(List<Media> mediaList, String mediaThumbnailUrl, String mediaType, int imageCount, int position) {
        View mediaView = null;
        if (mediaType.equalsIgnoreCase(MEDIA_IMAGE)) {
            if (imageCount == 2 || imageCount == 4) {
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
                lp.setMargins(5, 5, 5, 5);
                ImageView imageView = new ImageView(getContext());
                imageView.setLayoutParams(lp);
               /* GlideApp.with(getContext())
                        .load(mediaThumbnailUrl)
                        .centerCrop()
                        .into(imageView);*/
                Picasso.get().load(mediaThumbnailUrl).into(imageView);

                imageView.setOnClickListener(view -> {
                    if (listener != null) {
                        listener.onImageClicked(mediaList, position);
                    }
                });
                mediaView = imageView;
            } else {
                int height = getLayoutParams().height / 2;
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, height, 1.0f);
                lp.setMargins(5, 5, 5, 5);

                if (position == 4 && mediaList.size() > 5) {
                    FrameLayout flBottom = new FrameLayout(getContext());
                    flBottom.setLayoutParams(lp);

                    ImageView imageView = new ImageView(getContext());

                   /* GlideApp.with(getContext())
                            .load(mediaThumbnailUrl)
                            .centerCrop()
                            .into(imageView);*/
                    Picasso.get().load(mediaThumbnailUrl).into(imageView);
                    flBottom.addView(imageView);
                    View overlayView = new View(getContext());
                    overlayView.setAlpha(0.4f);
                    overlayView.setBackgroundColor(Color.parseColor("#000000"));
                    flBottom.addView(overlayView);
                    TextView tv = new TextView(getContext());
                    int remainingSize = mediaList.size() - 4;
                    tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
                    tv.setTextColor(Color.parseColor("#FFFFFF"));
                    tv.setText(String.format("+%d", remainingSize));
                    tv.setGravity(Gravity.CENTER);
                    flBottom.addView(tv);
                    mediaView = flBottom;
                    imageView.setOnClickListener(view -> {
                        if (listener != null) {
                            listener.onImageClicked(mediaList, position);
                        }
                    });
                } else {
                    ImageView imageView = new ImageView(getContext());
                    imageView.setLayoutParams(lp);

                   /* GlideApp.with(getContext())
                            .load(mediaThumbnailUrl)
                            .centerCrop()
                            .into(imageView);*/
                    Picasso.get().load(mediaThumbnailUrl).into(imageView);
                    mediaView = imageView;
                    imageView.setOnClickListener(view -> {
                        if (listener != null) {
                            listener.onImageClicked(mediaList, position);
                        }
                    });
                }
            }
        } else if (mediaType.equalsIgnoreCase(MEDIA_VIDEO)) {
            if (imageCount == 2 || imageCount == 4) {
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
                lp.setMargins(5, 5, 5, 5);
                FrameLayout flBottom = new FrameLayout(getContext());
                flBottom.setLayoutParams(lp);

                ImageView imageView = new ImageView(getContext());
               /* GlideApp.with(getContext())
                        .load(mediaThumbnailUrl)
                        .centerCrop()
                        .into(imageView);*/
                Picasso.get().load(mediaThumbnailUrl).into(imageView);
                flBottom.addView(imageView);

                View overlayView = new View(getContext());
                overlayView.setAlpha(0.4f);
                overlayView.setBackgroundColor(Color.parseColor("#000000"));
                flBottom.addView(overlayView);

                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.gravity = Gravity.CENTER;
                ImageView ivPlay = new ImageView(getContext());
                ivPlay.setImageResource(R.drawable.play_button);
                ivPlay.setLayoutParams(layoutParams);
                flBottom.addView(ivPlay);
                imageView.setOnClickListener(view -> {
                    if (listener != null)
                        listener.onImageClicked(mediaList, 0);
                });
                overlayView.setOnClickListener(view -> {
                    if (listener != null)
                        listener.onImageClicked(mediaList, 0);
                });
                ivPlay.setOnClickListener(view -> {
                    if (listener != null)
                        listener.onImageClicked(mediaList, 0);
                });
                mediaView = flBottom;
            } else {
                int height = getLayoutParams().height / 2;
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, height, 1.0f);
                lp.setMargins(5, 5, 5, 5);
                if (position == 47 && mediaList.size() > 5) {
                    FrameLayout flBottom = new FrameLayout(getContext());
                    flBottom.setLayoutParams(lp);

                    ImageView imageView = new ImageView(getContext());
                  /*  GlideApp.with(getContext())
                            .load(mediaThumbnailUrl)
                            .centerCrop()
                            .into(imageView);*/
                    Picasso.get().load(mediaThumbnailUrl).into(imageView);
                    flBottom.addView(imageView);

                    View overlayView = new View(getContext());
                    overlayView.setAlpha(0.4f);
                    overlayView.setBackgroundColor(Color.parseColor("#000000"));
                    flBottom.addView(overlayView);
                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    LinearLayout linearLayout = new LinearLayout(getContext());
                    linearLayout.setOrientation(VERTICAL);
                    layoutParams.gravity = Gravity.CENTER;
                    linearLayout.setGravity(layoutParams.gravity);
                    ImageView ivPlay = new ImageView(getContext());
                    ivPlay.setImageResource(R.drawable.play_button);
                    ivPlay.setLayoutParams(layoutParams);
                    linearLayout.addView(ivPlay);
                    TextView tv = new TextView(getContext());
                    int remainingSize = mediaList.size() - 4;
                    tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
                    tv.setTextColor(Color.parseColor("#FFFFFF"));
                    tv.setText(String.format("+%d", remainingSize));
                    //tv.setGravity(Gravity.END);
                    tv.setGravity(layoutParams.gravity);
                    linearLayout.addView(tv);
                    flBottom.addView(linearLayout);
                    imageView.setOnClickListener(view -> {
                        if (listener != null)
                            listener.onImageClicked(mediaList, 0);
                    });
                    overlayView.setOnClickListener(view -> {
                        if (listener != null)
                            listener.onImageClicked(mediaList, 0);
                    });
                    ivPlay.setOnClickListener(view -> {
                        if (listener != null)
                            listener.onImageClicked(mediaList, 0);
                    });
                    mediaView = flBottom;
                } else {
                    FrameLayout flBottom = new FrameLayout(getContext());
                    flBottom.setLayoutParams(lp);

                    ImageView imageView = new ImageView(getContext());
                  /*  GlideApp.with(getContext())
                            .load(mediaThumbnailUrl)
                            .centerCrop()
                            .into(imageView);*/
                    Picasso.get().load(mediaThumbnailUrl).into(imageView);
                    flBottom.addView(imageView);

                    View overlayView = new View(getContext());
                    overlayView.setAlpha(0.4f);
                    overlayView.setBackgroundColor(Color.parseColor("#000000"));
                    flBottom.addView(overlayView);

                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams.gravity = Gravity.CENTER;
                    ImageView ivPlay = new ImageView(getContext());
                    ivPlay.setImageResource(R.drawable.play_button);
                    ivPlay.setLayoutParams(layoutParams);
                    flBottom.addView(ivPlay);
                    imageView.setOnClickListener(view -> {
                        if (listener != null)
                            listener.onImageClicked(mediaList, 0);
                    });
                    overlayView.setOnClickListener(view -> {
                        if (listener != null)
                            listener.onImageClicked(mediaList, 0);
                    });
                    ivPlay.setOnClickListener(view -> {
                        if (listener != null)
                            listener.onImageClicked(mediaList, 0);
                    });
                    mediaView = flBottom;
                }
            }

        } else if (mediaType.equalsIgnoreCase(MEDIA_DOCUMENT)) {
            if (imageCount == 2 || imageCount == 4) {
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
                lp.setMargins(5, 5, 5, 5);
                ImageView imageView = new ImageView(getContext());
                imageView.setLayoutParams(lp);
                /*GlideApp.with(getContext())
                        .load( R.drawable.pdfview1)
                        .centerCrop()
                        .into(imageView);*/
                Picasso.get().load(mediaThumbnailUrl).into(imageView);
                imageView.setOnClickListener(view -> {
                    if (listener != null) {
                        listener.onImageClicked(mediaList, position);
                    }
                });
                mediaView = imageView;
            } else {
                int height = getLayoutParams().height / 2;
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, height, 1.0f);
                lp.setMargins(5, 5, 5, 5);

                if (position == 4 && mediaList.size() > 5) {
                    FrameLayout flBottom = new FrameLayout(getContext());
                    flBottom.setLayoutParams(lp);

                    ImageView imageView = new ImageView(getContext());
                    imageView.setLayoutParams(lp);
                   /* GlideApp.with(getContext())
                            .load( R.drawable.pdfview1)
                            .centerCrop()
                            .into(imageView);*/
                    Picasso.get().load(mediaThumbnailUrl).into(imageView);
                    flBottom.addView(imageView);
                    View overlayView = new View(getContext());
                    overlayView.setAlpha(0.4f);
                    overlayView.setBackgroundColor(Color.parseColor("#000000"));
                    flBottom.addView(overlayView);
                    TextView tv = new TextView(getContext());
                    int remainingSize = mediaList.size() - 4;
                    tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
                    tv.setTextColor(Color.parseColor("#FFFFFF"));
                    tv.setText(String.format("+%d", remainingSize));
                    tv.setGravity(Gravity.CENTER);
                    flBottom.addView(tv);

                    imageView.setOnClickListener(view -> {
                        if (listener != null) {
                            listener.onImageClicked(mediaList, position);
                        }
                    });
                    overlayView.setOnClickListener(view -> {
                        if (listener != null)
                            listener.onImageClicked(mediaList, 0);
                    });
                    mediaView = flBottom;
                } else {
                    ImageView imageView = new ImageView(getContext());
                    imageView.setLayoutParams(lp);
                  /*  GlideApp.with(getContext())
                            .load( R.drawable.pdfview1)
                            .centerCrop()
                            .into(imageView);*/
                    Picasso.get().load(mediaThumbnailUrl).into(imageView);
                    imageView.setOnClickListener(view -> {
                        if (listener != null) {
                            listener.onImageClicked(mediaList, position);
                        }
                    });
                    mediaView = imageView;
                }

            }
        }
        return mediaView;
    }

    public interface MediaGridClickListener {
        void onImageClicked(List<Media> mediaList, int position);
    }
}

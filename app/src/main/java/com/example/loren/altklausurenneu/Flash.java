package com.example.loren.altklausurenneu;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.StringDef;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import io.fotoapparat.Fotoapparat;
import io.fotoapparat.configuration.UpdateConfiguration;
import io.fotoapparat.selector.FlashSelectorsKt;

public class Flash {

    private String TAG = "FLASH";


    @StringDef({FLASH_OFF, FLASH_ON, FLASH_AUTO})
    public @interface FlashMode {
    }

    public static final String FLASH_OFF = "off";
    public static final String FLASH_ON = "on";
    public static final String FLASH_AUTO = "auto";

    private String modus;
    private Context context;
    private Fotoapparat fotoapparat;
    private ImageView imageView;
    private String defaultmode;


    public Flash(Fotoapparat fotoapparat, ImageView imageView, Context context, @FlashMode String Defaultmode) {
        this.fotoapparat = fotoapparat;
        this.imageView = imageView;
        this.context = context;
        setModus(Defaultmode);
    }

    public String getModus() {
        return modus;
    }

    /**
     * Set the modus of the flash
     *
     * @param modus off, auto, on
     */
    public void setModus(@FlashMode String modus) {
        this.modus = modus;
        if (getModus().equals("off")) {
            this.imageView.setImageResource(R.drawable.ic_flash_off);
            Log.d(TAG, "OOOOFF");
        }
        if (getModus().equals("on")) {
            this.imageView.setImageResource(R.drawable.ic_flash_on_indicator);
        }
        if (getModus().equals("auto")) {
            this.imageView.setImageResource(R.drawable.ic_automatic_flash_symbol);
        }

    }

    /**
     * Set modus first!! -> Updates configuration of flash
     */
    public void updateConfiguration() {

        this.fotoapparat.updateConfiguration(UpdateConfiguration
                .builder()
                .flash(
                        getModus().equals("off") ? FlashSelectorsKt.off() : (getModus().equals("on") ? FlashSelectorsKt.on() : FlashSelectorsKt.autoFlash())
                )
                .build());


    }

    /**
     * Saves the current chosen flash in shared Preferences
     */
    public void saveState() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Flash", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Modus", getModus());
        editor.commit();
    }

    /**
     * set the last flash mode
     */
    public void getState() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Flash", Context.MODE_PRIVATE);
        String modus = sharedPreferences.getString("Modus", null);
        if (modus != null) {
            setModus(modus);
        }
    }

    public void setNextModus() {

        if (this.getModus().equals("auto")) {
            this.setModus(FLASH_OFF);

            Log.d(TAG, "Equals Auto");
        } else {
            if (getModus().equals("off")) {
                this.setModus(FLASH_ON);
            } else {
                if (getModus().equals("on")) {
                    this.setModus(FLASH_AUTO);
                }
            }
        }


    }


}

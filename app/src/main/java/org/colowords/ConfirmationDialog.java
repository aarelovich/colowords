package org.colowords;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class ConfirmationDialog {

    public interface OnOptionSelectedListener {
        void onYesSelected();
        void onNoSelected();

    }

    public static void Show(Context context, String title, String message, final OnOptionSelectedListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (listener != null) {
                            listener.onYesSelected();
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (listener != null) {
                            listener.onNoSelected();
                        }
                    }
                })
                .setCancelable(false) // Prevent dialog from being dismissed on outside touch or back button press
                .show();
    }
}

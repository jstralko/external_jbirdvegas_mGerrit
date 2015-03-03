package com.jbirdvegas.mgerrit;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by gstralko on 3/4/15.
 */
public class SetAuthDialog extends DialogFragment {

    public interface AuthDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog, String authToken);
    }

    AuthDialogListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            mListener = (AuthDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement AuthDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View root = inflater.inflate(R.layout.set_username_password_layout, null);
        builder.setView(root);
        builder.setMessage(getResources().getString(R.string.set_username_password_title))
                .setPositiveButton(getString(R.string.set_username_password_set), null)
                .setNegativeButton(getString(R.string.set_username_password_cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });

        final AlertDialog d = builder.create();
        d.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {

                Button b = d.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        EditText etUsername = (EditText)root.findViewById(R.id.username);
                        EditText etPassword = (EditText)root.findViewById(R.id.password);
                        EditText etPassword2 = (EditText)root.findViewById(R.id.password2);

                        String username = etUsername.getText().toString();
                        String password = etPassword.getText().toString();
                        String password2 = etPassword2.getText().toString();

                        if (username == null || username.length() <= 0) {
                            return;
                        }

                        if (!password.equals(password2) || password.length() <= 0) {
                            return;
                        }

                        String plainText = String.format("%s:%s", username, password);
                        String authToken = Base64.encodeToString(plainText.getBytes(), Base64.NO_WRAP);
                        mListener.onDialogPositiveClick(SetAuthDialog.this, authToken);
                        d.dismiss();
                    }
                });
            }
        });

        return d;
    }
}
